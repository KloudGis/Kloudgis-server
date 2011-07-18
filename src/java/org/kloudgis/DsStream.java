/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis;

import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.persistence.EntityManager;
import org.kloudgis.data.store.AbstractPlaceDbEntity;
import org.kloudgis.org.Feature;
import org.kloudgis.org.Streamable;
import org.kloudgis.persistence.PersistenceManager;

/**
 *  Stream to receive feature from the ogr_reader
 *  The feature are streamed one by one
 * @author jeanfelixg
 */
public class DsStream implements Streamable {

    private int  iGeomNotParsed;
    private EntityManager emg;
    private HashMap<String, String> mapAttrs;
    private int iSize = 0;
    private Long sandbox;

    public DsStream(EntityManager emg, HashMap<String, String> mapAttrs, Long sandbox) {
        this.emg = emg;
        this.mapAttrs = mapAttrs;
        this.sandbox = sandbox;      
        emg.getTransaction().begin();
    }

    @Override
    public void streamFeature(Feature fea) {
        //System.err.println("Process feature: count=" + iSize);
        ArrayList<AbstractPlaceDbEntity> arlEntities = null;
        try {
            arlEntities = DatasourceFactory.getDbEntities(fea.getGeometry());
        } catch (ParseException ex) {
            //cannot parse
        }
        if (arlEntities != null) {
            for (AbstractPlaceDbEntity ent : arlEntities) {              
                ent.setupFromFeature(fea, emg, mapAttrs);
                emg.persist(ent);
                iSize++;
                if (iSize % PersistenceManager.COMMIT_BLOCK == 0) {                 
                    emg.flush();
                    emg.clear();
                    PersistenceManager.getInstance().markAccess(sandbox);
                }
            }
        } else {
            System.err.println("Geometry invalid:" + fea.getGeometry());
            iGeomNotParsed++;
        }
    }

    @Override
    public void streamCompleted() {
        emg.getTransaction().commit();
       // System.err.println("Commit completed.");
    }
    
    public int getGeoNotParsed(){
        return iGeomNotParsed;
    }
    
    public int getCount(){
        return iSize;
    }
}
