/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.featuretype;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.kloudgis.data.pojo.PojoUtils;
import org.kloudgis.data.pojo.QuickFeature;
import org.kloudgis.data.store.AbstractPlaceDbEntity;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.LayerDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class PlaceFeatureType extends AbstractFeatureType{
    
    public PlaceFeatureType(FeatureTypeDbEntity ft){
        super(ft);
    }
    
    @Override
    public List<QuickFeature> findQuickFeaturesAt(Point point, LayerDbEntity lay, Double onePixelWorld, Integer limit, EntityManager em) {
        List<QuickFeature> lstQ = new ArrayList<QuickFeature>();
        Class entity = featuretype.getFeatureClass();
        HibernateEntityManager hem = (HibernateEntityManager) em;
        if (entity != null) {
            int iPixels = lay.getPixelTolerance();
            Geometry inter = point;
            if(iPixels > 0){
                inter = point.buffer(iPixels * onePixelWorld);
                inter.setSRID(point.getSRID());
            }
            Criteria criteria = hem.getSession().createCriteria(entity);
            criteria.add(Restrictions.eq("layer_id", lay.getId()));
            criteria.add(SpatialRestrictions.intersects("geom", inter));
            criteria.setMaxResults(limit);
            List result = criteria.list();
            for(Object oR : result){
                lstQ.add(createQuickFeature((AbstractPlaceDbEntity)oR));
            }
        }
        return lstQ;
    }
    
    protected QuickFeature createQuickFeature(AbstractPlaceDbEntity fea){
        QuickFeature quick = new QuickFeature();
        quick.ft_id = featuretype.getId();
        quick.fid = fea.getId();
        quick.guid = quick.ft_id + "_" + quick.fid;
        quick.descr = fea.getName();
        quick.geo_type = fea.getGeom().getGeometryType();
        quick.coordinates = PojoUtils.toPojo(fea.getGeom().getCoordinates());
        return quick;
    }
}
