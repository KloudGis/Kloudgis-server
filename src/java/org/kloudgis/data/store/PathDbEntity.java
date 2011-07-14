
package org.kloudgis.data.store;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.kloudgis.data.pojo.PathFeature;
import org.kloudgis.org.Feature;

/**
 *
 * @author sylvain
 */
@Entity
@Table(name = "path")
public class PathDbEntity extends AbstractPlaceDbEntity {

    @SequenceGenerator(name = "path_seq_gen", sequenceName = "path_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "path_seq_gen")
    private Long id;

    @NotFound(action=NotFoundAction.IGNORE)
    @OneToMany (cascade=CascadeType.REMOVE, mappedBy = "fk")
    private List<PathTagDbEntity> tags = new ArrayList<PathTagDbEntity>();

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
        super.setupPojo(pojo);
        List<Long> lstT = new ArrayList();
        if(tags!=null){
            for (PathTagDbEntity t : tags) {
                lstT.add(t.getId());
            }
            pojo.tags=lstT;
        }
        return pojo;
    }

    @Override
    public void fromPojo(AbstractFeature pojo) {
        super.setupFromPojo((AbstractPlaceFeature)pojo);

        //parse geometry here
    }

    @Override
    protected void persistTags( Feature ftr, EntityManager em ) {
        Map<String,String> colAttrs = ftr.getAttributes();
        for( String attr : colAttrs.keySet() ) {
            PathTagDbEntity tag = new PathTagDbEntity();
            tag.setFK( this );
            tag.setKey( attr );
            Object obj = colAttrs.get(attr);
            tag.setValue( obj == null ? null : obj.toString() );
            tags.add( tag );
            em.persist( tag );
        }
    }
}