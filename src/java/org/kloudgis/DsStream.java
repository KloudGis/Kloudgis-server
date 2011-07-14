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

/**
 *
 * @author jeanfelixg
 */
public class DsStream implements Streamable {

    private StringBuilder stbGeomNotParsed = new StringBuilder();
    private EntityManager emg;
    private HashMap<String, String> mapAttrs;
    private int iSize = 0;

    public DsStream(EntityManager emg, HashMap<String, String> mapAttrs) {
        this.emg = emg;
        this.mapAttrs = mapAttrs;
        emg.getTransaction().begin();
    }

    @Override
    public void streamFeature(Feature fea) {
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
                if (iSize % 20 == 0) {
                    emg.flush();
                    emg.clear();
                }
            }
        } else {
            stbGeomNotParsed.append(fea.getGeometry()).append("\n");
        }
    }

    @Override
    public void streamCompleted() {
        emg.getTransaction().commit();
    }
    
    public String getGeoNotParsed(){
        return stbGeomNotParsed.toString();
    }
    
    public int getCount(){
        return iSize;
    }
}
