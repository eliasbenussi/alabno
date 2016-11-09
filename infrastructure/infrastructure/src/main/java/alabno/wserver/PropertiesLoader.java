package alabno.wserver;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    public int getPort() {
        return getIntProperty("port");
    }

    public int getSecurePort() {
        return getIntProperty("secure_port");
    }

    private int getIntProperty(String propertyName) {
        try {
            // get port number from config/server.properties
            InputStream properties_stream = this.getClass().getClassLoader().getResourceAsStream("server.properties");
            Properties server_properties = new Properties();
            server_properties.load(properties_stream);
            String port_raw = server_properties.getProperty(propertyName);
            int port = Integer.parseInt(port_raw);
            return port;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
