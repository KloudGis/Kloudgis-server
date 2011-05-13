/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.data.pojo.Member;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/members/sandbox")
@Produces({"application/json"})
public class MemberResourceBean {

    @GET
    @Path("{sandboxId}/membership")
    public Response getMembership(@CookieParam(value = "security-Kloudgis.org") String auth_token, @PathParam("sandboxId") Long sandboxId) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity user = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        em.close();
        if (user != null) {
            EntityManager emSand = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
            Query query = emSand.createQuery("from MemberDbEntity where user_id=:u").setParameter("u", user.getId());
            List<MemberDbEntity> lstM = query.getResultList();
            Member pojo = null;
            if (lstM.size() > 0) {
                pojo = lstM.get(0).toPojo(emSand);
            }
            emSand.close();
            return Response.ok(pojo).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
