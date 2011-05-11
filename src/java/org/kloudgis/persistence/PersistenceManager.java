/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.persistence;

import com.sun.jersey.api.NotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.ws.http.HTTPException;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.admin.store.SandboxDbEntity;

/**
 * SA: **** Temporary : To be replaced by dynamic PU
 * 
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;
    private static final String DEFAULT_PU = "sandboxPU";
    private static String ADMIN_PU = "adminPU";
    private static final PersistenceManager singleton = new PersistenceManager();
    private EntityManagerFactory adminFactory;
    //to remove
    private EntityManagerFactory defaultFactory;
    //TODO: Add a validation thread to kill emf if not used for a while
    private LinkedHashMap<String, FactoryWrapper> hashSandboxesFactory = new LinkedHashMap<String, FactoryWrapper>();

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        return singleton;
    }

    //to remove... useless
    public HibernateEntityManager getEntityManagerDefault() {
        return getEntityManager(DEFAULT_PU);
    }

    public HibernateEntityManager getAdminEntityManager() {
        return getEntityManager(ADMIN_PU);
    }

    public HibernateEntityManager getEntityManager(String namePU) {
        return (HibernateEntityManager) getEntityManagerFactory(namePU).createEntityManager();
    }

    private EntityManagerFactory getEntityManagerFactory(String namePU) {
        if (namePU.equals(ADMIN_PU)) {
            if (adminFactory != null) {
                return adminFactory;
            } else {
                return adminFactory = createEntityManagerFactory(ADMIN_PU);
            }
        }
        //to remove
        if (namePU.equals(DEFAULT_PU)) {
            if (defaultFactory != null) {
                return defaultFactory;
            } else {
                return defaultFactory = createEntityManagerFactory(DEFAULT_PU);
            }
        }
        return null;
    }

    public void closeEntityManagerFactories() {
        if (adminFactory != null) {
            adminFactory.close();
        }
        //to remove
        if (defaultFactory != null) {
            defaultFactory.close();
        }
        for (FactoryWrapper wrap : hashSandboxesFactory.values()) {
            if (wrap.getEmf() != null) {
                wrap.getEmf().close();
            }
        }
        hashSandboxesFactory.clear();
    }

    protected EntityManagerFactory createEntityManagerFactory(String namePU) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(namePU);
        return emf;
    }

    private EntityManagerFactory getSandboxEntityManagerFactory(String key, String url) {
        if (hashSandboxesFactory.get(key) == null) {
            return createSandboxManagerFactory(key, url);
        }
        hashSandboxesFactory.get(key).markAccess();
        return hashSandboxesFactory.get(key).getEmf();
    }

    protected EntityManagerFactory createSandboxManagerFactory(String key, String url) {
        Map prop = new HashMap();
        prop.put("hibernate.connection.url", url);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(DEFAULT_PU, prop);
        if (emf != null) {
            hashSandboxesFactory.put(key, new FactoryWrapper(emf));
        }
        return emf;
    }

    public EntityManager getEntityManagerBySandboxId(Long projectId) {
        EntityManager emAdmin = getEntityManager(ADMIN_PU);
        SandboxDbEntity sandbox = emAdmin.find(SandboxDbEntity.class, projectId);
        if (sandbox != null) {
            String key = sandbox.getUniqueKey();
            String url = sandbox.getConnectionUrl();
            if (key != null && key.length() > 0 && url != null && url.length() > 0) {
                EntityManagerFactory emf = getSandboxEntityManagerFactory(key, url);
                if (emf != null) {
                    EntityManager em = emf.createEntityManager();
                    if (em != null) {
                        return em;
                    }
                }
            }else{
                throw new NotFoundException("Missing project key or url");
            }
        }
        return null;
    }
}
