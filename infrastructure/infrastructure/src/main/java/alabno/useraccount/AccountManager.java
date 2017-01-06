package alabno.useraccount;

public interface AccountManager {

    public UserAccount createAccount(String username, String fullName, String email, UserType type);
    
    public UserAccount getAccount(String username);
    
    public UserAccount getOrCreateIfNecessary(String username, String fullName, String email, UserType type);
    
    public void setUserType(String username, UserType type);
    
}
