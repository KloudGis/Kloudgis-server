/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.store;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.kloudgis.data.pojo.AbstractPlaceFeature;
import org.kloudgis.data.pojo.ZoneFeature;

/**
 *
 * @author sylvain
 */
@Entity
@Table(name = "zone")
public class ZoneDbEntity extends AbstractPlaceDbEntity{

    @SequenceGenerator(name = "zone_seq_gen", sequenceName = "zone_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "zone_seq_gen")
    private Long id;


    @NotFound(action=NotFoundAction.IGNORE)
    @OneToMany (cascade=CascadeType.REMOVE)
    @JoinColumn(name="fk_id", referencedColumnName="id")
    private List<ZoneTagDbEntity> tags;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long inId) {
        id= inId;
    }

    @Override
    public ZoneFeature toPojo() {
        ZoneFeature pojo = new ZoneFeature();
        super.setupPojo(pojo);
        List<Long> lstT = new ArrayList();
        for (ZoneTagDbEntity t : tags) {
            lstT.add(t.getId());
        }
        pojo.tags=lstT;
        return pojo;
    }

    @Override
    public void fromPojo(AbstractPlaceFeature pojo) {
        super.setupFromPojo(pojo);
    }

}
