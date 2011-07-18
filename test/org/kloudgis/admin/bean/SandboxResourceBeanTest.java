/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.kloudgis.admin.bean.TestConstant.*;
import org.kloudgis.LoginFactory;
import org.kloudgis.admin.pojo.Feed;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.pojo.User;
import org.kloudgis.data.pojo.FetchResult;
import org.kloudgis.mapserver.MapServerFactory;

/**
 *
 * @author jeanfelixg
 */
public class SandboxResourceBeanTest {

    public SandboxResourceBeanTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.out.println("*** End of SandboxResourceBeanTest");
    }
    private ObjectMapper mapper = new ObjectMapper();
    private String auth_token;
    private User loggedUser;
    //to share from test to test
    private static Long sandboxId;
    private static Long sandboxIdNew;
    private static Long feedId;

    @Before
    public void setUp() {
        try {
            PostMethod postLogin = new PostMethod(strKloudURL + "/kg_server/public/login");
            String strPswd = LoginFactory.hashString("kwadmin", "SHA-256");
            postLogin.setRequestEntity(new StringRequestEntity("{\"user\":\"admin@kloudgis.com\",\"pwd\":\"" + strPswd + "\"}", "application/json", "UTF-8"));
            HttpClient htcLogin = new HttpClient();
            assertEquals(200, htcLogin.executeMethod(postLogin));
            String strBody = postLogin.getResponseBodyAsString(1000);
            auth_token = strBody.substring(strBody.indexOf(":") + 2, strBody.lastIndexOf("\""));
            postLogin.releaseConnection();
            System.out.println("Login completed");
            GetMethod getUser = new GetMethod(strKloudURL + "/kg_server/public/logged_user");
            getUser.setRequestHeader("cookie", "security-Kloudgis.org=" + auth_token);
            assertEquals(200, htcLogin.executeMethod(getUser));
            loggedUser = mapper.readValue(getUser.getResponseBodyAsStream(), User.class);
            getUser.releaseConnection();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @After
    public void tearDown() {
        try {
            GetMethod getLogout = new GetMethod(strKloudURL + "/kg_server/public/logout");
            HttpClient htcLogout = new HttpClient();
            assertEquals(200, htcLogout.executeMethod(getLogout));
            getLogout.releaseConnection();
            System.out.println("Logout OK");
        } catch (IOException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    /**
     * Test of getSandboxes method, of class SandboxResourceBean.
     */
    @Test
    public void testGetSandboxes() {
        System.out.println("getSandboxes");
        try {
            GetMethod getSand = new GetMethod(strKloudURL + "/kg_server/protected/admin/sandboxes");
            HttpClient htcLogout = new HttpClient();
            //not auth token,it  should refuse
            assertEquals(401, htcLogout.executeMethod(getSand));
            List<Sandbox> list = getSandboxes();
            assertEquals(1, list.size());
            assertEquals("test_sandbox", list.get(0).name);
            sandboxId = list.get(0).guid;
            getSand.releaseConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private List<Sandbox> getSandboxes() throws IOException {
        GetMethod getSand = new GetMethod(strKloudURL + "/kg_server/protected/admin/sandboxes");
        HttpClient htcLogout = new HttpClient();
        getSand.setRequestHeader("cookie", "security-Kloudgis.org=" + auth_token);
        int ret = htcLogout.executeMethod(getSand);
        List<Sandbox> list = mapper.readValue(getSand.getResponseBodyAsStream(), new TypeReference<List<Sandbox>>() {
        });
        getSand.releaseConnection();
        assertEquals(200, ret);
        return list;
    }

    /**
     * Test of addUser method, of class SandboxResourceBean.
     */
    @Test
    public void testUnBindUser() {
        System.out.println("UnBindUser");
        try {
            List<Sandbox> list = getSandboxes();
            assertEquals(1, list.size());
            PostMethod post = new PostMethod(strKloudURL + "/kg_server/protected/admin/sandboxes/" + sandboxId + "/unbind_usr/" + loggedUser.guid);
            HttpClient htcLogout = new HttpClient();
            post.setRequestHeader("cookie", "security-Kloudgis.org=" + auth_token);
            int ret = htcLogout.executeMethod(post);
            post.releaseConnection();
            assertEquals(200, ret);
            list = getSandboxes();
            assertEquals(0, list.size());
        } catch (IOException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    /**
     * Test of addUser method, of class SandboxResourceBean.
     */
    @Test
    public void testBindUser() {
        System.out.println("BindUser");
        try {
            List<Sandbox> list = getSandboxes();
            assertEquals(0, list.size());
            PostMethod post = new PostMethod(strKloudURL + "/kg_server/protected/admin/sandboxes/" + sandboxId + "/bind_usr/" + loggedUser.guid);
            HttpClient htcLogout = new HttpClient();
            post.setRequestHeader("cookie", "security-Kloudgis.org=" + auth_token);
            int ret = htcLogout.executeMethod(post);
            post.releaseConnection();
            assertEquals(200, ret);
            list = getSandboxes();
            assertEquals(1, list.size());
        } catch (IOException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }

    }

    /**
     * Test of addFeed method, of class SandboxResourceBean.
     */
    @Test
    public void testAddFeed() {
        System.out.println("addFeed");
        try {
            PostMethod post = new PostMethod(strKloudURL + "/kg_server/protected/admin/sandboxes/" + sandboxId + "/feeds");
            HttpClient htcLogout = new HttpClient();
            post.setRequestHeader("cookie", "security-Kloudgis.org=" + auth_token);
            Feed pojo = new Feed();
            pojo.title = "test";
            pojo.descr = "junit feed!";
            String json = mapper.writeValueAsString(pojo);
            post.setRequestEntity(new StringRequestEntity(json, "application/json", "UTF-8"));
            int ret = htcLogout.executeMethod(post);
            assertEquals(200, ret);
            System.out.println(post.getResponseBodyAsString(1000));
            feedId = mapper.readValue(post.getResponseBodyAsStream(), Feed.class).guid;
            post.releaseConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    /**
     * Test of getFeeds method, of class SandboxResourceBean.
     */
    @Test
    public void testGetFeeds() {
        System.out.println("testGetFeeds");
        try {
            GetMethod getSand = new GetMethod(strKloudURL + "/kg_server/protected/admin/sandboxes/feeds");
            HttpClient htcLogout = new HttpClient();
            getSand.setRequestHeader("cookie", "security-Kloudgis.org=" + auth_token);
            int ret = htcLogout.executeMethod(getSand);
            assertEquals(200, ret);
            System.out.println(getSand.getResponseBodyAsString(1000));
            FetchResult list = mapper.readValue(getSand.getResponseBodyAsStream(), new TypeReference<FetchResult<Feed>>() {
            });
            getSand.releaseConnection();
            boolean bFound = false;
            //look for the feed inserted in the previous test
            for (Object ob : list.features) {
                Feed fe = (Feed) ob;
                if (fe.guid.longValue() == feedId.longValue()) {
                    bFound = true;
                    //right sandbox id
                    assertEquals(sandboxId, fe.sandbox);
                    break;
                }
            }
            assertTrue(bFound);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    /**
     * Test of addSandbox method, of class SandboxResourceBean.
     */
    @Test
    public void testAddSandbox() {
        try {

            Connection conn = null;
            try {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection("jdbc:postgresql://" + strDbURL + "/postgis", strDbUser, strPassword);
                PreparedStatement pst = conn.prepareStatement("drop database test_sandbox2;");
                pst.execute();
                pst.close();
            } catch (Exception e) {
            }
            if (conn != null) {
                conn.close();
            }
            try {
                //geoserver
                MapServerFactory.deleteWorkspace(strGeoserverURL, "test_sandbox2", new UsernamePasswordCredentials(strGeoUser, strGeoPass));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strKloudURL + "/kg_server/protected/admin/sandboxes");
            post.addRequestHeader("Cookie", "security-Kloudgis.org=" + auth_token);
            ObjectMapper mapper = new ObjectMapper();
            Sandbox outSandbox = new Sandbox();
            outSandbox.connection_url = strDbURL;
            outSandbox.name = "test_sandbox2";
            outSandbox.url_geoserver = strGeoserverURL;
            String json = mapper.writeValueAsString(outSandbox);
            post.setRequestEntity(new StringRequestEntity(json, "application/json", "UTF-8"));
            int iRet = httpClient.executeMethod(post);
            System.out.println(post.getResponseBodyAsString(1000));
            assertEquals(200, iRet);
            Sandbox sand = mapper.readValue(post.getResponseBodyAsStream(), Sandbox.class);
            sandboxIdNew = sand.guid;
            System.out.println("Create sandbox 'test_sandbox' sucessful (" + sandboxIdNew + ")");

            //try again with the same name - it should refuse
            iRet = httpClient.executeMethod(post);
            assertEquals(500, iRet);
            Message mess = mapper.readValue(post.getResponseBodyAsStream(), Message.class);
            System.out.println("Ok but Error Message was: " + mess.message);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    /**
     * Test of addSandbox method, of class SandboxResourceBean.
     */
    @Test
    public void deleteAddSandbox() {
        try {
            //wait a minute to let the db connectiona go away (need that to be able to remove the DB).
            try {
                Thread.sleep(65000);
            } catch (InterruptedException ex) {
                Logger.getLogger(SandboxResourceBeanTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            HttpClient httpClient = new HttpClient();
            DeleteMethod delete = new DeleteMethod(strKloudURL + "/kg_server/protected/admin/sandboxes/" + sandboxIdNew);
            delete.addRequestHeader("Cookie", "security-Kloudgis.org=" + auth_token);
            int iRet = httpClient.executeMethod(delete);
            assertEquals(200, iRet);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
