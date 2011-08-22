/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.bean;

import java.util.ArrayList;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.kloudgis.data.pojo.TransactionId;



/**
 *
 * @author jeanfelixg
 */
@Path("/protected/transactions")
@Produces({"application/json"})
public class TransactionResourceBean {

    
    @POST
    public Response postTransactions(@QueryParam("sandbox") Long sandboxId){
        System.out.println("Transactions for " + sandboxId);
        return Response.ok(new ArrayList<TransactionId>()).build(); 
    }
}
