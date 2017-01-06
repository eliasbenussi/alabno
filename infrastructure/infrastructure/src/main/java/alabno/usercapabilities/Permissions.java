package alabno.usercapabilities;

import alabno.useraccount.UserType;

public interface Permissions {

    public boolean canPerform(UserType userType, String action);
    
}
