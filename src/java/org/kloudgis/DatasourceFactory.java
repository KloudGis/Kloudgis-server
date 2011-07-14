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
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.persistence.EntityManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.kloudgis.admin.pojo.Datasource;
import org.kloudgis.admin.store.DatasourceDbEntity;
import org.kloudgis.admin.store.SourceColumnsDbEntity;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.data.store.AbstractPlaceDbEntity;
import org.kloudgis.data.store.PathDbEntity;
import org.kloudgis.data.store.PoiDbEntity;
import org.kloudgis.data.store.ZoneDbEntity;
import org.kloudgis.gdal.Envelope;
import org.kloudgis.gdal.Feature;
import org.kloudgis.gdal.Parser;
import org.kloudgis.gdal.schema.AttrType;
import org.kloudgis.gdal.schema.RealListType;
import org.kloudgis.gdal.schema.RealType;
import org.kloudgis.gdal.schema.Schema;
import org.kloudgis.gdal.schema.StringListType;
import org.kloudgis.gdal.schema.StringType;
import org.kloudgis.persistence.PersistenceManager;

public class DatasourceFactory {

    public static Response loadData( UserDbEntity usr, Long lSandBoxID, Long lSourceID, HashMap<String, String> mapAttrs )
            throws ZipException, IOException, ParseException {
        System.out.println("+++Loading data to sandboxid=" + lSandBoxID + " from sourcesid=" + lSourceID);
        if( lSandBoxID == null ) {
            return Response.status( Response.Status.BAD_REQUEST ).entity(
                        new Message( "Sandbox ID cannot be null", MessageCode.SEVERE ) ).build();
        }
        if( lSourceID == null ) {
            return Response.status( Response.Status.BAD_REQUEST ).entity(
                        new Message( "Source ID cannot be null", MessageCode.SEVERE ) ).build();
        }
        if( usr == null && usr.getId() != null ) {
            return Response.status( Response.Status.UNAUTHORIZED ).entity(
                        new Message( "Unknown user.", MessageCode.SEVERE ) ).build();
        }
        Datasource dts = getDatasource( lSourceID );
        System.err.println("datasource found:" + dts.lID);
        int iCommitted = 0;
        if( dts != null && dts.lOwnerID != null ) {
            StringBuilder stbGeomNotParsed = new StringBuilder();
            if( dts.lOwnerID.equals( usr.getId() ) ) {
                EntityManager emg = PersistenceManager.getInstance().getEntityManagerBySandboxId( lSandBoxID );
                if( emg != null ) {
                    System.err.println("=> About to parse file: " + dts.filePath);
                    Parser prs = new Parser( unzip( dts.filePath, dts.strFileName ) );
                    Feature ftr = null;
                    while( ( ftr = prs.getNextFeature() ) != null ) {                    
                        ArrayList<AbstractPlaceDbEntity> arlEntities = getDbEntities( ftr.getGeometryAsWKT() );
                        if( arlEntities != null ) {
                            for( AbstractPlaceDbEntity ent : arlEntities ) {
                                emg.getTransaction().begin();
                                ent.setupFromFeature( ftr, emg, mapAttrs );
                                emg.persist( ent );
                                emg.getTransaction().commit();
                                iCommitted++;
                            }
                        } else {
                            stbGeomNotParsed.append( ftr.getGeometryAsWKT() ).append( "\n" );
                        }
                    }
                    prs.close();
                   /* int i = 0;
                    emg.getTransaction().begin();
                    while( i++ < 10000) {
                        
                        PoiDbEntity poi = new PoiDbEntity();
                        poi.setName(i+ "");
                        poi.setType("yoyo");
                        Point pt = GeometryFactory.generateLonLat();
                        poi.setGeom(pt);
                        emg.persist(poi);
                        if(i % 20 == 0){
                            emg.flush();
                            emg.clear();
                        }
                    }
                    emg.getTransaction().commit();
                    emg.close();*/
                    
                } else {
                    return Response.status( Response.Status.NOT_FOUND ).entity(
                            new Message( "Entity manager not found for sandbox id: " + lSandBoxID, MessageCode.SEVERE ) ).build();
                }
            } else {
                return Response.status( Response.Status.UNAUTHORIZED ).build();
            }
            if( stbGeomNotParsed.length() > 0 ) {
                return Response.status( Response.Status.UNSUPPORTED_MEDIA_TYPE ).entity(
                        new Message( "Could not parse the following geometries: " +
                        stbGeomNotParsed.toString(), MessageCode.WARNING ) ).build();
            }
        } else {
            return Response.status( Response.Status.NOT_FOUND ).entity(
                        new Message( "Datasource not found for id: " + lSourceID, MessageCode.SEVERE ) ).build();
        }
        return Response.ok().entity( new Message( "Number of features successfully committed: " + iCommitted, MessageCode.INFO ) ).build();
    }

