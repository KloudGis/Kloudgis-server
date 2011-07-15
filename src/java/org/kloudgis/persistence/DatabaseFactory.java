/*
 * @author corneliu
 */
package org.kloudgis.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import org.kloudgis.MessageCode;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.data.featuretype.NoteFeatureType;
import org.kloudgis.data.featuretype.PlaceFeatureType;
import org.kloudgis.data.store.FeatureTypeClassDbEntity;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.NoteDbEntity;
import org.kloudgis.data.store.PathDbEntity;
import org.kloudgis.data.store.PoiDbEntity;
import org.kloudgis.data.store.ZoneDbEntity;

public class DatabaseFactory {

    public static final String USER = "kloudgis";//regular user
    public static final String PASSWORD = "kwadmin";//regular password

    public static void createIndexes(EntityManager em) {
        if (em != null) {
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX note_gist_ix ON note USING gist(geom)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            }
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX poi_gist_ix ON poi USING gist(geom)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            }
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX path_gist_ix ON path USING gist(geom)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            }
            try {
                em.getTransaction().begin();
                em.createNativeQuery("CREATE INDEX zone_gist_ix ON zone USING gist(geom)").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            }
        }
    }

    public static void loadModel(EntityManager em){
        if (em != null) {
            Object count = em.createQuery("select count(*) from FeatureTypeDbEntity").getSingleResult();
            if(count == null || ((Number)count).intValue() == 0){
                em.getTransaction().begin();
                //POI
                FeatureTypeDbEntity ft = new FeatureTypeDbEntity();
                ft.setName("poi");
                ft.setLabel("Place of interest");
                ft.setFeatureClassName(PoiDbEntity.class.getName());
                ft.setClientClassName("CoreKG.Poi");
                em.persist(ft);
                FeatureTypeClassDbEntity ftClass = new FeatureTypeClassDbEntity();
                ftClass.setFtClass(PlaceFeatureType.class.getName());
                ftClass.setFtId(ft.getId());
                em.persist(ftClass);
                //PATH
                ft = new FeatureTypeDbEntity();
                ft.setName("path");
                ft.setLabel("Path");
                ft.setFeatureClassName(PathDbEntity.class.getName());
                ft.setClientClassName("CoreKG.Path");
                em.persist(ft);
                ftClass = new FeatureTypeClassDbEntity();
                ftClass.setFtClass(PlaceFeatureType.class.getName());
                ftClass.setFtId(ft.getId());
                em.persist(ftClass);
                //ZONE
                ft = new FeatureTypeDbEntity();
                ft.setName("zone");
                ft.setLabel("Zone");
                ft.setFeatureClassName(ZoneDbEntity.class.getName());
                ft.setClientClassName("CoreKG.Zone");
                em.persist(ft);
                ftClass = new FeatureTypeClassDbEntity();
                ftClass.setFtClass(PlaceFeatureType.class.getName());
                ftClass.setFtId(ft.getId());
                em.persist(ftClass);
                //NOTE
                ft = new FeatureTypeDbEntity();
                ft.setName("note");
                ft.setLabel("Note");
                ft.setFeatureClassName(NoteDbEntity.class.getName());
                ft.setClientClassName("CoreKG.Note");
                em.persist(ft);
                ftClass = new FeatureTypeClassDbEntity();
                ftClass.setFtClass(NoteFeatureType.class.getName());
                ftClass.setFtId(ft.getId());
                em.persist(ftClass);
                em.getTransaction().commit();
            }
        }
     }

    public static Response createDB( String strURL, String strName ) throws ClassNotFoundException, SQLException {
        Class.forName( "org.postgresql.Driver" );
        Connection con = DriverManager.getConnection( "jdbc:postgresql://" + strURL + "/postgres", USER, PASSWORD );
        if( con != null ) {
            PreparedStatement pst = con.prepareStatement( "CREATE DATABASE " + strName + " template=postgis;" );
            pst.execute();
            pst.close();
            con.close();
        } else {
            return Response.serverError().entity( new Message( "Could not create database: " + strName, MessageCode.SEVERE ) ).build();
        }
        return Response.ok().build();
    }
}