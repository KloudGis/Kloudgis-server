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
import org.kloudgis.data.pojo.PathFeature;
import org.kloudgis.data.store.PathDbEntity;

/**
 *
 * @author sylvain
 */
@Path("/protected/feature/path")
@Produces({"application/json"})
public class PathResourceBean extends AbstractFeatureResourceBean{


    @Override
    public Class getEntityDbClass() {
        return PathDbEntity.class;
    }

    @POST
    @Produces({"application/json"})
    public PathFeature insertFeature(PathFeature path, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        return (PathFeature) doAddFeature(path, req, sContext);
    }

    @PUT
    @Path("{fId}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public PathFeature updateFeature(PathFeature path, @PathParam("fId") Long fid, @Context HttpServletRequest req, @Context ServletContext sContext) throws WebApplicationException {
        return (PathFeature) doUpdateFeature(path, fid, req, sContext);
    }
}
