/*
 * @author corneliu
 */
package org.kloudgis.admin.bean;

import org.kloudgis.admin.pojo.Datasource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.kloudgis.AuthorizationManager;
import org.kloudgis.DatasourceFactory;
import org.kloudgis.SandboxUtils;
import org.kloudgis.admin.store.DatasourceDbEntity;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.persistence.PersistenceManager;

@Path("/protected/sources")
@Produces({"application/json"})
public class DatasourceResourceBean {

    @POST
    @Path("/load/{source}")
    @Produces({"application/json"})
    public Response loadData(@CookieParam(value = "security-Kloudgis.org") String strAuthToken,
            @QueryParam("sandbox") Long lSandboxID, @PathParam("source") Long lSourceID,
            HashMap<String, String> mapAttrs) throws IOException {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity usr = new AuthorizationManager().getUserFromAuthToken(strAuthToken, em);
        em.close();
        if (SandboxUtils.isMember(usr, lSandboxID)) {
                return DatasourceFactory.loadData(usr, lSandboxID, lSourceID, mapAttrs);
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Produces({"application/json"})
    public List<Long> addDatasource(@CookieParam(value = "security-Kloudgis.org") String strAuthToken, String strPath) throws Exception {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity usr = new AuthorizationManager().getUserFromAuthToken(strAuthToken, em);      
        if (usr == null) {
            em.close();
            throw new WebApplicationException(Response.Status.UNAUTHORIZED.getStatusCode());
        }
        em.getTransaction().begin();
        List<Long> dsId = null;
        try {
            dsId = DatasourceFactory.addDatasource(em, usr, strPath);
        } catch (Exception e) {
            em.getTransaction().rollback();
            em.close();
            throw e;
        } 
        em.getTransaction().commit();
        em.close();
        return dsId;

    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Datasource getDatasource(@CookieParam(value = "security-Kloudgis.org") String strAuthToken, @PathParam("id") Long lID) {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        UserDbEntity usr = new AuthorizationManager().getUserFromAuthToken(strAuthToken, em);
        em.close();
        if (usr == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED.getStatusCode());
        }
        DatasourceDbEntity dts = DatasourceFactory.getDatasource(lID);
        if (dts != null) {
            if (dts.getOwnerID() != usr.getId()) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED.getStatusCode());
            }
            return dts.toPojo();
        } else {
            throw new EntityNotFoundException("Datasource with id " + lID + " is not found.");
        }
    }
}