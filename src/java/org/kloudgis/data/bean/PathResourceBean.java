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
    public PathFeature insertFeature(PathFeature path, @QueryParam("sandbox") Long sandboxId) throws WebApplicationException {
        return (PathFeature) doAddFeature(path, sandboxId);
    }

    @PUT
    @Path("{fId}")
    @Consumes({"application/json"})
    public PathFeature updateFeature(PathFeature path, @PathParam("fId") Long fid, @QueryParam("sandbox") Long sandboxId) throws WebApplicationException {
        return (PathFeature) doUpdateFeature(path, fid, sandboxId);
    }
}
