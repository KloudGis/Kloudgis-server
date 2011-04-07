/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.pojo;

import org.kloudgis.data.store.ZoneDbEntity;

/**
 *
 * @author sylvain
 */
public class ZoneFeature extends AbstractPlaceFeature{

    @Override
    public ZoneDbEntity toDbEntity() {
        ZoneDbEntity zoneDb = new ZoneDbEntity();
        zoneDb.fromPojo(this);
        return zoneDb;
    }

}
