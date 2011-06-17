/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.pojo;

import java.util.List;

/**
 *
 * @author sylvain
 */
public abstract class AbstractPlaceFeature extends AbstractFeature{

    public String name;
    public String featureClass;
    public String type;
    public List<Long> tags;

    public Coordinate center;
    public String geo_type;
    public List<Coordinate> coordinates;

}
