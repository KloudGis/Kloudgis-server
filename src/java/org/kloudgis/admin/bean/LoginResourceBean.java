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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.LoginFactory;
import org.kloudgis.MessageCode;
import org.kloudgis.SandboxUtils;
import org.kloudgis.admin.pojo.Credential;
import org.kloudgis.admin.pojo.LoginResponse;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.pojo.User;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/public")
public class LoginResourceBean {

    /**
     * try to login with the provided credentials
     * @param req  
     * @param crd   the credentials to test
     * @return  the auth token if successful
     */
    @POST
    @Path("login")
    @Produces({"application/json"})
    public Response login(@Context HttpServletRequest req, Credential crd) {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity u = authenticate(em, crd.user, crd.pwd);
        if (u != null) {
            //unique token for this users
            String token = Calendar.getInstance().getTimeInMillis() + u.getSalt() + u.getEmail();
            String hashed_token = LoginFactory.hashString(token, "SHA-512");
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

    /**
     * Logout the current user
     * @param req
     * @return 
     */
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

    /**
     * Test if an email if valid
     * @param email
     * @return "Accepted" if OK, message otherwise
     */
    @Path("register/test_email/{val}")
    @GET
    @Produces({"application/json"})
    public Response testEmail(@PathParam("val") String email) {
        if (email == null || email.length() == 0) {
            return Response.ok("Not Accepted - Empty").build();
        } else if (email.equals("admin@kloudgis.org")) {
            return Response.ok("Not Accepted - Reserved").build();
        } else if (!LoginFactory.isUnique(email)) {
            return Response.ok("Not Accepted - In use").build();
        }
        return Response.ok("Accepted").build();
    }

    /**
     * Ping GET to test if the server is UP
     * @return 
     */
    @Path("ping")
    @GET
    @Produces({"application/json"})
    public Response pingServer() {   
        return Response.ok(new Message("ping", MessageCode.INFO)).build();
    }
    
    @Path("notification_check")
    @GET
    @Produces({"application/json"})
    public String notificationSecurity(@CookieParam(value = "security-Kloudgis.org") String auth_token, @QueryParam("sandbox") Long sandboxId) {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity u = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        boolean bGranted = false;
        if (u != null) {
            //only a user member of the sandbox can listener and send messages in this sandbox topic
            bGranted = SandboxUtils.isMember(u, sandboxId);
        }
        em.close();
        //TODO: remove 911 flag
        if(bGranted || auth_token != null && auth_token.equals("911")){
            return "Notification-Access-Granted";
        }else{
            return "Notification-Refused";
        }
    }
    
    @Path("chat_listen_check")
    @GET
    @Produces({"application/json"})
    public String notificationChatSecurity(@CookieParam(value = "security-Kloudgis.org") String auth_token, @QueryParam("email") String email) {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity u = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        boolean bGranted = false;
        if (u != null) {
            //only the user with this email can listen the email topic
            bGranted = u.getEmail() != null && u.getEmail().equals(email);
        }
        em.close();
        //TODO: remove 911 flag
        if(bGranted || auth_token != null && auth_token.equals("911")){
            return "Notification-Access-Granted";
        }else{
            return "Notification-Refused";
        }
    }
    
    /**
     * Get the logged user properties
     * @return user logged in
     */
    @Path("logged_user")
    @GET
    @Produces({"application/json"})
    public User loggedUser(@CookieParam(value = "security-Kloudgis.org") String auth_token) {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity u = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        User pojo = null;
        if (u != null) {
            pojo = u.toPojo(em);
        }
        em.close();
        return pojo;
    }

    /**
     * Add a new user
     * @param user_try  the new user properties
     * @param locale    the language
     * @return  message: success or rejected
     */
    @POST
    @Path("register")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response register(SignupUser user_try, @QueryParam("locale") String locale) {
        return Response.ok(LoginFactory.register(user_try, locale, UserDbEntity.ROLE_USER)).build();
    }

    private UserDbEntity authenticate(EntityManager em, String user, String password_hash) {
        try {
            if (user != null && password_hash != null) {
                UserDbEntity u = em.createQuery("from UserDbEntity where email=:u", UserDbEntity.class).setParameter("u", user).getSingleResult();
                if (u != null) {
                    String expectedPass = LoginFactory.encryptPassword(password_hash, u.getSalt());
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
}
