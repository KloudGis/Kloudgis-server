/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.pojo;

import java.sql.Timestamp;

/**
 *
 * @author jeanfelixg
 */
public class User {

    public Long guid;
    public String fullName;
    public String email;
    public String location;
    public String compagny;
    public Timestamp userCreated;
    public String accountType;
    public Timestamp accountExpire;

    public User() {
    }

    public User(Long id) {
        this.guid = id;
    }

}
