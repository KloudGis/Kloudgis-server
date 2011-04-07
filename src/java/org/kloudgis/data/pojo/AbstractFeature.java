/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.pojo;

import javax.persistence.EntityManager;
import org.kloudgis.data.store.AbstractPlaceDbEntity;

/**
 *
 * @author sylvain
 */
public abstract class AbstractFeature {

    public Long guid;
    public String wktGeom;

    public abstract AbstractPlaceDbEntity toDbEntity();

}
