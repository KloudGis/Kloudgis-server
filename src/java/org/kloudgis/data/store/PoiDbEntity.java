
package org.kloudgis.data.store;

import java.io.Serializable;
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
import org.kloudgis.data.pojo.PoiFeature;
import org.kloudgis.gdal.Attribute;
import org.kloudgis.gdal.Feature;

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
    @OneToMany (cascade=CascadeType.REMOVE, mappedBy = "fk")
    private List<PoiTagDbEntity> tags = new ArrayList<PoiTagDbEntity>();

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

    @Override
    protected void persistTags( Feature ftr, EntityManager em ) {
        Collection<Attribute> colAttrs = ftr.getAttributes();
        for( Attribute attr : colAttrs ) {
            PoiTagDbEntity tag = new PoiTagDbEntity();
            tag.setFK( this );
            tag.setKey( attr.getName() );
            Object obj = attr.getValue();
            tag.setValue( obj == null ? null : obj.toString() );
            tags.add( tag );
            em.persist( tag );
        }
    }
}