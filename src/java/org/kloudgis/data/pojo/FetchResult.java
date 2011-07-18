/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.List;

/**
 *
 * @author sylvain
 */
public class FetchResult <T> {

    public List<T> features;
    public Long count;

    public FetchResult() {
    }

    public FetchResult(List inFeatures, Long inCount) {
        features= inFeatures;
        count=inCount;
    }

}
