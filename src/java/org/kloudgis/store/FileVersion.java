/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.store;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

/**
 *
 * @author jeanfelixg
 */
@MappedSuperclass
public class FileVersion implements Serializable{
    @SequenceGenerator(name = "file_version_seq_gen", sequenceName = "file_version_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "file_version_seq_gen")
    private Long id;
    @Column
    private Long user_id;
    @Column
    private Timestamp file_time;
    @Column
    private Short status = 1;
    @Index (name="path_index")
    @Column
    private String file_path;
    
    @Type( type = "org.kloudgis.data.store.utils.StreamingBinaryArrayFileType" )
    private File file;
    
    
    public Long getId() {
        return id;
    }

    
    public File getFile(){
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    
    public String getPath() {
        return file_path;
    }
    
    
    public void setPath(String path) {
        this.file_path = path;
    }
    
    public Long getTime() {
        return file_time.getTime();
    }

    
    public void setTime(Long time) {
        this.file_time = new Timestamp(time);
    }
    
    
    public void setDisabled() {
        status = 0;
    }
      
    public void setEnabled() {
        status = 1;
    }
}
