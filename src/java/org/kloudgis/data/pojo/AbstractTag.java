/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.pojo;

import org.kloudgis.data.store.AbstractPlaceDbEntity;
import org.kloudgis.data.store.AbstractTagDbEntity;

/**
 *
 * @author sylvain
 */
public abstract class AbstractTag {
    
    public Long guid;
    public String key;
    public String value;
    public Long featureGuid;

    public abstract AbstractTagDbEntity toDbEntity();


}
