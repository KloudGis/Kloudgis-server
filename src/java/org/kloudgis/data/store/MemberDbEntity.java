/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.kloudgis.data.pojo.Member;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "members")
public class MemberDbEntity implements Serializable {
    
    @SequenceGenerator(name = "members_seq_gen", sequenceName = "members_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "members_seq_gen")
    private Long id;
    @Index (name="user_index")
    @Column
    private Long user_id;
    @Index (name="access_index")
    @Column
    private String access_type;

    public Member toPojo(EntityManager emSand) {
        Member pojo = new Member();
        pojo.guid = id;
        pojo.user = user_id;
        pojo.access = access_type;
        return pojo;
    }

    public void fromPojo(Member pojo) {
        this.id = pojo.guid;
        this.user_id = pojo.user;
        this.access_type = pojo.access;
    }
}
