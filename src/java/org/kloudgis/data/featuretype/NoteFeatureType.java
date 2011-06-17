/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.featuretype;

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
public class NoteFeatureType extends AbstractFeatureType{
    
    public NoteFeatureType(FeatureTypeDbEntity ft){
        super(ft);
    }
    
    @Override
    public List<QuickFeature> findQuickFeaturesAt(Point point, LayerDbEntity lay, Double onePixelWorld, Integer limit, EntityManager em) {
        return new ArrayList();
    }
}
