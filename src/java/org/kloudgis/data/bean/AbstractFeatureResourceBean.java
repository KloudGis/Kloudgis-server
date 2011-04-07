/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.bean;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.data.pojo.AbstractPlaceFeature;
import org.kloudgis.data.store.AbstractPlaceDbEntity;
import org.kloudgis.data.store.PersistenceManager;
/**
 *
 * @author sylvain
 */
public abstract class AbstractFeatureResourceBean {

    @GET
    @Path("{fId}")
    @Produces({"application/json"})
    public AbstractPlaceFeature getFeature(@PathParam("fId") Long fId) {
        EntityManager em = getEntityManager();
        AbstractPlaceDbEntity fDb = getFeatureDb(em, fId);
        if (fDb != null) {
            AbstractPlaceFeature f = fDb.toPojo();
            em.close();
            return f;
        }
        em.close();
        throw new EntityNotFoundException("Not found:" + fId);
    }

    @DELETE
    @Path("{fId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response deleteFeature(@PathParam("fId") Long fid, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        AbstractPlaceDbEntity uDb = (AbstractPlaceDbEntity) em.find(getEntityDbClass(), fid);
        if (uDb != null) {
            em.getTransaction().begin();
            em.remove(uDb);
            em.getTransaction().commit();
        }else{
            em.close();
            throw new EntityNotFoundException(fid + " Feature not found");
        }
        em.close();
        return Response.ok().build();
    }

    public AbstractPlaceFeature doAddFeature(AbstractPlaceFeature feature, HttpServletRequest req, ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        AbstractPlaceDbEntity fDb = feature.toDbEntity();
        em.persist(fDb);
        em.getTransaction().commit();
        feature = fDb.toPojo();
        em.close();
        return feature;
    }

    public AbstractPlaceFeature doUpdateFeature(AbstractPlaceFeature feature, Long fid, HttpServletRequest req, ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        AbstractPlaceDbEntity uDb = (AbstractPlaceDbEntity) em.find(getEntityDbClass(), fid);
        if (uDb != null) {
            em.getTransaction().begin();
            uDb.fromPojo(feature);
            em.getTransaction().commit();
            feature = uDb.toPojo();
        }else{
            em.close();
            throw new EntityNotFoundException(fid + " Feature not found");
        }
        em.close();
        return feature;
    }


    protected AbstractPlaceDbEntity getFeatureDb(EntityManager em, Long fId) {
        AbstractPlaceDbEntity fDb = (AbstractPlaceDbEntity) em.find(getEntityDbClass(), fId);
        return fDb;
    }

    //TODO : replace by dynamic PU
    protected HibernateEntityManager getEntityManager() {
        return PersistenceManager.getInstance().getEntityManagerDefault();
    }


    //abstract Methods here
    public abstract Class getEntityDbClass();

}
