/*
 * @author corneliu
 */
package org.kloudgis.mapserver;

import java.io.IOException;

public class GeoserverException extends IOException {


    public GeoserverException( int iResponseCode, String strResponse ) {
        super(iResponseCode + ": " + strResponse);
    }
}