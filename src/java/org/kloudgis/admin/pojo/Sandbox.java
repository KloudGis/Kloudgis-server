package org.kloudgis.admin.pojo;

/*
* EchoSphere - Spatial Platform for Application and Communication Extensions.
 *
 * http://www.echospheretechnologies.com
 *
 * Copyright (c) 2003 Echosphere Technologies, Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Echosphere Technologies, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Echosphere Technologies, Inc.
 */



/**
 *
 * @author jeanfelixg
 */
public class Sandbox {

    public Long     guid;
    public String   name;
    public String   owner;
    //format yyyy-MM-dd
    public String   dateCreation;

    //map home location center - coordinate longitude and latitude. Ex= -73.2,45.5
    public String   homeLonLatCenter;
    //zoom level for the home location.
    public Integer  homeZoomLevel;
    //srs for display coordinates on the map. Ex: EPSG:4326
    public String   displayProjection;
    //google, yahoo, bing, openlayers...
    public Long     baseLayerMode;
    //geoserver url
    public String   url_geoserver;
    //db url
    public String connection_url;
}
