/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.store;

import javax.persistence.MappedSuperclass;
import org.kloudgis.data.pojo.AbstractFeature;

/**
 *
 * @author sylvain
 */
@MappedSuperclass
public abstract class AbstractFeatureDbEntity {

    //Abstract methods

    public abstract Long getId();

    public abstract void setId(Long id);

    public abstract AbstractFeature toPojo();

    public abstract void fromPojo(AbstractFeature pojo);

}
