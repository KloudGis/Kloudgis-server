/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.bean;


import java.util.ArrayList;
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
import org.kloudgis.data.pojo.AbstractFeature;
import org.kloudgis.data.pojo.FetchResult;
import org.kloudgis.data.store.AbstractFeatureDbEntity;
import org.kloudgis.data.store.AbstractPlaceDbEntity;
import org.kloudgis.data.store.PersistenceManager;
/**
 *
 * @author sylvain
 */
public abstract class AbstractFeatureResourceBean {

    @GET
    @Produces({"application/json"})
    public FetchResult getFeatures(
            @DefaultValue("0") @QueryParam("start") Integer start,
            @DefaultValue("-1") @QueryParam("length") Integer length,
            @QueryParam("sort") String sort,
            @QueryParam("sortState") String sortState) {
        HibernateEntityManager em = getEntityManager();
        Criteria cr = em.getSession().createCriteria(getEntityDbClass()).setFirstResult(start);
        if (length >= 0) {
            cr.setMaxResults(length);
        }
        if (sort != null) {
            if (sortState != null && sortState.equals("DESC")) {
                cr.addOrder(Order.desc(sort));
            } else {
                cr.addOrder(Order.asc(sort));
            }

        }
        List<AbstractFeatureDbEntity> lstR = cr.list();
        List<AbstractFeature> lstEntity = toPojo(lstR);
        Long count = new Integer(lstR.size()).longValue();
        if (start.intValue() > 0 || length.intValue() != -1) {
            Query qCount = em.createQuery(
                    "SELECT COUNT(e) FROM " + getEntityDbClass().getSimpleName() + " e");
            count = (Long) qCount.getSingleResult();
        }
        FetchResult qResult = new FetchResult(lstEntity, count);
        em.close();
        System.out.println("AbstractFeatureResourceBean: qResult= "+qResult.toString());
        return qResult;
    }


    @GET
    @Path("count")
    @Produces({"application/json"})
    public Integer countSearch() {

        HibernateEntityManager em = getEntityManager();
        Integer count=new Integer(0);
        Query qCount = em.createQuery(
                    "SELECT COUNT(e) FROM " + getEntityDbClass().getSimpleName() + " e");
            count = (Integer) qCount.getSingleResult();
        em.close();
        return count;
    }

    protected List<AbstractFeature> toPojo(Collection<AbstractFeatureDbEntity> lstDbFea) {
        List<AbstractFeature> lstPojo = new ArrayList();
        if (lstDbFea != null && lstDbFea.size() > 0) {
            for (AbstractFeatureDbEntity fDb : lstDbFea) {
               lstPojo.add(fDb.toPojo());
            }
        }
        return lstPojo;
    }

    @GET
    @Path("{fId}")
    @Produces({"application/json"})
    public AbstractFeature getFeature(@PathParam("fId") Long fId) {
        EntityManager em = getEntityManager();
        AbstractPlaceDbEntity fDb = getFeatureDb(em, fId);
        if (fDb != null) {
            AbstractFeature f = fDb.toPojo();
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

    public AbstractFeature doAddFeature(AbstractFeature feature, HttpServletRequest req, ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        AbstractFeatureDbEntity fDb = feature.toDbEntity();
        em.persist(fDb);
        em.getTransaction().commit();
        feature = fDb.toPojo();
        em.close();
        return feature;
    }

    public AbstractFeature doUpdateFeature(AbstractFeature feature, Long fid, HttpServletRequest req, ServletContext sContext) throws WebApplicationException {
        EntityManager em = getEntityManager();
        AbstractFeatureDbEntity uDb = (AbstractFeatureDbEntity) em.find(getEntityDbClass(), fid);
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
