package alabno.wserver;

import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

public class SessionManager {

    // Maps user identification token with their actual connection
    private final Map<String, WebSocket> activeSessions = new HashMap<>();
    private final Map<WebSocket, String> reverseMap = new HashMap<>();
    private final Map<String, UserState> userViewStates = new HashMap<>();
    
    /**
     * @param token the token that identifies the new user
     * @param connection the connection used by the new user
     */
    public void createSession(String token, WebSocket connection) {
        if (activeSessions.containsKey(token)) {
            System.out.println("Detected duplicate login... Removing previous user");
            WebSocket previousConnection = activeSessions.get(token);
            reverseMap.remove(previousConnection);
        }
        // This can override
        activeSessions.put(token, connection);
        
        if (reverseMap.containsKey(connection)) {
            System.out.println("User logged in twice... Removing previous identity");
            
            String previousToken = reverseMap.get(connection);
            activeSessions.remove(previousToken);
        }
        // This can override
        reverseMap.put(connection, token);
        
        userViewStates.put(token, new UserState());
        
        System.out.println("Session created for token " + token);
    }
    
    /**
     * Ends a client connection
     * 
     * @param conn the connection that was closed
     */
    public void endSession(WebSocket conn) {
        String token = reverseMap.get(conn);
        
        // Token can be null if user connected but never logged in
        if (token == null) {
            return;
        }
        
        activeSessions.remove(token);
        reverseMap.remove(conn);
        userViewStates.remove(token);
    }
    
    /**
     * Sends a message to all connected clients
     * 
     * @param message to be sent to all connected clients
     */
    public void broadcastMessage(String message) {
        for (WebSocket conn : reverseMap.keySet()) {
            conn.send(message);
        }
    }

    /**
     * @param token
     *            identification of the user to be checked
     * @return the connection corresponding to the user, or null if nothing can
     *         be found
     */
    public WebSocket getConnection(String token) {
        return activeSessions.get(token);
    }
    
    public UserState getUserState(String token) {
        return userViewStates.get(token);
    }

}
