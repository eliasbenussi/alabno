package alabno.usercapabilities;

import alabno.useraccount.UserType;

public class AllPermissions implements Permissions {

    @Override
    public boolean canPerform(UserType userType, String action) {
        return true;
    }

}
