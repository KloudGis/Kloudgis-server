/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.bean;

/**
 *
 * @author jeanfelixg
 */
public class TestConstant {
    public static final String strDbURL = "192.168.12.36:5432";
    public static final String strAdminDbURL = "jdbc:postgresql://" + strDbURL + "/test_admin";
    public static final String strSandboxDbURL = "jdbc:postgresql://" + strDbURL + "/test_sandbox";
    public static final String strGeoserverURL = "http://192.168.12.36:8080/geoserver211";
    public static final String strDbUser = "kloudgis_test";
    public static final String strPassword = "qwerty";
    public static final String strKloudURL = "http://localhost:8080";
    public static final String strGeoUser = "admin";
    public static final String strGeoPass = "geoserver";
}
