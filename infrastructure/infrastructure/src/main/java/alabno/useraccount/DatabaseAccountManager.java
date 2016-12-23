package alabno.useraccount;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alabno.database.MySqlDatabaseConnection;
import alabno.database.TransactionBuilder;

public class DatabaseAccountManager implements AccountManager {

    private MySqlDatabaseConnection dbconn;
    private EnumMap<UserType, String> typeMap = new EnumMap<>(UserType.class);
    private Map<String, UserType> typeMapRev = new HashMap<>();
    private EnumMap<UserType, String> dbtableMap = new EnumMap<>(UserType.class);

    public DatabaseAccountManager(MySqlDatabaseConnection dbconn) {
        this.dbconn = dbconn;
        initTypeMap();
    }

    private void initTypeMap() {
        typeMap.put(UserType.ADMIN, "a");
        typeMap.put(UserType.PROFESSOR, "p");
        typeMap.put(UserType.STUDENT, "s");
        
        for (UserType k : typeMap.keySet()) {
            String v = typeMap.get(k);
            typeMapRev.put(v, k);
        }
        
        dbtableMap.put(UserType.ADMIN, "admin");
        dbtableMap.put(UserType.PROFESSOR, "professor");
        dbtableMap.put(UserType.STUDENT, "student");
    }

    @Override
    public UserAccount createAccount(String username, String fullName, String email, UserType type) {
        String typeString = typeMap.get(type);
        if (typeString == null) {
            throw new RuntimeException("Typemap has no entry for " + type);
        }
        
        TransactionBuilder transaction = new TransactionBuilder();
        
        String sql =  "INSERT INTO `user`(`username`, `type`, `fullname`, `email`) VALUES (?,?,?,?)";
        String[] args = {username, typeString, fullName, email};
        transaction.add(sql, args);
        
        switch (type) {
        case ADMIN:
            transaction.add("INSERT INTO `admin`(`username`) VALUES (?)", new String[] {username});
            break;
        case PROFESSOR:
            transaction.add("INSERT INTO `professor`(`username`) VALUES (?)", new String[] {username});
            break;
        case STUDENT:
            transaction.add("INSERT INTO `student`(`username`) VALUES (?)", new String[] {username});
            break;
        default:
            throw new RuntimeException("Error, unrecognized UserType " + type);
        }

        dbconn.executeTransaction(transaction);

        return getAccount(username);
    }

    @Override
    public UserAccount getAccount(String username) {
        String sql = "SELECT * FROM `user` WHERE `username` = ?";
        String[] args = {username};
        
        List<Map<String, Object>> results = dbconn.retrieveStatement(sql, args);
        
        if (results == null || results.size() == 0) {
            return null;
        } else if (results.size() == 1) {
            Map<String, Object> result = results.get(0);
            String fullName = (String) result.get("fullname");
            String email = (String) result.get("email");
            String typeString = (String) result.get("type");
            UserType type = typeMapRev.get(typeString);
            return new UserAccount(username, fullName, email, type);
        } else {
            throw new RuntimeException("Error when trying to retrieve a user from database: Results size is " + results.size());
        }
    }

    @Override
    public UserAccount getOrCreateIfNecessary(String username, String fullName, String email, UserType type) {
        UserAccount acc = getAccount(username);
        if (acc == null) {
            return createAccount(username, fullName, email, type);
        } else {
            return acc;
        }
    }

    @Override
    public void setUserType(String username, UserType type) {
        // check that account exists
        UserAccount acc = getAccount(username);
        
        if (acc == null) {
            return;
        }
        
        // change type in the user table
        
        // remove from the old table
        
        // insert in the new table
        
        // TODO finish this
    }
    
    
    
}
