/*
 * @author corneliu
 */
package org.kloudgis.admin.pojo;

public class Message {

    public String message;
    public String message_loc;
    public byte bType;

    public Message() {}

    public Message( String strMessage, byte bType ) {
        message = strMessage;
        this.bType = bType;
    }
}