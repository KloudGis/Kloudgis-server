/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.store;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.kloudgis.data.pojo.AbstractPlaceFeature;
import org.kloudgis.data.pojo.PoiFeature;

/**
 *
 * @author sylvain
 */
public class PoiDbEntity extends AbstractPlaceDbEntity {

    @SequenceGenerator(name = "poi_seq_gen", sequenceName = "poi_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "poi_seq_gen")
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long inId) {
        id=inId;
    }

    @Override
    public PoiFeature toPojo() {
        PoiFeature pojo = new PoiFeature();
        super.setupFromPojo(pojo);
        return pojo;
    }

    @Override
    public void fromPojo(AbstractPlaceFeature pojo) {
        super.setupFromPojo(pojo);
        //TODO: parse geometry here
    }

}
