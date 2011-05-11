/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.persistence;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author jeanfelixg
 */
public class FactoryWrapper {
    
    private EntityManagerFactory emf;
    
    private Date last_access;
    
    public FactoryWrapper(EntityManagerFactory emf){
        this.emf = emf;
        last_access = Calendar.getInstance().getTime();
    }
    
    public EntityManagerFactory getEmf(){
        return this.emf;
    }
    
    public Date getLastAccess(){
        return last_access;
    }
    
    public void markAccess(){
        this.last_access = Calendar.getInstance().getTime();
    }
}
