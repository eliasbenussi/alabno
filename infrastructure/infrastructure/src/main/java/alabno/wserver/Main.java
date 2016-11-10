package alabno.wserver;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

public class Main {

    public static void main(String[] args) throws Exception {

        PropertiesLoader properties_loader = new PropertiesLoader();

        boolean secure = args.length > 0 && "https".equals(args[0]);

        System.out.println("Running the server in secure mode");

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

        System.out.println("Starting WebSocket server on port " + port);
        AutoMarkerWSServer the_server = new AutoMarkerWSServer(port);

        if (secure) {
            // Set up the WebSocket server in secure mode
            // Set WSS server certificates and keystores
            // keytool -genkey -validity 3650 -keystore "keystore.jks"
            // -storepass "storepassword" -keypass "keypassword" -alias
            // "default" -dname "CN=127.0.0.1, OU=MyOrgUnit, O=MyOrg, L=MyCity,
            // S=MyRegion, C=MyCountry"
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

    }

}
