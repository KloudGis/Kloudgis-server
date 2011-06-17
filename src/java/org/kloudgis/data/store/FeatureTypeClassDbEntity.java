/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "featuretype_class")
public class FeatureTypeClassDbEntity implements Serializable {
    
    @SequenceGenerator(name = "ftc_seq_gen", sequenceName = "ftc_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ftc_seq_gen")
    private Long id;
    @Column
    private Long ft_id;
    @Column
    private String ft_class;    
    
    
    public Class getFtClass() {
        try {
            return Class.forName(ft_class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public void setFtId(Long id){
        ft_id = id;
    }
    
    public void setFtClass(String className){
        ft_class = className;
    }

}
