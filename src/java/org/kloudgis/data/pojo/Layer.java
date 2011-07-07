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

package org.kloudgis.data.pojo;

/**
 *
 * @author jeanfelixg
 */
public class Layer {

    public Long     guid;
    //if its a group layer of not
    public Boolean  isGroupLayer;
    //if its a layer grouped in a group layer
    public Boolean  isGroupedLayer;
    //rendering order (1 is the first to be draw)
    public Integer  renderOrder; 
    //select on map ?
    public Boolean  isSelectable;
    public Integer  pixelTolerance;
    public Long     featuretype;

    //layer parameters
    //user who has created the layer in the sandbox
    public Long   owner;
    
    public String   label;
    //the unique name including the workspace Ex: cite:mylayer
    public String   name;  
    //the coordinate system Ex: EPSG:4326
    public String   srs;
    //relative or complete url to geoserver
    public String   url;
    //number of tiles render outside the visible area
    public Integer  buffer;
    //effect on zoom, pan.  null or "resize" is the only valid values.  resize scale the image on zoom while waiting for the updated image.
    public String   transitionEffect;
    public Boolean  visibility;
    //whether of not the server should render features outside the max extent.
    //maxExtent has to be provided to have any effect.
    public Boolean  displayOutsideExtent;   

}
