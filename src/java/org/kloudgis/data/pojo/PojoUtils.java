/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeanfelixg
 */
public class PojoUtils {
    
    public static List<org.kloudgis.data.pojo.Coordinate> toPojo(com.vividsolutions.jts.geom.Coordinate[] coordinates) {
        ArrayList<org.kloudgis.data.pojo.Coordinate> list = new ArrayList(coordinates.length);
        for(com.vividsolutions.jts.geom.Coordinate c : coordinates){
            list.add(new Coordinate(c.x, c.y));
        }
        return list;
    }
}
