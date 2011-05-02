/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.admin.pojo;

/**
 *
 * @author jeanfelixg
 */
public class LoginResponse {

    public String content;

    public LoginResponse(){}

    public LoginResponse(String hashed_token) {
        this.content = hashed_token;
    }
}
