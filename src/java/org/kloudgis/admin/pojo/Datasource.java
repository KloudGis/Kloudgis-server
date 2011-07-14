/*
 * @author corneliu
 */
package org.kloudgis.admin.pojo;

import java.util.Set;

public class Datasource {

    public String strFileName;
    public String strGeomName;
    public Integer iGeomType;
    public Integer iCRS;
    public Integer iFeatureCount;
    public Integer iLayerCount;
    public Integer iColumnCount;
    public Long lID;
    public Long lFileSize;
    public Long lLastModified;
    public Long lOwnerID;
    public Double dEnvelopeMinX;
    public Double dEnvelopeMinY;
    public Double dEnvelopeMaxX;
    public Double dEnvelopeMaxY;
    public String filePath;
    public Set<Long> setCols;
}