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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.data.pojo.Member;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/members")
@Produces({"application/json"})
public class MemberResourceBean {

    @GET
    @Path("membership")
    public Response getMembership(@CookieParam(value = "security-Kloudgis.org") String auth_token, @QueryParam("sandbox") Long sandboxId) {
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
    
    @POST  
    public Response addMember(@CookieParam(value="security-Kloudgis.org") String auth_token, @QueryParam("sandbox") Long sandboxId, Member usr){
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        AuthorizationManager authMan = new AuthorizationManager();
        UserDbEntity userSecure = authMan.getUserFromAuthToken(auth_token, em);
        if (userSecure != null ) {
            EntityManager emSand = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
            em.getTransaction().begin();
            SandboxDbEntity sandbox = em.find(SandboxDbEntity.class, sandboxId);          
            UserDbEntity userDb = em.find(UserDbEntity.class, usr.user);
            //add a link in the admin PU
            sandbox.bindUser(userDb);
            em.getTransaction().commit();
            em.close();
            //add a member in the sandbox PU
            emSand.getTransaction().begin();
            MemberDbEntity db = new MemberDbEntity();
            db.fromPojo(usr);
            em.persist(db);
            emSand.getTransaction().commit();
            emSand.close();           
            return Response.ok().build();
        }else{
            em.close();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
