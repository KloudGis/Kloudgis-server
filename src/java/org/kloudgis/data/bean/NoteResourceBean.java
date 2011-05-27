/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.GeometryFactory;
import org.kloudgis.data.pojo.FetchResult;
import org.kloudgis.data.pojo.NoteMarker;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.data.store.utils.DistanceOrder;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/feature/note")
@Produces({"application/json"})
public class NoteResourceBean extends AbstractFeatureResourceBean{

    @GET
    @Path("clusters")
    public Response getNoteClusters(@CookieParam(value = "security-Kloudgis.org") String auth_token, @QueryParam("center_lat") Double lat, @QueryParam("center_lon") Double lon, @QueryParam("distance") Double distance, @QueryParam("sandbox") Long sandboxId,
            @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("-1") @QueryParam("length") Integer length, @DefaultValue("false") @QueryParam("count") Boolean count) {
        if (auth_token != null) {
            HibernateEntityManager em = (HibernateEntityManager) PersistenceManager.getInstance().getEntityManagerBySandboxId(sandboxId);
            if (em != null) {
                List<NoteMarker> clusters = new ArrayList();
                NoteMarker cluster;
                boolean clustered = false;
                Criteria criteria = em.getSession().createCriteria(NoteDbEntity.class);
                Long theCount = null;
                Point pt = GeometryFactory.createPoint(new Coordinate(lon, lat));
                pt.setSRID(4326);
                criteria.addOrder(new DistanceOrder("geom", pt));
                NoteDbEntity feature;
                List< NoteDbEntity> features = criteria.list();
                for (int i = 0; i < features.size(); i++) {
                    feature = features.get(i);
                    Point geo = feature.getGeometry();
                    if (geo != null) {
                        clustered = false;
                        for (int j = clusters.size() - 1; j >= 0; j--) {
                            cluster = clusters.get(j);
                            if (shouldCluster(cluster, feature, distance)) {
                                addToCluster(cluster, feature);
                                clustered = true;
                                break;
                            }
                        }
                        if (!clustered) {
                            clusters.add(createCluster(feature));
                        }
                    }
                }
                if(count){
                    theCount = (long) clusters.size();                    
                }  
                clusters = clusters.subList(start, Math.min(start + length, clusters.size()));
                em.close();
                return Response.ok(new FetchResult(clusters, theCount)).build();
            }
        }
        return null;

    }

    private boolean shouldCluster(NoteMarker cluster, NoteDbEntity feature, Double distance) {
        Coordinate coordCluster = new Coordinate(cluster.lon, cluster.lat);
        Coordinate coordNote = feature.getGeometry().getCoordinate();
        return coordCluster.distance(coordNote) <= distance;
    }

    private void addToCluster(NoteMarker cluster, NoteDbEntity feature) {
        cluster.notes.add(feature.getId());
    }

    private NoteMarker createCluster(NoteDbEntity feature) {
        NoteMarker cluster = new NoteMarker();
        cluster.lon = feature.getGeometry().getX();
        cluster.lat = feature.getGeometry().getY();
        cluster.notes.add(feature.getId());
        cluster.guid=feature.getId();
        return cluster;
    }

    @Override
    public Class getEntityDbClass() {
        return NoteDbEntity.class;
    }
    
}
