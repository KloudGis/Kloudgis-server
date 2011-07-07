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

import java.io.Serializable;
import java.security.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.kloudgis.data.featuretype.AbstractFeatureType;
import org.kloudgis.data.pojo.Layer;
import org.kloudgis.data.store.utils.Model;
/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "layers")
public class LayerDbEntity implements Serializable {

    @SequenceGenerator(name = "layer_seq_gen", sequenceName = "layer_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "layer_seq_gen")
    private Long            id;

    @Column
    private Boolean         group_layer;
    @Column
    private Boolean         grouped_layer;
    @Column
    private Integer         render_order;
    @Column
    private Long          featuretype_id;

    @Column(length = 100)
    private String          name;
    @Column
    private Long          owner;
    @Column(length = 100)
    private String          label;
    @Column
    private Timestamp       date_creation;
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
    private Boolean         selectable;
    @Column
    private Integer         pixel_tolerance;

    public Layer toPojo(EntityManager em) {

        Layer pojo = new Layer();
        pojo.guid = id;
        pojo.isGroupLayer = group_layer;
        pojo.isGroupedLayer = grouped_layer;
        pojo.renderOrder = render_order;
        pojo.isSelectable = selectable;
        pojo.featuretype = featuretype_id;
        pojo.pixelTolerance = pixel_tolerance;
        
        pojo.name = name;
        pojo.owner = owner;
        pojo.label = label;
        pojo.srs = srs;
        pojo.url = url;
        pojo.buffer = buffer;
        pojo.transitionEffect = transition_effect;
        pojo.visibility = visibility;
        pojo.displayOutsideExtent = display_outside_max_extent;
        return pojo;
    }

    public AbstractFeatureType getFeatureType(EntityManager em) {
        return Model.getFeatureType(featuretype_id, em);
    }

    public Long getId() {
        return id;
    }

    public int getPixelTolerance() {
        return pixel_tolerance == null ? 0 : pixel_tolerance.intValue();
    }

    public void setCRS( String strCRS ) {
        srs = strCRS;
    }

    public void setFeatureTypeID( long lFtId ) {
        featuretype_id = lFtId;
    }

    public void setName( String strName ) {
        name = strName;
    }

    public void setVisible( boolean bVisibility ) {
        visibility = bVisibility;
    }

    public void setSelectable( boolean bSelectability ) {
        selectable = bSelectability;
    }

    public void setLabel( String strLabel ) {
        label = strLabel;
    }

    public void setOwner( Long lOwner ) {
        owner = lOwner;
    }
}