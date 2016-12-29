package alabno.userstate;

import java.time.Duration;
import java.time.Instant;

/**
 * Class that contains the data necessary to restore a RememberedSession
 *
 */
public class RememberedSessionData {
	
	private static final long MAXVALIDITY = 60; //  minutes
	
	private UserSession userSession;
	private Instant timeStamp;
	
	public RememberedSessionData(UserSession userSession) {
		this.userSession = userSession;
		this.timeStamp = Instant.now();
	}
	
	public UserSession getUserSession() {
		return userSession;
	}
	
	public boolean isValid() {
		Instant now = Instant.now();
		long diff = Duration.between(now, timeStamp).toMinutes();
		return diff < MAXVALIDITY;
	}
	
}
