/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    @Column
    private Long fk_id;

    public Long getFk_id() {
        return fk_id;
    }

    public void setFk_id(Long fk_id) {
        this.fk_id = fk_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

}
