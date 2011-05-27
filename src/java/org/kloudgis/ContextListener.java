/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.kloudgis.persistence.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
public final class ContextListener implements ServletContextListener {


    public void contextInitialized(ServletContextEvent sce) {
        PersistenceManager.getInstance().getAdminEntityManager().close();
//        em.getTransaction().begin();
//        BaseLayerDbEntity base = new BaseLayerDbEntity();
//        base.setName("google");
//        em.persist(base);
//        BaseLayerModeDbEntity mode = new BaseLayerModeDbEntity();
//        mode.setName("plan");
//        em.persist(mode);
//        base.addBaseLayerMode(mode);
//        SandboxDbEntity s = new SandboxDbEntity();
//        s.setName("test");
//        s.setBaseLayerMode(mode);
//        em.persist(s);
//        em.getTransaction().commit();
//        em.close();
        PersistenceManager.getInstance().getEntityManagerDefault().close();
        //init spatial indexes
        
    }

    public void contextDestroyed(ServletContextEvent sce) {
        PersistenceManager.getInstance().closeEntityManagerFactories();
    }
}

