/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.kloudgis.data.pojo.AbstractFeature;
import org.kloudgis.data.pojo.Coordinate;
import org.kloudgis.data.pojo.Note;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "note")
public class NoteDbEntity extends AbstractFeatureDbEntity implements Serializable{
    @SequenceGenerator(name = "note_seq_gen", sequenceName = "note_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "note_seq_gen")
    private Long id;   
    @Column
    private String title;
    @Column
    private String description;    
    @Column
    private String author;
    @Column
    private Date   date_create;
    @Column
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Geometry geom;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public AbstractFeature toPojo() {
        Note pojo = new Note();
        pojo.guid = id;
        pojo.coordinate = geom == null? null : new Coordinate(geom.getCentroid().getX(), geom.getCentroid().getY());
        pojo.title = title;
        pojo.description = description;
        pojo.author = author;
        pojo.date = date_create;
        return pojo;
        
    }

    @Override
    public void fromPojo(AbstractFeature pojo) {
        if(pojo instanceof Note){
            Note note = (Note) pojo;
            this.id = note.guid;
            this.title = note.title;
            this.description = note.description;
            //geom ?
        }
    }

    public Point getGeometry() {
        return (Point) geom;
    }
}
