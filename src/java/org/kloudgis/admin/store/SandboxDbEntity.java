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
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
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
    @SequenceGenerator(name = "sandbox_seq_gen", sequenceName = "sandbox_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sandbox_seq_gen")
    private Long id;
    @Index(name="s_name_index")
    @Column(length = 100)
    private String name;
    @Index(name="s_owner_index")
    @Column(length = 100)
    private String owner;
    @Index(name="s_date_creation_index")
    @Column
    private Timestamp date_creation;
    @Index(name="s_key_index")
    @Column(length = 250)
    private String unique_key;
    @Column(length = 250)
    private String connection_url;
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
    @ManyToOne(fetch= FetchType.LAZY, optional=true,cascade= CascadeType.REMOVE)
    private BaseLayerModeDbEntity base_layer_mode;
    @Column(length = 250)
    private String geoserver_url;
    //feeds
    @OneToMany(mappedBy="sandbox", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<FeedDbEntity> feeds;
    //binded user
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name="sandbox_users",
            joinColumns=@JoinColumn(name="sandbox_id"),
            inverseJoinColumns=@JoinColumn(name="user_id")
        )
    private Set<UserDbEntity> binded_users = new HashSet();

    public Long getId(){
        return id;
    }

    public void setName(String str){
        this.name = str;
    }

    public void setBaseLayerMode(BaseLayerModeDbEntity mode){
        this.base_layer_mode = mode;
    }

    public void setUniqueKey(String key){
        this.unique_key = key;
    }

    public void setURL( String strURL ) {
        connection_url = strURL;
    }

    public void setGeoserverURL( String strURL ) {
        geoserver_url = strURL;
    }

    public void setOwner( String strOwner ) {
        owner = strOwner;
    }

    public String getUniqueKey(){
        return unique_key;
    }

    public String getConnectionUrl() {
        return connection_url;
    }

    public String getGeoserverUrl() {
        return geoserver_url;
    }

    public Set<FeedDbEntity> getFeed() {
        return feeds;
    }
    
    public String getName(){
        return name;
    }

    public void addFeed(FeedDbEntity feedDb) {
        feedDb.setSandbox(this);
        this.feeds.add(feedDb);
    }

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
        pojo.connection_url = connection_url;
        pojo.baseLayerMode = base_layer_mode == null ? null :base_layer_mode.getID();
        return pojo;
    }

    public void bindUser(UserDbEntity user) {
        binded_users.add(user);
    }

    public void unBindUser(UserDbEntity user) {
        binded_users.remove(user);
    }
}