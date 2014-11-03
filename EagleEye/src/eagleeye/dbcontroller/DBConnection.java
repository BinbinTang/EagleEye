package eagleeye.dbcontroller;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

	public static Connection dbConnector()
	{
		Connection dbConn = null;
		
		try {
			
			Class.forName("org.sqlite.JDBC");
			String OS = System.getProperty("os.name");
			if (OS.startsWith("Windows"))
				dbConn = DriverManager.getConnection("jdbc:sqlite:DBresources\\ForensicsSuiteDB.sqlite");
			else
				dbConn = DriverManager.getConnection("jdbc:sqlite:DBresources/ForensicsSuiteDB.sqlite");
			return dbConn;
			
		} catch (Exception e) {
			
			System.out.println("Connection fail");
			return dbConn;
		}
	}
}