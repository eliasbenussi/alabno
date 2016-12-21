package alabno.userstate;

import org.java_websocket.WebSocket;

import alabno.userauth.UserAccount;

public class UserSession {

    private WebSocket conn;
    private UserState state = new UserState();
    private UserAccount account;

    public UserSession(WebSocket conn, UserAccount account) {
        this.conn = conn;
        this.account = account;
    }

    public UserState getState() {
        return state;
    }
    
    public UserAccount getAccount() {
        return account;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((conn == null) ? 0 : conn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserSession other = (UserSession) obj;
        if (conn == null) {
            if (other.conn != null)
                return false;
        } else if (!conn.equals(other.conn))
            return false;
        return true;
    }

    public void send(String message) {
        conn.send(message);
    }

    public WebSocket getWebSocket() {
        return conn;
    }

	public void resetState() {
		state = new UserState();
	}

}
