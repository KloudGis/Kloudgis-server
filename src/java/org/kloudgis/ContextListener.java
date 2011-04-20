/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.kloudgis.data.store.PersistenceManager;

/**
 *
 * @author jeanfelixg
 */
public final class ContextListener implements ServletContextListener {


    public void contextInitialized(ServletContextEvent sce) {
        PersistenceManager.getInstance().getEntityManager(PersistenceManager.ADMIN_PU);
        PersistenceManager.getInstance().getEntityManager(PersistenceManager.DEFAULT_PU);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        PersistenceManager.getInstance().closeEntityManagerFactories();
    }
}

