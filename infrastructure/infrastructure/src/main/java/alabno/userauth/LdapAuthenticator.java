package alabno.userauth;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import alabno.database.MySqlDatabaseConnection;
import alabno.utils.FileUtils;

public class LdapAuthenticator implements Authenticator {

    private MySqlDatabaseConnection dbconn;
    
    public LdapAuthenticator(MySqlDatabaseConnection dbconn) {
        this.dbconn = dbconn;
    }
    
    @Override
    public UserAccount authenticate(String username, String password) {
        if (username == null || password == null || password.isEmpty()) {
            return null;
        }
        username = username.trim().replace(",", "");
        if (username.isEmpty()) {
            return null;
        }
        
        String fullName = null;
        String email = null;
        String userType = null;

        DirContext ctx = null;
        try {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, 
                "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, loadConfigUrl());

            // Authenticate as S. User and password "mysecret"
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, loadConfigPrincipal().replace("?", username));
            env.put(Context.SECURITY_CREDENTIALS, password);

            // Create the initial context
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            return null;
        } finally {
            if (ctx != null)
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
        }
        return new UserAccount(username, "gihajoihsaer", "fiaher", UserType.ADMIN);
    }

    private String loadConfigUrl() {
        return loadConfigString("ldapurl");
    }

    private String loadConfigPrincipal() {
        return loadConfigString("ldapprincipal");
    }
    
    private String loadConfigString(String key) {
        String sql = "SELECT * FROM `configuration` WHERE `key` = '"+key+"'";
        List<Map<String, String>> results = dbconn.retrieveQueryString(sql);
        String configUrl = null;
        for (Map<String, String> m : results) {
            configUrl = m.get("value");
        }
        return configUrl;
    }

    
    /**
     * Simple testing utility main
     */
    public static void main(String[] args) {
        FileUtils.initWorkDir();
        
        String username = null;
        String password = null;

        Scanner scanner = new Scanner(System.in);
        
        MySqlDatabaseConnection dbconn = new MySqlDatabaseConnection();

        LdapAuthenticator authenticator = new LdapAuthenticator(dbconn);
        
        System.out.println(authenticator.loadConfigUrl());
        System.out.println(authenticator.loadConfigPrincipal());

        while (true) {

            System.out.print("\n\nUsername: ");
            username = scanner.nextLine();
            System.out.print("Password: ");
            password = scanner.nextLine();

            System.out.println(authenticator.authenticate(username, password));
        }

    }

}
