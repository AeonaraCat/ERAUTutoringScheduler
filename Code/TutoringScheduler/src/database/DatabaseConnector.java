package database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnector {
	private Connection database;
	private Statement smt;
	private Properties config;

	public DatabaseConnector() {
		config = new Properties();
		try {
			FileInputStream file = new FileInputStream("config.properties");
			config.load(file);
		} catch (Exception exc) {
			System.out.println("Failed to load config file!");
			exc.printStackTrace();
		}
		String connectionUrl = "jdbc:mysql://prclab1.erau.edu:3306/" + config.getProperty("DB");
		try {
			database = DriverManager.getConnection(connectionUrl, config.getProperty("Username"),
					config.getProperty("Password"));
			if (database != null) {
				System.out.println("Successfully connected to prclab");
			}
		} catch (Exception exc) {
			System.out.println("COULD NOT CONNECT TO PRCLAB");
			exc.printStackTrace();
		}
	}

	public DatabaseConnector(String host, String database, String user, String password) {
		String connectionURL = "jdbc:mysql://" + host + "/" + database;
		System.out.println("Attempting to connect to database " + database + "at host " + host);
		try {
			this.database = DriverManager.getConnection(connectionURL, user, password);
		} catch (SQLException sqlExc) {
			System.out.println("Connectioned Failed");
			database = null;
			sqlExc.printStackTrace();
		}
	}

	public ResultSet runQuery(String query) {
		ResultSet rset = null;
		try {
			if (database != null && !database.isClosed()) {
				smt = database.createStatement();
				rset = smt.executeQuery(query);
			} else {
				System.out.println("Database: " + Boolean.toString(database != null) + " closed: "
						+ Boolean.toString(database.isClosed()));
			}
		} catch (Exception exc) {
			System.out.print("Failed to execute query");
			exc.printStackTrace();
			rset = null;
		}
		return rset;
	}

	public void printResultSet(ResultSet rset) {
		int columnsNumber;
		try {
			ResultSetMetaData rsmd = rset.getMetaData();
			columnsNumber = rsmd.getColumnCount();
			while (rset.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1)
						System.out.print(", ");
					String columnValue = rset.getString(i);
					System.out.print(rsmd.getColumnName(i) + " " + columnValue);
				}
				System.out.println("");
			}
		} catch (SQLException exc) {
			// TODO Auto-generated catch block
			exc.printStackTrace();
		}

	}

	public void updateDatabase(String updateQuery) {
		try {
			if (database != null && !database.isClosed()) {
				smt = database.createStatement();
				smt.executeUpdate(updateQuery);
			} else {
				System.out.println("Database: " + Boolean.toString(database != null) + " closed: "
						+ Boolean.toString(database.isClosed()));
			}
		} catch (Exception exc) {
			System.out.print("Failed to execute query");
			exc.printStackTrace();
		}
	}


	public Connection getDatabase() {
		return database;
	}

	public void close() {
		if (smt != null) {
			try {
				smt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (database != null) {
			try {
				database.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	protected void finalize() {
		if (smt != null) {
			try {
				smt.close();
			} catch (SQLException exc) {
				exc.printStackTrace();
			}
			smt = null; 
		}
		if (database != null) {
			try {
				database.close();
			} catch (SQLException exc) {
				exc.printStackTrace();
			}
			database = null;
		}
	}

}
