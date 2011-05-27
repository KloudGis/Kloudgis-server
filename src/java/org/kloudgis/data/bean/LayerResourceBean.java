/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import com.sun.jersey.api.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.kloudgis.data.pojo.Layer;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/layers")
public class LayerResourceBean {

    @GET
    @Produces({"application/json"})
    public Response getLayers(@QueryParam("sandbox") Long sandboxId) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
        if (em != null) {
            List<LayerDbEntity> lstDb = em.createQuery("from LayerDbEntity", LayerDbEntity.class).getResultList();
            List<Layer> lstFT = new ArrayList(lstDb.size());
            for (LayerDbEntity fDb : lstDb) {
                Layer pojo = fDb.toPojo(em);
                lstFT.add(pojo);
            }
            em.close();
            return Response.ok(lstFT).build();
        }else{
            throw new NotFoundException("Sandbox entity manager not found for id:" + sandboxId + ".");
        }
    }
    
    @GET
    @Path("{layerId}")
    @Produces({"application/json"})
    public Response getLayer(@QueryParam("sandbox") Long sandboxId, @PathParam("layerId") Long layerId) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
        if (em != null) {
            LayerDbEntity layDb = em.find(LayerDbEntity.class, layerId);
            Layer pojo = null;
            if(layDb != null){
                pojo = layDb.toPojo(em);
            }else{
                em.close();
                throw new NotFoundException("Layer " + layerId + " is not found in sandbox " + sandboxId + ".");
            }
            em.close();
            return Response.ok(pojo).build();
        }else{
            throw new NotFoundException("Sandbox entity manager not found for id:" + sandboxId + ".");
        }
    }
}
