package alabno.userauth;

import alabno.useraccount.UserAccount;

public interface Authenticator {

    /**
     * @param username
     * @param password
     * @return a UserAccount object filled with the details of the logged in user
     * returns null if authentication fails
     * if the user can be authenticated but is not found in the AutoMarker database,
     * a new profile with default values will be automatically created before returning
     */
    UserAccount authenticate(String username, String password);
    
}
