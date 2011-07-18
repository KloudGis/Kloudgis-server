/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import com.vividsolutions.jts.io.ParseException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.Credentials;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.DatasourceFactory;
import org.kloudgis.mapserver.GeoserverException;
import org.kloudgis.mapserver.MapServerFactory;
import org.kloudgis.MessageCode;
import org.kloudgis.admin.pojo.DatasourceInfo;
import org.kloudgis.admin.pojo.Feed;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.store.DatasourceDbEntity;
import org.kloudgis.admin.store.FeedDbEntity;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.data.pojo.FetchResult;
import org.kloudgis.data.pojo.Member;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.persistence.DatabaseFactory;
import org.kloudgis.persistence.PersistenceManager;
import org.xml.sax.SAXException;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/admin/sandboxes")
@Produces({"application/json"})
public class SandboxResourceBean {

    /**
     * Get all the sandboxes.
     * @param auth_token
     * @return 
     */
    @GET
    public Response getSandboxes(@CookieParam(value = "security-Kloudgis.org") String auth_token) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity user = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        if (user != null) {
            Set<SandboxDbEntity> lstR = user.getSandboxes();
            List<Sandbox> lstPojo = new ArrayList();
            for (SandboxDbEntity sand : lstR) {
                lstPojo.add(sand.toPojo(em));
            }
            em.close();
            return Response.ok(lstPojo).build();
        } else {
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Bind a user to this sandbox
     * @param auth_token
     * @param sandboxId
     * @param usr
     * @return 
     */
    @POST
    @Path("{sandboxId}/bind_usr/{usrId}")
    public Response bindUser(@CookieParam(value = "security-Kloudgis.org") String auth_token, @PathParam("sandboxId") Long sandboxId, @PathParam("usrId") Long userId) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        AuthorizationManager authMan = new AuthorizationManager();
        UserDbEntity userSecure = authMan.getUserFromAuthToken(auth_token, em);
        if (userSecure != null) {
            em.getTransaction().begin();
            SandboxDbEntity sandbox = em.find(SandboxDbEntity.class, sandboxId);
            UserDbEntity userDb = em.find(UserDbEntity.class, userId);
            sandbox.bindUser(userDb);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
        } else {
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * UnBind a user to this sandbox
     * @param auth_token
     * @param sandboxId
     * @param usr
     * @return 
     */
    @POST
    @Path("{sandboxId}/unbind_usr/{usrId}")
    public Response unbindUser(@CookieParam(value = "security-Kloudgis.org") String auth_token, @PathParam("sandboxId") Long sandboxId, @PathParam("usrId") Long userId) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        AuthorizationManager authMan = new AuthorizationManager();
        UserDbEntity userSecure = authMan.getUserFromAuthToken(auth_token, em);
        if (userSecure != null) {
            em.getTransaction().begin();
            SandboxDbEntity sandbox = em.find(SandboxDbEntity.class, sandboxId);
            UserDbEntity userDb = em.find(UserDbEntity.class, userId);
            sandbox.unBindUser(userDb);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
        } else {
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Add a feed to this sandbox
     * @param auth_token
     * @param sandboxId
     * @param feed
     * @return 
     */
    @POST
    @Path("{sandboxId}/feeds")
    public Response addFeed(@CookieParam(value = "security-Kloudgis.org") String auth_token, @PathParam("sandboxId") Long sandboxId, Feed feed) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        AuthorizationManager authMan = new AuthorizationManager();
        UserDbEntity userSecure = authMan.getUserFromAuthToken(auth_token, em);
        if (userSecure != null) {
            em.getTransaction().begin();
            SandboxDbEntity sandbox = em.find(SandboxDbEntity.class, sandboxId);
            FeedDbEntity feedDb = new FeedDbEntity();
            feedDb.fromPojo(feed);
            sandbox.addFeed(feedDb);
            em.getTransaction().commit();
            Feed feedC = feedDb.toPojo(em);
            em.close();
            return Response.ok(feedC).build();
        } else {
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Get all the feeds from all sandboxes
     * @param auth_token
     * @param start
     * @param length
     * @param count
     * @return 
     */
    @GET
    @Path("feeds")
    public Response getFeeds(@CookieParam(value = "security-Kloudgis.org") String auth_token, @DefaultValue("0") @QueryParam("start") Integer start,
            @DefaultValue("-1") @QueryParam("length") Integer length, @DefaultValue("false") @QueryParam("count") Boolean count) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity user = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        if (user != null) {
            List<Feed> lstPojo = new ArrayList();
            Set<SandboxDbEntity> setS = user.getSandboxes();
            Criteria crit = buildFeedCriteria(em, setS);
            Long theCount = null;
            if (count) {
                theCount = ((Number) crit.setProjection(Projections.rowCount()).uniqueResult()).longValue();
                crit = buildFeedCriteria(em, setS);
            }
            crit.addOrder(Order.desc("date_creation"));
            crit.setFirstResult(start);
            if (length >= 0) {
                crit.setMaxResults(length);
            }
            List<FeedDbEntity> listF = crit.list();
            for (FeedDbEntity feed : listF) {
                lstPojo.add(feed.toPojo(em));
            }
            em.close();
            return Response.ok(new FetchResult(lstPojo, theCount)).build();
        } else {
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
// start of corneliu's stuff

    /**
     * Add a new sandbox 
     * @param strAuthToken auth token (security)
     * @param sbx   the sandbox properties
     * @return response code
     */
    @POST
    public Response addSandbox(@CookieParam(value = "security-Kloudgis.org") String strAuthToken, Sandbox inPojo) {
        if (inPojo == null || inPojo.connection_url == null || inPojo.name == null || inPojo.url_geoserver == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        System.out.println("Add SANDBOX!");
        HibernateEntityManager hemAdmin = PersistenceManager.getInstance().getAdminEntityManager();
        EntityManager emgSandbox = null;
        UserDbEntity usr = new AuthorizationManager().getUserFromAuthToken(strAuthToken, hemAdmin);
        if (usr == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        System.out.println("User OK:" + usr.getEmail());
        String strURL = inPojo.connection_url;
        Sandbox pojo = null;
        String dbName = inPojo.name;
        System.out.println("Will create the database at:" + strURL + " with name " + dbName);
        try {
            Message mess = DatabaseFactory.createDB(strURL, dbName);
            if (mess == null) {
                System.out.println("Create database OK");
                hemAdmin.getTransaction().begin();
                SandboxDbEntity sandbox = addSandbox(inPojo, dbName, usr, hemAdmin);
                hemAdmin.getTransaction().commit();
                pojo = sandbox.toPojo(hemAdmin);
                System.out.println("Sandbox db entity created with ID=" + sandbox.getId());
                emgSandbox = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandbox.getId());            
                
                if (emgSandbox == null) {
                    hemAdmin.close();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                            new Message("Error creating sandbox.", MessageCode.SEVERE)).build();
                }
                DatabaseFactory.createIndexes(emgSandbox);
                DatabaseFactory.loadModel(emgSandbox);
                emgSandbox.getTransaction().begin();
                //add everything the sandbox needs
                addMember(emgSandbox, usr);
                addLayer(emgSandbox, "poi", "EPSG:4326", usr.getId());
                addLayer(emgSandbox, "path", "EPSG:4326", usr.getId());
                addLayer(emgSandbox, "zone", "EPSG:4326", usr.getId());
                emgSandbox.getTransaction().commit();
                emgSandbox.close();
                //add default sources
                hemAdmin.getTransaction().begin();
                System.out.println("About to load default POI");
                long lPoiSourceID = addSource(usr, hemAdmin, "poi");
                if (lPoiSourceID >= 0) {
                    System.out.println("About to load default PATH");
                    long lPathSourceID = addSource(usr, hemAdmin, "path");
                    if (lPathSourceID >= 0) {
                        System.out.println("About to load default ZONE");
                        long lZoneSourceID = addSource(usr, hemAdmin, "zone");
                        if (lZoneSourceID >= 0) {
                            hemAdmin.getTransaction().commit();
                            //load the data in the sandbox
                            PersistenceManager.getInstance().markAccess(sandbox.getId());
                            loadSource(usr, sandbox.getId(), lPoiSourceID);
                            System.out.println("Poi data loaded");
                            PersistenceManager.getInstance().markAccess(sandbox.getId());
                            loadSource(usr, sandbox.getId(), lPathSourceID);
                            System.out.println("Path data loaded");
                            PersistenceManager.getInstance().markAccess(sandbox.getId());
                            loadSource(usr, sandbox.getId(), lZoneSourceID);
                            System.out.println("Zone data loaded");
                            //SET the geoserver
                            String strGeoserverURL = sandbox.getGeoserverUrl();
                            String workspace = sandbox.getName();
                            System.out.println("About to add geoserver WORKSPACE with name:" + workspace);
                            MapServerFactory.addWorkspace(strGeoserverURL, workspace, MapServerFactory.credentials);
                            int iColonIndex = strURL.lastIndexOf(":");
                            String strHost = null;
                            String strPort = null;
                            if (iColonIndex > 0) {
                                strHost = strURL.substring(0, iColonIndex);
                                strPort = strURL.substring(iColonIndex + 1);
                            }
                            System.out.println("About to add geoserver Store");
                            MapServerFactory.addStore(strGeoserverURL, strHost, strPort, workspace, dbName, MapServerFactory.credentials);
                            System.out.println("About to add geoserver LAYERS");
                            MapServerFactory.addLayer(hemAdmin, strGeoserverURL, workspace, dbName, "poi", lPoiSourceID, MapServerFactory.credentials);
                            MapServerFactory.addLayer(hemAdmin, strGeoserverURL, workspace, dbName, "path", lPathSourceID, MapServerFactory.credentials);
                            MapServerFactory.addLayer(hemAdmin, strGeoserverURL, workspace, dbName, "zone", lZoneSourceID, MapServerFactory.credentials);
                            System.out.println("About to add geoserver Styles");
                            assignStyles(strGeoserverURL, workspace, MapServerFactory.credentials);
                            hemAdmin.close();
                        } else {
                            hemAdmin.getTransaction().commit();
                            hemAdmin.close();
                            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                                    new Message("Error when inserting zone data.", MessageCode.SEVERE)).build();
                        }
                    } else {
                        hemAdmin.getTransaction().commit();
                        hemAdmin.close();
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                                new Message("Error when inserting path data.", MessageCode.SEVERE)).build();
                    }
                } else {
                    hemAdmin.getTransaction().commit();
                    hemAdmin.close();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                            new Message("Error when inserting poi data.", MessageCode.SEVERE)).build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (hemAdmin != null && hemAdmin.isOpen()) {
                if (hemAdmin.getTransaction().isActive()) {
                    hemAdmin.getTransaction().rollback();
                }
                hemAdmin.close();
            }
            if (emgSandbox != null && emgSandbox.isOpen()) {
                if (emgSandbox.getTransaction().isActive()) {
                    emgSandbox.getTransaction().rollback();
                }
                emgSandbox.close();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new Message("Unexepected Error:" + e.getMessage(), MessageCode.SEVERE)).build();
        }
        return Response.ok(pojo).build();
    }

    @DELETE
    @Path("{sandboxId}")
    public String deleteSandbox(@CookieParam(value = "security-Kloudgis.org") String auth_token, @PathParam("sandboxId") Long sandboxId) throws Exception {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity user = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        if (user != null) {
            em.getTransaction().begin();
            try {
                SandboxDbEntity sandbox = em.find(SandboxDbEntity.class, sandboxId);
                MapServerFactory.deleteWorkspace(sandbox.getGeoserverUrl(), sandbox.getName(), MapServerFactory.credentials);
                em.remove(sandbox);
                //Make a DB backup before?
                DatabaseFactory.dropDb(sandbox.getConnectionUrl(), sandbox.getName());

            } catch (Exception e) {
                em.getTransaction().rollback();
                em.close();
                throw e;
            }
            em.getTransaction().commit();
        }
        em.close();
        return "success";
    }

    private SandboxDbEntity addSandbox(Sandbox sbx, String unique, UserDbEntity user, HibernateEntityManager hemAdmin) {
        SandboxDbEntity sandbox = new SandboxDbEntity();
        sandbox.setName(sbx.name);
        sandbox.setURL(sbx.connection_url);
        sandbox.setUniqueKey(sbx.name);
        sandbox.setGeoserverURL(sbx.url_geoserver);
        sandbox.setUniqueKey(unique);
//        BaseLayerModeDbEntity blm = ...
//        blm.setID( sbx.baseLayerMode );
//        sen.setBaseLayerMode( blm );
        sandbox.bindUser(user);
        hemAdmin.persist(sandbox);
        
        return sandbox;
    }

    private MemberDbEntity addMember(EntityManager emgSandbox, UserDbEntity usr) {
        Member mbm = new Member();
        mbm.user = usr.getId();
        mbm.access = "whatever";//
        MemberDbEntity men = new MemberDbEntity();
        men.fromPojo(mbm);
        emgSandbox.persist(men);
        return men;
    }

    //default data
    private long addSource(UserDbEntity usr, HibernateEntityManager hem, String strType) throws ZipException, IOException, ParseException {
        Query qry = hem.createQuery("from DatasourceDbEntity where strFileName='" + strType + ".shp'");
        List<Object> lstRS = qry.getResultList();
        int iSize = lstRS.size();
        long lID = -1;
        if (iSize > 0) {
            lID = ((DatasourceDbEntity) lstRS.get(0)).getID();
        } else {
            DatasourceInfo info = new DatasourceInfo();
            info.path = MapServerFactory.getWebInfPath() + "classes/" + strType + ".shp";
            info.crs = 4326;
            lID = DatasourceFactory.addDatasource(hem, usr, info).get(0);
        }
        return lID;
    }

    private boolean loadSource(UserDbEntity usr, long lSandboxID, long sourceid) {
        try {
            return DatasourceFactory.loadData(usr, lSandboxID, sourceid, new HashMap<String, String>()).getStatus() != Response.Status.OK.getStatusCode();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void assignStyles(String strGeoserverURL, String strWorkspaceName, Credentials crd)
            throws MalformedURLException, IOException, ParserConfigurationException, SAXException, GeoserverException {
        List<String> lst = MapServerFactory.listStyles(strGeoserverURL, crd);
        String strPath = MapServerFactory.getWebInfPath() + "classes/";
        if (!lst.contains("poi")) {
            MapServerFactory.uploadStyle(strGeoserverURL, strPath + "poi.sld", crd);
        }
        if (!lst.contains("path")) {
            MapServerFactory.uploadStyle(strGeoserverURL, strPath + "path.sld", crd);
        }
        if (!lst.contains("zone")) {
            MapServerFactory.uploadStyle(strGeoserverURL, strPath + "zone.sld", crd);
        }
        MapServerFactory.assignStyle(strGeoserverURL, strWorkspaceName, "poi", "poi", crd);
        MapServerFactory.assignStyle(strGeoserverURL, strWorkspaceName, "path", "path", crd);
        MapServerFactory.assignStyle(strGeoserverURL, strWorkspaceName, "zone", "zone", crd);
    }

    private void addLayer(EntityManager emgSandbox, String strName, String strCRS, Long strOwner) {
        long lFtId = 0;
        Query qry = emgSandbox.createQuery("from FeatureTypeDbEntity where name='" + strName + "'");
        if (qry != null) {
            List<Object> lstRS = qry.getResultList();
            if (lstRS != null && lstRS.size() > 0) {
                FeatureTypeDbEntity fte = (FeatureTypeDbEntity) lstRS.get(0);
                if (fte != null) {
                    lFtId = fte.getId();
                }
            }
        }
        LayerDbEntity ent = new LayerDbEntity();
        ent.setCRS(strCRS);
        ent.setFeatureTypeID(lFtId);
        ent.setLabel(strName);
        ent.setName(strName);
        ent.setOwner(strOwner);
        ent.setSelectable(true);
        ent.setVisible(true);
        emgSandbox.persist(ent);
    }
// end of corneliu's stuff

    private Criteria buildFeedCriteria(HibernateEntityManager em, Set<SandboxDbEntity> lstR) {
        Criteria critMaster = em.getSession().createCriteria(FeedDbEntity.class);
        Criteria crit = critMaster.createCriteria("sandbox");
        Criterion or = null;
        for (SandboxDbEntity sand : lstR) {
            if (or != null) {
                or = Restrictions.or(or, Restrictions.eq("id", sand.getId()));
            } else {
                or = Restrictions.eq("id", sand.getId());
            }
        }
        crit.add(or);
        return critMaster;
    }
}