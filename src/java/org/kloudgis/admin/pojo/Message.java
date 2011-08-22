/*
 * @author corneliu
 */
package org.kloudgis.admin.pojo;

public class Message {

    public String message;
    public String message_loc;
    public Integer type;

    public Message() {}

    public Message( String strMessage, Integer bType ) {
        message = strMessage;
        this.type = bType;
    }
}