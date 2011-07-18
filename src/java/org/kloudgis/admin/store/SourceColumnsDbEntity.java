/*
 * @author corneliu
 */
package org.kloudgis.admin.store;

import org.kloudgis.admin.pojo.SourceColumns;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table( name = "ds_columns" )
public class SourceColumnsDbEntity implements Serializable {

    @SequenceGenerator(name = "cols_seq_gen", sequenceName = "columns_fid_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cols_seq_gen")
    private Long lID;
    @Column
    private String strName;
    @Column
    private String strType;
    @ManyToOne
    private DatasourceDbEntity dts;

    public SourceColumns toPojo() {
        SourceColumns pojo = new SourceColumns();
        pojo.lID = lID;
        pojo.strName = strName;
        pojo.strType = strType;
        pojo.lDatasetID = dts == null ? ( long )-1 : dts.getID();
        return pojo;
    }

    public void setName( String strName ) {
        this.strName = strName;
    }

    public void setType( String strType ) {
        this.strType = strType;
    }

    public void setDatasource( DatasourceDbEntity dts ) {
        this.dts = dts;
    }

    public Long getID() {
        return lID;
    }

    public void setID( Long lID ) {
        this.lID = lID;
    }
}