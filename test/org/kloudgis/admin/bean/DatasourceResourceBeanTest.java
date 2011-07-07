/*
 * @author corneliu
 */
package org.kloudgis.admin.bean;

import org.kloudgis.MapServerFactory;
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
import static org.junit.Assert.*;

public class DatasourceResourceBeanTest {

    @Test
    public void testAddDatasource() throws MalformedURLException, IOException, SQLException, ClassNotFoundException {
        System.out.println("addDatasource");

//        get a direct connection to the test database so we can check if things went right
        Class.forName( "org.postgresql.Driver" );
        Connection conn = DriverManager.getConnection( "jdbc:postgresql://localhost:5432/kloud_admin", "postgres", "" );

//        drop the existing tables so we can start with a clean slate
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement( "truncate table columns cascade;" );
            pst.execute();
            pst = conn.prepareStatement( "truncate table datasource cascade;" );
            pst.execute();
            pst.close();
        } catch( Exception e ) {}

//        get the relative path to the place where the data files for this test are
        String strPath = MapServerFactory.getWebInfPath() + "../../../test_res";

//        insert shape file
        URL url = new URL( "http://localhost:8080/kg_server/protected/sources" );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        OutputStream ost = httpCon.getOutputStream();
        ost.write( ( strPath + "/cities.shp" ).getBytes() );
        ost.flush();
        int iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );

