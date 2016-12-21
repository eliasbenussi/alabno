package alabno.userstate;

import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

import alabno.userauth.UserAccount;

public class ActiveSessions {

    // Maps user identification token with their actual connection
    private final Map<String, UserSession> activeSessions = new HashMap<>();
    private final Map<WebSocket, String> reverseMap = new HashMap<>();
    private final Map<RememberedSessionKey, RememberedSessionData> rememberedSessions = new HashMap<>();
    
    /**
     * @param token the token that identifies the new user
     * @param userAccount 
     * @param session the connection used by the new user
     */
    public void createSession(String token, WebSocket conn, UserAccount userAccount) {
        UserSession session = new UserSession(conn, userAccount);
        
        if (activeSessions.containsKey(token)) {
            System.out.println("Detected duplicate login... Removing previous user");
            UserSession previousConnection = activeSessions.get(token);
            reverseMap.remove(previousConnection);
        }
        // This can override
        activeSessions.put(token, session);
        
        if (reverseMap.containsKey(session)) {
            System.out.println("User logged in twice... Removing previous identity");
            
            String previousToken = reverseMap.get(session);
            activeSessions.remove(previousToken);
        }
        // This can override
        reverseMap.put(conn, token);
        
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
        
        // Save into Remembered Sessions
        UserSession userSession = activeSessions.get(token);
        RememberedSessionKey rkey = new RememberedSessionKey(userSession.getAccount().getUsername(), token);
        RememberedSessionData rdata = new RememberedSessionData(userSession);
        rememberedSessions.put(rkey, rdata);
        
        activeSessions.remove(token);
        reverseMap.remove(conn);
    }
    
    /**
     * @param conn
     * @param username
     * @param token
     * @return true if the session was successfully restored <br />
     * false if the session could not be restored, when not found or when expired
     */
    public boolean restoreSession(WebSocket conn, String username, String token) {
    	RememberedSessionKey rkey = new RememberedSessionKey(username, token);
    	RememberedSessionData rdata = rememberedSessions.get(rkey);
    	
    	if (rdata == null) {
    		System.out.println("Could not restore session: session not found in remembered sessions");
    		return false;
    	}
    	
    	if (rdata.isValid()) {
    		// Restore the session
    		UserSession userSession = rdata.getUserSession();
    		activeSessions.put(token, userSession);
    		reverseMap.put(conn, token);
    		rememberedSessions.remove(rkey);
    		System.out.println("Successfully restored a session");
    		return true;
    	} else {
    		rememberedSessions.remove(rkey);
    		System.out.println("Could not restore a session: found session is not valid anymore");
    		return false;
    	}
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
    public UserSession getConnection(String token) {
        return activeSessions.get(token);
    }
    
    public UserState getUserState(String token) {
        UserSession theSession = activeSessions.get(token);
        if (theSession == null) {
            return null;
        }
        return theSession.getState();
    }

}
