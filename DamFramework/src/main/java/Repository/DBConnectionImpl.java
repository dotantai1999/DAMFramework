package Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConnectionImpl {
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("db");

	private static Connection connection = null;

	private DBConnectionImpl(){}

	public static Connection getConnection() {
		if (connection != null) {
			return connection;
		}

		try {
			Class.forName(resourceBundle.getString("driverName"));
			String url = resourceBundle.getString("url");
			String user = resourceBundle.getString("user");
			String password = resourceBundle.getString("password");
			connection = DriverManager.getConnection(url, user, password);
			return connection;

		} catch (ClassNotFoundException e) {
			return null;
		} catch (SQLException e) {
			return null;
		}
	}

	public static void close(){
		if(connection != null){
			try {
				connection.close();
				connection = null;
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}
	}
}
