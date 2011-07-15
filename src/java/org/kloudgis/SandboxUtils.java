/*
 * @author corneliu
 */
package org.kloudgis;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.data.pojo.Member;
import org.kloudgis.data.store.MemberDbEntity;
import org.kloudgis.persistence.PersistenceManager;

public class SandboxUtils {

    /**
     * get the member from a target sandbox.
     * @param usr       the user to find in the member list
     * @param lSandboxId    the target sandbox id
     * @return 
     */
    public static Member getMember( UserDbEntity usr, long lSandboxId ) {
        if( usr != null ) {
            EntityManager emSand = PersistenceManager.getInstance().getEntityManagerBySandboxId( lSandboxId );
            if( emSand != null ) {
                Query query = emSand.createQuery( "from MemberDbEntity where user_id=:u" ).setParameter( "u", usr.getId() );
                List<MemberDbEntity> lstM = query.getResultList();
                Member pojo = null;
                if( lstM.size() > 0 ) {
                    pojo = lstM.get( 0 ).toPojo( emSand );
                }
                emSand.close();
                return pojo;
            }
        }
        return null;
    }

    public static boolean isMember( UserDbEntity usr, long lSandboxId ) {
        return getMember( usr, lSandboxId ) != null;
    }
}