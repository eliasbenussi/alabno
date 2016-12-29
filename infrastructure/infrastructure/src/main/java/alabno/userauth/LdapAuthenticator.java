package alabno.userauth;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import alabno.database.MySqlDatabaseConnection;
import alabno.useraccount.AccountManager;
import alabno.useraccount.DatabaseAccountManager;
import alabno.useraccount.UserAccount;
import alabno.useraccount.UserType;
import alabno.utils.FileUtils;

public class LdapAuthenticator implements Authenticator {

    private MySqlDatabaseConnection dbconn;
    private String configUrl;
    private String configPrincipal;
    private String configDomainBase;
    private AccountManager accountManager;
    private static String[] returnAttributes = {"employeeType", "mail", "displayName"};

    public LdapAuthenticator(MySqlDatabaseConnection dbconn, AccountManager accountManager) {
        this.dbconn = dbconn;
        this.accountManager = accountManager;
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
        UserType defaultUserType = null;

        String principalFilter = loadConfigPrincipal().replace("?", username);

        DirContext ctx = null;
        try {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, loadConfigUrl());

            // Authenticate as S. User and password "mysecret"
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, principalFilter);
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put(Context.REFERRAL, "follow");

            // Create the initial context
            ctx = new InitialDirContext(env);

            // Query Directory for more information
            SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchCtls.setReturningAttributes(returnAttributes);
            NamingEnumeration<SearchResult> result = null;

            result = ctx.search(loadDomainBase(), "sAMAccountName="+username, searchCtls);

            while (result.hasMore()) {
                SearchResult r = result.next();
                Attributes attributes = r.getAttributes();
                Attribute fullNameAttribute = attributes.get("displayname");
                fullName = (String) fullNameAttribute.get();
                Attribute emailAttribute = attributes.get("mail");
                email = (String) emailAttribute.get();
                Attribute emTypeAttribute = attributes.get("employeetype");
                userType = (String) emTypeAttribute.get();

            }
            
            if (userType.contains("student")) {
                defaultUserType = UserType.STUDENT;
            } else {
                defaultUserType = UserType.PROFESSOR;
            }
            
            // Database lookup
            return accountManager.getOrCreateIfNecessary(username, fullName, email, defaultUserType);
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        } finally {
            if (ctx != null)
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
        }

    }

    private String loadConfigUrl() {
        if (this.configUrl == null) {
            this.configUrl = loadConfigString("ldapurl");
        }
        return this.configUrl;
    }

    private String loadConfigPrincipal() {
        if (this.configPrincipal == null) {
            this.configPrincipal = loadConfigString("ldapprincipal");
        }
        return this.configPrincipal;
    }

    private String loadDomainBase() {
        if (this.configDomainBase == null) {
            this.configDomainBase = loadConfigString("ldapdomainbase");
        }
        return this.configDomainBase;
    }

    private String loadConfigString(String key) {
        String sql = "SELECT * FROM `configuration` WHERE `key` = '" + key + "'";
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
        AccountManager accountManager = new DatabaseAccountManager(dbconn);

        LdapAuthenticator authenticator = new LdapAuthenticator(dbconn, accountManager);

        System.out.println(authenticator.loadConfigUrl());
        System.out.println(authenticator.loadConfigPrincipal());
        System.out.println(authenticator.loadDomainBase());

        while (true) {

            System.out.print("\n\nUsername: ");
            username = scanner.nextLine();
            System.out.print("Password: ");
            password = scanner.nextLine();

            System.out.println(authenticator.authenticate(username, password));
        }
        
    }

}
