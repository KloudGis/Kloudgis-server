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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.admin.pojo.Credential;
import org.kloudgis.admin.pojo.LoginResponse;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.pojo.User;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.admin.store.UserRoleDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/public")
public class LoginResourceBean {

    @POST
    @Path("login")
    @Produces({"application/json"})
    public Response login(@Context HttpServletRequest req, Credential crd) {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity u = authenticate(em, crd.user, crd.pwd);
        if (u != null) {
            //unique token for this users
            String token = Calendar.getInstance().getTimeInMillis() + u.getSalt() + u.getEmail();
            String hashed_token = hashString(token, "SHA-512");
            em.getTransaction().begin();
            u.setAuthToken(hashed_token);
            em.getTransaction().commit();
            em.close();
            //create a session
            HttpSession session = req.getSession(true);
            session.setAttribute("timeout", Calendar.getInstance().getTimeInMillis());
            return Response.ok(new LoginResponse(hashed_token)).build();
        }
        em.close();
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @Path("logout")
    @GET
    @Produces({"application/json"})
    public Response logout(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.ok().build();
    }

    @Path("register/test_email/{val}")
    @GET
    @Produces({"application/json"})
    public Response testEmail(@PathParam("val") String email) {
        if (email == null || email.length() == 0) {
            return Response.ok("Not Accepted - Empty").build();
        } else if (email.equals("admin@kloudgis.org")) {
            return Response.ok("Not Accepted - Reserved").build();
        } else if (!isUnique(email)) {
            return Response.ok("Not Accepted - In use").build();
        }
        return Response.ok("Accepted").build();
    }

    @Path("ping")
    @GET
    @Produces({"application/json"})
    public Response pingServer() {
        return Response.ok("Ping").build();
    }

    @Path("logged_user")
    @POST
    @Produces({"application/json"})
    public User loggedUser(@Context HttpServletRequest req, Credential crd) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
            UserDbEntity u = authenticate(em, crd.user, crd.pwd);
            if (u != null) {
                User pojo = u.toPojo(em);
                em.close();
                return pojo;
            }
        }
        return null;
    }

    //register
    @POST
    @Path("register")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response register(SignupUser user_try, @QueryParam("locale") String locale) {
        if (user_try == null || user_try.user == null || !user_try.user.contains("@")) {
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
            user.setEmail(user_try.user);
            user.setFullName(user_try.name);
            user.setCompagny(user_try.compagny);
            user.setLocation(user_try.location);
            user.setSalt(new String(new char[]{randChar(), randChar(), randChar(), randChar(), randChar(), randChar(), randChar(), randChar()}));
            user.setPassword(encryptPassword(user_try.pwd, user.getSalt()));
            user.setActive(false);
            if (!isUnique(user_try.user)) {
                Message message = new Message();
                message.message = "rejected";
                if (locale != null && locale.equals("fr")) {
                    message.message_loc = "Refuser - Déjà pris";
                } else {
                    message.message_loc = "Refused - In use";
                }
                return Response.ok(message).build();
            } else {
                EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
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

    private UserDbEntity authenticate(EntityManager em, String user, String password_hash) {
        try {
            if (user != null && password_hash != null) {
                UserDbEntity u = em.createQuery("from UserDbEntity where email=:u", UserDbEntity.class).setParameter("u", user).getSingleResult();
                if (u != null) {
                    String expectedPass = encryptPassword(password_hash, u.getSalt());
                    if (expectedPass != null && expectedPass.equals(u.getPasswordHash())) {
                        return u;
                    }
                }
            } else if (password_hash != null) {
                return new AuthorizationManager().getUserFromAuthToken(password_hash, em);
            }
        } catch (NoResultException e) {
        }
        return null;
    }

    private boolean isUnique(String email) {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        Query query = em.createQuery("from UserDbEntity where email=:em", UserDbEntity.class);
        query.setParameter("em", email);
        List<UserDbEntity> lstU = query.getResultList();
        em.close();
        return lstU.isEmpty();
    }

    private String encryptPassword(String hashed_password, String salt) {
        String string_to_hash = hashed_password + "@Kloudgis.org#" + salt;
        return hashString(string_to_hash, "SHA-256");
    }

    private String hashString(String message, String algo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            md.update(message.getBytes());
            byte[] byteData = md.digest();
            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.getStackTrace();
        }
        return null;
    }

    private static char randChar() {
        int rnd = (int) (Math.random() * 52); // or use Random or whatever
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);

    }
}
