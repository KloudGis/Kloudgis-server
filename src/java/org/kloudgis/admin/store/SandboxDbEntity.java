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
package org.kloudgis.admin.store;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.kloudgis.admin.pojo.Sandbox;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "sandboxes")
public class SandboxDbEntity implements Serializable {

    //general info
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Index(name="name_index")
    @Column(length = 100)
    private String name;
    @Index(name="owner_index")
    @Column(length = 100)
    private String owner;
    @Index(name="date_creation_index")
    @Column
    private Timestamp date_creation;
    //map metadata
    //lon lat map center
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry center_map;
    @Column
    private Integer center_zoom_level;
    //projection to display coordinates.  "ESPG:4326"
    @Column(length = 30)
    private String display_projection;

    public Sandbox toPojo(EntityManager em) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Sandbox pojo = new Sandbox();
        pojo.guid = id;
        pojo.name = name;
        pojo.owner = owner;
        pojo.dateCreation = date_creation == null ? null : format.format(date_creation);
        //map meta
        if(center_map != null){
            Coordinate cCenter = center_map.getCoordinates()[0];
            pojo.homeLonLatCenter = cCenter.x + "," + cCenter.y;
            pojo.homeZoomLevel = center_zoom_level;
        }
        pojo.displayProjection = display_projection;
        return pojo;
    }
}