    public static Datasource getDatasource( Long lID ) {
        EntityManager emg = PersistenceManager.getInstance().getAdminEntityManager();
        DatasourceDbEntity ent = ( DatasourceDbEntity )emg.find( DatasourceDbEntity.class, lID );
        Datasource dts = null;
        if( ent != null ) {
            dts = ent.toPojo();
        }
        emg.close();
        return dts;
    }

    public static Datasource addDatasource( UserDbEntity usr, String strPath ) throws WebApplicationException, IOException {
        Datasource ftr = null;
        if( strPath != null ) {
            File file = new File( strPath );
            if( file.exists() ) {
                EntityManager emg = PersistenceManager.getInstance().getAdminEntityManager();
                DatasourceDbEntity dse = persistDatasourceEntity( usr, file, emg );
                if( dse != null ) {
                    ftr = dse.toPojo();
                } else {
                    throw new WebApplicationException( new Exception( "Could not add the datasource: " + strPath ) );
                }
                emg.close();
            } else {
                throw new WebApplicationException( new IllegalArgumentException( "File not found for path: " + strPath ) );
            }
        } else {
            throw new WebApplicationException( new IllegalArgumentException( "The path can't be null." ) );
        }
        return ftr;
    }

    private static DatasourceDbEntity persistDatasourceEntity( UserDbEntity usr, File file, EntityManager em ) throws IOException {
        Parser prs = new Parser( file.getAbsolutePath() );
        String strErrors = prs.getErrorMessages();
        if( strErrors == null ) {
            em.getTransaction().begin();
            DatasourceDbEntity dse = new DatasourceDbEntity();
            dse.setFileName( file.getName() );
            dse.setCRS( prs.getCRS() );
            Schema scm = prs.getSchema();
            if( scm != null ) {
                dse.setColumnCount( scm.getAttrCount() );
            }
            dse.setFeatureCount( prs.getFeatureCount() );
            dse.setFileSize( prs.getFileSize() );
            dse.setGeomName( prs.getGeomName() );
            dse.setGeomType( prs.getGeomType() );
            dse.setLastModified( prs.getLastModified() );
            dse.setLayerCount( prs.getLayerCount() );
            dse.setOwnerID( usr.getId() );
            Envelope env = prs.getExtent();
            if( env != null ) {
                dse.setMinX( env.getLowX() );
                dse.setMinY( env.getLowY() );
                dse.setMaxX( env.getHighX() );
                dse.setMaxY( env.getHighY() );
            }
            File zip = zip( file );
            dse.setDataFile( zip );
            em.persist( dse );
            persistColumnsEntities( prs, em, dse );
            em.getTransaction().commit();
            zip.delete();
            return dse;
        }
        return null;
    }

