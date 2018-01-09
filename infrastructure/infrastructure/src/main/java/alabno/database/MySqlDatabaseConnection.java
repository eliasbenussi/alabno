package alabno.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import alabno.utils.FileUtils;

public class MySqlDatabaseConnection implements DatabaseConnection {

    // JDBC driver name and database URL
    private String DB_URL;

    // Database credentials
    static final String USER = "python";
    private String dbPassword = "";

    private Connection conn;

    public MySqlDatabaseConnection() {
        if (System.getenv("ALABNOLOCAL").equals("1")) {
            DB_URL = "jdbc:mysql://localhost:3306/Automarker";
        } else {
            DB_URL = "jdbc:mysql://alabno.jstudios.ovh:3306/Automarker";
        }
        
        setupPassword();
        connect();
    }

    private void setupPassword() {
        String workDir = FileUtils.getWorkDir();
        String passwordPath = workDir + "/dbpass.txt";
        File passwordFile = new File(passwordPath);
        Scanner scanner = null;
        try {
            scanner = new Scanner(passwordFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find the password file! Make sure to use RunServer");
            System.exit(1);
        }
        String thePassword = scanner.nextLine();
        scanner.close();
        this.dbPassword = thePassword;
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            conn = DriverManager.getConnection(DB_URL, USER, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see alabno.database.DatabaseConnection#retrieveQueryString(java.lang.String)
     */
    @Override
    public synchronized List<Map<String, String>> retrieveQueryString(String sql) {
        return retrieveQueryString(sql, true);
    }

    // if `retry` is set, the database will try to re-establish
    // a broken connection and query again. Otherwise, it will
    // print the Exception to console
    private List<Map<String, String>> retrieveQueryString(String sql, boolean retry) {
        List<Map<String, String>> result = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            Map<String, String> row;
            ResultSetMetaData metaData = rs.getMetaData();
            Integer columnCount = metaData.getColumnCount();

            while (rs.next()) {
                row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getString(i));
                }
                result.add(row);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            connect();
            if (retry) {
                return retrieveQueryString(sql, false);
            } else {
                e.printStackTrace();
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see alabno.database.DatabaseConnection#retrieveQuery(java.lang.String)
     */
    @Override
    public synchronized List<Map<String, Object>> retrieveQuery(String query) {
        return retrieveQuery(query, new String[0], true);
    }

    /* (non-Javadoc)
     * @see alabno.database.DatabaseConnection#retrieveStatement(java.lang.String, java.lang.String[])
     */
    @Override
    public synchronized List<Map<String, Object>> retrieveStatement(String query, String[] parameters) {
        return retrieveQuery(query, parameters, true);
    }

    // if `retry` is set, the database will try to re-establish
    // a broken connection and query again. Otherwise, it will
    // print the Exception to console
    private List<Map<String, Object>> retrieveQuery(String query, String[] parameters, boolean retry) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                stmt.setString(i + 1, parameters[i]);
            }

            ResultSet rs = stmt.executeQuery();

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
                return retrieveQuery(query, parameters, false);
            } else {
                e.printStackTrace();
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see alabno.database.DatabaseConnection#executeQuery(java.lang.String)
     */
    @Override
    public synchronized int executeQuery(String query) {
        return executeQuery(query, true);
    }

    // if `retry` is set, the database will try to re-establish
    // a broken connection and query again. Otherwise, it will
    // print the Exception to console
    private int executeQuery(String query, boolean retry) {
        int result = 0;
        try {
            Statement stmt = conn.createStatement();
            result = stmt.executeUpdate(query);
            stmt.close();
        } catch (Exception e) {
            connect();
            if (retry) {
                return executeQuery(query, false);
            } else {
                e.printStackTrace();
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see alabno.database.DatabaseConnection#executeStatement(java.lang.String, java.lang.String[])
     */
    @Override
    public synchronized int executeStatement(String query, String[] parameters) {
        return executeStatement(query, parameters, true);
    }

    private int executeStatement(String query, String[] parameters, boolean retry) {
        int result = 0;
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                stmt.setString(i + 1, parameters[i]);
            }
            result = stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            connect();
            if (retry) {
                return executeStatement(query, parameters, false);
            } else {
                e.printStackTrace();
            }
        }

        return result;
    }
    
    /* (non-Javadoc)
     * @see alabno.database.DatabaseConnection#executeTransaction(alabno.database.TransactionBuilder)
     */
    @Override
    public synchronized int executeTransaction(TransactionBuilder tb) {
        int result = 0;
        try {
            conn.setAutoCommit(false);

            for (TransactionElement element : tb.getElements()) {
                String query = element.getSql();
                String[] parameters = element.getArgs();
                
                PreparedStatement stmt = conn.prepareStatement(query);
                for (int i = 0; i < parameters.length; i++) {
                    stmt.setString(i + 1, parameters[i]);
                }
                result = stmt.executeUpdate();
                stmt.close();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connect();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
