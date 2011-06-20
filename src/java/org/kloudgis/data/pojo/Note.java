/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.sql.Date;
import org.kloudgis.data.store.NoteDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class Note extends AbstractFeature{

    public String   title;   
    public String   description;
    public String   author;
    public Coordinate coordinate;
    public Date     date;
    
    @Override
    public NoteDbEntity toDbEntity() {
        NoteDbEntity db= new NoteDbEntity();
        db.fromPojo(this);
        return db;
    }
    
}
