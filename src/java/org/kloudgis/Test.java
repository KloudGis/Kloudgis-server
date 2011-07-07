/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis;

import org.gdal.ogr.Driver;
import org.gdal.ogr.ogr;

/**
 *
 * @author jeanfelixg
 */
public class Test {
    
    public static void main(String [] a){
        ogr.RegisterAll();
         int count = ogr.GetDriverCount();
        System.out.println(count + " available Drivers");
        for (int i = 0; i < count; i++) {
            try {
                Driver driver = ogr.GetDriver(i);
                System.out.println( i + " " + driver.getName());
            } catch (Exception e) {
                System.err.println("Error loading driver " + i);
            }
        }
    }
}
