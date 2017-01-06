package alabno.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NullDatabaseConnection implements DatabaseConnection {

    @Override
    public List<Map<String, String>> retrieveQueryString(String sql) {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> retrieveQuery(String query) {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> retrieveStatement(String query, String[] parameters) {
        return new ArrayList<>();
    }

    @Override
    public int executeQuery(String query) {
        return 1;
    }

    @Override
    public int executeStatement(String query, String[] parameters) {
        return 1;
    }

    @Override
    public int executeTransaction(TransactionBuilder tb) {
        return 1;
    }

}
