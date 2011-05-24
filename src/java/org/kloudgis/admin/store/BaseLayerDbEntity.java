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

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "base_layers")
public class BaseLayerDbEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column (length=30)
    private String name;
    @Column (length=50)
    private String label_fr;
    @Column (length=50)
    private String label_en;
    @OneToMany(fetch= FetchType.EAGER, mappedBy = "base_layer")
    private Set<BaseLayerModeDbEntity> base_layer_modes;

    public void setLabelFr(String str){
        this.label_fr = str;
    }

    public void setLabelEn(String str){
        this.label_en = str;
    }

    public void setName(String str){
        this.name = str;
    }

    public void addBaseLayerMode(BaseLayerModeDbEntity mode){
        mode.setBaseLayer(this);
    }
}
