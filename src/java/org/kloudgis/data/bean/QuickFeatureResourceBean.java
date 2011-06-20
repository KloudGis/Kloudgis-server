/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import com.sun.jersey.api.NotFoundException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.kloudgis.GeometryFactory;
import org.kloudgis.data.featuretype.AbstractFeatureType;
import org.kloudgis.data.pojo.QuickFeature;
import org.kloudgis.data.store.LayerDbEntity;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/qfeatures")
@Produces({"application/json"})
public class QuickFeatureResourceBean {

    @GET
    @Path("lon_lat")
    public Response getFeaturesAt(@QueryParam("sandbox") Long sandboxId, @QueryParam("lon") Double lon, @QueryParam("lat") Double lat,
            @QueryParam("layers") String layers, @QueryParam("limit") Integer limit, @QueryParam("one_pixel") Double onePixelWorld) {
        EntityManager em = PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
        if (em != null) {
            Point point = GeometryFactory.createPoint(new Coordinate(lon, lat));
            point.setSRID(4326);
            ArrayList<QuickFeature> arrQ = new ArrayList();
            for (String id : layers.split(",")) {
                if (limit > 0) {
                    LayerDbEntity lay = em.find(LayerDbEntity.class, Long.valueOf(id));
                    AbstractFeatureType ft = lay.getFeatureType(em);
                    if (ft != null) {
                        List<QuickFeature> arrL = ft.findQuickFeaturesAt(point, lay, onePixelWorld, limit, em);
                        arrQ.addAll(arrL);
                        limit -= arrL.size();
                    }
                }
            }
            em.close();
            return Response.ok(arrQ).build();
        } else {
            throw new NotFoundException("Sandbox entity manager not found for id:" + sandboxId + ".");
        }
    }
}
