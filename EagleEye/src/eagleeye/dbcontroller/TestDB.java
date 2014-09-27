package eagleeye.dbcontroller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class TestDB {

	public static void main(String[] args) {
		
		Connection myconn = DBConnection.dbConnector();
		
		String query = "SELECT TestID, TestName, TestPassword FROM TEST WHERE TestID = 2";
		Statement stmt = null;
		
		try {
			stmt = myconn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				
				String name = rs.getString("TestName");
				String Password = rs.getString("TestPassword");
				int ID = rs.getInt("TestID");
				
				System.out.println(name + " " + Password + " "+ ID);
			}
			
		} catch (SQLException e) {
			
			System.out.println("Query Failed");
			System.out.println(e);
			
		}
		
		
		
		
		
	}

}
