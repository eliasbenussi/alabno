package alabno.database;

import java.util.List;
import java.util.Map;

public interface DatabaseConnection {

    /**
     * @param sql
     *            the SELECT query
     * @return results of the query where columns content is all strings
     */
    List<Map<String, String>> retrieveQueryString(String sql);

    /**
     * @param query
     *            the SELECT query
     * @return results of the query
     */
    List<Map<String, Object>> retrieveQuery(String query);

    List<Map<String, Object>> retrieveStatement(String query, String[] parameters);

    /**
     * @param query
     *            the INSERT, UPDATE, or DELETE query
     * @return number of rows returned
     */
    int executeQuery(String query);

    /**
     * @param query
     *            the INSERT, UPDATE, or DELETE query
     * @param parameters
     *            to interpolate in statement
     * @return number of rows returned
     */
    int executeStatement(String query, String[] parameters);

    int executeTransaction(TransactionBuilder tb);

}