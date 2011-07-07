/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.pojo;

import org.kloudgis.data.store.ZoneTagDbEntity;

/**
 *
 * @author sylvain
 */
public class ZoneTag extends AbstractTag{

    @Override
    public ZoneTagDbEntity toDbEntity() {
        ZoneTagDbEntity entity = new ZoneTagDbEntity();
        try{
            Long iguid=Long.valueOf(guid);
            entity.setId(iguid);

        }catch(java.lang.NumberFormatException e){
            throw new NumberFormatException(guid + ": Guid is not a number ");
        }

//        try{
//            Long ifk=Long.valueOf(featureGuid);
//            entity.setFK(ifk);
//
//        }catch(java.lang.NumberFormatException e){
//            throw new NumberFormatException(featureGuid + ": Foreign key is not a number ");
//        }

        entity.setKey(key);
        entity.setValue(value);
        return entity;
    }

}
