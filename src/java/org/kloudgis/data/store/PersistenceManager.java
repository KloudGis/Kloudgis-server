/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.hibernate.ejb.HibernateEntityManager;


/**
 * SA: **** Temporary : To be replaced by dynamic PU
 * 
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;
    public static final String ADMIN_PU = "defaultPU";
    private static final PersistenceManager singleton = new PersistenceManager();
    private LinkedHashMap<String, EntityManagerFactory> hashFactory = new LinkedHashMap<String, EntityManagerFactory>();
  
    private ArrayList<IPersistenceUnitListener> arrlListener = new ArrayList();

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        return singleton;
    }

    public HibernateEntityManager getEntityManagerAdmin() {
        return getEntityManager(ADMIN_PU);
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
            fireEntityManagerCreated(namePU);
        }
        return emf;
    }

    private void fireEntityManagerCreated(String pu) {
        for(IPersistenceUnitListener ls : (List<IPersistenceUnitListener>)arrlListener.clone()){
            ls.persistenceUnitCreated(pu);
        }
    }

    public void addPersistenceUnitListener(IPersistenceUnitListener listener) {
        arrlListener.add(listener);
    }

    public void removePersistenceUnitListener(IPersistenceUnitListener listener) {
        arrlListener.remove(listener);
    }
}
