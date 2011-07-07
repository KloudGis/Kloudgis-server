
package org.kloudgis.data.store;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author sylvain
 */
@Entity
@Table(name = "zone_tag")
public class ZoneTagDbEntity extends AbstractTagDbEntity {

    @SequenceGenerator(name = "zone_tag_seq_gen", sequenceName = "zone_tag_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "zone_tag_seq_gen")
    private Long id;

    @ManyToOne
    private ZoneDbEntity fk;

    public void setFK( ZoneDbEntity ent ) {
        fk = ent;
    }

    public ZoneDbEntity getFK() {
        return fk;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}