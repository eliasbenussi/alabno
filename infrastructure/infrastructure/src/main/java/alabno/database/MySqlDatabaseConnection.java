package alabno.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import alabno.utils.FileUtils;

public class MySqlDatabaseConnection {

	// JDBC driver name and database URL
	private String DB_URL = "jdbc:mysql://tc.jstudios.ovh:3306/Automarker";

	// Database credentials
	static final String USER = "python";
	private String dbPassword = "";

	private Connection conn;

	public MySqlDatabaseConnection() {
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

	/**
	 * @param sql
	 *            the SELECT query
	 * @return results of the query where columns content is all strings
	 */
	public List<Map<String, String>> retrieveQueryString(String sql) {
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

	/**
	 * @param query
	 *            the SELECT query
	 * @return results of the query
	 */
	public List<Map<String, Object>> retrieveQuery(String query) {
		return retrieveQuery(query, new String[0], true);
	}

	public List<Map<String, Object>> retrieveStatement(String query, String[] parameters) {
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

	/**
	 * @param query
	 *            the INSERT, UPDATE, or DELETE query
	 * @return number of rows returned
	 */
	public int executeQuery(String query) {
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

	/**
	 * @param query
	 *            the INSERT, UPDATE, or DELETE query
	 * @param parameters
	 *            to interpolate in statement
	 * @return number of rows returned
	 */
	public int executeStatement(String query, String[] parameters) {
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

}