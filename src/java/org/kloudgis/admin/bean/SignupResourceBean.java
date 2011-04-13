/*
 * EchoSphere - Spatial Platform for Application and Communication Extensions.
 *
 * http://www.echospheretechnologies.com
 *
 * Copyright (c) 2003 Echosphere Technologies, Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Echosphere Technologies, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Echosphere Technologies, Inc.
 */
package org.kloudgis.admin.bean;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.admin.store.UserRoleDbEntity;
import org.kloudgis.data.store.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/public/signup")
public class SignupResourceBean {

    @Path("test_email/{val}")
    @GET
    @Produces({"application/json"})
    public Response testEmail(@PathParam("val") String email) {
        if (email == null || email.length() == 0) {
            return Response.ok("Not Accepted - Empty").build();
        } else if (email.equals("admin@kloudgis.org")) {
            return Response.ok("Not Accepted - Reserved").build();
        } else if (!isUnique(email)){
            return Response.ok("Not Accepted - In use").build();
        }
        return Response.ok("Accepted").build();
    }

    @Path("create")
    @POST
    @Produces({"application/json"})
    public Response createAccount(SignupUser user_try, @QueryParam("locale") String locale) {
        if (user_try == null || user_try.email == null || !user_try.email.contains("@")) {
            Message message = new Message();
            message.message = "rejected";
            if (locale != null && locale.equals("fr")) {
                message.message_loc = "Refuser - Invalide";
            } else {
                message.message_loc = "Refused - Invalid";
            }
            return Response.ok(message).build();
        } else {
            UserDbEntity user = new UserDbEntity();
            user.setEmail(user_try.email);
            user.setFullName(user_try.name);
            user.setCompagny(user_try.compagny);
            user.setLocation(user_try.location);
            user.setPassword(user_try.password);
            user.setActive(false);
            if (!isUnique(user_try.email)) {
                Message message = new Message();
                message.message = "rejected";
                if (locale != null && locale.equals("fr")) {
                    message.message_loc = "Refuser - Déjà pris";
                } else {
                    message.message_loc = "Refused - In use";
                }
                return Response.ok(message).build();
            } else {
                EntityManager em = PersistenceManager.getInstance().getEntityManager(PersistenceManager.ADMIN_PU);
                em.getTransaction().begin();
                UserRoleDbEntity role = new UserRoleDbEntity();
                role.setRoleName(UserDbEntity.ROLE_USER);
                user.addRole(role);
                em.persist(role);               
                em.persist(user);
                em.getTransaction().commit();
                em.close();
                Message message = new Message();
                message.message = "sucess";
                if (locale != null && locale.equals("fr")) {
                    message.message_loc = "Succès";
                } else {
                    message.message_loc = "Success";
                }
                return Response.ok(message).build();
            }
        }
    }
    
    private boolean isUnique(String email) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager(PersistenceManager.ADMIN_PU);
        Query query = em.createQuery("from UserDbEntity where email=:em", UserDbEntity.class);
        query.setParameter("em", email);
        List<UserDbEntity> lstU = query.getResultList();
        em.close();
        return lstU.isEmpty();
    }
}
