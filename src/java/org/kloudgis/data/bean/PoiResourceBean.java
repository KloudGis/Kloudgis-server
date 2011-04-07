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
import org.kloudgis.data.pojo.PoiFeature;
import org.kloudgis.data.store.PoiDbEntity;

/**
 *
 * @author sylvain
 */
@Path("/protected/poi")
@Produces({"application/json"})
public class PoiResourceBean extends AbstractFeatureResourceBean{

    

    @Override
    public Class getEntityDbClass() {
        return PoiDbEntity.class;
    }

    @POST
    @Produces({"application/json"})
    public PoiFeature insertFeature(PoiFeature poi, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        return (PoiFeature) doAddFeature(poi, req, sContext);
    }

    @PUT
    @Path("{fId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public PoiFeature updateFeature(PoiFeature poi, @PathParam("fId") Long fid, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        return (PoiFeature) doUpdateFeature(poi, fid, req, sContext);
    }
}
