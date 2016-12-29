package alabno.useraccount;

import java.util.HashMap;
import java.util.Map;

public class LocalAccountManager implements AccountManager {

    private Map<String, UserAccount> accounts = new HashMap<>();
    
    @Override
    public UserAccount createAccount(String username, String fullName, String email, UserType type) {
        UserAccount acc = new UserAccount(username, fullName, email, type);
        accounts.put(username, acc);
        return acc;
    }

    @Override
    public UserAccount getAccount(String username) {
        return accounts.get(username);
    }

    @Override
    public UserAccount getOrCreateIfNecessary(String username, String fullName, String email, UserType type) {
        if (accounts.containsKey(username)) {
            return accounts.get(username);
        }
        
        UserAccount acc = createAccount(username, fullName, email, type);
        return acc;
    }

    @Override
    public void setUserType(String username, UserType type) {
        UserAccount acc = accounts.get(username);
        if (acc != null) {
            acc.setUserType(type);
        }
    }

}
