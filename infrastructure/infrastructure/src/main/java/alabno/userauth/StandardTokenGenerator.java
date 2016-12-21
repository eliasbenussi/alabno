package alabno.userauth;

import java.util.Random;

public class StandardTokenGenerator implements TokenGenerator {

	private Random rand = new Random();
	
	@Override
	public String generateToken(String username, String userType) {
		int randInt = rand.nextInt();
		return username + "-" + userType + randInt;
	}

}
