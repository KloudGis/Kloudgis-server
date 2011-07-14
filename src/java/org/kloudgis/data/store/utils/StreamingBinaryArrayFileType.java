/**
 * I hope this will be helpful to some of you, feel free to use as is or modify, no strings attached.
 * This class is used to stream large binary files through a buffer between the app's file cache directory
 * (see System.getProperty("java.io.tmpdir") + "/" + appCacheDirName) and the JDBC database.
 * 
 * NOTE: The app's cache directory and its contents will be deleted on JVM exit or 
 * whenever the custom-made pruning process is run to prune old cache files (I use a Spring Scheduler task).
 * 
 * @author green_ears
 *
 */
package org.kloudgis.data.store.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import org.hibernate.HibernateException;
import org.hibernate.type.ImmutableType;

public class StreamingBinaryArrayFileType extends ImmutableType {

    private static final int BUFFER_SIZE_IN_BYTES = 4 * 1024;

    /* (non-Javadoc)
     * @see net.sf.hibernate.type.NullableType#fromStringValue (java.lang.String)
     */
    @Override
    public Object fromStringValue(String arg0) throws HibernateException {
        return new File(arg0);
    }

    /* (non-Javadoc)
     * @see net.sf.hibernate.type.NullableType#get(java.sql.Re sultSet, java.lang.String)
     */
    @Override
    public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
        File tempFile = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(rs.getBinaryStream(name));
            File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
            if (!tempDirectory.exists()) {
                tempDirectory.mkdir();
            }
            File appTempDirectory = new File(tempDirectory, "green_cache");
            if (!appTempDirectory.exists()) {
                appTempDirectory.mkdir();
                appTempDirectory.deleteOnExit();
            }
            tempFile = new File(appTempDirectory, new Date().getTime() + "" + Math.random());
            tempFile.deleteOnExit();
            bos = new BufferedOutputStream(new FileOutputStream(tempFile));
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE_IN_BYTES];
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new HibernateException("SELECT from LONGRAW to File failed.");
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return tempFile;
    }

    /* (non-Javadoc)
     * @see net.sf.hibernate.type.NullableType#set(java.sql.Pr eparedStatement, java.lang.Object, int)
     */
    @Override
    public void set(PreparedStatement pstmt, Object value, int index) throws HibernateException, SQLException {
        // Check the file length, it can't be greater than Integer.MAX_VALUE because we are going to cast the length
        // down to an int after
        if (((File) value).length() >= Integer.MAX_VALUE) {
            throw new HibernateException("File size exceeds " + Integer.MAX_VALUE + " in length.");
        }

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream((File) value), BUFFER_SIZE_IN_BYTES);
            pstmt.setBinaryStream(index, bis, (int) ((File) value).length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new HibernateException("INSERT/UPDATE from File to LONGRAW failed.");
        }
        /**
         * Apparently Hibernate will close this stream when it is done... It must remain open for this to work!
        finally {
        if(bis != null) {
        try {
        bis.close();
        } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        }
        }*/
    }

    /* (non-Javadoc)
     * @see net.sf.hibernate.type.NullableType#sqlType()
     */
    @Override
    public int sqlType() {
        return Types.LONGVARBINARY;
    }

    /* (non-Javadoc)
     * @see net.sf.hibernate.type.NullableType#toString(java.l ang.Object)
     */
    @Override
    public String toString(Object arg0) throws HibernateException {
        return ((File) arg0).toString();
    }

    /* (non-Javadoc)
     * @see net.sf.hibernate.type.Type#equals(java.lang.Object , java.lang.Object)
     */
    public boolean equals(Object arg0, Object arg1) throws HibernateException {
        if (arg0.getClass() == File.class && arg1.getClass() == File.class) {
            if (arg0 == null && arg1 == null) {
                return true;
            } else if (arg0 != null && arg1 != null) {
                File arg0File = (File) arg0, arg1File = (File) arg1;
                if (arg0File.getAbsolutePath().compareTo(arg1File.getAbsolutePath() ) == 0 && arg0File.length() == arg1File.length() ) {
                    return true;
                }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see net.sf.hibernate.type.Type#getName()
     */
    @Override
    public String getName() {
        return "streaming-binary-array-file";
    }

    /* (non-Javadoc)
     * @see net.sf.hibernate.type.Type#getReturnedClass()
     */
    @Override
    public Class getReturnedClass() {
        return File.class;
    }
}