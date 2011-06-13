/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.persistence.PersistenceManager;
import org.kloudgis.data.pojo.FeatureType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author sylvain
 */
@Path("/protected/featuretypes")
@Produces({"application/json"})
public class FeatureTypeResourceBean {

    @GET
    @Produces({"application/json"})
    public List<FeatureType> getFeatureTypes(@QueryParam("sandbox") Long sandboxId) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
        List<FeatureTypeDbEntity> lstDb = em.createNamedQuery("FeatureType.findAll").getResultList();
        List<FeatureType> lstFT = new ArrayList(lstDb.size());
        for (FeatureTypeDbEntity fDb : lstDb) {
            lstFT.add(fDb.toPojo(em));
        }
        em.close();
        return lstFT;
    }

    @GET
    @Path("{fId}")
    @Produces({"application/json"})
    public FeatureType getFeatureType(@QueryParam("sandbox") Long sandboxId, @PathParam("fId") Integer fId) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
        FeatureTypeDbEntity fDb = em.find(FeatureTypeDbEntity.class, fId);
        if (fDb != null) {
            FeatureType f = fDb.toPojo(em);
            em.close();
            return f;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + fId);
    }

    @POST
    @Produces({"application/json"})
    public FeatureType addFeatureType(FeatureType ft, @QueryParam("sandbox") Long sandboxId) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
        ensureUniqueName(ft, em);
        em.getTransaction().begin();
        FeatureTypeDbEntity uDb = ft.toDbEntity();
        em.persist(uDb);
        em.getTransaction().commit();
        ft.guid = uDb.getId();
        em.close();
        return ft;
    }

    @PUT
    @Path("{fId}")
    @Consumes({"application/json"})
    public FeatureType updateFeature(FeatureType ft, @PathParam("fId") Long fId, @QueryParam("sandbox") Long sandboxId) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
        try {
            FeatureTypeDbEntity gDb = em.find(FeatureTypeDbEntity.class, fId);
            if (gDb != null) {
                //ensureUniqueName(ft);
                em.getTransaction().begin();
                gDb.updateFrom(ft);
                em.getTransaction().commit();
            }

        } catch (java.lang.NumberFormatException e) {
            throw new NumberFormatException(ft.guid + ": Guid is not a number " + e);
        } finally {
            em.close();
        }

        return ft;
    }

    @Path("{fId}")
    @DELETE
    public Response deleteFeature(@PathParam("fId") Long gId, @QueryParam("sandbox") Long sandboxId) throws WebApplicationException {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
        FeatureTypeDbEntity gDb = em.find(FeatureTypeDbEntity.class, gId);
        if (gDb != null) {
            em.getTransaction().begin();
            em.remove(gDb);
            em.getTransaction().commit();
            em.close();
            return Response.ok().build();
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + gId);
    }

    //FIXE ME : SA TO BE REFACTOR MORE GENERIC
    private void ensureUniqueName(FeatureType ft, EntityManager em) {
        int iCpt = 1;
        String uName = ft.name;
        boolean bUnique = true;
        do {
            bUnique = true;

            Query query = em.createQuery("SELECT g from FeatureTypeDbEntity g where g.name = :gname");
            query = query.setParameter("gname", uName);
            List lstR = query.getResultList();
            bUnique = lstR.isEmpty();
            if (!bUnique) {
                uName = ft.name + "_" + iCpt++;
            }
        } while (!bUnique);
        ft.name = uName;
    }
}