    private static void persistColumnsEntities( Parser prs, EntityManager em, DatasourceDbEntity dse ) {
        String strErrors = prs.getErrorMessages();
        if( strErrors == null ) {
            Schema scm = prs.getSchema();
            if( scm != null ) {
                Set<AttrType> setAttrs = scm.getAttrTypes();
                if( setAttrs != null ) {
                    for( AttrType att : setAttrs ) {
                        SourceColumnsDbEntity cle = new SourceColumnsDbEntity();
                        cle.setName( att.getName() );
                        cle.setType( att.getClass().getName() );
                        if( att instanceof RealType ) {
                            cle.setPrecision( ( ( RealType )att ).getPrecision() );
                        } else if( att instanceof RealListType ) {
                            cle.setPrecision( ( ( RealListType )att ).getPrecision() );
                        } else if( att instanceof StringType ) {
                            cle.setLength( ( ( StringType )att ).getWidth() );
                            cle.setJustify( ( ( StringType )att ).getJustify() );
                        } else if( att instanceof StringListType ) {
                            cle.setLength( ( ( StringListType )att ).getWidth() );
                            cle.setJustify( ( ( StringListType )att ).getJustify() );
                        }
                        cle.setDatasource( dse );
                        em.persist( cle );
                    }
                }
            }
        }
    }

    private static File zip( File file ) throws IOException {
        File fTemp = File.createTempFile( file.getName(), ".zip" );
        byte[] buf = new byte[2048];
        ZipOutputStream out = new ZipOutputStream( new FileOutputStream( fTemp ) );
        ArrayList<File> arlFiles = getAllFiles( file );
        for( File f : arlFiles ) {
            FileInputStream in = new FileInputStream( f );
            out.putNextEntry( new ZipEntry( f.getName() ) );
            int len;
            while( ( len = in.read( buf ) ) > 0 ) {
                out.write( buf, 0, len );
            }
            out.closeEntry();
            in.close();
        }
        out.close();
        return fTemp;
    }

    private static ArrayList<File> getAllFiles( File file ) {
        ArrayList<File> arlFiles = new ArrayList<File>();
        String strName = file.getName();
        if( strName.toLowerCase().endsWith( ".shp" ) ) {
            File[] files = file.getParentFile().listFiles();
            strName = strName.substring( 0, strName.lastIndexOf( "." ) + 1 );
            for( File f : files ) {
                String strF = f.getName();
                if( strF.startsWith( strName ) ) {
                    arlFiles.add( f );
                }
            }
        } else {
            arlFiles.add( file );
        }
        return arlFiles;
    }

    private static ArrayList<AbstractPlaceDbEntity> getDbEntities( String strWKT ) throws ParseException {
        Geometry geo = GeometryFactory.readWKT( strWKT );
        if( geo instanceof Point ) {
            ArrayList<AbstractPlaceDbEntity> arlGeom = new ArrayList<AbstractPlaceDbEntity>( 1 );
            PoiDbEntity  pen = new PoiDbEntity();
            pen.setGeom( geo );
            arlGeom.add( pen );
            return arlGeom;
        } else if( geo instanceof LineString ) {
            ArrayList<AbstractPlaceDbEntity> arlGeom = new ArrayList<AbstractPlaceDbEntity>( 1 );
            PathDbEntity pen = new PathDbEntity();
            pen.setGeom( geo );
            arlGeom.add( pen );
            return arlGeom;
        } else if( geo instanceof Polygon ) {
            ArrayList<AbstractPlaceDbEntity> arlGeom = new ArrayList<AbstractPlaceDbEntity>( 1 );
            ZoneDbEntity zen = new ZoneDbEntity();
            zen.setGeom( geo );
            arlGeom.add( zen );
            return arlGeom;
        } if( geo instanceof MultiPoint ) {
            return getPoiEntities( ( MultiPoint )geo );
        } else if( geo instanceof MultiLineString ) {
            return getPathEntities( ( MultiLineString )geo );
        } else if( geo instanceof MultiPolygon ) {
            return getZoneEntities( ( MultiPolygon )geo );
        } else if( geo instanceof GeometryCollection ) {
            return getGeomCollectionEntities( ( GeometryCollection )geo );
        }
        return null;
    }

