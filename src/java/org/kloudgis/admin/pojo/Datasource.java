/*
 * @author corneliu
 */
package org.kloudgis.admin.pojo;

import java.util.Set;

public class Datasource {

    public String strFileName;
    public String strGeomName;
    public String strGeomType;
    public Integer iCRS;
    public Integer iFeatureCount;
    public Integer iColumnCount;
    public Long lID;
    public Long lOwnerID;
    public Double dEnvelopeMinX;
    public Double dEnvelopeMinY;
    public Double dEnvelopeMaxX;
    public Double dEnvelopeMaxY;
    public Set<Long> setCols;
    public String strLayerName;
}