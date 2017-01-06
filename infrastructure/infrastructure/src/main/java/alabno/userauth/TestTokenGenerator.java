package alabno.userauth;

public class TestTokenGenerator implements TokenGenerator {

	@Override
	public String generateToken(String username, String userType) {
		return username + "-" + username.hashCode();
	}

}
