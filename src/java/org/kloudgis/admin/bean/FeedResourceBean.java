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
import javax.ws.rs.Path;
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
import org.kloudgis.admin.store.FeedDbEntity;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.data.pojo.FetchResult;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/feeds")
public class FeedResourceBean {

    @GET
    @Produces({"application/json"})
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
            }
            crit.setProjection(null);
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
        Criteria crit = em.getSession().createCriteria(FeedDbEntity.class);
        Criterion or = null;
        for (SandboxDbEntity sand : lstR) {
            if (or != null) {
                or = Restrictions.or(or, Restrictions.eq("sandbox_id", sand.getId()));
            } else {
                or = Restrictions.eq("sandbox_id", sand.getId());
            }
        }
        crit.add(or);      
        return crit;
    }
}