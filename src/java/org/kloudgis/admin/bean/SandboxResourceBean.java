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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.CookieParam;
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
import org.kloudgis.GeoserverException;
import org.kloudgis.MapServerFactory;
import org.kloudgis.MessageCode;
import org.kloudgis.admin.pojo.Feed;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.pojo.User;
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

    @POST
    @Path("{sandboxId}/users")
    public Response addUser(@CookieParam(value = "security-Kloudgis.org") String auth_token, @PathParam("sandboxId") Long sandboxId, User usr) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        AuthorizationManager authMan = new AuthorizationManager();
        UserDbEntity userSecure = authMan.getUserFromAuthToken(auth_token, em);
        if (userSecure != null) {
            em.getTransaction().begin();
            SandboxDbEntity sandbox = em.find(SandboxDbEntity.class, sandboxId);
            UserDbEntity userDb = em.find(UserDbEntity.class, usr.guid);
            userDb.addSandbox(sandbox);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
        } else {
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

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
            em.close();
            return Response.ok().build();
        } else {
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

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

    @POST
    public Response addSandbox(@CookieParam(value = "security-Kloudgis.org") String strAuthToken, Sandbox sbx) {
        if (sbx == null || sbx.connection_url == null || sbx.name == null || sbx.url_geoserver == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        System.out.println("Add SANDBOX!");
        HibernateEntityManager hem = PersistenceManager.getInstance().getAdminEntityManager();
        EntityManager emgSandbox = null;
        UserDbEntity usr = new AuthorizationManager().getUserFromAuthToken(strAuthToken, hem);
        if (usr == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        System.out.println("User OK:" + usr.getEmail());
        int iSlashIndex = sbx.connection_url.lastIndexOf("/");
        String strURL = null;
        String strName = null;
        if (iSlashIndex > 0) {
            strURL = sbx.connection_url.substring(0, iSlashIndex);
            strName = sbx.connection_url.substring(iSlashIndex + 1);
        }
        Response rsp = null;
        System.out.println("Will create the database at:" + strURL + " with name " + strName);
        try {
            rsp = DatabaseFactory.createDB(strURL, strName);
            if (rsp.getStatus() == Response.Status.OK.getStatusCode()) {
                System.out.println("Create database OK");
                hem.getTransaction().begin();
                SandboxDbEntity sen = new SandboxDbEntity();
                long lSandboxID = addSandbox(sbx, sen, hem);
                hem.getTransaction().commit();
                System.out.println("Sandbox db entity created with ID=" + lSandboxID);
                emgSandbox = PersistenceManager.getInstance().getEntityManagerBySandboxId(lSandboxID);
                if (emgSandbox == null) {
                    hem.close();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                            new Message("Error creating sandbox.", MessageCode.SEVERE)).build();
                }
                DatabaseFactory.createIndexes(emgSandbox);
                DatabaseFactory.loadDefaultValues(emgSandbox);
                emgSandbox.getTransaction().begin();
                //add everything the sandbox needs
                addMember(emgSandbox, usr);
                addLayer(emgSandbox, "poi", "EPSG:4326", usr.getId());
                addLayer(emgSandbox, "path", "EPSG:4326", usr.getId());
                addLayer(emgSandbox, "zone", "EPSG:4326", usr.getId());
                emgSandbox.getTransaction().commit();
                emgSandbox.close();
                //add default sources
                hem.getTransaction().begin();
                System.out.println("About to load default POI");
                long lPoiSourceID = addSource(usr, hem, "poi");
                if (lPoiSourceID >= 0) {
                    System.out.println("About to load default PATH");
                    long lPathSourceID = addSource(usr, hem, "path");
                    if (lPathSourceID >= 0) {
                        System.out.println("About to load default ZONE");
                        long lZoneSourceID = addSource(usr, hem, "zone");
                        if (lZoneSourceID >= 0) {
                            hem.getTransaction().commit();
                            //load the data in the sandbox
                            loadSource(usr, lSandboxID, lPoiSourceID);
                            System.out.println("Poi data loaded");
                            loadSource(usr, lSandboxID, lPathSourceID);
                            System.out.println("Path data loaded");
                            loadSource(usr, lSandboxID, lZoneSourceID);
                            System.out.println("Zone data loaded");
                            //SET the geoserver
                            String strGeoserverURL = sen.getGeoserverUrl();
                            System.out.println("About to add geoserver WORKSPACE with name:" + strName);
                            MapServerFactory.addWorkspace(strGeoserverURL, strName, MapServerFactory.credentials);
                            int iColonIndex = strURL.lastIndexOf(":");
                            String strHost = null;
                            String strPort = null;
                            if (iColonIndex > 0) {
                                strHost = strURL.substring(0, iColonIndex);
                                strPort = strURL.substring(iColonIndex + 1);
                            }
                            System.out.println("About to add geoserver Store");
                            MapServerFactory.addStore(strGeoserverURL, strHost, strPort, strName, MapServerFactory.credentials);
                            System.out.println("About to add geoserver LAYERS");
                            MapServerFactory.addLayer(hem, strGeoserverURL, strName, strName, "poi", lPoiSourceID, MapServerFactory.credentials);
                            MapServerFactory.addLayer(hem, strGeoserverURL, strName, strName, "path", lPathSourceID, MapServerFactory.credentials);
                            MapServerFactory.addLayer(hem, strGeoserverURL, strName, strName, "zone", lZoneSourceID, MapServerFactory.credentials);
                            System.out.println("About to add geoserver Styles");
                            assignStyles(strGeoserverURL, strName, MapServerFactory.credentials);
                            hem.close();
                        } else {
                            hem.getTransaction().commit();
                            hem.close();
                            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                                    new Message("Error when inserting zone data.", MessageCode.SEVERE)).build();
                        }
                    } else {
                        hem.getTransaction().commit();
                        hem.close();
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                                new Message("Error when inserting path data.", MessageCode.SEVERE)).build();
                    }
                } else {
                    hem.getTransaction().commit();
                    hem.close();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                            new Message("Error when inserting poi data.", MessageCode.SEVERE)).build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (hem != null && hem.isOpen()) {
                if (hem.getTransaction().isActive()) {
                    hem.getTransaction().rollback();
                }
                hem.close();
            }
            if (emgSandbox != null && emgSandbox.isOpen()) {
                if (emgSandbox.getTransaction().isActive()) {
                    emgSandbox.getTransaction().rollback();
                }
                emgSandbox.close();
            }
        }
        System.out.println("Add sandbox completed.  Return " + rsp);
        return rsp;
    }

    private long addSandbox(Sandbox sbx, SandboxDbEntity sen, HibernateEntityManager hem) {
        sen.setName(sbx.name);
        sen.setURL(sbx.connection_url);
        sen.setUniqueKey(sbx.name);
        sen.setGeoserverURL(sbx.url_geoserver);
//        BaseLayerModeDbEntity blm = ...
//        blm.setID( sbx.baseLayerMode );
//        sen.setBaseLayerMode( blm );
        hem.persist(sen);
        return sen.getId();
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

    private long addSource(UserDbEntity usr, HibernateEntityManager hem,  String strType) throws ZipException, IOException, ParseException {
        Query qry = hem.createQuery("from DatasourceDbEntity where strFileName='" + strType + ".shp'");
        List<Object> lstRS = qry.getResultList();
        int iSize = lstRS.size();
        long lID = -1;
        if (iSize > 0) {
            lID = ((DatasourceDbEntity) lstRS.get(0)).getID();
        } else {
            lID = DatasourceFactory.addDatasource(hem, usr, MapServerFactory.getWebInfPath() + "classes/" + strType + ".shp").get(0);
        }       
        return lID;
    }
    
    private boolean loadSource(UserDbEntity usr, long lSandboxID, long sourceid){
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