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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;

/**
 *
 * @author jeanfelixg
 */
@Entity
@org.hibernate.annotations.Table(
    appliesTo="user_roles",
    indexes = { @Index(name="main_idx", columnNames = { "email", "role_name" } ) }
)
@Table(name="user_roles")
public class UserRoleDbEntity implements Serializable {

    @SequenceGenerator(name = "role_seq_gen", sequenceName = "role_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "role_seq_gen")
    private Long id;
    @Column(length = 100)
    private String email;
    @Column(length = 50)
    private String role_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getRoleName() {
        return role_name;
    }

    public void setEmail(String name) {
        email = name;
    }

    public void setRoleName(String name) {
        role_name = name;
    }

    public void setUser(UserDbEntity user) {
        if (user != null) {
            this.email = user.getEmail();
        } else {
            this.email = null;
        }
    }

}
