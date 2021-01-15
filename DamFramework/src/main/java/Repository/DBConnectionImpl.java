package Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

public class DBConnectionImpl {
//	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("db");

	private static Connection connection = null;

	public static String fileName;

	private DBConnectionImpl(){}

	public static Connection getConnection() {
		if (connection != null) {
			return connection;
		}

		try {
			Map<String, String> dbConfig = getDBConfig();


			Class.forName(dbConfig.get("driverName"));
			String url = dbConfig.get("url");
			String user = dbConfig.get("user");
			String password = dbConfig.get("password");
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

	public static Map<String, String> getDBConfig(){
		Map<String, String> result = new HashMap<>();

		File myObj = new File(fileName);
		System.out.println(myObj.getAbsolutePath());
		try {
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				String[] temp = data.split(" = ");
				result.put(temp[0],temp[1]);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}
