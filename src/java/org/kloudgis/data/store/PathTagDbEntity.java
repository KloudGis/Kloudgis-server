
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
@Table(name = "path_tag")
public class PathTagDbEntity extends AbstractTagDbEntity{

    @SequenceGenerator(name = "path_tag_seq_gen", sequenceName = "path_tag_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "path_tag_seq_gen")
    private Long id;

    @ManyToOne
    private PathDbEntity fk;

    public void setFK( PathDbEntity ent ) {
        fk = ent;
    }

    public PathDbEntity getFK() {
        return fk;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}