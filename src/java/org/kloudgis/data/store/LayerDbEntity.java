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
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.security.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.kloudgis.data.pojo.Layer;
/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "layers")
public class LayerDbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long            id;

    @Column
    private Boolean         group_layer;
    @Column
    private Boolean         should_render;
    @Column
    private Boolean         base_layer;
    @Column
    private Integer         render_order;
    @Column(length=30)
    private String          type;

    //any custom parameters
    @Column
    private String          extra_param;

    @Column(length = 100)
    private String          name;
    @Column(length = 100)
    private String          owner;
    @Column(length = 100)
    private String          label;
    @Column
    private Timestamp       date_creation;
    @Column(length = 100)
    private String          featuretype;
    @Column(length = 30)
    private String          srs;
    @Column(length = 254)
    private String          url;
    @Column
    private Integer         buffer;
    @Column(length = 50)
    private String          transition_effect;
    @Column
    private Boolean         visibility;
    @Column
    private Boolean         display_outside_max_extent;
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry        max_extent;

    public Layer toPojo(EntityManager em) {

        Layer pojo = new Layer();
        pojo.guid = id;
        pojo.groupLayer = group_layer;
        pojo.shouldRender = should_render;
        pojo.baseLayer = base_layer;
        pojo.renderOrder = render_order;
        pojo.type = type;
        pojo.extraParam = extra_param;

        pojo.name = name;
        pojo.owner = owner;
        pojo.label = label;
        pojo.featuretype = featuretype;
        pojo.srs = srs;
        pojo.url = url;
        pojo.buffer = buffer;
        pojo.transitionEffect = transition_effect;
        pojo.visibility = visibility;
        pojo.displayOutsideExtent = display_outside_max_extent;
        if(max_extent != null){
            Envelope env = max_extent.getEnvelopeInternal();
            pojo.max_extent_left = env.getMinX();
            pojo.max_extent_right = env.getMaxX();
            pojo.max_extent_bottom = env.getMinY();
            pojo.max_extent_top = env.getMaxY();
        }

        return pojo;
    }
}
