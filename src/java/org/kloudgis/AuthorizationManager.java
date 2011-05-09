/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.kloudgis.admin.store.UserDbEntity;

/**
 *
 * @author jeanfelixg
 */
public final class AuthorizationManager {

    public UserDbEntity getUserFromAuthToken(String password_hash, EntityManager em) {
        try {
            UserDbEntity u = em.createQuery("from UserDbEntity where auth_token=:token", UserDbEntity.class).setParameter("token", password_hash).getSingleResult();
            return u;
        } catch (NoResultException e) {
            return null;
        }
    }
}
