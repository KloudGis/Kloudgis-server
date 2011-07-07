/*
 * @author corneliu
 */
package org.kloudgis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.persistence.Query;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.admin.pojo.Datasource;
import org.kloudgis.admin.store.DatasourceDbEntity;
import org.kloudgis.persistence.DatabaseFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class MapServerFactory {

    public static void addWorkspace( String strGeoserverURL, String strName, Credentials crd )
            throws MalformedURLException, IOException, GeoserverException {
        PostMethod pst = new PostMethod( strGeoserverURL + "workspaces" );
        pst.setRequestEntity( new StringRequestEntity( "<workspace><name>" + strName + "</name></workspace>", "application/xml", "UTF-8" ) );
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials( AuthScope.ANY, crd );
        int iResponse = htc.executeMethod( pst );
        String strBody = pst.getResponseBodyAsString( 1500 );
        pst.releaseConnection();
        if( iResponse != Response.Status.CREATED.getStatusCode() ) {
            throw new GeoserverException( iResponse, strBody );
        }
    }

    public static void addStore( String strGeoserverURL, String strHost, String strPort, String strName, Credentials crd )
            throws MalformedURLException, IOException, GeoserverException {
        PostMethod pst = new PostMethod( strGeoserverURL + "workspaces/" + strName + "/datastores" );
        pst.setRequestEntity( new StringRequestEntity( "<dataStore><name>" + strName + "</name><enabled>true</enabled><connectionParameters><host>" +
                strHost + "</host><port>" + strPort + "</port><database>" + strName + "</database><user>" + DatabaseFactory.USER +
                "</user><passwd>" + DatabaseFactory.PASSWORD + "</passwd><dbtype>postgis</dbtype><namespace>" + strName +
                "</namespace></connectionParameters></dataStore>", "application/xml", "UTF-8" ) );
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials( AuthScope.ANY, crd );
        int iResponse = htc.executeMethod( pst );
        String strBody = pst.getResponseBodyAsString( 1500 );
        pst.releaseConnection();
        if( iResponse != Response.Status.CREATED.getStatusCode() ) {
            throw new GeoserverException( iResponse, strBody );
        }
    }

    public static List<String> listStyles( String strGeoserverURL, Credentials crd )
            throws MalformedURLException, IOException, ParserConfigurationException, SAXException, GeoserverException {
        List<String> lst = null;
        GetMethod gtm = new GetMethod( strGeoserverURL + "styles" );
        gtm.setRequestHeader( new Header( "Accept", "text/xml" ) );
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials( AuthScope.ANY, crd );
        int iResponse = htc.executeMethod( gtm );
        String strBody = gtm.getResponseBodyAsString();
        if( iResponse == Response.Status.OK.getStatusCode() ) {
            InputStream ins = gtm.getResponseBodyAsStream();
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder dcb = dbf.newDocumentBuilder();
            doc = dcb.parse( ins );
            Element eleRoot = doc.getDocumentElement();
            NodeList lstStyle = eleRoot.getElementsByTagName( "style" );
            lst = new ArrayList<String>();
            for( int i = 0; i < lstStyle.getLength(); i++ ) {
                Node nodStyle = lstStyle.item( i );
                if( nodStyle instanceof Element ) {
                    NodeList lstName = ( ( Element )nodStyle ).getElementsByTagName( "name" );
                    for( int j = 0; j < lstName.getLength(); j++ ) {
                        Node nodName = lstName.item( j );
                        if( nodName instanceof Element ) {
                            lst.add( nodName.getTextContent() );
                        }
                    }
                }
            }
            ins.close();
        }
        gtm.releaseConnection();
        if( iResponse != Response.Status.OK.getStatusCode() ) {
            throw new GeoserverException( iResponse, strBody );
        }
        return lst;
    }

    public static void uploadStyle( String strGeoserverURL, String strStylePath, Credentials crd )
            throws MalformedURLException, IOException, GeoserverException {
        if( strStylePath == null || !strStylePath.toLowerCase().endsWith( ".sld" ) ) {
            throw new GeoserverException( Response.Status.BAD_REQUEST.getStatusCode(), "The file is not a style file." );
        }
        System.out.println( "path = " + strStylePath );
        File fileSytle = new File( strStylePath );
        if( !fileSytle.exists() ) {
            throw new GeoserverException( Response.Status.NOT_FOUND.getStatusCode(), "File not found: " + strStylePath );
        }
        String strStyleName = fileSytle.getName().substring( 0, fileSytle.getName().lastIndexOf( ".sld" ) );
        PostMethod pst = new PostMethod( strGeoserverURL + "styles" );
        pst.setRequestEntity( new StringRequestEntity( "<style><name>" + strStyleName + "</name><filename>" + strStyleName + ".sld</filename></style>",
                "application/xml", "UTF-8" ) );
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials( AuthScope.ANY, crd );
        int iResponse = htc.executeMethod( pst );
        String strBody = pst.getResponseBodyAsString( 1500 );
        pst.releaseConnection();
        if( iResponse == Response.Status.CREATED.getStatusCode() ) {
            PutMethod ptm = new PutMethod( strGeoserverURL + "styles/" + strStyleName );
            ptm.setRequestEntity( new FileRequestEntity( fileSytle, "application/vnd.ogc.sld+xml" ) );
            iResponse = htc.executeMethod( ptm );
            strBody = ptm.getResponseBodyAsString( 1500 );
            ptm.releaseConnection();
            if( iResponse != Response.Status.OK.getStatusCode() ) {
                throw new GeoserverException( iResponse, strBody );
            }
        } else {
            throw new GeoserverException( iResponse, strBody );
        }
    }

    public static void assignStyle( String strGeoserverURL, String strWorkspaceName, String strLayerName, String strStyleName, Credentials crd )
            throws MalformedURLException, IOException, GeoserverException {
        PutMethod ptm = new PutMethod( strGeoserverURL + "layers/" + strWorkspaceName + ":" + strLayerName );
        ptm.setRequestEntity( new StringRequestEntity( "<layer><defaultStyle><name>" + strStyleName + "</name></defaultStyle><enabled>" +
                "true</enabled><styles><style><name>" + strStyleName + "</name></style></styles></layer>", "application/xml", "UTF-8" ) );
        HttpClient htc = new HttpClient();
        htc.getState().setCredentials( AuthScope.ANY, crd );
        int iResponse = htc.executeMethod( ptm );
        String strBody = ptm.getResponseBodyAsString( 1500 );
        ptm.releaseConnection();
        if( iResponse != Response.Status.OK.getStatusCode() ) {
            throw new GeoserverException( iResponse, strBody );
        }
    }

    public static String getFileContent( String strFile ) throws FileNotFoundException {
        File file = new File( strFile );
        if( file.exists() ) {
            StringBuilder stb = new StringBuilder();
            Scanner scn = new Scanner( new FileInputStream( file ) );
            while( scn.hasNextLine() ) {
                stb.append( scn.nextLine() );
            }
            scn.close();
            return stb.toString();
        }
        return null;
    }

    public static String getWebInfPath() {
        String className = MapServerFactory.class.getResource( "MapServerFactory.class" ).getFile();
        return className.substring( 0, className.indexOf( "WEB-INF" ) + 8 );
    }

    public static void addLayer( HibernateEntityManager hem, String strGeoserverURL, String strWSName, String strDSName,
            String strFTName, long lDatasourceID, Credentials crd ) throws MalformedURLException, IOException, GeoserverException {
        Query qry = hem.createQuery( "from DatasourceDbEntity where lid=" + lDatasourceID );
        List<Object> lstRS = qry.getResultList();
        int iSize = lstRS.size();
        if( iSize > 0 ) {
            DatasourceDbEntity dse = ( DatasourceDbEntity )lstRS.get( 0 );
            if( dse != null ) {
                Datasource dsp = dse.toPojo();
                PostMethod pst = new PostMethod( strGeoserverURL + "workspaces/" + strWSName + "/datastores/" + strDSName + "/featuretypes" );
                pst.setRequestEntity( new StringRequestEntity( "<featureType><name>" + strFTName + "</name><srs>EPSG:4326</srs><nativeBoundingBox><minx>" +
                        dsp.dEnvelopeMinX + "</minx><maxx>" + dsp.dEnvelopeMaxX + "</maxx><miny>" + dsp.dEnvelopeMinY + "</miny><maxy>" + dsp.dEnvelopeMaxY +
                        "</maxy></nativeBoundingBox><latLonBoundingBox><minx>" + dsp.dEnvelopeMinX + "</minx><maxx>" + dsp.dEnvelopeMaxX + "</maxx><miny>" +
                        dsp.dEnvelopeMinY + "</miny><maxy>" + dsp.dEnvelopeMaxY + "</maxy></latLonBoundingBox></featureType>", "application/xml", "UTF-8" ) );
                HttpClient htc = new HttpClient();
                htc.getState().setCredentials( AuthScope.ANY, crd );
                int iResponse = htc.executeMethod( pst );
                String strBody = pst.getResponseBodyAsString( 1500 );
                pst.releaseConnection();
                if( iResponse != Response.Status.CREATED.getStatusCode() ) {
                    throw new GeoserverException( iResponse, strBody );
                }
            } else {
                throw new GeoserverException( Response.Status.NOT_FOUND.getStatusCode(), "Datasource is null for id: " + lDatasourceID );
            }
        } else {
            throw new GeoserverException( Response.Status.NOT_FOUND.getStatusCode(), "Datasource not found for id: " + lDatasourceID );
        }
    }
}