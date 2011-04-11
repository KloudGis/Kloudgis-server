/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.pojo;

import org.kloudgis.data.store.FeatureTypeDbEntity;
import javax.persistence.EntityManager;

/**
 * @author sylvain
 */
public class FeatureType {
    public String guid;
    public String name;
    public String description;
    public String label;
    //public List<Long> attrtypes;

    public FeatureTypeDbEntity toDbEntity() {
        FeatureTypeDbEntity entity = new FeatureTypeDbEntity();
        try{
            Long iguid=Long.valueOf(guid);
            entity.setId(iguid); 
        }catch(java.lang.NumberFormatException e){
            throw new NumberFormatException(guid + ": Guid is not a number ");
        }

        entity.setName(name);
        entity.setDescription(description);
        entity.setLabel(label); 

        return entity;
    }

}
