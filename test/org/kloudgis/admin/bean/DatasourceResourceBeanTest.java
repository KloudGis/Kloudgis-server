/*
 * @author corneliu
 */
package org.kloudgis.admin.bean;

import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.persistence.DatabaseFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.kloudgis.LoginFactory;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import com.vividsolutions.jts.io.ParseException;
import java.io.IOException;
import java.net.MalformedURLException;
import com.vividsolutions.jts.geom.Geometry;
import java.sql.ResultSet;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.kloudgis.GeometryFactory;
import org.kloudgis.mapserver.MapServerFactory;
import static org.junit.Assert.*;
import static org.kloudgis.admin.bean.TestConstant.*;

public class DatasourceResourceBeanTest {

    public void dropCreate() throws ClassNotFoundException, SQLException {
        System.out.println("addDatasource");
        try {
            //geoserver
            MapServerFactory.deleteWorkspace(strGeoserverURL, "test_sandbox", new UsernamePasswordCredentials(strGeoUser, strGeoPass));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //        get native postgres connection for testing
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://" + strDbURL + "/postgis", strDbUser, strPassword);
        try {
            PreparedStatement pst = conn.prepareStatement("drop database test_sandbox;");
            pst.execute();
            pst.close();
        } catch (Exception e) {
        }
        conn.close();
        
        Connection conn2 = null;
        try {
            conn2 = DriverManager.getConnection("jdbc:postgresql://" + strDbURL + "/test_admin", strDbUser, strPassword);
            PreparedStatement pst = conn2.prepareStatement("truncate datasources cascade;");
            pst.execute();
            pst.close();
            pst = conn2.prepareStatement("truncate ds_columns cascade;");
            pst.execute();
            pst.close();
            pst = conn2.prepareStatement("truncate sandboxes cascade;");
            pst.execute();
            pst.close();
            conn2.close();
        } catch (Exception ee) {
            if (conn2 != null) {
                conn2.close();
            }
        }
        try {
            DatabaseFactory.createDB(strDbURL, "test_admin");
        } catch (Exception e) {
        }
        
    }

    @Test
    public void testAddDatasources() throws MalformedURLException, IOException, SQLException, ClassNotFoundException {
        System.out.println("Test datasource!!");
        try {
            dropCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PostMethod postLogin = new PostMethod(strKloudURL + "/kg_server/public/login");
        String strPswd = LoginFactory.hashString("kwadmin", "SHA-256");
        postLogin.setRequestEntity(new StringRequestEntity("{\"user\":\"admin@kloudgis.com\",\"pwd\":\"" + strPswd + "\"}", "application/json", "UTF-8"));
        HttpClient htcLogin = new HttpClient();
        assertEquals(200, htcLogin.executeMethod(postLogin));
        String strBody = postLogin.getResponseBodyAsString(1000);
        String strAuth = strBody.substring(strBody.indexOf(":") + 2, strBody.lastIndexOf("\""));
        postLogin.releaseConnection();
        System.out.println("Login completed");
        
//        get the relative path to the place where the data files for this test are
        String strPath = MapServerFactory.getWebInfPath() + "../../../test_res";
//        insert shape file
        System.out.println("About to add cities.shp");
        URL url = new URL(strKloudURL + "/kg_server/protected/sources");
        HttpURLConnection httpCon = null;
        OutputStream ost = null;
        int iRet = 0;
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        String path = strPath + "/cities.shp";
        //no crs provided: auto-detect
        String json = "{\"path\":\"" + path  +"\"}";
        ost.write(json.getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        System.out.println("add cities.shp sucessful");
        
        
        System.out.println("About to add cities900913.shp");
        url = new URL(strKloudURL + "/kg_server/protected/sources");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        path = strPath + "/cities_900913.shp";
        //no crs provided: auto-detect
        json = "{\"path\":\"" + path  +"\"}";
        ost.write(json.getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        System.out.println("add cities_900913.shp sucessful");

        System.out.println("About to add 2325_integration.dgn");
//        insert dgn file
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        path = strPath + "/2325_integration.dgn";
        json = "{\"path\":\"" + path  +"\",\"crs\":\"4326\"}";
        ost.write(json.getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);

        System.out.println("add 2325_integration.dgn sucessful");

        System.out.println("About to add 2325_BORN_REN.dxf");
//        insert dxf file
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        path = strPath + "/2325_BORN_REN.dxf";
        json = "{\"path\":\"" + path  +"\",\"crs\":\"4326\"}";
        ost.write(json.getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        System.out.println("Add 2325_BORN_REN.dxf sucessful");

        System.out.println("About to add places.gml");
//        insert gml file
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        path = strPath + "/places.gml";
        json = "{\"path\":\"" + path  +"\",\"crs\":\"4326\"}";
        ost.write(json.getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        System.out.println("Add places.gml sucessful");

        System.out.println("About to add LOTOCCUPE.kml");
//        insert kml file
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        path = strPath + "/LOTOCCUPE.kml";
        json = "{\"path\":\"" + path  +"\",\"crs\":\"4326\"}";
        ost.write(json.getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        System.out.println("Add LOTOCCUPE.kml sucessful");

//        get a direct connection to the test database so we can check if things went right
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(strAdminDbURL, strDbUser, strPassword);

//        test the shape file insertion
        PreparedStatement pst = conn.prepareStatement("select lid, denvelopemaxx, denvelopemaxy, denvelopeminx, denvelopeminy,"
                + " icrs, icolumncount, ifeaturecount, strgeomtype, strlayerName, lownerid, strfilename from datasources;");
        ResultSet rst = pst.executeQuery();
        assertTrue(rst.next());
        long ds_id = rst.getLong(1);
        assertEquals(58.5526, rst.getDouble(2), 0);
        assertEquals(68.9713, rst.getDouble(3), 0);
        assertEquals(-21.8522, rst.getDouble(4), 0);
        assertEquals(28.1388, rst.getDouble(5), 0);
        // assertEquals(0,rst.getInt(6));
        assertEquals(11, rst.getInt(7));
        assertEquals(1267, rst.getInt(8));
        assertEquals("point", rst.getString(9));
        assertEquals("cities", rst.getString(10));
        assertEquals(1, rst.getInt(11));
        assertEquals("cities.shp", rst.getString(12));

        System.out.println("Check cities.shp OK");

        PreparedStatement pst1 = conn.prepareStatement("select strname, strtype from ds_columns where dts_lid=" + ds_id + ";");
        ResultSet rst1 = pst1.executeQuery();
        assertTrue(rst1.next());
        assertEquals("TYPE", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NATION", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("CNTRYNAME", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("LEVEL", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NAME", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NAMEPRE", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("CODE", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("PROVINCE", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("PROVNAME", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("UNPROV", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("CONURB", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertFalse(rst1.next());

        System.out.println("Check cities.shp columns OK");
        rst1.close();
        pst1.close();
        
        assertTrue(rst.next());
        ds_id = rst.getLong(1);
        assertEquals(6518045.616622, rst.getDouble(2), 0);
        assertEquals(10741877.292520, rst.getDouble(3), 0);
        assertEquals(-2432575.776713, rst.getDouble(4), 0);
        assertEquals(3266484.591261, rst.getDouble(5), 0);
        // assertEquals(0,rst.getInt(6));
        assertEquals(11, rst.getInt(7));
        assertEquals(1267, rst.getInt(8));
        assertEquals("point", rst.getString(9));
        assertEquals("cities_900913", rst.getString(10));
        assertEquals(1, rst.getInt(11));
        assertEquals("cities_900913.shp", rst.getString(12));

        System.out.println("Check cities900913.shp OK");

        pst1 = conn.prepareStatement("select strname, strtype from ds_columns where dts_lid=" + ds_id + ";");
        rst1 = pst1.executeQuery();
        assertTrue(rst1.next());
        assertEquals("TYPE", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NATION", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("CNTRYNAME", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("LEVEL", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NAME", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NAMEPRE", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("CODE", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("PROVINCE", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("PROVNAME", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("UNPROV", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("CONURB", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertFalse(rst1.next());

        rst1.close();
        pst1.close();


        System.out.println("Check cities_900913.shp columns OK");

//        test the dgn file insertion
        assertTrue(rst.next());
        ds_id = rst.getLong(1);
        assertEquals(229754.9006, rst.getDouble(2), 0.000001);
        assertEquals(5064226, rst.getDouble(3), 0.000001);
        assertEquals(208626.4227, rst.getDouble(4), 0.000001);
        assertEquals(5040476.0779, rst.getDouble(5), 0.000001);
        assertEquals(9, rst.getInt(7));
        assertEquals(8002, rst.getInt(8));
        assertEquals("Unknown", rst.getString(9));
        assertEquals("elements", rst.getString(10));
        assertEquals(1, rst.getInt(11));
        assertEquals("2325_integration.dgn", rst.getString(12));

        System.out.println("Check 2325_integration.dgn OK");

        PreparedStatement pst2 = conn.prepareStatement("select strname, strtype from ds_columns where dts_lid=" + ds_id + ";");
        rst1 = pst2.executeQuery();
        assertTrue(rst1.next());
        assertEquals("Type", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("Level", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("GraphicGroup", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("ColorIndex", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("Weight", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("Style", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("EntityNum", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("MSLink", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("Text", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertFalse(rst1.next());
        rst1.close();
        pst2.close();

        System.out.println("Check 2325_integration.dgn Columns OK");

//        test the dxf file insertion
        assertTrue(rst.next());
        ds_id = rst.getLong(1);
        assertEquals(230565.659999877, rst.getDouble(2), 0.000001);
        assertEquals(5065429.70789984, rst.getDouble(3), 0.000001);
        assertEquals(213470.867699484, rst.getDouble(4), 0.000001);
        assertEquals(5038624.22039983, rst.getDouble(5), 0.000001);
        assertEquals(6, rst.getInt(7));
        assertEquals(1712, rst.getInt(8));
        assertEquals("Unknown", rst.getString(9));
        assertEquals("entities", rst.getString(10));
        assertEquals(1, rst.getInt(11));
        assertEquals("2325_BORN_REN.dxf", rst.getString(12));

        System.out.println("Check 2325_BORN_REN.dxf OK");

        PreparedStatement pst3 = conn.prepareStatement("select strname, strtype from ds_columns where dts_lid=" + ds_id + ";");
        rst1 = pst3.executeQuery();
        assertTrue(rst1.next());
        assertEquals("Layer", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("SubClasses", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("ExtendedEntity", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("Linetype", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("EntityHandle", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("Text", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertFalse(rst1.next());
        rst1.close();
        pst3.close();

        System.out.println("Check 2325_BORN_REN.dxf Columns OK");

//        test the gml file insertion
        assertTrue(rst.next());
        ds_id = rst.getLong(1);
        assertEquals(-52.80807, rst.getDouble(2), 0);
        assertEquals(82.43198, rst.getDouble(3), 0);
        assertEquals(-140.87349, rst.getDouble(4), 0);
        assertEquals(42.05346, rst.getDouble(5), 0);
        assertEquals(16, rst.getInt(7));
        assertEquals(497, rst.getInt(8));
        assertEquals("point", rst.getString(9));
        assertEquals("placept", rst.getString(10));
        assertEquals(1, rst.getInt(11));
        assertEquals("places.gml", rst.getString(12));

        System.out.println("Check places.gml OK");

        PreparedStatement pst4 = conn.prepareStatement("select strname, strtype from ds_columns where dts_lid=" + ds_id + ";");
        rst1 = pst4.executeQuery();
        assertTrue(rst1.next());
        assertEquals("AREA", rst1.getString(1));
        assertEquals("Real", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("PERIMETER", rst1.getString(1));
        assertEquals("Real", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("PACEL_", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("PACEL_ID", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NAME", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("REG_CODE", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NTS50", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("LAT", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("LONG", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("POP91", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("SGC_CODE", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("CAPITAL", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("POP_RANGE", rst1.getString(1));
        assertEquals("Integer", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("UNIQUE_KEY", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NAME_E", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("NAME_F", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertFalse(rst1.next());
        rst1.close();
        pst4.close();

        System.out.println("Check places.gml Columns OK");

//        test the kml file insertion
        assertTrue(rst.next());
        ds_id = rst.getLong(1);
        assertEquals(-72.5046028211896, rst.getDouble(2), 0.000001);
        assertEquals(45.3592196152746, rst.getDouble(3), 0.000001);
        assertEquals(-72.5514406587745, rst.getDouble(4), 0.000001);
        assertEquals(45.3160540739093, rst.getDouble(5), 0.000001);
        assertEquals(2, rst.getInt(7));
        assertEquals(2903, rst.getInt(8));
        assertEquals("polygon", rst.getString(9));
        assertEquals("Layer #0", rst.getString(10));
        assertEquals(1, rst.getInt(11));
        assertEquals("LOTOCCUPE.kml", rst.getString(12));

        System.out.println("Check LOTOCCUPE.kml OK");

        PreparedStatement pst5 = conn.prepareStatement("select strname, strtype from ds_columns where dts_lid=" + ds_id + ";");
        rst1 = pst5.executeQuery();
        assertTrue(rst1.next());
        assertEquals("Name", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertTrue(rst1.next());
        assertEquals("Description", rst1.getString(1));
        assertEquals("String", rst1.getString(2));
        assertFalse(rst1.next());

        rst1.close();
        pst5.close();
        conn.close();

        System.out.println("Check LOTOCCUPE.kml Columns OK");

        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        path = strPath + "/phantom.shp";
        json = "{\"path\":\"" + path  +"\",\"crs\":\"4326\"}";
        ost.write(json.getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(500, iRet);

        System.out.println("Check phantom.shp OK");

        GetMethod gtm = new GetMethod(strKloudURL + "/kg_server/public/logout");
        HttpClient htcLogout = new HttpClient();
        assertEquals(200, htcLogout.executeMethod(gtm));
        gtm.releaseConnection();

        System.out.println("Logout OK");
    }

    @Test
    public void testLoadData() throws Exception {
        System.out.println("***** loadData");

        System.out.println("About to login");
//        login and get the authorization token
        PostMethod pstm = new PostMethod(strKloudURL + "/kg_server/public/login");
        String strPswd = LoginFactory.hashString("kwadmin", "SHA-256");
        pstm.setRequestEntity(new StringRequestEntity("{\"user\":\"admin@kloudgis.com\",\"pwd\":\"" + strPswd + "\"}", "application/json", "UTF-8"));
        HttpClient htcLogin = new HttpClient();
        assertEquals(200, htcLogin.executeMethod(pstm));
        String strAuth = pstm.getResponseBodyAsString();
        strAuth = strAuth.substring(strAuth.indexOf(":") + 2, strAuth.lastIndexOf("\""));
        pstm.releaseConnection();
        System.out.println("Login OK");

        System.out.println("About to create a sandbox 'test_sandbox'");
//        add sandbox db
        
        int iRet;
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(strKloudURL + "/kg_server/protected/admin/sandboxes");
        post.addRequestHeader("Cookie", "security-Kloudgis.org=" + strAuth);
        ObjectMapper mapper = new ObjectMapper();
        Sandbox outSandbox = new Sandbox();
        outSandbox.connection_url = strDbURL;
        outSandbox.name = "test_sandbox";
        outSandbox.url_geoserver = strGeoserverURL;
        String json = mapper.writeValueAsString(outSandbox);
        post.setRequestEntity(new StringRequestEntity(json, "application/json", "UTF-8"));
        iRet = httpClient.executeMethod(post);
        assertEquals(200, iRet);
        Sandbox sand = mapper.readValue(post.getResponseBodyAsStream(), Sandbox.class);
        long sId = sand.guid;
        System.out.println("Create sandbox 'test_sandbox' sucessful");


        Connection conn = DriverManager.getConnection(strSandboxDbURL, strDbUser, strPassword);
        
        URL url;
        HttpURLConnection httpCon;
        OutputStream ost = null;
        truncate(conn);
        System.out.println("About to load the SHP");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("cities.shp") + "?sandbox=" + sId);
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write(("{}").getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200,iRet);
        assertEquals(getCount(conn, "poi"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "TYPE"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "NATION"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "CNTRYNAME"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "LEVEL"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "NAME"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "NAMEPRE"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "CODE"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "PROVINCE"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "PROVNAME"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "UNPROV"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "CONURB"), 1267);
        double[] extent = getExtent(conn, "poi");
        assertEquals(-21.8522, extent[0], 0);
        assertEquals(28.1388, extent[1], 0);
        assertEquals(58.5526, extent[2], 0);
        assertEquals(68.9713, extent[3], 0);
        assertEquals(getCount(conn, "path"), 0);
        assertEquals(getCount(conn, "zone"), 0);
        System.out.println("Load the SHP successful");
        
        truncate(conn);
        System.out.println("About to load the SHP 900913");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("cities_900913.shp") + "?sandbox=" + sId);
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write(("{}").getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(iRet, 200);
        assertEquals(getCount(conn, "poi"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "TYPE"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "NATION"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "CNTRYNAME"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "LEVEL"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "NAME"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "NAMEPRE"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "CODE"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "PROVINCE"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "PROVNAME"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "UNPROV"), 1267);
        assertEquals(getTagCount(conn, "poi_tag", "CONURB"), 1267);
        extent = getExtent(conn, "poi");
        //extent in 4326 now (converted) But Precision loss.
        assertEquals(-21.8522, extent[0], 0.5);
        assertEquals(28.1388, extent[1], 0.5);
        assertEquals(58.5526, extent[2], 0.5);
        assertEquals(68.9713, extent[3], 0.5);
        assertEquals(getCount(conn, "path"), 0);
        assertEquals(getCount(conn, "zone"), 0);
        System.out.println("Load the SHP 900913 successful");

        truncate(conn);
        System.out.println("About to load the GML");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("gml") + "?sandbox=" + sId);
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write(("{}").getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        assertEquals(getCount(conn, "poi"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "AREA"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "PERIMETER"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "PACEL_"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "PACEL_ID"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "NAME"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "REG_CODE"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "NTS50"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "LAT"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "LONG"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "POP91"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "SGC_CODE"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "CAPITAL"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "POP_RANGE"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "UNIQUE_KEY"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "NAME_E"), 497);
        assertEquals(getTagCount(conn, "poi_tag", "NAME_F"), 497);
        assertEquals(getCount(conn, "path"), 0);
        assertEquals(getCount(conn, "zone"), 0);
        System.out.println("Load GML sucessuful");

        truncate(conn);
        System.out.println("About to load the DXF");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("dxf") + "?sandbox=" + sId);
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write(("{}").getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        assertEquals(getCount(conn, "poi"), 899);
        assertEquals(getTagCount(conn, "poi_tag", "Layer"), 899);
        assertEquals(getTagCount(conn, "poi_tag", "SubClasses"), 899);
        assertEquals(getTagCount(conn, "poi_tag", "ExtendedEntity"), 899);
        assertEquals(getTagCount(conn, "poi_tag", "Linetype"), 899);
        assertEquals(getTagCount(conn, "poi_tag", "EntityHandle"), 899);
        assertEquals(getTagCount(conn, "poi_tag", "Text"), 899);
        assertEquals(getCount(conn, "path"), 813);
        assertEquals(getTagCount(conn, "path_tag", "Layer"), 813);
        assertEquals(getTagCount(conn, "path_tag", "SubClasses"), 813);
        assertEquals(getTagCount(conn, "path_tag", "ExtendedEntity"), 813);
        assertEquals(getTagCount(conn, "path_tag", "Linetype"), 813);
        assertEquals(getTagCount(conn, "path_tag", "EntityHandle"), 813);
        assertEquals(getTagCount(conn, "path_tag", "Text"), 813);
        assertEquals(getCount(conn, "zone"), 0);
        System.out.println("Load the DXF sucessful");

        truncate(conn);
        System.out.println("About to load the KML");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("kml") + "?sandbox=" + sId);
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write(("{}").getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        assertEquals(getCount(conn, "poi"), 0);
        assertEquals(getCount(conn, "path"), 0);
        assertEquals(getCount(conn, "zone"), 2903);
        assertEquals(getTagCount(conn, "zone_tag", "Name"), 2903);
        assertEquals(getTagCount(conn, "zone_tag", "Description"), 2903);

        System.out.println("Load the KML sucessful");
        
        truncate(conn);
        System.out.println("About to load the DGN");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("dgn") + "?sandbox=" + sId);
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write(("{}").getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        assertEquals(getCount(conn, "poi"), 4002);
        assertEquals(getTagCount(conn, "poi_tag", "Type"), 4002);
        assertEquals(getTagCount(conn, "poi_tag", "Level"), 4002);
        assertEquals(getTagCount(conn, "poi_tag", "GraphicGroup"), 4002);
        assertEquals(getTagCount(conn, "poi_tag", "ColorIndex"), 4002);
        assertEquals(getTagCount(conn, "poi_tag", "Weight"), 4002);
        assertEquals(getTagCount(conn, "poi_tag", "Style"), 4002);
        assertEquals(getTagCount(conn, "poi_tag", "EntityNum"), 4002);
        assertEquals(getTagCount(conn, "poi_tag", "MSLink"), 4002);
        assertEquals(getTagCount(conn, "poi_tag", "Text"), 4002);
        assertEquals(getCount(conn, "path"), 0);
        assertEquals(getCount(conn, "zone"), 4000);
        assertEquals(getTagCount(conn, "zone_tag", "Type"), 4000);
        assertEquals(getTagCount(conn, "zone_tag", "Level"), 4000);
        assertEquals(getTagCount(conn, "zone_tag", "GraphicGroup"), 4000);
        assertEquals(getTagCount(conn, "zone_tag", "ColorIndex"), 4000);
        assertEquals(getTagCount(conn, "zone_tag", "Weight"), 4000);
        assertEquals(getTagCount(conn, "zone_tag", "Style"), 4000);
        assertEquals(getTagCount(conn, "zone_tag", "EntityNum"), 4000);
        assertEquals(getTagCount(conn, "zone_tag", "MSLink"), 4000);
        assertEquals(getTagCount(conn, "zone_tag", "Text"), 4000);
        System.out.println("Load the DGN sucessful");

        System.out.println("About to load the -1");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/-1?sandbox=" + sId);
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write(("{}").getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(404, iRet);
        System.out.println("Fail to load the -1 => OK");

        System.out.println("About to load the KML (Again)");

        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("kml") + "?sandbox=-1");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write(("{}").getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(401, iRet);
        System.out.println("Failed to load the KML (Again) => OK");

        System.out.println("About to load the not long ID (Crazy)");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/2.5?sandbox=3.5");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write(("{}").getBytes());
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(iRet, 404);
        System.out.println("Failed to load the not long ID (Crazy) = > OK");

        GetMethod gtm = new GetMethod(strKloudURL + "/kg_server/public/logout");
        HttpClient htcLogout = new HttpClient();
        assertEquals(200, htcLogout.executeMethod(gtm));
        gtm.releaseConnection();
        System.out.println("Logout OK");       
    }

    private void truncate(Connection conn) throws SQLException {
        System.out.println("Truncate poi, path and zone from the sandbox");
        PreparedStatement pst = conn.prepareStatement("truncate table path cascade;");
        pst.execute();
        pst.close();
        pst = conn.prepareStatement("truncate table poi cascade;");
        pst.execute();
        pst.close();
        pst = conn.prepareStatement("truncate table zone cascade;");
        pst.execute();
        pst.close();
        pst = conn.prepareStatement("truncate table path_tag cascade;");
        pst.execute();
        pst.close();
        pst = conn.prepareStatement("truncate table poi_tag cascade;");
        pst.execute();
        pst.close();
        pst = conn.prepareStatement("truncate table zone_tag cascade;");
        pst.execute();
        pst.close();
    }

    private int getID(String strExt) throws SQLException {
        Connection con = DriverManager.getConnection(strAdminDbURL, strDbUser, strPassword);
        PreparedStatement pst = con.prepareStatement("select lid from datasources where strfilename like'%" + strExt + "';");
        ResultSet rst = pst.executeQuery();
        int i = -1;
        while (rst.next()) {
            i = rst.getInt(1);
            break;
        }
        rst.close();
        pst.close();
        con.close();
        return i;
    }

    private int getTagCount(Connection con, String strTagType, String strTag) throws SQLException {
        PreparedStatement pst = con.prepareStatement("select count(*) from " + strTagType + " where key='" + strTag + "';");
        ResultSet rst = pst.executeQuery();
        int ret = 0;
        if (rst.next()) {
            ret = rst.getInt(1);
        }
        rst.close();
        pst.close();
        return ret;
    }

    private double[] getExtent(Connection con, String table) throws SQLException {
        PreparedStatement pst = con.prepareStatement("select extent(geom) from " + table + ";");
        ResultSet rst = pst.executeQuery();
        String ret = "";
        double[] env = new double[4];
        if (rst.next()) {
            ret = rst.getString(1);
            int iFirstBracket = ret.indexOf("(");
            int iFirstSpace = ret.indexOf(" ");
            int iFirstComma = ret.indexOf(",");
            int iLastSpace = ret.lastIndexOf(" ");
            int iLastBracket = ret.lastIndexOf(")");
            env[0] = Double.valueOf(ret.substring(iFirstBracket + 1, iFirstSpace));
            env[1] = Double.valueOf(ret.substring(iFirstSpace + 1, iFirstComma));
            env[2] = Double.valueOf(ret.substring(iFirstComma + 1, iLastSpace));
            env[3] = Double.valueOf(ret.substring(iLastSpace + 1, iLastBracket));
        }
        rst.close();
        pst.close();
        return env;
    }

    private int getCount(Connection con, String strTable) throws SQLException, ParseException {
        PreparedStatement pst = con.prepareStatement("select astext(geom) from " + strTable + ";");
        ResultSet rst = pst.executeQuery();
        int iCount = 0;
        while (rst.next()) {
            String str = rst.getString(1);
            Geometry geo = GeometryFactory.readWKT(str);
            assertNotNull(geo);
            assertFalse(geo.isEmpty());
            iCount++;
        }
        rst.close();
        pst.close();
        return iCount;
    }
}