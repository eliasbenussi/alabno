package alabno.utils;

import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

/**
 * Utility functions related to connections
 *
 */
public class ConnUtils {

    public static void sendAlert(WebSocket conn, String string) {
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "alert");
        msgobj.put("message", string);
        conn.send(msgobj.toJSONString());
    }
    
}
