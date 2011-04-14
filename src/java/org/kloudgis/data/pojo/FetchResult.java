/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sylvain
 */
@XmlRootElement
public class FetchResult {

    private List<AbstractFeature> lstFeatures;
    private Long count;

    public FetchResult() {
    }

    public FetchResult(List<AbstractFeature> lFeatures, Long count) {
        setFeatures(lFeatures);
        setCount(count);
    }

    /**
     * @return the lFeatures
     */
    @XmlElement
    public List<AbstractFeature> getFeatures() {
        return lstFeatures;
    }

    /**
     * @param lstFeatures the lFeatures to set
     */
    public final void setFeatures(List<AbstractFeature> lFeatures) {
        this.lstFeatures = lFeatures;
    }

    /**
     * @return the count
     */
    @XmlElement
    public Long getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public final void setCount(Long count) {
        this.count = count;
    }

}
