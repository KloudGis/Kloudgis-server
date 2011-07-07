
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
@Table(name = "poi_tag")
public class PoiTagDbEntity extends AbstractTagDbEntity{

    @SequenceGenerator(name = "poi_tag_seq_gen", sequenceName = "poi_tag_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "poi_tag_seq_gen")
    private Long id;

    @ManyToOne
    private PoiDbEntity fk;

    public void setFK( PoiDbEntity ent ) {
        fk = ent;
    }

    public PoiDbEntity getFK() {
        return fk;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}