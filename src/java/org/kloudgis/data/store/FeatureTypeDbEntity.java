/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Point;
import java.util.List;
import org.kloudgis.data.pojo.FeatureType;
import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.kloudgis.data.pojo.QuickFeature;
/**
 *
 * @author sylvain
 */
@Entity
@Table(name = "featuretype")
@NamedQueries({
    @NamedQuery(name = "FeatureType.findAll", query = "SELECT c FROM FeatureTypeDbEntity c order by c.label")})
public class FeatureTypeDbEntity implements Serializable {

    @SequenceGenerator(name = "ft_seq_gen", sequenceName = "ft_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ft_seq_gen")
    private Long id;
    @Column
    private String name;
    
    @Column
    private String label;

    @Column
    private String client_class_name;

    @Column
    private String feature_class_name;


    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public String getClientClassName() {
        return client_class_name;
    }
    
    public Class getFeatureClass() {
        try {
            return Class.forName(feature_class_name);
        } catch (Exception ex) {
            return null;
        }
    } 
    
    public void setClientClassName(String name){
        this.client_class_name = name;
    }
    
    public void setFeatureClassName(String name){
        this.feature_class_name = name;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof FeatureTypeDbEntity)) {
            return false;
        }
        FeatureTypeDbEntity other = (FeatureTypeDbEntity) otherOb;
        return ((id == null ? other.id == null : id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public FeatureType toPojo(EntityManager em) {
        FeatureType pojo = new FeatureType();
        pojo.guid = getId();
        pojo.name = getName();
        pojo.label = getLabel();
        pojo.class_name = getClientClassName();
        return pojo;
    }

    public void updateFrom(FeatureType ft) {
        this.setName(ft.name);
        this.setLabel(ft.label);
    }

    public List<QuickFeature> findQuickFeaturesAt(Point point, LayerDbEntity lay, Double onePixelWorld, Integer limit, EntityManager em) {
        List<QuickFeature> lstQ = new ArrayList<QuickFeature>();
        return lstQ;
    }
    
}
