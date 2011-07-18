/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.store;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
    @Index(name = "name_index")
    @Column(length = 100)
    private String title;
    @Index(name = "descr_index")
    @Column(length = 100)
    private String descr;
    @Index(name = "date_creation_index")
    @Column
    private Timestamp date_creation = new Timestamp(Calendar.getInstance().getTimeInMillis());
    @ManyToOne(fetch= FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "sandbox_id")
    private SandboxDbEntity sandbox;

    public Feed toPojo(EntityManager em) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Feed pojo = new Feed();
        pojo.guid = id;
        pojo.title = title;
        pojo.descr = descr;
        pojo.dateCreation = date_creation == null ? null : format.format(date_creation);
        pojo.sandbox = sandbox.getId();
        return pojo;
    }

    public void fromPojo(Feed pojo) {
        if(pojo.guid !=null){
            this.id = pojo.guid;
        }
        this.title = pojo.title;
        this.descr = pojo.descr;
    }

    public void setSandbox(SandboxDbEntity sand) {
        this.sandbox = sand;
    }
    
    public Long getID(){
        return id;
    }
}
