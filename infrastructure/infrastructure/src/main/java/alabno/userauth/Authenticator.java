package alabno.userauth;

public interface Authenticator {

    UserAccount authenticate(String username, String password);
    
}
