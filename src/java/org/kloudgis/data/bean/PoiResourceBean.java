/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import org.kloudgis.data.pojo.PoiFeature;
import org.kloudgis.data.store.PoiDbEntity;

/**
 *
 * @author sylvain
 */
@Path("/protected/feature/poi")
@Produces({"application/json"})
public class PoiResourceBean extends AbstractFeatureResourceBean{

    

    @Override
    public Class getEntityDbClass() {
        return PoiDbEntity.class;
    }

    @POST
    public PoiFeature insertFeature(PoiFeature poi, @QueryParam("sandbox") Long sandboxId) throws WebApplicationException {
        return (PoiFeature) doAddFeature(poi, sandboxId);
    }

    @PUT
    @Path("{fId}")
    @Consumes({"application/json"})
    public PoiFeature updateFeature(PoiFeature poi, @PathParam("fId") Long fid, @QueryParam("sandbox") Long sandboxId) throws WebApplicationException {
        return (PoiFeature) doUpdateFeature(poi, fid, sandboxId);
    }
}
