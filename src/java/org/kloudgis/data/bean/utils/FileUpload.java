/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author jeanfelixg
 */
public class FileUpload {

    public static File[] processUpload(HttpServletRequest request, File destFolder) throws Exception {
        ArrayList<File> arrlFiles = new ArrayList();
        destFolder.mkdirs();
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileItemFactory.setSizeThreshold(1 * 1024 * 1024); //1 MB
        fileItemFactory.setRepository(File.createTempFile("upload", "file").getParentFile());
        ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        List items = uploadHandler.parseRequest(request);
        Iterator itr = items.iterator();
        while (itr.hasNext()) {
            FileItem item = (FileItem) itr.next();
            if (item.isFormField()) {
            } else {
                File file = new File(destFolder, item.getName());
                item.write(file);
                System.out.println("File written:" + item.getName());
                arrlFiles.add(file);
            }
        }
        return arrlFiles.toArray(new File[arrlFiles.size()]);
    }
}
