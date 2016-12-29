package alabno.userauth;

public interface TokenGenerator {

	public String generateToken(String username, String userType);
	
}
