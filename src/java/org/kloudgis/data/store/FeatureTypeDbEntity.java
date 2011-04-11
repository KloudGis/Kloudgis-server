/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import org.kloudgis.data.pojo.FeatureType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
/**
 *
 * @author sylvain
 */
@Entity
@Table(name = "featuretype")
@NamedQueries({
    @NamedQuery(name = "FeatureType.findAll", query = "SELECT c FROM FeatureTypeDbEntity c order by c.label")})
public class FeatureTypeDbEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    
    @Column
    private String label;

   

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    //setters
   

    public void setDescription(String description) {
        this.description = description;
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
        pojo.guid = getId().toString();
        pojo.name = getName();
        pojo.description = getDescription();
        pojo.label = getLabel();

        return pojo;
    }

    public void updateFrom(FeatureType ft) {
        this.setName(ft.name);
        this.setDescription(ft.description);
        this.setLabel(ft.label);

    }
}
