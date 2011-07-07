
package org.kloudgis.data.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.kloudgis.data.pojo.AbstractFeature;
import org.kloudgis.data.pojo.AbstractPlaceFeature;
import org.kloudgis.data.pojo.ZoneFeature;
import org.kloudgis.gdal.Attribute;
import org.kloudgis.gdal.Feature;

/**
 *
 * @author sylvain
 */
@Entity
@Table(name = "zone")
public class ZoneDbEntity extends AbstractPlaceDbEntity {

    @SequenceGenerator(name = "zone_seq_gen", sequenceName = "zone_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "zone_seq_gen")
    private Long id;

    @NotFound(action=NotFoundAction.IGNORE)
    @OneToMany (cascade=CascadeType.REMOVE, mappedBy = "fk")
    private List<ZoneTagDbEntity> tags = new ArrayList<ZoneTagDbEntity>();

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
        if(tags!=null){
            for (ZoneTagDbEntity t : tags) {
                lstT.add(t.getId());
            }
            pojo.tags=lstT;
        }
        return pojo;
    }

    @Override
    public void fromPojo(AbstractFeature pojo) {
        super.setupFromPojo((AbstractPlaceFeature)pojo);
    }

    @Override
    protected void persistTags( Feature ftr, EntityManager em ) {
        Collection<Attribute> colAttrs = ftr.getAttributes();
        for( Attribute attr : colAttrs ) {
            ZoneTagDbEntity tag = new ZoneTagDbEntity();
            tag.setFK( this );
            tag.setKey( attr.getName() );
            Object obj = attr.getValue();
            tag.setValue( obj == null ? null : obj.toString() );
            tags.add( tag );
            em.persist( tag );
        }
    }
}