package alabno.userauth;

public class NullAuthenticator implements Authenticator {

    @Override
    public UserAccount authenticate(String username, String password) {
        return new UserAccount(username, username, username + "@example.com", UserType.PROFESSOR);
    }

}
