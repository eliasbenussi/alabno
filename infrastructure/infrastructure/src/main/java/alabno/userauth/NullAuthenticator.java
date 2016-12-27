package alabno.userauth;

import alabno.useraccount.UserAccount;
import alabno.useraccount.UserType;

public class NullAuthenticator implements Authenticator {

    @Override
    public UserAccount authenticate(String username, String password) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        
        char firstLetter = username.toLowerCase().charAt(0);
        
        if (firstLetter == 'a') {
            return new UserAccount(username, username, username + "@example.com", UserType.ADMIN);
        } else if (firstLetter == 'p') {
            return new UserAccount(username, username, username + "@example.com", UserType.PROFESSOR);
        } else {
            return new UserAccount(username, username, username + "@example.com", UserType.STUDENT);
        }

    }

}
