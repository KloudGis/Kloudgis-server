/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.admin.pojo.Feed;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.pojo.User;
import org.kloudgis.admin.store.FeedDbEntity;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.data.pojo.FetchResult;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/admin/sandboxes")
@Produces({"application/json"})
public class SandboxResourceBean {

    @GET  
    public Response getSandboxes(@CookieParam(value="security-Kloudgis.org") String auth_token) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity user = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        if (user != null) {        
            Set<SandboxDbEntity> lstR =  user.getSandboxes();
            List<Sandbox> lstPojo = new ArrayList();
            for (SandboxDbEntity sand : lstR) {
                lstPojo.add(sand.toPojo(em));
            }
            em.close();
            return Response.ok(lstPojo).build();
        }else{
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    @POST  
    @Path("{sandboxId}/users")
    public Response addUser(@CookieParam(value="security-Kloudgis.org") String auth_token, @PathParam("sandboxId") Long sandboxId, User usr){
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        AuthorizationManager authMan = new AuthorizationManager();
        UserDbEntity userSecure = authMan.getUserFromAuthToken(auth_token, em);
        if (userSecure != null ) {
            em.getTransaction().begin();
            SandboxDbEntity sandbox = em.find(SandboxDbEntity.class, sandboxId);          
            UserDbEntity userDb = em.find(UserDbEntity.class, usr.guid);
            userDb.addSandbox(sandbox);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
        }else{
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    @POST  
    @Path("{sandboxId}/feeds")
    public Response addFeed(@CookieParam(value="security-Kloudgis.org") String auth_token, @PathParam("sandboxId") Long sandboxId, Feed feed){
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        AuthorizationManager authMan = new AuthorizationManager();
        //UserDbEntity userSecure = authMan.getUserFromAuthToken(auth_token, em);
       // if (userSecure != null ) {
            em.getTransaction().begin();
            SandboxDbEntity sandbox = em.find(SandboxDbEntity.class, sandboxId);          
            FeedDbEntity feedDb = new FeedDbEntity();
            feedDb.fromPojo(feed);           
            sandbox.addFeed(feedDb);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
       // }else{
       //     em.close();
       //     return Response.status(Response.Status.UNAUTHORIZED).build();
      //  }
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
                theCount = ((Number)crit.setProjection(Projections.rowCount()).uniqueResult()).longValue();             
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
