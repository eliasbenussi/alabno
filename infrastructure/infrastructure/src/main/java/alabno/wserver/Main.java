package alabno.wserver;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import alabno.msfeedback.markmarker.MarkMarkerUpdater;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import alabno.database.DatabaseConnection;
import alabno.database.MySqlDatabaseConnection;
import alabno.msfeedback.FeedbackUpdaters;
import alabno.msfeedback.haskellupdater.HaskellMarkerUpdater;
import alabno.useraccount.AccountManager;
import alabno.useraccount.DatabaseAccountManager;
import alabno.useraccount.LocalAccountManager;
import alabno.userauth.Authenticator;
import alabno.userauth.LdapAuthenticator;
import alabno.userauth.NullAuthenticator;
import alabno.userauth.StandardTokenGenerator;
import alabno.userauth.TokenGenerator;
import alabno.usercapabilities.Permissions;
import alabno.usercapabilities.StandardPermissions;
import alabno.utils.FileUtils;

public class Main {

    public static void main(String[] args) throws Exception {

        FileUtils.initWorkDir();

        PropertiesLoader properties_loader = new PropertiesLoader();

        boolean secure = args.length > 0 && "https".equals(args[0]);

        int port = 0;

        if (secure) {
            port = properties_loader.getSecurePort();
        } else {
            port = properties_loader.getPort();
        }

        if (port == -1) {
            System.out.println("Error when reading properties");
            System.exit(1);
        }

        // Setup microservices feedback
        DatabaseConnection dbconn = new MySqlDatabaseConnection();
        FeedbackUpdaters updaters = new FeedbackUpdaters();
        updaters.register(new HaskellMarkerUpdater(dbconn));
        updaters.register(new MarkMarkerUpdater(dbconn));

        // Setup account manager
        AccountManager accountManager = null;
        if (secure) {
            accountManager = new DatabaseAccountManager(dbconn);
        } else {
            accountManager = new LocalAccountManager();
        }

        // Setup account authenticator
        Authenticator authenticator = null;
        if (secure) {
            authenticator = new LdapAuthenticator(dbconn, accountManager);
        } else {
            authenticator = new NullAuthenticator();
        }

        // Token generator
        TokenGenerator tokenGenerator = new StandardTokenGenerator();
        
        // Permissions loading
        Permissions permissions = new StandardPermissions();

        // Start WebSocket server

        while (true) {
            System.out.println("Starting WebSocket server on port " + port);
            AutoMarkerWSServer the_server = new AutoMarkerWSServer(port, updaters, dbconn, authenticator,
                    tokenGenerator, permissions);

            if (secure) {
                // Set up the WebSocket server in secure mode
                String STORETYPE = "JKS";
                String KEYSTORE = "frontend/mykeystore.jks";
                String STOREPASSWORD = "albano";
                String KEYPASSWORD = "albano";

                KeyStore ks = KeyStore.getInstance(STORETYPE);
                File kf = new File(KEYSTORE);
                ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ks, KEYPASSWORD.toCharArray());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(ks);

                SSLContext sslContext = null;
                sslContext = SSLContext.getInstance("TLS");
                System.out.println("Number of key managers: " + kmf.getKeyManagers().length);
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                the_server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
            }
            the_server.start();
            do {
                Thread.sleep(1000);
            } while (the_server.isRunning());
        }

    }

}
