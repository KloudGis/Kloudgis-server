/*
 * @author corneliu
 */
package org.kloudgis.admin.bean;

import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
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
import org.junit.Test;
import org.kloudgis.GeometryFactory;
import org.kloudgis.MapServerFactory;
import org.kloudgis.persistence.DatabaseFactory;
import static org.junit.Assert.*;

public class DatasourceResourceBeanTest {

    private final String strDbURL = "localhost:5432";
    private final String strAdminDbURL = "jdbc:postgresql://" + strDbURL + "/test_admin";
    private final String strSandboxDbURL = "jdbc:postgresql://" + strDbURL + "/test_sandbox";
    private final String strGeoserverURL = "http://192.168.12.36:8080/geoserver210/rest";
    private final String strDbUser = "kloudgis";
    private final String strPassword = "kwadmin";
    private final String strKloudURL = "http://localhost:8080";
    private final String strGeoUser = "admin";
    private final String strGeoPass = "geoserver";

    public void dropCreate() throws ClassNotFoundException, SQLException {
        System.out.println("addDatasource");

        //        get native postgres connection for testing
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://" + strDbURL + "/postgis", strDbUser, strPassword);
        try {
            PreparedStatement pst = conn.prepareStatement("drop database test_sandbox;");
            pst.execute();
            pst.close();
        } catch (Exception e) {
        }
        try {
            PreparedStatement pst = conn.prepareStatement("drop database test_admin;");
            pst.execute();
            pst.close();
        } catch (Exception e) {
            Connection conn2 = null;
            try {
                conn2 = DriverManager.getConnection("jdbc:postgresql://" + strDbURL + "/test_admin", strDbUser, strPassword);
                PreparedStatement pst = conn2.prepareStatement("truncate datasource;");
                pst.execute();
                pst.close();
                pst = conn2.prepareStatement("truncate src_column;");
                pst.execute();
                pst.close();
                conn2.close();
            } catch (Exception ee) {
                if (conn2 != null) {
                    conn2.close();
                }
            }
        }
        conn.close();
//        create the db
        DatabaseFactory.createDB(strDbURL, "test_admin");
    }

    @Test
    public void testAddDatasources() throws MalformedURLException, IOException, SQLException, ClassNotFoundException {
        System.out.println("Test datasource!!");
        try {
            dropCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Create test_admin completed");

        PostMethod pstm = new PostMethod(strKloudURL + "/kg_server/public/login");
        String strPswd = LoginFactory.hashString("kwadmin", "SHA-256");
        pstm.setRequestEntity(new StringRequestEntity("{\"user\":\"admin@kloudgis.com\",\"pwd\":\"" + strPswd + "\"}", "application/json", "UTF-8"));
        HttpClient htcLogin = new HttpClient();
        assertEquals(htcLogin.executeMethod(pstm), 200);
        String strBody = pstm.getResponseBodyAsString(1000);
        String strAuth = strBody.substring(strBody.indexOf(":") + 2, strBody.lastIndexOf("\""));
        pstm.releaseConnection();
        System.out.println("Login completed");
//        get the relative path to the place where the data files for this test are
        String strPath = "/gitroot/Kloudgis-server/test_res";//MapServerFactory.getWebInfPath() + "../../../test_res";
//        insert shape file
        System.out.println("About to add cities.shp");
        URL url = new URL(strKloudURL + "/kg_server/protected/sources");
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        OutputStream ost = httpCon.getOutputStream();
        ost.write((strPath + "/cities.shp").getBytes());
        ost.flush();
        int iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);
        System.out.println("add cities.shp sucessful");

        System.out.println("About to add 2325_integration.dgn");
//        insert dgn file
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write((strPath + "/2325_integration.dgn").getBytes());
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
        ost.write((strPath + "/2325_BORN_REN.dxf").getBytes());
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
        ost.write((strPath + "/places.gml").getBytes());
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
        ost.write((strPath + "/LOTOCCUPE.kml").getBytes());
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
        PreparedStatement pst = conn.prepareStatement("select lid, denvelopemaxx, denvelopemaxy, denvelopeminx, denvelopeminy, octet_length(\"file\"::bytea),"
                + " icrs, icolumncount, ifeaturecount, igeomtype, ilayercount, lfilesize, llastmodified, lownerid, strfilename, strgeomname from datasource;");
        ResultSet rst = pst.executeQuery();
        assertTrue(rst.next());
        assertEquals(rst.getDouble(2), 58.5526, 0);
        assertEquals(rst.getDouble(3), 68.9713, 0);
        assertEquals(rst.getDouble(4), -21.8522, 0);
        assertEquals(rst.getDouble(5), 28.1388, 0);
        assertEquals(rst.getInt(6), 73780);
        assertEquals(rst.getInt(7), 0);
        assertEquals(rst.getInt(8), 11);
        assertEquals(rst.getInt(9), 1267);
        assertEquals(rst.getInt(10), 1);
        assertEquals(rst.getInt(11), 1);
        assertEquals(rst.getLong(12), 35576);
        assertEquals(rst.getLong(13), 981014400000l);
        assertEquals(rst.getInt(14), 1);
        assertEquals(rst.getString(15), "cities.shp");
        assertEquals(rst.getString(16), "Point");

        System.out.println("Check cities.shp OK");

        PreparedStatement pst1 = conn.prepareStatement("select * from ds_column where dts_lid=1;");
        ResultSet rst1 = pst1.executeQuery();
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 0);
        assertEquals(rst1.getString(5), "TYPE");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 0);
        assertEquals(rst1.getString(5), "NATION");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 60);
        assertEquals(rst1.getString(5), "CNTRYNAME");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 0);
        assertEquals(rst1.getString(5), "LEVEL");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 60);
        assertEquals(rst1.getString(5), "NAME");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 10);
        assertEquals(rst1.getString(5), "NAMEPRE");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 10);
        assertEquals(rst1.getString(5), "CODE");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 0);
        assertEquals(rst1.getString(5), "PROVINCE");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 60);
        assertEquals(rst1.getString(5), "PROVNAME");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 0);
        assertEquals(rst1.getString(5), "UNPROV");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst1.next());
        assertEquals(rst1.getInt(3), 60);
        assertEquals(rst1.getString(5), "CONURB");
        assertEquals(rst1.getString(6), "org.kloudgis.gdal.schema.StringType");
        rst1.close();
        pst1.close();

        System.out.println("Check cities.shp columns OK");

