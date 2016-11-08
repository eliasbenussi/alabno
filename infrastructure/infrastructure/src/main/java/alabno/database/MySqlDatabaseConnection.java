package alabno.database;

import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.ResultSet;
        import java.sql.ResultSetMetaData;
        import java.sql.Statement;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class MySqlDatabaseConnection {

    // JDBC driver name and database URL
    private String DB_URL = " jdbc:mysql:http://tc.jstudios.ovh/phpMyAdmin-4.6.4-english";

    // Database credentials
    static final String USER = "python";
    static final String PASS = "python";

    private Connection conn;

    public MySqlDatabaseConnection() {
        connect();
    }

    private void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sql the SELECT query
     * @return results of the query
     */
    public List<Map<String, Object>> retrieveQuery(String sql) {
        return retrieveQuery(sql, true);
    }

    // if `retry` is set, the database will try to re-establish
    // a broken connection and query again. Otherwise, it will
    // print the Exception to console
    private List<Map<String, Object>> retrieveQuery(String sql, boolean retry) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            Map<String, Object> row;
            ResultSetMetaData metaData = rs.getMetaData();
            Integer columnCount = metaData.getColumnCount();
            while (rs.next()) {
                row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                result.add(row);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            connect();
            if (retry) {
                return retrieveQuery(sql, false);
            } else {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param sql the INSERT, UPDATE, or DELETE query
     * @return number of rows returned
     */
    public int executeQuery(String sql) {
        return executeQuery(sql, true);
    }

    // if `retry` is set, the database will try to re-establish
    // a broken connection and query again. Otherwise, it will
    // print the Exception to console
    private int executeQuery(String sql, boolean retry) {
        int result = 0;
        try {
            Statement stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception e) {
            connect();
            if (retry) {
                return executeQuery(sql, false);
            } else {
                e.printStackTrace();
            }
        }
        return result;
    }

}