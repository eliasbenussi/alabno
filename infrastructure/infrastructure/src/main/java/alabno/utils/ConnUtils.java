package alabno.utils;

import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

/**
 * Utility functions related to connections
 *
 */
public class ConnUtils {

    @SuppressWarnings("unchecked")
    public static void sendAlert(WebSocket conn, String string) {
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "alert");
        msgobj.put("message", string);
        conn.send(msgobj.toJSONString());
    }
    
    public enum Color {
        RED, GREEN, YELLOW, BLACK;
        
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    @SuppressWarnings("unchecked")
    public static void sendStatusInfo(WebSocket conn, String message, Color color, Integer timeout) {
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "status_info");
        msgobj.put("message", message);
        msgobj.put("color", color.toString());
        msgobj.put("timeout", timeout);
        conn.send(msgobj.toJSONString());
    }

}
