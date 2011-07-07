/*
 * @author corneliu
 */
package org.kloudgis.admin.bean;

import org.kloudgis.admin.pojo.Datasource;
import com.vividsolutions.jts.io.ParseException;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.kloudgis.DatasourceFactory;

@Path( "/protected/sources" )
@Produces( { "application/json" } )
public class DatasourceResourceBean {

    @POST
    @Path( "/load/{source}" )
    @Produces( { "application/json" } )
    public Response loadData( @QueryParam( "sandbox" ) Long lSandBoxID, @PathParam( "source" ) Long lSourceID,
    HashMap<String, String> mapAttrs ) throws ZipException, IOException, ParseException {
        return DatasourceFactory.loadData( lSandBoxID, lSourceID, mapAttrs );
    }

    @POST
    @Produces( { "application/json" } )
    public Datasource addDatasource( String strPath ) throws WebApplicationException, IOException {
        return DatasourceFactory.addDatasource( strPath );
    }

    @GET
    @Path("{id}")
    @Produces ( { "application/json" } )
    public Datasource getDatasource( @PathParam("id") Long lID ) {
        return DatasourceFactory.getDatasource( lID );
    }
}