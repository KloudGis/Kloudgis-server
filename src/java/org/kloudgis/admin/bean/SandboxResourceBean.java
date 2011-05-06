/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.data.store.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/sandbox")
public class SandboxResourceBean {

    @GET
    @Produces({"application/json"})
    public Response getSandboxes(@CookieParam(value="security-Kloudgis.org") String auth_token) {
        HibernateEntityManager em = PersistenceManager.getInstance().getEntityManager(PersistenceManager.ADMIN_PU);
        UserDbEntity user = new AuthorizationManager().getUserFromAuthToken(auth_token, em);
        if (user != null) {        
            Criteria cr = em.getSession().createCriteria(SandboxDbEntity.class);
            //TODO: filter by user
            List<SandboxDbEntity> lstR = cr.list();
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
}