//        test the dgn file insertion
        assertTrue(rst.next());
        assertEquals(rst.getDouble(2), 229754.9006, 0.000001);
        assertEquals(rst.getDouble(3), 5064226, 0.000001);
        assertEquals(rst.getDouble(4), 208626.4227, 0.000001);
        assertEquals(rst.getDouble(5), 5040476.0779, 0.000001);
        assertEquals(rst.getInt(6), 1297824);
        assertEquals(rst.getInt(7), -1);
        assertEquals(rst.getInt(8), 9);
        assertEquals(rst.getInt(9), 8002);
        assertEquals(rst.getInt(10), 0);
        assertEquals(rst.getInt(11), 1);
        assertEquals(rst.getLong(12), 3180544);
        assertEquals(rst.getLong(13), 1248178009000l);
        assertEquals(rst.getInt(14), 1);
        assertEquals(rst.getString(15), "2325_integration.dgn");
        assertEquals(rst.getString(16), "Unknown");

        System.out.println("Check 2325_integration.dgn OK");

        PreparedStatement pst2 = conn.prepareStatement("select * from ds_column where dts_lid=2;");
        ResultSet rst2 = pst2.executeQuery();
        assertTrue(rst2.next());
        assertEquals(rst2.getString(5), "Type");
        assertEquals(rst2.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst2.next());
        assertEquals(rst2.getString(5), "Level");
        assertEquals(rst2.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst2.next());
        assertEquals(rst2.getString(5), "GraphicGroup");
        assertEquals(rst2.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst2.next());
        assertEquals(rst2.getString(5), "ColorIndex");
        assertEquals(rst2.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst2.next());
        assertEquals(rst2.getString(5), "Weight");
        assertEquals(rst2.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst2.next());
        assertEquals(rst2.getString(5), "Style");
        assertEquals(rst2.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst2.next());
        assertEquals(rst2.getString(5), "EntityNum");
        assertEquals(rst2.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst2.next());
        assertEquals(rst2.getString(5), "MSLink");
        assertEquals(rst2.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst2.next());
        assertEquals(rst2.getString(5), "Text");
        assertEquals(rst2.getString(6), "org.kloudgis.gdal.schema.StringType");
        rst2.close();
        pst2.close();

        System.out.println("Check 2325_integration.dgn Columns OK");

