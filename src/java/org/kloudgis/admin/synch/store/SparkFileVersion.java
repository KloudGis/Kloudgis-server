/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.synch.store;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.kloudgis.store.FileVersion;


/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name="spark_version")
public class SparkFileVersion extends FileVersion{
   
}
