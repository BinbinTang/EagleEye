package eagleeye.dbcontroller;

import eagleeye.entities.*;
import java.sql.*;
import java.util.ArrayList;

public class DBQueryController {

	protected int deviceID;
	protected DBQueries queryMaker;
		
	public DBQueryController(int deviceID) {
		
		this.deviceID = deviceID;
		queryMaker = new DBQueries();
	}
	
	
	
	//use for tree-view
	//Call this method if using tree
	public ArrayList<Directory> getAllDirectoriesAndFiles() {
		
		ArrayList<Directory> listOfDirectory = getAllDirectories();
		ArrayList<File> listOfFiles = getAllFiles();
		
		ArrayList<Directory> organizedDirectoryList = this.organizeFilesAndDirectory(listOfDirectory, listOfFiles);
		
		return organizedDirectoryList;
		
	}
	
	public ArrayList<Directory> getAllDirectories(){
		
		ArrayList<Directory> listOfDirectory = new ArrayList<Directory>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			Statement stmt = conn.createStatement();
			String query = queryMaker.getAllDirectories(deviceID);
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				
				Directory d = new Directory();
				d.modifyDeviceID(deviceID);
				d.modifyDirectoryName(rs.getString("DirectoryName"));
				d.modifyDirectoryID(rs.getInt("OriginDirectory"));
				d.modifyParentDirectory(rs.getInt("PreDirectory"));
				d.modifyDateAccessed(rs.getString("DateAccessed"));
				d.modifyDateCreated(rs.getString("DateCreated"));
				d.modifyDateDeleted(rs.getString("DateDeleted"));
				d.modifyIsRecovered(rs.getBoolean("IsRecovered"));
				
				listOfDirectory.add(d);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
				
		return listOfDirectory;
	}
	
	public ArrayList<File> getAllFiles() {
		
		ArrayList<File> listOfFiles = new ArrayList<File>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			Statement stmt = conn.createStatement();
			String query = queryMaker.getAllFiles(deviceID);
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				
				File f = new File();
				f.modifyCategory(rs.getString("ExtTypeName"));
				f.modifyFileName(rs.getString("FileName"));
				f.modifyFileExt(rs.getString("FileExt"));
				f.modifyIsRecovered(rs.getBoolean("IsRecovered"));
				f.modifyDateDeleted(rs.getString("DateDeleted"));
				f.modifyIsModified(rs.getBoolean("IsModified"));
				f.modifyModifiedExt(rs.getString("ModifiedExt"));
				f.modifyDateCreated(rs.getString("DateCreated"));
				f.modifyDateAccessed(rs.getString("DateAccessed"));
				f.modifyDateModified(rs.getString("DateModified"));
				f.modifyFilePath(rs.getString("FilePath"));
				f.modifyDirectoryID(rs.getInt("DirectoryID"));
				f.modifyDirectoryName(rs.getString("DirectoryName"));
				
				listOfFiles.add(f);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			System.out.println("Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return listOfFiles;
		
	}
	
	//Helper method
	private ArrayList<Directory> organizeFilesAndDirectory (ArrayList<Directory> listOfDirectory, ArrayList<File> listOfFiles){
		
		for(File f : listOfFiles) {
			
			int fileDirectory = f.getDirectoryID();
			
			for(Directory d : listOfDirectory) {
				
				int directoryID = d.getDirectoryID();
				
				if(fileDirectory == directoryID) {
					d.addNewFile(f);
					break;
				}
			}
		}
		
		return listOfDirectory;
		
	}
}
