/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.pojo;

import org.kloudgis.data.store.PoiDbEntity;

/**
 *
 * @author sylvain
 */
public class PoiFeature extends AbstractPlaceFeature{

    @Override
    public PoiDbEntity toDbEntity() {
        PoiDbEntity poiDb= new PoiDbEntity();
        poiDb.fromPojo(this);
        return poiDb;
    }

}
