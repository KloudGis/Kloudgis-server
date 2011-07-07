/*
 * @author corneliu
 */
package org.kloudgis;

public class GeoserverException extends Exception {

    private int iResponseCode;
    private String strResponse;

    public GeoserverException( int iResponseCode, String strResponse ) {
        this.iResponseCode = iResponseCode;
        this.strResponse = strResponse;
    }

    public int getCode() {
        return iResponseCode;
    }

    public String getResponse() {
        return strResponse;
    }

    @Override
    public String toString() {
        return strResponse;
    }
}