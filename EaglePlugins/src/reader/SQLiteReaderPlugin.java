package reader;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eagleeye.pluginmanager.Plugin;

public class SQLiteReaderPlugin implements Plugin{
	
	
	private class DBReader {
		private String path;
		private Connection dbConn;
		private DBReader(String _path){
			path = _path;
		}
		private Map<String, List<List<String>>> read()
		{
			dbConn = null;
			Map<String, List<List<String>>> tables = new HashMap <String, List<List<String>>>();
			try {				
				Class.forName("org.sqlite.JDBC");
				System.out.println("connect to "+path);
				dbConn = DriverManager.getConnection("jdbc:sqlite:"+path);
				List<String> tableNames = getAllTableNames();		
				for(String n : tableNames){
					List<List<String>> tb = getAllRowsInTable(n);
					tables.put(n,tb);
				}
				for(String n: tableNames){
					List<List<String>> tb = tables.get(n);
					System.out.println("TABLE: "+n+" \t"+(tb.size()-1)+" rows");
					int printSize = (tb.size()>=3)?3:tb.size();
					for(int i = 0; i<printSize; i++)
						System.out.println(tb.get(i));
					System.out.println("...");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try{
					if(dbConn !=null) dbConn.close();
				}catch(SQLException e){
					//connection close failed
					e.printStackTrace();
				}
			}
			return tables;
		}
		private List<String> getAllTableNames() throws SQLException{
			List<String> tableNames = new ArrayList<String>();
			
			Statement statement=dbConn.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs=statement.executeQuery("select name from sqlite_master where type='table' order by name");
			while(rs.next()){
				tableNames.add(rs.getString("name"));
			}
			rs.close();
			statement.close();
			return tableNames;
		}
		private List<List<String>> getAllRowsInTable(String tableName) throws SQLException{
			List<List<String>> tableRows = new ArrayList<List<String>>();
			Statement statement=dbConn.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs=statement.executeQuery("select * from "+tableName);
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			List<String> lables = new ArrayList<String>();
			for(int i=1;i<=cols;i++) lables.add(rsmd.getColumnName(i));
			tableRows.add(lables);
			while(rs.next()){
				List<String> entry = new ArrayList<String>();
				for(int i=1;i<=cols;i++) entry.add(rs.getString(i));
				tableRows.add(entry);
			}
			rs.close();
			statement.close();
			return tableRows;
		}
	}
	private DBReader dbr;
	
	@Override
	public String getName() {
		return "SQLite Reader";
	}

	@Override
	public Object getResult() {
		return dbr.read();
	}

	@Override
	public Type getType() {
		return Type.READER;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int setParameter(List argList) {
		String path = (String) argList.get(0);
		dbr = new DBReader(path);
		return 0;
	}
	public static void main(String[] args) { 
		SQLiteReaderPlugin sp = new SQLiteReaderPlugin();
		List paths = new ArrayList();
		String root = ".."+File.separator+".."+File.separator+".."+File.separator+"device_ios";
		//paths.add(root+File.separator+"private"+File.separator+"var"+File.separator+"mobile"+File.separator+"Library"+File.separator+"Calendar"+File.separator+"Extras.db");
		//paths.add(root+File.separator+"private"+File.separator+"var"+File.separator+"mobile"+File.separator+"Library"+File.separator+"Calendar"+File.separator+"Calendar.sqlitedb");
		//paths.add(root+File.separator+"private"+File.separator+"var"+File.separator+"mobile"+File.separator+"Library"+File.separator+"SMS"+File.separator+"sms.db");
		//paths.add(root+File.separator+"private"+File.separator+"var"+File.separator+"mobile"+File.separator+"Library"+File.separator+"AddressBook"+File.separator+"AddressBook.sqlitedb");
		//paths.add(root+File.separator+"private"+File.separator+"var"+File.separator+"mobile"+File.separator+"Library"+File.separator+"Safari"+File.separator+"Bookmarks.db");
		//paths.add(root+File.separator+"private"+File.separator+"var"+File.separator+"mobile"+File.separator+"Applications"+File.separator+"00CAE5F5-CA3E-45D2-91F2-33E3F2FB12E1"+File.separator+"Documents"+File.separator+"ChatStorage.sqlite");
		
		root = ".."+File.separator+"EagleEye"+File.separator+"output"+File.separator+"mtd8.dd"+File.separator+"mtd8.dd";
		//paths.add(root+File.separator+"data"+File.separator+"com.android.providers.calendar"+File.separator+"databases"+File.separator+"calendar.db");
		paths.add(root+File.separator+"data"+File.separator+"com.google.android.providers.gmail"+File.separator+"databases"+File.separator+"mailstore.yobtaog@gmail.com.db");
		sp.setParameter(paths);
		sp.getResult();
	}

	@Override
	public int setAvailablePlugins(List<Plugin> pls) {
		// TODO Auto-generated method stub
		return 0;
	}

}
