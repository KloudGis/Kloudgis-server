/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.kloudgis.data.pojo.AbstractFeature;
import org.kloudgis.data.pojo.AbstractPlaceFeature;
import org.kloudgis.data.pojo.PoiFeature;

/**
 *
 * @author sylvain
 */
@Entity
@Table(name = "poi")

public class PoiDbEntity extends AbstractPlaceDbEntity implements Serializable{

    @SequenceGenerator(name = "poi_seq_gen", sequenceName = "poi_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "poi_seq_gen")
    private Long id;

    @NotFound(action=NotFoundAction.IGNORE)
    @OneToMany (cascade=CascadeType.REMOVE)
    @JoinColumn(name="fk_id", referencedColumnName="id")
    private List<PoiTagDbEntity> tags;

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
        super.setupPojo(pojo);
        List<Long> lstT = new ArrayList();
        if(tags!=null){
            for (PoiTagDbEntity t : tags) {
               lstT.add(t.getId());
            }
            pojo.tags=lstT;
        }
        return pojo;
    }

    @Override
    public void fromPojo(AbstractFeature pojo) {
        super.setupFromPojo((AbstractPlaceFeature)pojo);
        //TODO: parse geometry here
    }

}
