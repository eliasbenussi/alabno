package alabno.userstate;

/**
 * This is the key object for the RememberedSessions, which are the sessions
 * that are sleeping after user closed the websocket connection but that are still
 * valid.
 * It's important to notice that the equals method essentially only operates on the
 * username, because if tokens are equal then necessarily usernames are equal (all the
 * TokenGenerators put the username as part of the token to ensure this)
 *
 */
public class RememberedSessionKey {

	private final String username;
	private final String token;
	
	public RememberedSessionKey(String username, String token) {
		if (username == null || token == null) {
			throw new RuntimeException("Error! Trying to create a RememberedSessionKey with ["+ username +"] ["+ token +"]");
		}
		this.username = username;
		this.token = token;
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RememberedSessionKey other = (RememberedSessionKey) obj;
		return this.username.equals(other.username) || this.token.equals(other.token);
	}

}
