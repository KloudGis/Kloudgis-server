/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.util.LinkedHashMap;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.ejb.HibernateEntityManager;


/**
 * SA: **** Temporary : To be replaced by dynamic PU
 * 
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;
    public static final String DEFAULT_PU = "sandboxPU";
    public static String ADMIN_PU = "adminPU";
    private static final PersistenceManager singleton = new PersistenceManager();
    private LinkedHashMap<String, EntityManagerFactory> hashFactory = new LinkedHashMap<String, EntityManagerFactory>();
  

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        return singleton;
    }

    public HibernateEntityManager getEntityManagerDefault() {
        return getEntityManager(DEFAULT_PU);
    }

  
    public HibernateEntityManager getEntityManager(String namePU) {
        return (HibernateEntityManager) getEntityManagerFactory(namePU).createEntityManager();
    }

    private EntityManagerFactory getEntityManagerFactory(String namePU) {
        if (hashFactory.get(namePU) == null) {
            return createEntityManagerFactory(namePU);
        }
        return hashFactory.get(namePU);
    }

    public void closeEntityManagerFactories() {
        for (EntityManagerFactory emf : hashFactory.values()) {
            if (emf != null) {
                emf.close();
                emf = null;
            }
        }
    }

    protected EntityManagerFactory createEntityManagerFactory(String namePU) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(namePU);
        if (emf != null) {
            hashFactory.put(namePU, emf);
        }
        return emf;
    }
}