    private static ArrayList<AbstractPlaceDbEntity> getGeomCollectionEntities( GeometryCollection gcl ) {
        if( gcl != null ) {
            int iSize = gcl.getNumGeometries();
            ArrayList<AbstractPlaceDbEntity> arlGeom = new ArrayList<AbstractPlaceDbEntity>( iSize );
            for( int i = 0; i < iSize; i++ ) {
                Geometry geo = gcl.getGeometryN( i );
                if( geo instanceof Point ) {
                    PoiDbEntity  pen = new PoiDbEntity();
                    pen.setGeom( geo.getGeometryN( i ) );
                    arlGeom.add( pen );
                } else if( geo instanceof LineString ) {
                    PathDbEntity pen = new PathDbEntity();
                    pen.setGeom( geo );
                    arlGeom.add( pen );
                } else if( geo instanceof Polygon ) {
                    ZoneDbEntity zen = new ZoneDbEntity();
                    zen.setGeom( geo );
                    arlGeom.add( zen );
                }
            }
            return arlGeom;
        }
        return null;
    }

    private static ArrayList<AbstractPlaceDbEntity> getZoneEntities( MultiPolygon mpg ) {
        int iSize = mpg.getNumGeometries();
        ArrayList<AbstractPlaceDbEntity> arlEnt = new ArrayList<AbstractPlaceDbEntity>( iSize );
        for( int i = 0; i < iSize; i++ ) {
            Geometry geo = mpg.getGeometryN( i );
            if( geo != null ) {
                ZoneDbEntity zen = new ZoneDbEntity();
                zen.setGeom( mpg.getGeometryN( i ) );
                arlEnt.add( zen );
            }
        }
        return arlEnt;
    }

    private static ArrayList<AbstractPlaceDbEntity> getPathEntities( MultiLineString mls ) {
        int iSize = mls.getNumGeometries();
        ArrayList<AbstractPlaceDbEntity> arlEnt = new ArrayList<AbstractPlaceDbEntity>( iSize );
        for( int i = 0; i < iSize; i++ ) {
            Geometry geo = mls.getGeometryN( i );
            if( geo != null ) {
                PathDbEntity pen = new PathDbEntity();
                pen.setGeom( mls.getGeometryN( i ) );
                arlEnt.add( pen );
            }
        }
        return arlEnt;
    }

    private static ArrayList<AbstractPlaceDbEntity> getPoiEntities( MultiPoint mpt ) {
        int iSize = mpt.getNumGeometries();
        ArrayList<AbstractPlaceDbEntity> arlEnt = new ArrayList<AbstractPlaceDbEntity>( iSize );
        for( int i = 0; i < iSize; i++ ) {
            Geometry geo = mpt.getGeometryN( i );
            if( geo != null ) {
                PoiDbEntity  pen = new PoiDbEntity();
                pen.setGeom( mpt.getGeometryN( i ) );
                arlEnt.add( pen );
            }
        }
        return arlEnt;
    }

   public static String unzip( String strPath, String strFileName ) throws ZipException, IOException {
        String strFolderPath = strPath + "_folder/";
        new File( strFolderPath ).mkdirs();
        ZipFile zpf = new ZipFile( strPath );
        Enumeration enu = zpf.entries();
        while( enu.hasMoreElements() ) {
            ZipEntry zen = ( ZipEntry )enu.nextElement();
            FileOutputStream fos = new FileOutputStream( strFolderPath + zen.getName() );
            InputStream in = zpf.getInputStream( zen );
            byte[] buffer = new byte[2048];
            int len;
            while( ( len = in.read( buffer ) ) >= 0 ) {
                fos.write( buffer, 0, len );
            }
            in.close();
            fos.close();
        }
        return strFolderPath + strFileName;
    }

}