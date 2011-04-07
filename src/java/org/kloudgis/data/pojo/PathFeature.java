/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.pojo;

import org.kloudgis.data.store.PathDbEntity;

/**
 *
 * @author sylvain
 */
public class PathFeature extends AbstractPlaceFeature{

    @Override
    public PathDbEntity toDbEntity() {
        PathDbEntity pathDb= new PathDbEntity();
        pathDb.fromPojo(this);
        return pathDb;
    }

}
