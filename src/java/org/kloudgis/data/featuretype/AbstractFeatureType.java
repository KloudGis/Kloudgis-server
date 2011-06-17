/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.featuretype;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.kloudgis.data.pojo.QuickFeature;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.LayerDbEntity;

/**
 *
 * @author jeanfelixg
 */
public abstract class AbstractFeatureType {
    
    protected FeatureTypeDbEntity featuretype;
    
    public AbstractFeatureType(FeatureTypeDbEntity ft){
        this.featuretype = ft;
    }
    
    public FeatureTypeDbEntity getEntity(){
        return featuretype;
    }
    
    @Override
    public String toString(){
        return "Featuretype: " + (getEntity() == null ? "" : getEntity().getName());
    }
    
    public abstract List<QuickFeature> findQuickFeaturesAt(Point point, LayerDbEntity lay, Double onePixelWorld, Integer limit, EntityManager em);
   
}
