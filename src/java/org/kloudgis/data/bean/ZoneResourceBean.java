/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.bean;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import org.kloudgis.data.pojo.ZoneFeature;
import org.kloudgis.data.store.ZoneDbEntity;
/**
 *
 * @author sylvain
 */
@Path("/protected/zone")
@Produces({"application/json"})
public class ZoneResourceBean extends AbstractFeatureResourceBean{
    


    @Override
    public Class getEntityDbClass() {
        return ZoneDbEntity.class;
    }

    @POST
    @Produces({"application/json"})
    public ZoneFeature insertFeature(ZoneFeature poi, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        return (ZoneFeature) doAddFeature(poi, req, sContext);
    }

    @PUT
    @Path("{fId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public ZoneFeature updateFeature(ZoneFeature zone, @PathParam("fId") Long fid, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        return (ZoneFeature) doUpdateFeature(zone, fid, req, sContext);
    }
}
