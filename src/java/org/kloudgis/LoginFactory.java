
package org.kloudgis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.kloudgis.admin.pojo.Message;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.store.UserDbEntity;
import org.kloudgis.admin.store.UserRoleDbEntity;
import org.kloudgis.persistence.PersistenceManager;

public class LoginFactory {

    /**
     * Add a new user in the list
     * @param user_try      the parameters for the new user
     * @param locale        language  (fr, en, ...)
     * @param strRole       the role to set the user (admin, user, ...)   
     * @return result message
     */
    public static Message register(SignupUser user_try, String locale, String strRole) {
        if (user_try == null || user_try.user == null || !user_try.user.contains("@")) {
            Message message = new Message();
            message.message = "rejected";
            if (locale != null && locale.equals("fr")) {
                message.message_loc = "Refuser - Invalide";
            } else {
                message.message_loc = "Refused - Invalid";
            }
            return message;
        } else {
            UserDbEntity user = new UserDbEntity();
            user.setEmail(user_try.user);
            user.setFullName(user_try.name);
            user.setCompagny(user_try.compagny);
            user.setLocation(user_try.location);
            user.setSalt(new String(new char[]{randChar(), randChar(), randChar(), randChar(), randChar(), randChar(), randChar(), randChar()}));
            user.setPassword(encryptPassword(user_try.pwd, user.getSalt()));
            user.setActive(false);
            if (!isUnique(user_try.user)) {
                Message message = new Message();
                message.message = "rejected";
                if (locale != null && locale.equals("fr")) {
                    message.message_loc = "Refuser - Déjà pris";
                } else {
                    message.message_loc = "Refused - In use";
                }
                return message;
            } else {
                EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
                em.getTransaction().begin();
                UserRoleDbEntity role = new UserRoleDbEntity();
                role.setRoleName(strRole);
                user.addRole(role);
                em.persist(role);
                em.persist(user);
                em.getTransaction().commit();
                em.close();
                Message message = new Message();
                message.message = "success";
                if (locale != null && locale.equals("fr")) {
                    message.message_loc = "Succès";
                } else {
                    message.message_loc = "Success";
                }
                return message;
            }
        }
    }

    /**
     * Test if a email is unique
     * @param email
     * @return True if not yet used
     */
    public static boolean isUnique(String email) {
        EntityManager em = PersistenceManager.getInstance().getAdminEntityManager();
        Query query = em.createQuery("from UserDbEntity where email=:em", UserDbEntity.class);
        query.setParameter("em", email);
        List<UserDbEntity> lstU = query.getResultList();
        em.close();
        return lstU.isEmpty();
    }

    /**
     * encrypt the password with a salt
     * @param hashed_password   password already hashed
     * @param salt              the user salt 
     * @return encrypted password
     */
    public static String encryptPassword(String hashed_password, String salt) {
        String string_to_hash = hashed_password + "@Kloudgis.org#" + salt;
        return hashString(string_to_hash, "SHA-256");
    }

    private static char randChar() {
        int rnd = (int) (Math.random() * 52); // or use Random or whatever
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);

    }

    /**
     * Hash a string with the provided algorithm
     * @param  message  the string to hash
     * @param algo      the algo to use (SHA-256, ...)
     * @return  the string hashed
     */
    public static String hashString(String message, String algo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            md.update(message.getBytes());
            byte[] byteData = md.digest();
            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.getStackTrace();
        }
        return null;
    }
}