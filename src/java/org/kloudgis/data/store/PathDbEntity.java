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
import org.kloudgis.data.pojo.PathFeature;

/**
 *
 * @author sylvain
 */
@Entity
@Table(name = "path")
public class PathDbEntity extends AbstractPlaceDbEntity{

    @SequenceGenerator(name = "path_seq_gen", sequenceName = "path_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "path_seq_gen")
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long inId) {
        id= inId;
    }

    @Override
    public PathFeature toPojo() {
        PathFeature pojo = new PathFeature();
        super.setupFromPojo(pojo);
        return pojo;
    }

    @Override
    public void fromPojo(AbstractPlaceFeature pojo) {
        super.setupFromPojo(pojo);
        
        //parse geometry here
    }

}
