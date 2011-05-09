/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.admin.pojo.Feed;
import org.kloudgis.admin.store.FeedDbEntity;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.data.store.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/feed")
public class FeedResourceBean {

    @GET
    @Produces({"application/json"})
    public Response getFeeds(@CookieParam(value="security-Kloudgis.org") String auth_token) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(PersistenceManager.ADMIN_PU);
        UserDbEntity user = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        if (user != null) {        
            Criteria cr = em.getSession().createCriteria(FeedDbEntity.class);
            //TODO: filter by user
            List<FeedDbEntity> lstR = cr.list();
            List<Feed> lstPojo = new ArrayList();
            for (FeedDbEntity feed : lstR) {
                lstPojo.add(feed.toPojo(em));
            }
            em.close();
            return Response.ok(lstPojo).build();
        }else{
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
}