/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.bean;


import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.PersistenceManager;
import org.kloudgis.data.pojo.FeatureType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.ejb.HibernateEntityManager;
/**
 *
 * @author sylvain
 */
@Path("/protected/featuretypes")
@Produces({"application/json"})
public class FeatureTypeResourceBean {

    //TODO : replace by dynamic PU
    protected HibernateEntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerDefault();
    }

    @GET
    @Produces({"application/json"})
    public List<FeatureType> getFeatureTypes(@Context HttpServletRequest req) {
        EntityManager em = getEntityManager();
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
    public FeatureType getFeature(@PathParam("fId") Integer fId) {
        EntityManager em = getEntityManager();
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
    public FeatureType addFeatureType(FeatureType ft, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        ensureUniqueName(ft, em);
        em.getTransaction().begin();
        FeatureTypeDbEntity uDb = ft.toDbEntity();
        em.persist(uDb);
        em.getTransaction().commit();
        ft.guid = (uDb.getId()).toString();
        em.close();
        return ft;
    }

    @PUT
    @Path("{fId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public FeatureType updateFeature(FeatureType ft, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        try{
            Long iguid=Long.valueOf(ft.guid);
            FeatureTypeDbEntity gDb = em.find(FeatureTypeDbEntity.class, iguid);
        if (gDb != null) {
            //ensureUniqueName(ft);
            em.getTransaction().begin();
            gDb.updateFrom(ft);
            em.getTransaction().commit();
        }
        em.close();
        }catch(java.lang.NumberFormatException e){
            throw new NumberFormatException(ft.guid + ": Guid is not a number "+ e);
        }

        return ft;
    }

    @Path("{fId}")
    @DELETE
    public Response deleteFeature(@PathParam("fId") Integer gId, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();

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
