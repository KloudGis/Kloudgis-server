/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.store;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.kloudgis.admin.pojo.User;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "users")
public class UserDbEntity implements Serializable {

    public static final String ROLE_ADM = "admin_role";
    public static final String ROLE_USER = "user_role";
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "user_seq_gen")
    private Long id;
    @Index(name = "email_index")
    @Column(length = 100)
    private String email;
    @Column(length = 100)
    private String fullName;
    @Column(length = 50)
    private String compagny;
    @Index(name = "loc_index")
    @Column(length = 50)
    private String location;
    @Column
    private Boolean isActive;
    @Column
    private byte[] picture;
    //security
    @Column(columnDefinition = "TEXT")
    private String password_hash;
    @Column(length = 10)
    private String password_salt;
    @Column(columnDefinition = "TEXT")
    private String auth_token;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name="sandbox_users",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="sandbox_id")
        )
    private Set<SandboxDbEntity> sandboxes;

    public void setSalt(String s) {
        password_salt = s;
    }

    public String getSalt() {
        return password_salt;
    }

    public void setPassword(String s) {
        password_hash = s;
    }

    public Object getPasswordHash() {
        return password_hash;
    }

    public void setAuthToken(String hashed_token) {
        this.auth_token = hashed_token;
    }

    public UserDbEntity() {
    }

    public UserDbEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the compagny
     */
    public String getCompagny() {
        return compagny;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    public Boolean isActive() {
        return isActive;
    }

    public boolean isSuperUser(EntityManager em) {
        if (this.email != null) {
            Query q = em.createNativeQuery("select count(*) from user_roles where email = :u and role_name =:r");
            q.setParameter("u", email);
            q.setParameter("r", ROLE_ADM);
            Object o = q.getSingleResult();
            if (o instanceof Number && ((Number) o).intValue() > 0) {
                return true;
            }
        }
        return false;
    }

    public UserRoleDbEntity getRole(String role, EntityManager em) {
        if (this.email != null && role != null) {
            Query q = em.createQuery("from UserRoleDbEntity where email = :u and role_name =:r", UserRoleDbEntity.class);
            q.setParameter("u", email);
            q.setParameter("r", role);
            List<UserRoleDbEntity> list = q.getResultList();
            if (list.size() > 0) {
                return list.get(0);
            }
        }
        return null;
    }

    public List<UserRoleDbEntity> getRoles(EntityManager em) {
        Query q = em.createQuery("from UserRoleDbEntity where email = :m", UserRoleDbEntity.class);
        q.setParameter("m", email);
        return q.getResultList();
    }

    public String toString() {
        return "User=" + id + "";
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param cie the compagny to set
     */
    public void setCompagny(String cie) {
        this.compagny = cie;
    }

    /**
     * @param loc the location to set
     */
    public void setLocation(String loc) {
        this.location = loc;
    }

    public void setActive(Boolean bAct) {
        this.isActive = bAct;
    }

    public void addRole(UserRoleDbEntity rNew) {
        rNew.setUser(this);
    }

    public void removeRole(UserRoleDbEntity role, EntityManager em) {
        em.remove(role);
    }

    @Override
    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof UserDbEntity)) {
            return false;
        }
        UserDbEntity other = (UserDbEntity) otherOb;
        return ((id == null ? other.id == null : id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public void setSuperUser(Boolean superUser, EntityManager em) {
        //validate admin_role is set for this user.
        if (superUser) {
            if (!isSuperUser(em)) {
                UserRoleDbEntity adm = new UserRoleDbEntity();
                adm.setRoleName(ROLE_ADM);
                addRole(adm);
                em.persist(adm);
            }
        } else {
            UserRoleDbEntity adm = getRole(ROLE_ADM, em);
            if (adm != null) {
                removeRole(adm, em);
            }
        }
    }

    public User toPojo(EntityManager em) {
        User entity = new User();
        entity.guid = getId();
        entity.fullName = getFullName();
        entity.email = getEmail();
        entity.compagny = getCompagny();
        entity.location = location;
        // entity.isActive = isActive();
        // entity.isSuperUser = isSuperUser(em);
        return entity;
    }

    public Set<SandboxDbEntity> getSandboxes() {
        return sandboxes;
    }
   
}
