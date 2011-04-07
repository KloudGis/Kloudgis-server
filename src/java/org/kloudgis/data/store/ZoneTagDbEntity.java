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
@Table(name = "zone_tag")
public class ZoneTagDbEntity extends AbstractTagDbEntity{

    @SequenceGenerator(name = "zone_tag_seq_gen", sequenceName = "zone_tag_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "zone_tag_seq_gen")
    private Long id;

    @Column
    private Long fk_id;

}
