package alabno.useraccount;

import alabno.userauth.TokenGenerator;

public class UserAccount {

    private String username;
    private String names;
    private String email;
    private UserType userType;

    public UserAccount(String username, String names, String email, UserType userType) {
        super();
        this.username = username;
        this.names = names;
        this.email = email;
        this.userType = userType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserAccount other = (UserAccount) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    public String getEmail() {
        return email;
    }

    public String getNames() {
        return names;
    }

    public String getUsername() {
        return username;
    }

    public UserType getUserType() {
        return userType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

	public String generateToken(TokenGenerator tokenGenerator) {
		return tokenGenerator.generateToken(username, userType.toString());
	}

    @Override
    public String toString() {
        return "UserAccount [username=" + username + ", names=" + names + ", email=" + email + ", userType=" + userType
                + "]";
    }

    public void setUserType(UserType type) {
        this.userType = type;
    }
	
	

}
