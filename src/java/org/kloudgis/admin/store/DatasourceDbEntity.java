/*
 * @author corneliu
 */
package org.kloudgis.admin.store;

import org.kloudgis.admin.pojo.Datasource;
import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

@Entity
@Table( name = "datasource" )
public class DatasourceDbEntity implements Serializable {

    @SequenceGenerator(name = "ds_seq_gen", sequenceName = "datasource_fid_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ds_seq_gen")
    private Long lID;
    @Column( length = 250 )
    @Index( name = "ds_name_ix" )
    private String strFileName;
    @Column
    private String strGeomName;
    @Column
    private Integer iGeomType;
    @Column
    private Integer iCRS;
    @Column
    private Integer iFeatureCount;
    @Column
    private Integer iLayerCount;
    @Column
    private Integer iColumnCount;
    @Column
    private Long lFileSize;
    @Column
    private Long lLastModified;
    @Column
    private Double dEnvelopeMinX;
    @Column
    private Double dEnvelopeMinY;
    @Column
    private Double dEnvelopeMaxX;
    @Column
    private Double dEnvelopeMaxY;
    @Column
    private Long lOwnerID;
    @Column
    @Type( type = "org.kloudgis.data.store.utils.StreamingBinaryArrayFileType" )
    private File file;
    @OneToMany( mappedBy = "dts" )
    private Set<SourceColumnsDbEntity> setCols;

    public Datasource toPojo() {
        Datasource pojo = new Datasource();
        pojo.lID = lID;
        pojo.strFileName = strFileName;
        pojo.strGeomName = strGeomName;
        pojo.iGeomType = iGeomType;
        pojo.iCRS = iCRS;
        pojo.iFeatureCount = iFeatureCount;
        pojo.iLayerCount = iLayerCount;
        pojo.iColumnCount = iColumnCount;
        pojo.lFileSize = lFileSize;
        pojo.lLastModified = lLastModified;
        pojo.dEnvelopeMinX = dEnvelopeMinX;
        pojo.dEnvelopeMinY = dEnvelopeMinY;
        pojo.dEnvelopeMaxX = dEnvelopeMaxX;
        pojo.dEnvelopeMaxY = dEnvelopeMaxY;
        pojo.lOwnerID = lOwnerID;
        pojo.filePath = file.getAbsolutePath();
        Set<Long> setColumns = new LinkedHashSet<Long>();
        if( setCols != null ) {
            for( SourceColumnsDbEntity cle : setCols ) {
                setColumns.add( cle.getID() );
            }
        }
        pojo.setCols = setColumns;
        return pojo;
    }

    public void setFileName( String strFileName ) {
        this.strFileName = strFileName;
    }

    public void setGeomName( String strGeomName ) {
        this.strGeomName = strGeomName;
    }

    public void setGeomType( int iGeomType ) {
        this.iGeomType = iGeomType;
    }

    public void setCRS( int iCRS ) {
        this.iCRS = iCRS;
    }

    public void setFeatureCount( int iFeatureCount ) {
        this.iFeatureCount = iFeatureCount;
    }

    public void setLayerCount( int iLayerCount ) {
        this.iLayerCount = iLayerCount;
    }

    public void setColumnCount( int iColumnCount ) {
        this.iColumnCount = iColumnCount;
    }

    public void setFileSize( long lFileSize ) {
        this.lFileSize = lFileSize;
    }

    public void setLastModified( long lLastModified ) {
        this.lLastModified = lLastModified;
    }

    public void setMinX( double dEnvelopeMinX ) {
        this.dEnvelopeMinX = dEnvelopeMinX;
    }

    public void setMinY( double dEnvelopeMinY ) {
        this.dEnvelopeMinY = dEnvelopeMinY;
    }

    public void setMaxX( double dEnvelopeMaxX ) {
        this.dEnvelopeMaxX = dEnvelopeMaxX;
    }

    public void setMaxY( double dEnvelopeMaxY ) {
        this.dEnvelopeMaxY = dEnvelopeMaxY;
    }

    public void setDataFile( File file ) {
        this.file = file;
    }

    public void setColumns( Set<SourceColumnsDbEntity> setCols ) {
        this.setCols = setCols;
    }

    public void setOwnerID( Long lOwnerID ) {
        this.lOwnerID = lOwnerID;
    }

    public Set<SourceColumnsDbEntity> getColumns() {
        return setCols;
    }

    public Long getID() {
        return lID;
    }

    public void setID( Long lID ) {
        this.lID = lID;
    }
}