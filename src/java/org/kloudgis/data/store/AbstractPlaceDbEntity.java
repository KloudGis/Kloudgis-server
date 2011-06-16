 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.store;


import com.vividsolutions.jts.geom.Geometry;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.kloudgis.data.pojo.AbstractPlaceFeature;

/**
 *
 * @author sylvain
 */
@MappedSuperclass
public abstract class AbstractPlaceDbEntity extends AbstractFeatureDbEntity{


    @Column
    private String   name;

    @Column
    private String   featureClass;

    @Column
    private String   type;

    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geom;
    @Index(name="layer_id_index")
    @Column
    private Long     layer_id;

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    public String getFeatureClass() {
        return featureClass;
    }

    public void setFeatureClass(String featureClass) {
        this.featureClass = featureClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void setupFromPojo(AbstractPlaceFeature inPojo){
        name=inPojo.name;
        featureClass=inPojo.featureClass;
        type=inPojo.type;
        setId(inPojo.guid);
        
    }

    public void setupPojo(AbstractPlaceFeature inPojo){
        inPojo.name=getName();
        inPojo.featureClass=getFeatureClass();
        inPojo.type=getType();
        inPojo.guid=getId();
    }   
}
