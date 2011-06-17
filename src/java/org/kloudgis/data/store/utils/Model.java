/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store.utils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.kloudgis.data.featuretype.AbstractFeatureType;
import org.kloudgis.data.featuretype.DefaultFeatureType;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.FeatureTypeClassDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class Model {

    public static AbstractFeatureType getFeatureType(Long id, EntityManager em) {
        FeatureTypeDbEntity ftDb = em.find(FeatureTypeDbEntity.class, id);
        if (ftDb != null) {
            Query query = em.createQuery("from FeatureTypeClassDbEntity where ft_id=:id", FeatureTypeClassDbEntity.class).setParameter("id", id);
            Class clazz;
            try {
                FeatureTypeClassDbEntity fte = (FeatureTypeClassDbEntity) query.getSingleResult();
                clazz = fte.getFtClass();
                if (clazz == null) {
                    throw new NoResultException();
                }
            } catch (NoResultException e) {
                clazz = DefaultFeatureType.class;
            }
            try {
                return (AbstractFeatureType) clazz.getConstructor(FeatureTypeDbEntity.class).newInstance(ftDb);
            } catch (Exception ex) {
                ex.printStackTrace();
            } 
        }
        return null;
    }
}
