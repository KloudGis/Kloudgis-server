/*
 * @author corneliu
 */
package org.kloudgis;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.kloudgis.admin.pojo.DatasourceInfo;
import org.kloudgis.admin.store.DatasourceDbEntity;
import org.kloudgis.admin.store.SourceColumnsDbEntity;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.data.store.AbstractPlaceDbEntity;
import org.kloudgis.data.store.PathDbEntity;
import org.kloudgis.data.store.PoiDbEntity;
import org.kloudgis.data.store.ZoneDbEntity;
import org.kloudgis.org.Envelope;
import org.kloudgis.org.FileInfo;
import org.kloudgis.org.LayerInfo;
import org.kloudgis.org.runtime.Ogr2ogr;
import org.kloudgis.org.runtime.OgrInfo;
import org.kloudgis.persistence.PersistenceManager;

public class DatasourceFactory {

    /**
     * Load a datasource in to a sandbox.
     * @param usr           the logged user
     * @param lSandBoxID    the target sandbox
     * @param lSourceID     the source 
     * @param mapAttrs      mapping for static field (optional)
     * @return Http Response
     * @throws IOException 
     */
    public static Response loadData(UserDbEntity usr, Long lSandBoxID, Long lSourceID, HashMap<String, String> mapAttrs) throws IOException {
        System.err.println("+++Loading data to sandboxid=" + lSandBoxID + " from sourcesid=" + lSourceID);
        if (lSandBoxID == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new Message("Sandbox ID cannot be null", MessageCode.SEVERE)).build();
        }
        if (lSourceID == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new Message("Source ID cannot be null", MessageCode.SEVERE)).build();
        }
        if (usr == null && usr.getId() != null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(
                    new Message("Unknown user.", MessageCode.SEVERE)).build();
        }
        DatasourceDbEntity dbDatasource = getDatasource(lSourceID);
        int iCommitted = 0;
        if (dbDatasource != null && dbDatasource.getOwnerID() != null) {
            int geoNotParsed = 0;
            if (dbDatasource.getOwnerID().equals(usr.getId())) {
                EntityManager emg = PersistenceManager.getInstance().getEntityManagerBySandboxId(lSandBoxID);
                if (emg != null) {
                    File fRead = null;
                    try {
                        fRead = new File(unzip(dbDatasource.getDataFile().getAbsolutePath(), dbDatasource.getFileName()));
                    } catch (Exception e) {
                        emg.close();
                        throw new IOException(e);
                    }
                    //convert to lat long if necessary
                    if (dbDatasource.getCRS() == null || dbDatasource.getCRS().intValue() != 4326) {
                        String strCRS = null;
                        Integer iCrs = dbDatasource.getCRS();
                        if (iCrs != null) {
                            strCRS = "EPSG:" + iCrs;
                        }
                        try {
                            fRead = new Ogr2ogr().convertTo(fRead, "EPSG:4326", strCRS);
                        } catch (IOException e) {
                            System.out.println("Could'nt convert file:" + e.getMessage());
                            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                                    new Message("Could'nt convert", MessageCode.SEVERE)).build();
                        }
                    }
                    System.err.println("=> About to parse file: " + fRead.getAbsolutePath());
                    DsStream stream = new DsStream(emg, mapAttrs, lSandBoxID);
                    try {
                        new OgrInfo().readFeatures(fRead, stream, dbDatasource.getLayer());
                    } catch (Exception e) {
                        emg.close();
                        throw new IOException(e);
                    }
                    geoNotParsed = stream.getGeoNotParsed();
                    iCommitted = stream.getCount();
                    //to be sure
                    if (emg.getTransaction().isActive()) {
                        emg.getTransaction().commit();
                    }
                    emg.close();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).entity(
                            new Message("Entity manager not found for sandbox id: " + lSandBoxID, MessageCode.SEVERE)).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            if (geoNotParsed > 0) {
                Response.ok().entity(new Message("Could not parse the following number geometries: " + geoNotParsed, MessageCode.WARNING)).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(
                    new Message("Datasource not found for id: " + lSourceID, MessageCode.SEVERE)).build();
        }
        return Response.ok().entity(new Message("Number of features successfully committed: " + iCommitted, MessageCode.INFO)).build();
    }

    /**
     * Get a datasource by its Id
     * @param lID the datasource id
     * @return 
     */
    public static DatasourceDbEntity getDatasource(Long lID) {
        EntityManager emg = PersistenceManager.getInstance().getAdminEntityManager();
        DatasourceDbEntity ent = (DatasourceDbEntity) emg.find(DatasourceDbEntity.class, lID);
        emg.close();
        return ent;
    }

    /**
     * Add a file to the datasource table
     * @param emg       entity manage for ADMIN
     * @param usr       the logged user
     * @param strPath   the path to the file to load
     * @return the list of datasource id created
     * @throws WebApplicationException
     * @throws IOException 
     */
    public static List<Long> addDatasource(EntityManager emg, UserDbEntity usr, DatasourceInfo info) throws IOException {
        if (info != null && info.path != null) {
            File file = new File(info.path);
            if (file.exists()) {
                ArrayList<DatasourceDbEntity> arrlDse = persistDatasourceEntity(usr, file, info.crs, emg);
                if (arrlDse != null && arrlDse.size() > 0) {
                    List<Long> arrlID = new ArrayList();
                    for (DatasourceDbEntity ds : arrlDse) {
                        arrlID.add(ds.getID());
                    }
                    return arrlID;
                } else {
                    throw new IOException(new Exception("Could not add the datasource: " + info.path));
                }
            } else {
                throw new IOException(new IllegalArgumentException("File not found for path: " + info.path));
            }
        } else {
            throw new WebApplicationException(new IllegalArgumentException("The path can't be null."));
        }
    }

    private static ArrayList<DatasourceDbEntity> persistDatasourceEntity(UserDbEntity usr, File file, Integer iCRS, EntityManager em) throws IOException {
        FileInfo info = new OgrInfo().info(file);
        File zip = zip(file);
        ArrayList<DatasourceDbEntity> arrlDs = new ArrayList();
        for (LayerInfo layer : info.getLayers()) {
            DatasourceDbEntity dse = new DatasourceDbEntity();
            dse.setFileName(info.getName());
            Map<String, String> scm = layer.getSchema();
            dse.setColumnCount(scm.size());
            dse.setFeatureCount(layer.getFeatureCount());
            dse.setGeomType(layer.getGeomeryType());
            dse.setLayerName(layer.getLayerName());
            dse.setOwnerID(usr.getId());
            dse.setCRS(iCRS);
            Envelope env = layer.getExtent();
            if (env != null) {
                dse.setMinX(env.getLowX());
                dse.setMinY(env.getLowY());
                dse.setMaxX(env.getHighX());
                dse.setMaxY(env.getHighY());
            }
            //TODO Jeff: put the file in a separe table to not have to duplicate the file for each layer ?
            dse.setDataFile(zip);
            em.persist(dse);
            arrlDs.add(dse);
            persistColumnsEntities(layer, em, dse);
        }
        return arrlDs;
    }

    private static void persistColumnsEntities(LayerInfo info, EntityManager em, DatasourceDbEntity dse) {
        Map<String, String> scm = info.getSchema();
        if (scm != null) {
            for (String att : scm.keySet()) {
                SourceColumnsDbEntity cle = new SourceColumnsDbEntity();
                cle.setName(att);
                cle.setType(scm.get(att));
                cle.setDatasource(dse);
                em.persist(cle);
            }

        }

    }

    private static File zip(File file) throws IOException {
        File fTemp = File.createTempFile(file.getName(), ".zip");
        byte[] buf = new byte[2048];
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fTemp));
        ArrayList<File> arlFiles = getAllFiles(file);
        for (File f : arlFiles) {
            FileInputStream in = new FileInputStream(f);
            out.putNextEntry(new ZipEntry(f.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
        out.close();
        return fTemp;
    }

    private static ArrayList<File> getAllFiles(File file) {
        ArrayList<File> arlFiles = new ArrayList<File>();
        String strName = file.getName();
        if (strName.toLowerCase().endsWith(".shp")) {
            File[] files = file.getParentFile().listFiles();
            strName = strName.substring(0, strName.lastIndexOf(".") + 1);
            for (File f : files) {
                String strF = f.getName();
                if (strF.startsWith(strName)) {
                    arlFiles.add(f);
                }
            }
        } else {
            arlFiles.add(file);
        }
        return arlFiles;
    }

    protected static ArrayList<AbstractPlaceDbEntity> getDbEntities(String strWKT) throws ParseException {
        Geometry geo = GeometryFactory.readWKT(strWKT);
        if (geo instanceof Point) {
            ArrayList<AbstractPlaceDbEntity> arlGeom = new ArrayList<AbstractPlaceDbEntity>(1);
            PoiDbEntity pen = new PoiDbEntity();
            pen.setGeom(geo);
            arlGeom.add(pen);
            return arlGeom;
        } else if (geo instanceof LineString) {
            ArrayList<AbstractPlaceDbEntity> arlGeom = new ArrayList<AbstractPlaceDbEntity>(1);
            PathDbEntity pen = new PathDbEntity();
            pen.setGeom(geo);
            arlGeom.add(pen);
            return arlGeom;
        } else if (geo instanceof Polygon) {
            ArrayList<AbstractPlaceDbEntity> arlGeom = new ArrayList<AbstractPlaceDbEntity>(1);
            ZoneDbEntity zen = new ZoneDbEntity();
            zen.setGeom(geo);
            arlGeom.add(zen);
            return arlGeom;
        }
        if (geo instanceof MultiPoint) {
            return getPoiEntities((MultiPoint) geo);
        } else if (geo instanceof MultiLineString) {
            return getPathEntities((MultiLineString) geo);
        } else if (geo instanceof MultiPolygon) {
            return getZoneEntities((MultiPolygon) geo);
        } else if (geo instanceof GeometryCollection) {
            return getGeomCollectionEntities((GeometryCollection) geo);
        }
        return null;
    }

    private static ArrayList<AbstractPlaceDbEntity> getGeomCollectionEntities(GeometryCollection gcl) {
        if (gcl != null) {
            int iSize = gcl.getNumGeometries();
            ArrayList<AbstractPlaceDbEntity> arlGeom = new ArrayList<AbstractPlaceDbEntity>(iSize);
            for (int i = 0; i < iSize; i++) {
                Geometry geo = gcl.getGeometryN(i);
                if (geo instanceof Point) {
                    PoiDbEntity pen = new PoiDbEntity();
                    pen.setGeom(geo.getGeometryN(i));
                    arlGeom.add(pen);
                } else if (geo instanceof LineString) {
                    PathDbEntity pen = new PathDbEntity();
                    pen.setGeom(geo);
                    arlGeom.add(pen);
                } else if (geo instanceof Polygon) {
                    ZoneDbEntity zen = new ZoneDbEntity();
                    zen.setGeom(geo);
                    arlGeom.add(zen);
                }
            }
            return arlGeom;
        }
        return null;
    }

    private static ArrayList<AbstractPlaceDbEntity> getZoneEntities(MultiPolygon mpg) {
        int iSize = mpg.getNumGeometries();
        ArrayList<AbstractPlaceDbEntity> arlEnt = new ArrayList<AbstractPlaceDbEntity>(iSize);
        for (int i = 0; i < iSize; i++) {
            Geometry geo = mpg.getGeometryN(i);
            if (geo != null) {
                ZoneDbEntity zen = new ZoneDbEntity();
                zen.setGeom(mpg.getGeometryN(i));
                arlEnt.add(zen);
            }
        }
        return arlEnt;
    }

    private static ArrayList<AbstractPlaceDbEntity> getPathEntities(MultiLineString mls) {
        int iSize = mls.getNumGeometries();
        ArrayList<AbstractPlaceDbEntity> arlEnt = new ArrayList<AbstractPlaceDbEntity>(iSize);
        for (int i = 0; i < iSize; i++) {
            Geometry geo = mls.getGeometryN(i);
            if (geo != null) {
                PathDbEntity pen = new PathDbEntity();
                pen.setGeom(mls.getGeometryN(i));
                arlEnt.add(pen);
            }
        }
        return arlEnt;
    }

    private static ArrayList<AbstractPlaceDbEntity> getPoiEntities(MultiPoint mpt) {
        int iSize = mpt.getNumGeometries();
        ArrayList<AbstractPlaceDbEntity> arlEnt = new ArrayList<AbstractPlaceDbEntity>(iSize);
        for (int i = 0; i < iSize; i++) {
            Geometry geo = mpt.getGeometryN(i);
            if (geo != null) {
                PoiDbEntity pen = new PoiDbEntity();
                pen.setGeom(mpt.getGeometryN(i));
                arlEnt.add(pen);
            }
        }
        return arlEnt;
    }

    private static String unzip(String strPath, String strFileName) throws ZipException, IOException {
        String strFolderPath = strPath + "_folder/";
        new File(strFolderPath).mkdirs();
        ZipFile zpf = new ZipFile(strPath);
        Enumeration enu = zpf.entries();
        while (enu.hasMoreElements()) {
            ZipEntry zen = (ZipEntry) enu.nextElement();
            FileOutputStream fos = new FileOutputStream(strFolderPath + zen.getName());
            InputStream in = zpf.getInputStream(zen);
            byte[] buffer = new byte[2048];
            int len;
            while ((len = in.read(buffer)) >= 0) {
                fos.write(buffer, 0, len);
            }
            in.close();
            fos.close();
        }
        return strFolderPath + strFileName;
    }
}