//        test the dxf file insertion
        assertTrue(rst.next());
        assertEquals(rst.getDouble(2), 230565.659999877, 0.000001);
        assertEquals(rst.getDouble(3), 5065429.70789984, 0.000001);
        assertEquals(rst.getDouble(4), 213470.867699484, 0.000001);
        assertEquals(rst.getDouble(5), 5038624.22039983, 0.000001);
        assertEquals(rst.getInt(6), 60506);
        assertEquals(rst.getInt(7), -1);
        assertEquals(rst.getInt(8), 6);
        assertEquals(rst.getInt(9), 1712);
        assertEquals(rst.getInt(10), 0);
        assertEquals(rst.getInt(11), 1);
        assertEquals(rst.getLong(12), 474690);
        assertEquals(rst.getLong(13), 1272982590000l);
        assertEquals(rst.getInt(14), 1);
        assertEquals(rst.getString(15), "2325_BORN_REN.dxf");
        assertEquals(rst.getString(16), "Unknown");

        System.out.println("Check 2325_BORN_REN.dxf OK");

        PreparedStatement pst3 = conn.prepareStatement("select * from ds_column where dts_lid=3;");
        ResultSet rst3 = pst3.executeQuery();
        assertTrue(rst3.next());
        assertEquals(rst3.getString(5), "Layer");
        assertEquals(rst3.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst3.next());
        assertEquals(rst3.getString(5), "SubClasses");
        assertEquals(rst3.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst3.next());
        assertEquals(rst3.getString(5), "ExtendedEntity");
        assertEquals(rst3.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst3.next());
        assertEquals(rst3.getString(5), "Linetype");
        assertEquals(rst3.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst3.next());
        assertEquals(rst3.getString(5), "EntityHandle");
        assertEquals(rst3.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst3.next());
        assertEquals(rst3.getString(5), "Text");
        assertEquals(rst3.getString(6), "org.kloudgis.gdal.schema.StringType");
        rst3.close();
        pst3.close();

        System.out.println("Check 2325_BORN_REN.dxf Columns OK");

//        test the gml file insertion
        assertTrue(rst.next());
        assertEquals(rst.getDouble(2), -52.80807, 0);
        assertEquals(rst.getDouble(3), 82.43198, 0);
        assertEquals(rst.getDouble(4), -140.87349, 0);
        assertEquals(rst.getDouble(5), 42.05346, 0);
        assertEquals(rst.getInt(6), 32212);
        assertEquals(rst.getInt(7), -1);
        assertEquals(rst.getInt(8), 16);
        assertEquals(rst.getInt(9), 497);
        assertEquals(rst.getInt(10), 1);
        assertEquals(rst.getInt(11), 1);
        assertEquals(rst.getLong(12), 358258);
        assertEquals(rst.getLong(13), 1301316630000l);
        assertEquals(rst.getInt(14), 1);
        assertEquals(rst.getString(15), "places.gml");
        assertEquals(rst.getString(16), "Point");

        System.out.println("Check places.gml OK");

        PreparedStatement pst4 = conn.prepareStatement("select * from ds_column where dts_lid=4;");
        ResultSet rst4 = pst4.executeQuery();
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "AREA");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.RealType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "PERIMETER");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.RealType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "PACEL_");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "PACEL_ID");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 25);
        assertEquals(rst4.getString(5), "NAME");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "REG_CODE");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 6);
        assertEquals(rst4.getString(5), "NTS50");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "LAT");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "LONG");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "POP91");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "SGC_CODE");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "CAPITAL");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 0);
        assertEquals(rst4.getString(5), "POP_RANGE");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.IntegerType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 5);
        assertEquals(rst4.getString(5), "UNIQUE_KEY");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 11);
        assertEquals(rst4.getString(5), "NAME_E");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst4.next());
        assertEquals(rst4.getInt(3), 11);
        assertEquals(rst4.getString(5), "NAME_F");
        assertEquals(rst4.getString(6), "org.kloudgis.gdal.schema.StringType");
        rst4.close();
        pst4.close();

        System.out.println("Check places.gml Columns OK");

