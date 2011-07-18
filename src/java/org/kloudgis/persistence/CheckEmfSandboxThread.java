/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.persistence;

import java.util.Calendar;
import java.util.Map;

/**
 *
 * @author jeanfelixg
 */
public class CheckEmfSandboxThread extends Thread {

    public boolean isStoped = false;
    
    public CheckEmfSandboxThread() {
        super("Emf validator");
    }
    
    public void stopChecking() {
        this.isStoped = true;
        this.interrupt();
    }

    @Override
    public void run() {

        while (!isStoped) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
            }
            try {
                System.out.println("Testing emf. "  +PersistenceManager.getInstance().hashSandboxesFactory.size() + " emf.");
                Long time = Calendar.getInstance().getTimeInMillis();
                Map<Long, FactoryWrapper> map = PersistenceManager.getInstance().hashSandboxesFactory;
                for (Long id : map.keySet()) {
                    FactoryWrapper fw = map.get(id);
                    if (time - fw.getLastAccess() > 60000) {
                        System.out.println("closing emf of sandbox" + id + " . Was idle for " + (time - fw.getLastAccess()) + " ms");
                        fw.getEmf().close();
                         map.remove(id);
                    }
                }
            } catch (Exception e) {
                System.out.println("Emf check error:"  + e.getMessage());
            }
        }
    }
}
