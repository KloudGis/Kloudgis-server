 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.data.store;


import com.vividsolutions.jts.geom.Geometry;
import javax.persistence.Column;
import org.hibernate.annotations.Type;
import org.kloudgis.data.pojo.AbstractPlaceFeature;

/**
 *
 * @author sylvain
 */
public abstract class AbstractPlaceDbEntity {


    @Column
    private String name;

    @Column
    private String featureClass;

    @Column
    private String type;

    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geom;

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

    //Abstract methods
    
    public abstract Long getId();

    public abstract void setId(Long id);

    public abstract AbstractPlaceFeature toPojo();
    
    public abstract void fromPojo(AbstractPlaceFeature pojo);

}
