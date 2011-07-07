
package org.kloudgis.data.store;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author sylvain
 */
@MappedSuperclass
public abstract class AbstractTagDbEntity {

    @Column
    private String key;

    @Column
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}