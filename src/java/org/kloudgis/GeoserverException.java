/*
 * @author corneliu
 */
package org.kloudgis;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class GeoserverException extends WebApplicationException {

    private int iResponseCode;
    private String strResponse;

    public GeoserverException( int iResponseCode, String strResponse ) {
        super(Response.Status.fromStatusCode(iResponseCode));
        this.iResponseCode = iResponseCode;
        this.strResponse = strResponse;
    }

    @Override
    public String toString() {
        return "http code: " + iResponseCode + "==>" + strResponse;
    }
}