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
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.admin.store.SandboxDbEntity;
import org.kloudgis.data.featuretype.NoteFeatureType;
import org.kloudgis.data.featuretype.PlaceFeatureType;
import org.kloudgis.data.store.FeatureTypeClassDbEntity;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.data.store.PathDbEntity;
import org.kloudgis.data.store.PoiDbEntity;
import org.kloudgis.data.store.ZoneDbEntity;

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
    //TODO: Add a validation thread to kill emf if not used for a while
    private LinkedHashMap<String, FactoryWrapper> hashSandboxesFactory = new LinkedHashMap<String, FactoryWrapper>();

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {
        return singleton;
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
        return null;
    }

    public void closeEntityManagerFactories() {
        if (adminFactory != null) {
            adminFactory.close();
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

    protected synchronized EntityManagerFactory createSandboxManagerFactory(String key, String url) {
        System.out.println("create emf for " + url);
        Map prop = new HashMap();
        prop.put("hibernate.connection.url", "jdbc:postgresql_postGIS://" + url);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(DEFAULT_PU, prop);
        if (emf != null) {
            //do not duplicate
            if(hashSandboxesFactory.get(key) != null){
               emf.close();
               return hashSandboxesFactory.get(key).getEmf();
            }else{
                hashSandboxesFactory.put(key, new FactoryWrapper(emf));
                createIndexes(emf);
                loadDefaultValues(emf);
            }
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
            } else {
                throw new NotFoundException("Missing project key or url");
            }
        }
        return null;
    }
    
    //put the next to methods into the create project bean to do it only once.

    private void createIndexes(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        if (em != null) {
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX note_gist_ix ON note USING gist(geom)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
            } 
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX poi_gist_ix ON poi USING gist(geom)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
            }
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX path_gist_ix ON path USING gist(geom)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
            }
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX zone_gist_ix ON zone USING gist(geom)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
            }
            em.close();
        }
    }
    
     private void loadDefaultValues(EntityManagerFactory emf){
          EntityManager em = emf.createEntityManager();
        if (em != null) {
            Object count = em.createQuery("select count(*) from " + FeatureTypeDbEntity.class.getSimpleName()).getSingleResult();
            if(count == null || ((Number)count).intValue() == 0){
                em.getTransaction().begin();
                //POI
                FeatureTypeDbEntity ft = new FeatureTypeDbEntity();
                ft.setName("poi");
                ft.setLabel("Place of interest");
                ft.setFeatureClassName(PoiDbEntity.class.getName());
                ft.setClientClassName("CoreKG.Poi");
                em.persist(ft);
                FeatureTypeClassDbEntity ftClass = new FeatureTypeClassDbEntity();
                ftClass.setFtClass(PlaceFeatureType.class.getName());
                ftClass.setFtId(ft.getId());
                em.persist(ftClass);
                //PATH
                ft = new FeatureTypeDbEntity();
                ft.setName("path");
                ft.setLabel("Path");
                ft.setFeatureClassName(PathDbEntity.class.getName());
                ft.setClientClassName("CoreKG.Path");
                em.persist(ft);
                ftClass = new FeatureTypeClassDbEntity();
                ftClass.setFtClass(PlaceFeatureType.class.getName());
                ftClass.setFtId(ft.getId());
                em.persist(ftClass);
                //ZONE
                ft = new FeatureTypeDbEntity();
                ft.setName("zone");
                ft.setLabel("Zone");
                ft.setFeatureClassName(ZoneDbEntity.class.getName());
                ft.setClientClassName("CoreKG.Zone");
                em.persist(ft);
                ftClass = new FeatureTypeClassDbEntity();
                ftClass.setFtClass(PlaceFeatureType.class.getName());
                ftClass.setFtId(ft.getId());
                em.persist(ftClass);
                //NOTE
                ft = new FeatureTypeDbEntity();
                ft.setName("note");
                ft.setLabel("Note");
                ft.setFeatureClassName(NoteDbEntity.class.getName());
                ft.setClientClassName("CoreKG.Note");
                em.persist(ft);
                ftClass = new FeatureTypeClassDbEntity();
                ftClass.setFtClass(NoteFeatureType.class.getName());
                ftClass.setFtId(ft.getId());
                em.persist(ftClass);
                em.getTransaction().commit();
            }
            em.close();
        }
     }
}
