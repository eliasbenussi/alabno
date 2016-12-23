package alabno.userauth;

import alabno.useraccount.UserAccount;
import alabno.useraccount.UserType;

public class NullAuthenticator implements Authenticator {

    @Override
    public UserAccount authenticate(String username, String password) {
        return new UserAccount(username, username, username + "@example.com", UserType.PROFESSOR);
    }

}
