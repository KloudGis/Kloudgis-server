/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.store;

import javax.persistence.Column;

/**
 *
 * @author sylvain
 */
public abstract class AbstractTagDbEntity {
    

    @Column
    private String key;

    @Column
    private String value;

}
