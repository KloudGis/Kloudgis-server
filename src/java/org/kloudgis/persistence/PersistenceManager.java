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
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.LoginFactory;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.admin.store.UserDbEntity;

/**
 *
 */
public class PersistenceManager {

    public static final boolean DEBUG = true;
    private static final String DEFAULT_PU = "sandboxPU";
    private static String ADMIN_PU = "adminPU";
    private static PersistenceManager instance;
    public static int COMMIT_BLOCK = 30;
    private EntityManagerFactory adminFactory;
    protected LinkedHashMap<Long, FactoryWrapper> hashSandboxesFactory = new LinkedHashMap<Long, FactoryWrapper>();
    private CheckEmfSandboxThread validThread;

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    public HibernateEntityManager getAdminEntityManager() {
        if (adminFactory != null) {
            return (HibernateEntityManager) adminFactory.createEntityManager();
        } else {
            adminFactory = createEntityManagerFactory(ADMIN_PU);
            HibernateEntityManager emAdmin = (HibernateEntityManager) adminFactory.createEntityManager();
            Criteria crit = emAdmin.getSession().createCriteria(UserDbEntity.class);
            Long lCount = ((Number) crit.setProjection(Projections.rowCount()).uniqueResult()).longValue();
            if (lCount.longValue() == 0) {
                SignupUser usr = new SignupUser();
                usr.user = "admin@kloudgis.com";
                usr.pwd = LoginFactory.hashString("kwadmin", "SHA-256");
                LoginFactory.register(usr, "en", UserDbEntity.ROLE_ADM);
            }
            return (HibernateEntityManager) adminFactory.createEntityManager();
        }
    }

    public void startEmfValidation() {
        validThread = new CheckEmfSandboxThread();
        validThread.start();
    }

    public void closeEntityManagerFactories() {
        if (adminFactory != null) {
            adminFactory.close();
            adminFactory = null;
        }
        for (FactoryWrapper wrap : hashSandboxesFactory.values()) {
            if (wrap.getEmf() != null) {
                wrap.getEmf().close();
            }
        }
        hashSandboxesFactory.clear();
        validThread.stopChecking();
        validThread = null;
    }

    protected EntityManagerFactory createEntityManagerFactory(String namePU) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(namePU);
        return emf;
    }

    private EntityManagerFactory getSandboxEntityManagerFactory(Long key, String url) {
        if (hashSandboxesFactory.get(key) == null) {
            return createSandboxManagerFactory(key, url);
        }
        hashSandboxesFactory.get(key).markAccess();
        return hashSandboxesFactory.get(key).getEmf();
    }

    public void markAccess(Long key) {
        if (hashSandboxesFactory.get(key) != null) {
            hashSandboxesFactory.get(key).markAccess();
        }
    }

    protected synchronized EntityManagerFactory createSandboxManagerFactory(Long key, String url) {
        System.out.println("create emf for " + url);
        Map prop = new HashMap();
        prop.put("hibernate.connection.url", "jdbc:postgresql_postGIS://" + url);
        prop.put("hibernate.connection.username", DatabaseFactory.USER_PU);
        prop.put("hibernate.connection.password", DatabaseFactory.PASSWORD_PU);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(DEFAULT_PU, prop);
        if (emf != null) {
            //do not duplicate
            if (hashSandboxesFactory.get(key) != null) {
                hashSandboxesFactory.get(key).getEmf().close();
            }
            hashSandboxesFactory.put(key, new FactoryWrapper(emf));
        }
        return emf;
    }

    public EntityManager getEntityManagerBySandboxId(Long projectId) {
        EntityManager emAdmin = getAdminEntityManager();
        try {
            SandboxDbEntity sandbox = emAdmin.find(SandboxDbEntity.class, projectId);
            emAdmin.close();
            if (sandbox != null) {
                Long key = sandbox.getId();
                String url = sandbox.getConnectionUrl() + "/" + sandbox.getName();
                if (key != null && url != null && url.length() > 0) {
                    EntityManagerFactory emf = getSandboxEntityManagerFactory(key, url);
                    if (emf != null) {
                        EntityManager em = emf.createEntityManager();
                        if (em != null) {
                            return em;
                        }
                    }
                }
            }
        } catch (EntityNotFoundException e) {
            System.out.println("sandbox not found:" + projectId);
        }
        return null;
    }
}