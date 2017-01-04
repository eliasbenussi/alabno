package alabno.wserver;

import alabno.database.DatabaseConnection;
import alabno.database.MySqlDatabaseConnection;
import alabno.localjobstatus.LocalJobStatusAll;
import alabno.msfeedback.FeedbackUpdaters;
import alabno.msfeedback.haskellupdater.HaskellMarkerUpdater;
import alabno.msfeedback.markmarker.MarkMarkerUpdater;
import alabno.useraccount.AccountManager;
import alabno.useraccount.DatabaseAccountManager;
import alabno.useraccount.LocalAccountManager;
import alabno.userauth.*;
import alabno.usercapabilities.Permissions;
import alabno.usercapabilities.StandardPermissions;
import alabno.utils.FileUtils;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

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

        // Local Job status
        LocalJobStatusAll localJobs = new LocalJobStatusAll();

        // Start WebSocket server

        while (true) {
            System.out.println("Starting WebSocket server on port " + port);
            AutoMarkerWSServer the_server = new AutoMarkerWSServer(port, updaters, dbconn, authenticator,
                    tokenGenerator, permissions, localJobs);

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