//        test the shape file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='cities.shp';" );
        ResultSet rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), 58.5526, 0 );
        assertEquals( rst.getDouble( 3 ), 68.9713, 0 );
        assertEquals( rst.getDouble( 4 ), -21.8522, 0 );
        assertEquals( rst.getDouble( 5 ), 28.1388, 0 );
        assertTrue( rst.getBytes( 6 ).length > 0 );
        assertEquals( rst.getInt( 7 ), 0 );
        assertEquals( rst.getInt( 8 ), 11 );
        assertEquals( rst.getInt( 9 ), 1267 );
        assertEquals( rst.getInt( 10 ), 1 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 35576 );
        assertEquals( rst.getLong( 13 ), 981014400000l );
        assertEquals( rst.getString( 14 ), "cities.shp" );
        assertEquals( rst.getString( 15 ), "Point" );
        long lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "TYPE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "NATION" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 60 );
        assertEquals( rst.getString( 5 ), "CNTRYNAME" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "LEVEL" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 60 );
        assertEquals( rst.getString( 5 ), "NAME" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 10 );
        assertEquals( rst.getString( 5 ), "NAMEPRE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 10 );
        assertEquals( rst.getString( 5 ), "CODE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "PROVINCE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 60 );
        assertEquals( rst.getString( 5 ), "PROVNAME" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "UNPROV" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 60 );
        assertEquals( rst.getString( 5 ), "CONURB" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        rst.close();
        pst.close();

//        insert dgn file
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        ost = httpCon.getOutputStream();
        ost.write( ( strPath + "/2325_integration.dgn" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );

//        test the dgn file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='2325_integration.dgn';" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), 229754.9006, 0.000001 );
        assertEquals( rst.getDouble( 3 ), 5064226, 0.000001 );
        assertEquals( rst.getDouble( 4 ), 208626.4227, 0.000001 );
        assertEquals( rst.getDouble( 5 ), 5040476.0779, 0.000001 );
        assertTrue( rst.getBytes( 6 ).length > 0 );
        assertEquals( rst.getInt( 7 ), -1 );
        assertEquals( rst.getInt( 8 ), 9 );
        assertEquals( rst.getInt( 9 ), 8002 );
        assertEquals( rst.getInt( 10 ), 0 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 3180544 );
        assertEquals( rst.getLong( 13 ), 1248178009000l );
        assertEquals( rst.getString( 14 ), "2325_integration.dgn" );
        assertEquals( rst.getString( 15 ), "Unknown" );
        lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Type" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Level" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "GraphicGroup" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "ColorIndex" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Weight" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Style" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "EntityNum" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "MSLink" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Text" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        rst.close();
        pst.close();

//        insert dxf file
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        ost = httpCon.getOutputStream();
        ost.write( ( strPath + "/2325_BORN_REN.dxf" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );

//        test the dxf file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='2325_BORN_REN.dxf';" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), 230565.659999877, 0.000001 );
        assertEquals( rst.getDouble( 3 ), 5065429.70789984, 0.000001 );
        assertEquals( rst.getDouble( 4 ), 213470.867699484, 0.000001 );
        assertEquals( rst.getDouble( 5 ), 5038624.22039983, 0.000001 );
        assertTrue( rst.getBytes( 6 ).length > 0 );
        assertEquals( rst.getInt( 7 ), -1 );
        assertEquals( rst.getInt( 8 ), 6 );
        assertEquals( rst.getInt( 9 ), 1712 );
        assertEquals( rst.getInt( 10 ), 0 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 474690 );
        assertEquals( rst.getLong( 13 ), 1272982590000l );
        assertEquals( rst.getString( 14 ), "2325_BORN_REN.dxf" );
        assertEquals( rst.getString( 15 ), "Unknown" );
        lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Layer" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "SubClasses" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "ExtendedEntity" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Linetype" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "EntityHandle" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Text" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        rst.close();
        pst.close();

//        insert gml file
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        ost = httpCon.getOutputStream();
        ost.write( ( strPath + "places.gml" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );

//        test the gml file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='places.gml';" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), -52.80807, 0 );
        assertEquals( rst.getDouble( 3 ), 82.43198, 0 );
        assertEquals( rst.getDouble( 4 ), -140.87349, 0 );
        assertEquals( rst.getDouble( 5 ), 42.05346 , 0 );
        assertTrue( rst.getBytes( 6 ).length > 0 );
        assertEquals( rst.getInt( 7 ), -1 );
        assertEquals( rst.getInt( 8 ), 16 );
        assertEquals( rst.getInt( 9 ), 497 );
        assertEquals( rst.getInt( 10 ), 1 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 358258 );
        assertEquals( rst.getLong( 13 ), 1301316630000l );
        assertEquals( rst.getString( 14 ), "places.gml" );
        assertEquals( rst.getString( 15 ), "Point" );
        lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "AREA" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.RealType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "PERIMETER" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.RealType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "PACEL_" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "PACEL_ID" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 25 );
        assertEquals( rst.getString( 5 ), "NAME" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "REG_CODE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 6 );
        assertEquals( rst.getString( 5 ), "NTS50" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "LAT" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "LONG" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "POP91" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "SGC_CODE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "CAPITAL" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "POP_RANGE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 5 );
        assertEquals( rst.getString( 5 ), "UNIQUE_KEY" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 11 );
        assertEquals( rst.getString( 5 ), "NAME_E" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 11 );
        assertEquals( rst.getString( 5 ), "NAME_F" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        rst.close();
        pst.close();

//        insert kml file
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        ost = httpCon.getOutputStream();
        ost.write( ( strPath + "LOTOCCUPE.kml" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );

//        test the kml file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='LOTOCCUPE.kml';" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), -72.5046028211896, 0.00000001 );
        assertEquals( rst.getDouble( 3 ), 45.3592196152746, 0.00000001 );
        assertEquals( rst.getDouble( 4 ), -72.5514406587745, 0.00000001 );
        assertEquals( rst.getDouble( 5 ), 45.3160540739093, 0.00000001 );
        assertTrue( rst.getBytes( 6 ).length > 0 );
        assertEquals( rst.getInt( 7 ), 0 );
        assertEquals( rst.getInt( 8 ), 2 );
        assertEquals( rst.getInt( 9 ), 2903 );
        assertEquals( rst.getInt( 10 ), -2147483645 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 14479348 );
        assertEquals( rst.getLong( 13 ), 1306241742000l );
        assertEquals( rst.getString( 14 ), "LOTOCCUPE.kml" );
        assertEquals( rst.getString( 15 ), "Unknown" );
        lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "Name" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "Description" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        rst.close();
        pst.close();
        conn.close();

        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        ost = httpCon.getOutputStream();
        ost.write( ( strPath + "/phantom.shp" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 500 );
    }

    @Test
    public void testLoadData() throws Exception {
        System.out.println("loadData");
        Class.forName( "org.postgresql.Driver" );
        Connection conn = DriverManager.getConnection( "jdbc:postgresql://localhost:5432/jf@toto.ca_proj1", "postgres", "" );

        truncate( conn );
        URL url = new URL( "http://localhost:8080/kg_server/protected/sources/load/" + getID( "dgn" ) + "?sandbox=1" );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty( "Content-type", "application/json" );
        OutputStream ost = httpCon.getOutputStream();
        ost.write( ( "{}" ).getBytes() );
        ost.flush();
        int iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );
        assertEquals( getCount( conn, "poi" ), 4002 );
        assertEquals( getTagCount( conn, "poi_tag", "Type" ), 4002 );
        assertEquals( getTagCount( conn, "poi_tag", "Level" ), 4002 );
        assertEquals( getTagCount( conn, "poi_tag", "GraphicGroup" ), 4002 );
        assertEquals( getTagCount( conn, "poi_tag", "ColorIndex" ), 4002 );
        assertEquals( getTagCount( conn, "poi_tag", "Weight" ), 4002 );
        assertEquals( getTagCount( conn, "poi_tag", "Style" ), 4002 );
        assertEquals( getTagCount( conn, "poi_tag", "EntityNum" ), 4002 );
        assertEquals( getTagCount( conn, "poi_tag", "MSLink" ), 4002 );
        assertEquals( getTagCount( conn, "poi_tag", "Text" ), 4002 );
        assertEquals( getCount( conn, "path" ), 0 );
        assertEquals( getCount( conn, "zone" ), 4000 );
        assertEquals( getTagCount( conn, "zone_tag", "Type" ), 4000 );
        assertEquals( getTagCount( conn, "zone_tag", "Level" ), 4000 );
        assertEquals( getTagCount( conn, "zone_tag", "GraphicGroup" ), 4000 );
        assertEquals( getTagCount( conn, "zone_tag", "ColorIndex" ), 4000 );
        assertEquals( getTagCount( conn, "zone_tag", "Weight" ), 4000 );
        assertEquals( getTagCount( conn, "zone_tag", "Style" ), 4000 );
        assertEquals( getTagCount( conn, "zone_tag", "EntityNum" ), 4000 );
        assertEquals( getTagCount( conn, "zone_tag", "MSLink" ), 4000 );
        assertEquals( getTagCount( conn, "zone_tag", "Text" ), 4000 );

        truncate( conn );
        url = url = new URL( "http://localhost:8080/kg_server/protected/sources/load/" + getID( "shp" ) + "?sandbox=1" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty( "Content-type", "application/json" );
        ost = httpCon.getOutputStream();
        ost.write( ( "{}" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );
        assertEquals( getCount( conn, "poi" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "TYPE" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "NATION" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "CNTRYNAME" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "LEVEL" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "NAME" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "NAMEPRE" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "CODE" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "PROVINCE" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "PROVNAME" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "UNPROV" ), 1267 );
        assertEquals( getTagCount( conn, "poi_tag", "CONURB" ), 1267 );
        assertEquals( getCount( conn, "path" ), 0 );
        assertEquals( getCount( conn, "zone" ), 0 );

        truncate( conn );
        url = new URL( "http://localhost:8080/kg_server/protected/sources/load/" + getID( "dxf" ) + "?sandbox=1" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty( "Content-type", "application/json" );
        ost = httpCon.getOutputStream();
        ost.write( ( "{}" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );
        assertEquals( getCount( conn, "poi" ), 899 );
        assertEquals( getTagCount( conn, "poi_tag", "Layer" ), 899 );
        assertEquals( getTagCount( conn, "poi_tag", "SubClasses" ), 899 );
        assertEquals( getTagCount( conn, "poi_tag", "ExtendedEntity" ), 899 );
        assertEquals( getTagCount( conn, "poi_tag", "Linetype" ), 899 );
        assertEquals( getTagCount( conn, "poi_tag", "EntityHandle" ), 899 );
        assertEquals( getTagCount( conn, "poi_tag", "Text" ), 899 );
        assertEquals( getCount( conn, "path" ), 813 );
        assertEquals( getTagCount( conn, "path_tag", "Layer" ), 813 );
        assertEquals( getTagCount( conn, "path_tag", "SubClasses" ), 813 );
        assertEquals( getTagCount( conn, "path_tag", "ExtendedEntity" ), 813 );
        assertEquals( getTagCount( conn, "path_tag", "Linetype" ), 813 );
        assertEquals( getTagCount( conn, "path_tag", "EntityHandle" ), 813 );
        assertEquals( getTagCount( conn, "path_tag", "Text" ), 813 );
        assertEquals( getCount( conn, "zone" ), 0 );

        truncate( conn );
        url = new URL( "http://localhost:8080/kg_server/protected/sources/load/" + getID( "gml" ) + "?sandbox=1" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty( "Content-type", "application/json" );
        ost = httpCon.getOutputStream();
        ost.write( ( "{}" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );
        assertEquals( getCount( conn, "poi" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "AREA" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "PERIMETER" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "PACEL_" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "PACEL_ID" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "NAME" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "REG_CODE" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "NTS50" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "LAT" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "LONG" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "POP91" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "SGC_CODE" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "CAPITAL" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "POP_RANGE" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "UNIQUE_KEY" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "NAME_E" ), 497 );
        assertEquals( getTagCount( conn, "poi_tag", "NAME_F" ), 497 );
        assertEquals( getCount( conn, "path" ), 0 );
        assertEquals( getCount( conn, "zone" ), 0 );

        truncate( conn );
        url = new URL( "http://localhost:8080/kg_server/protected/sources/load/" + getID( "kml" ) + "?sandbox=1" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty( "Content-type", "application/json" );
        ost = httpCon.getOutputStream();
        ost.write( ( "{}" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 200 );
        assertEquals( getCount( conn, "poi" ), 0 );
        assertEquals( getCount( conn, "path" ), 0 );
        assertEquals( getCount( conn, "zone" ), 2903 );
        assertEquals( getTagCount( conn, "zone_tag", "Name" ), 2903 );
        assertEquals( getTagCount( conn, "zone_tag", "Description" ), 2903 );
        conn.close();

        url = new URL( "http://localhost:8080/kg_server/protected/sources/load/-1?sandbox=1" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty( "Content-type", "application/json" );
        ost = httpCon.getOutputStream();
        ost.write( ( "{}" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 404 );

        url = new URL( "http://localhost:8080/kg_server/protected/sources/load/" + getID( "kml" ) + "?sandbox=-1" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty( "Content-type", "application/json" );
        ost = httpCon.getOutputStream();
        ost.write( ( "{}" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 404 );

        url = new URL( "http://localhost:8080/kg_server/protected/sources/load/2.5?sandbox=3.5" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty( "Content-type", "application/json" );
        ost = httpCon.getOutputStream();
        ost.write( ( "{}" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        httpCon.disconnect();
        ost.close();
        assertEquals( iRet, 404 );
    }

    private void truncate( Connection conn ) throws SQLException {
        PreparedStatement pst = conn.prepareStatement( "truncate table path cascade;" );
        pst.execute();
        pst.close();
        pst = conn.prepareStatement( "truncate table poi cascade;" );
        pst.execute();
        pst.close();
        pst = conn.prepareStatement( "truncate table zone cascade;" );
        pst.execute();
        pst.close();
        pst = conn.prepareStatement( "truncate table path_tag cascade;" );
        pst.execute();
        pst.close();
        pst = conn.prepareStatement( "truncate table poi_tag cascade;" );
        pst.execute();
        pst.close();
        pst = conn.prepareStatement( "truncate table zone_tag cascade;" );
        pst.execute();
        pst.close();
    }

    private int getID( String strExt ) throws SQLException {
        Connection con = DriverManager.getConnection( "jdbc:postgresql://localhost:5432/kloud_admin", "postgres", "" );
        PreparedStatement pst = con.prepareStatement( "select lid from datasource where strfilename like'%." + strExt + "';" );
        ResultSet rst = pst.executeQuery();
        int i = -1;
        while( rst.next() ) {
            i = rst.getInt( 1 );
            break;
        }
        rst.close();
        pst.close();
        con.close();
        return i;
    }

    private int getTagCount( Connection con, String strTagType, String strTag ) throws SQLException {
        PreparedStatement pst = con.prepareStatement( "select value from " + strTagType + " where key='" + strTag + "';" );
        ResultSet rst = pst.executeQuery();
        int i = 0;
        while( rst.next() ) {
            assertNotNull( rst.getString( 1 ) );
            i++;
        }
        rst.close();
        pst.close();
        return i;
    }

    private int getCount( Connection con, String strTable ) throws SQLException, ParseException {
        PreparedStatement pst = con.prepareStatement( "select astext(geom) from " + strTable + ";" );
        ResultSet rst = pst.executeQuery();
        int iCount = 0;
        while( rst.next() ) {
            String str = rst.getString( 1 );
            Geometry geo = GeometryFactory.readWKT( str );
            assertNotNull( geo );
            assertFalse( geo.isEmpty() );
            iCount++;
        }
        rst.close();
        pst.close();
        return iCount;
    }
}