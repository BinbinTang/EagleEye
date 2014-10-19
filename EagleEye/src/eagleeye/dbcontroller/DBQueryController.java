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
	
	public ArrayList<Device> getAllDevices() {
		
		ArrayList<Device> listOfDevices = new ArrayList<Device> (); 
		Connection conn = DBConnection.dbConnector();
		
		try {
			Statement stmt = conn.createStatement();
			String query = queryMaker.getAllDevices();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				Device device = new Device();
				device.modifyDeviceID(rs.getInt("DeviceID"));
				device.modifyDeviceName(rs.getString("DeviceName"));
				device.modifyDeviceOwner(rs.getString("DeviceOwner"));
				device.modifyContentSize(rs.getString("ContentSize"));
				device.modifyDateCreated(rs.getString("DateCreated"));
				device.modifyLastViewedOn(rs.getString("LastViewedOn"));
				
				listOfDevices.add(device);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return listOfDevices;
	}
	
	
	//use for tree-view
	//Call this method if using tree
	public ArrayList<Directory> getAllDirectoriesAndFiles() {
		
		ArrayList<Directory> listOfDirectory = getAllDirectories();
		ArrayList<FileEntity> listOfFiles = getAllFiles();
		
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
	
	public ArrayList<FileEntity> getAllFiles() {
		
		ArrayList<FileEntity> listOfFiles = new ArrayList<FileEntity>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			Statement stmt = conn.createStatement();
			String query = queryMaker.getAllFiles(deviceID);
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				
				FileEntity f = new FileEntity();
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
	private ArrayList<Directory> organizeFilesAndDirectory (ArrayList<Directory> listOfDirectory, ArrayList<FileEntity> listOfFiles){
		
		for(FileEntity f : listOfFiles) {
			
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
