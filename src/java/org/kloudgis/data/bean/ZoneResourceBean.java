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
import org.kloudgis.data.pojo.ZoneFeature;
import org.kloudgis.data.store.ZoneDbEntity;
/**
 *
 * @author sylvain
 */
@Path("/protected/feature/zone")
@Produces({"application/json"})
public class ZoneResourceBean extends AbstractFeatureResourceBean{
    


    @Override
    public Class getEntityDbClass() {
        return ZoneDbEntity.class;
    }

    @POST
    public ZoneFeature insertFeature(ZoneFeature poi, @QueryParam("sandbox") Long sandboxId) throws WebApplicationException {
        return (ZoneFeature) doAddFeature(poi, sandboxId);
    }

    @PUT
    @Path("{fId}")
    @Consumes({"application/json"})
    public ZoneFeature updateFeature(ZoneFeature zone, @PathParam("fId") Long fid, @QueryParam("sandbox") Long sandboxId) throws WebApplicationException {
        return (ZoneFeature) doUpdateFeature(zone, fid, sandboxId);
    }
}
