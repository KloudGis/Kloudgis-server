/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.admin.synch.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.admin.synch.store.SparkFileVersion;
import org.kloudgis.data.bean.utils.FileUpload;
import org.kloudgis.persistence.PersistenceManager;
import org.kloudgis.pojo.FileVersion;

/**
 *
 * @author jeanfelixg
 */
@Path("/protected/synch/sparc")
@Produces({"application/json"})
public class SparcVersionBean {

    @GET
    @Path("files_date")
    public Response getLatestFilesDate() {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        Query query = em.getSession().createSQLQuery("select v.id, v.file_path, v.file_time from sparc_version v LEFT JOIN sparc_version v2 on (v.status = 1 AND v.file_path = v2.file_path AND v.file_time < v2.file_time) WHERE v2.id is null");
        List<Object[]> files = query.list();
        ArrayList<FileVersion> arrlPojo = new ArrayList();
        for (Object[] oFile : files) {
            org.kloudgis.pojo.FileVersion pojo = new org.kloudgis.pojo.FileVersion();
            pojo.id = ((Number)oFile[0]).longValue();
            pojo.path = (String) oFile[1];
            pojo.time = ((Timestamp) oFile[2]).getTime();
            arrlPojo.add(pojo);
        }
        em.close();
        return Response.ok(arrlPojo).build();
    }
    
    
    
    @GET
    @Path("{id}")
    public Response getFile(@PathParam("id") Long id) throws FileNotFoundException {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        SparkFileVersion spark = em.find(SparkFileVersion.class,id);
        FileInputStream in = new FileInputStream(spark.getFile());
        em.close();
        return Response.ok(in,MediaType.APPLICATION_OCTET_STREAM).build();
    }
    
    @POST
    public Response postFile(@Context HttpServletRequest req, @QueryParam("path") String path, @QueryParam("time") Long time) {
        File dest = new File("/tmp/spark_files");
        HibernateEntityManager em = null;
        try {
            File[] files = FileUpload.processUpload(req, dest);
            if(files.length > 0){
                File f = files[0];
                em = PersistenceManager.getInstance().getAdminEntityManager();
                Transaction tx = em.getSession().beginTransaction();
                SparkFileVersion newFile = new SparkFileVersion();
                newFile.setPath(path);
                newFile.setTime(time);
                newFile.setFile(f);
                newFile.setEnabled();
                em.persist(newFile);
                tx.commit();
                em.close();
                org.kloudgis.pojo.FileVersion version = new FileVersion();
                version.id = newFile.getId();
                version.path = newFile.getPath();
                version.time = newFile.getTime();               
                return Response.ok(version).build();
            }else{
                throw new FileNotFoundException("Could'nt parse the file from the request");
            }
            //insert in the database...
        } catch (Exception ex) {
            if(em != null){
                em.close();
            }
            return Response.serverError().build();
        }
    }
    
    @PUT
    @Path("path/{path}")
    public Response activateFile(@PathParam("path") String path) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        Criteria criteria = em.getSession().createCriteria(SparkFileVersion.class);
        criteria.add(Restrictions.eq("file_path", path));
        List<SparkFileVersion> list = criteria.list();
        Transaction tx = em.getSession().beginTransaction();
        int iCpt = 0;
        for(SparkFileVersion file : list){
            file.setEnabled();
            iCpt++;
        }
        tx.commit();
        em.close();
        return Response.ok(iCpt + "").build();
    }
    
    @DELETE
    @Path("path/{path}")
    public Response desactivateFile(@PathParam("path") String path) {
        HibernateEntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        Criteria criteria = em.getSession().createCriteria(SparkFileVersion.class);
        criteria.add(Restrictions.eq("file_path", path));
        List<SparkFileVersion> list = criteria.list();
        Transaction tx = em.getSession().beginTransaction();
        int iCpt = 0;
        for(SparkFileVersion file : list){
            file.setDisabled();
            iCpt++;
        }
        tx.commit();
        em.close();
        return Response.ok(iCpt + "").build();
    }
}
