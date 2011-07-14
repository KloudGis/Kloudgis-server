
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.Serializable;
import java.util.HashMap;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.kloudgis.data.pojo.AbstractPlaceFeature;
import org.kloudgis.data.pojo.Coordinate;
import org.kloudgis.data.pojo.PojoUtils;
import org.kloudgis.org.Feature;

/**
 *
 * @author sylvain
 */
@MappedSuperclass
public abstract class AbstractPlaceDbEntity extends AbstractFeatureDbEntity implements Serializable{

    @Column
    private String name;

    @Column
    private String featureClass;

    @Column
    private String type;

    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geom;
    
    @Index(name="layer_id_index")
    @Column
    private Long    layer_id;

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
        if(geom != null){
            Point center = geom.getCentroid();
            inPojo.center = new Coordinate(center.getX(), center.getY());
            inPojo.geo_type = geom.getGeometryType();
            inPojo.coordinates = PojoUtils.toPojo(geom.getCoordinates());
        }

    }

    public void setupFromFeature( Feature ftr, EntityManager em, HashMap<String, String> mapAttrs ) {
        if( mapAttrs != null ) {
            setFeatureClass( getString( ftr.getAttrValue( mapAttrs.get( "featureClass" ) ) ) );
            setName( getString( ftr.getAttrValue( mapAttrs.get( "name" ) ) ) );
            setType( getString( ftr.getAttrValue( mapAttrs.get( "type" ) ) ) );
        }
        persistTags( ftr, em );
    }

    protected abstract void persistTags( Feature ftr, EntityManager em );

    protected String getString( Object obj ) {
        if( obj == null ) {
            return null;
        } else if( obj instanceof String ) {
            return ( String )obj;
        }
        return obj.toString();
    }
}