//        test the kml file insertion
        assertTrue(rst.next());
        assertEquals(rst.getDouble(2), -72.5046028211896, 0.00000001);
        assertEquals(rst.getDouble(3), 45.3592196152746, 0.00000001);
        assertEquals(rst.getDouble(4), -72.5514406587745, 0.00000001);
        assertEquals(rst.getDouble(5), 45.3160540739093, 0.00000001);
        assertEquals(rst.getInt(6), 688154);
        assertEquals(rst.getInt(7), 0);
        assertEquals(rst.getInt(8), 2);
        assertEquals(rst.getInt(9), 2903);
        assertEquals(rst.getInt(10), -2147483645);
        assertEquals(rst.getInt(11), 1);
        assertEquals(rst.getLong(12), 14479348);
        assertEquals(rst.getLong(13), 1306241742000l);
        assertEquals(rst.getInt(14), 1);
        assertEquals(rst.getString(15), "LOTOCCUPE.kml");
        assertEquals(rst.getString(16), "Unknown");

        System.out.println("Check LOTOCCUPE.kml OK");

        PreparedStatement pst5 = conn.prepareStatement("select * from ds_column where dts_lid=5;");
        ResultSet rst5 = pst5.executeQuery();
        assertTrue(rst5.next());
        assertEquals(rst5.getInt(3), 0);
        assertEquals(rst5.getString(5), "Name");
        assertEquals(rst5.getString(6), "org.kloudgis.gdal.schema.StringType");
        assertTrue(rst5.next());
        assertEquals(rst5.getInt(3), 0);
        assertEquals(rst5.getString(5), "Description");
        assertEquals(rst5.getString(6), "org.kloudgis.gdal.schema.StringType");
        rst5.close();
        pst5.close();
        conn.close();

        System.out.println("Check LOTOCCUPE.kml Columns OK");

        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        ost = httpCon.getOutputStream();
        ost.write((strPath + "/phantom.shp").getBytes());
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
        URL url = new URL(strKloudURL + "/kg_server/protected/admin/sandboxes");
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("POST");
        httpCon.setRequestProperty("Content-type", "application/json");
        httpCon.setRequestProperty("Cookie", "security-Kloudgis.org=" + strAuth);
        OutputStream ost = httpCon.getOutputStream();
        ost.write(("{\"connection_url\":\"" + strDbURL + "/test_sandbox\",\"name\":\"test_sandbox\",\"url_geoserver\":\"" + strGeoserverURL + "\"}").getBytes());
        ost.flush();
        int iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals(200, iRet);

        System.out.println("Create sandbox 'test_sandbox' sucessful");
        Connection conn = DriverManager.getConnection(strSandboxDbURL, strDbUser, strPassword);

        truncate(conn);
        System.out.println("About to load the DGN");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("dgn") + "?sandbox=1");
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

        truncate(conn);
        System.out.println("About to load the SHP");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("shp") + "?sandbox=1");
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
        assertEquals(getCount(conn, "path"), 0);
        assertEquals(getCount(conn, "zone"), 0);
        System.out.println("Load the SHP successful");

        truncate(conn);
        System.out.println("About to load the DXF");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("dxf") + "?sandbox=1");
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
        System.out.println("About to load the GML");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("gml") + "?sandbox=1");
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
        System.out.println("About to load the KML");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/" + getID("kml") + "?sandbox=1");
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
//        deleteWorkspaceAndDB( conn );

        System.out.println("About to load the -1");
        url = new URL(strKloudURL + "/kg_server/protected/sources/load/-1?sandbox=1");
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
        PreparedStatement pst = con.prepareStatement("select lid from datasource where strfilename like'%." + strExt + "';");
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
        PreparedStatement pst = con.prepareStatement("select value from " + strTagType + " where key='" + strTag + "';");
        ResultSet rst = pst.executeQuery();
        int i = 0;
        while (rst.next()) {
            assertNotNull(rst.getString(1));
            i++;
        }
        rst.close();
        pst.close();
        return i;
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

    private void deleteWorkspaceAndDB(Connection conn) throws MalformedURLException, IOException, SQLException {
        //        delete layers
        Authenticator.setDefault(new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(strGeoUser, strGeoPass.toCharArray());
            }
        });
        URL url = new URL(strGeoserverURL + "/layers/poi");
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("DELETE");
        int iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        assertEquals(iRet, 200);
        url = new URL(strGeoserverURL + "/layers/path");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("DELETE");
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        assertEquals(iRet, 200);
        url = new URL(strGeoserverURL + "/layers/zone");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("DELETE");
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        assertEquals(iRet, 200);

//        delete feature types
        url = new URL(strGeoserverURL + "/workspaces/test_sandbox/datastores/test_sandbox/featuretypes/poi");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("DELETE");
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        assertEquals(iRet, 200);
        url = new URL(strGeoserverURL + "/workspaces/test_sandbox/datastores/test_sandbox/featuretypes/path");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("DELETE");
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        assertEquals(iRet, 200);
        url = new URL(strGeoserverURL + "/workspaces/test_sandbox/datastores/test_sandbox/featuretypes/zone");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("DELETE");
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        assertEquals(iRet, 200);

//        delete the store
        url = new URL(strGeoserverURL + "/workspaces/test_sandbox/datastores/test_sandbox");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("DELETE");
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        assertEquals(iRet, 200);

//        delete workspace
        url = new URL(strGeoserverURL + "/workspaces/test_sandbox");
        httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("DELETE");
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        assertEquals(iRet, 200);

//        delete the admin db and the sandbox db
        PreparedStatement pst = conn.prepareStatement("drop database test_sandbox;");
        pst.execute();
        pst.close();
        pst = conn.prepareStatement("drop database test_admin;");
        pst.execute();
        pst.close();
        conn.close();
    }
}