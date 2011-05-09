/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.store;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.kloudgis.admin.pojo.Feed;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "feeds")
public class FeedDbEntity implements Serializable {
    @SequenceGenerator(name = "feed_seq_gen", sequenceName = "feed_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "feed_seq_gen")
    private Long id;
    @Index(name="name_index")
    @Column(length = 100)
    private String title;
    @Index(name="descr_index")
    @Column(length = 100)
    private String descr;
    @Index(name="date_creation_index")
    @Column
    private Timestamp date_creation;
    @Column
    private Boolean is_read;

    
    
    public Feed toPojo(EntityManager em) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Feed pojo = new Feed();
        pojo.guid = id;
        pojo.title = title;
        pojo.descr = descr;
        pojo.dateCreation = date_creation == null ? null : format.format(date_creation);
        return pojo;
    }
}
