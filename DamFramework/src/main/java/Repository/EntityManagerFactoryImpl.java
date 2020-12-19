package Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EntityManagerFactoryImpl {
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("db");

	public static Connection getConnection() {

		try {
			Class.forName(resourceBundle.getString("driverName"));
			String url = resourceBundle.getString("url");
			String user = resourceBundle.getString("user");
			String password = resourceBundle.getString("password");
			return DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			return null;
		} catch (SQLException e) {
			return null;
		}
	}
}
