package eagleeye.dbcontroller;

import eagleeye.entities.*;

import java.sql.*;
import java.util.ArrayList;

public class DBQueryController {

	protected int deviceID;
	protected DBQueries queryMaker;
		
	public DBQueryController() {
		System.out.println("new DB set");
		this.deviceID = -1;
		queryMaker = new DBQueries();
	}
	
	public void setDeviceID(int deviceID){
		
		this.deviceID = deviceID;
	}
	
	public int getDeviceID(){
	
		return deviceID;
	}
	
	public ArrayList<Device> getAllDevices() {
		
		ArrayList<Device> listOfDevices = new ArrayList<Device> (); 
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllDevices());
			ResultSet rs = stmt.executeQuery();
			
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
		
		System.out.println("CaiJun: D_List Size =" + listOfDirectory.size());
		System.out.println("CaiJun: F_List Size =" + listOfFiles.size());
		
		ArrayList<Directory> organizedDirectoryList = this.organizeFilesAndDirectory(listOfDirectory, listOfFiles);
		
		return organizedDirectoryList;
		
	}
	
	public ArrayList<Directory> getAllDirectories(){
		
		ArrayList<Directory> listOfDirectory = new ArrayList<Directory>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllDirectories());
			stmt.setInt(1, deviceID);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				
				Directory d = new Directory();
				d.modifyDirectoryID(rs.getInt("DirectoryID"));
				d.modifyDirectoryName(rs.getString("DirectoryName"));
				d.modifyDeviceID(rs.getInt("DeviceID"));
				d.modifyParentDirectory(rs.getInt("ParentDirectoryID"));
				d.modifyDateCreated(rs.getString("DateCreated"));
				d.modifyDateAccessed(rs.getString("DateAccessed"));
				d.modifyDateModified(rs.getString("DateModified"));
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
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllFiles());
			stmt.setInt(1, deviceID);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				
				FileEntity f = new FileEntity();
			
				f.modifyFileName(rs.getString("FileName"));
				f.modifyFileID(rs.getInt("FileID"));
				f.modifyDirectoryID(rs.getInt("DirectoryID"));
				f.modifyDirectoryName(rs.getString("DirectoryName"));
				f.modifyFileExt(rs.getString("FileExt"));
				f.modifyFileExtID(rs.getInt("FileExtID"));
				f.modifyDateCreated(rs.getString("DateCreated"));
				f.modifyDateAccessed(rs.getString("DateAccessed"));
				f.modifyDateModified(rs.getString("DateModified"));
				f.modifyIsModified(rs.getBoolean("IsModified"));
				f.modifyContentType(rs.getString("ContentType"));
				f.modifyIsRecovered(rs.getBoolean("IsRecovered"));
				f.modifyFilePath(rs.getString("FilePath"));
				f.modifyCategory(rs.getString("ExtTypeName"));
								
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
	
	public ArrayList<FileEntity> getFilteredFiles(Filter filter) {
		
		boolean isKeywordPresent = isKeywordPresent(filter.getKeyword());
		boolean isCategoryPresent = isCategoryPresent(filter.getCategoryName());
		ArrayList<FileEntity> listOfFiles = new ArrayList<FileEntity>();
		
		Connection conn = DBConnection.dbConnector();
		
		try {
			
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getFilteredFiles(isKeywordPresent, isCategoryPresent));
			stmt = setFieldsForFilter(stmt,isKeywordPresent,isCategoryPresent,filter);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				
				FileEntity f = new FileEntity();
			
				f.modifyFileName(rs.getString("FileName"));
				f.modifyFileID(rs.getInt("FileID"));
				f.modifyDirectoryID(rs.getInt("DirectoryID"));
				f.modifyDirectoryName(rs.getString("DirectoryName"));
				f.modifyFileExt(rs.getString("FileExt"));
				f.modifyFileExtID(rs.getInt("FileExtID"));
				f.modifyDateCreated(rs.getString("DateCreated"));
				f.modifyDateAccessed(rs.getString("DateAccessed"));
				f.modifyDateModified(rs.getString("DateModified"));
				f.modifyIsModified(rs.getBoolean("IsModified"));
				f.modifyContentType(rs.getString("ContentType"));
				f.modifyIsRecovered(rs.getBoolean("IsRecovered"));
				f.modifyFilePath(rs.getString("FilePath"));
				f.modifyCategory(rs.getString("ExtTypeName"));
								
				listOfFiles.add(f);
			}
			
			conn.close();
		} catch (Exception e) {
			
			System.out.println("Filter Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return listOfFiles;
		
	}
	
	//HELPER METHODS SECTION
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
	
	private PreparedStatement setFieldsForFilter(PreparedStatement stmt,boolean isKeywordPresent,boolean isCategoryPresent, Filter filter) throws SQLException {
		
		if(isKeywordPresent && isCategoryPresent) {
			
			stmt.setInt(1,deviceID);
			stmt.setString(2, filter.getKeyword());
			stmt.setInt(3, filter.getCategoryID());
			stmt.setString(4, filter.getStartDateAsString());
			stmt.setString(5, filter.getEndDateAsString());
			stmt.setString(6, filter.getStartTime());
			stmt.setString(7, filter.getEndTime());
			stmt.setInt(8, filter.getIsModified());
			stmt.setInt(9, filter.getIsRecovered());
						
		} else {
			
			if(isKeywordPresent) {
				
				stmt.setInt(1,deviceID);
				stmt.setString(2, filter.getKeyword());
				stmt.setString(3, filter.getStartDateAsString());
				stmt.setString(4, filter.getEndDateAsString());
				stmt.setString(5, filter.getStartTime());
				stmt.setString(6, filter.getEndTime());
				stmt.setInt(7, filter.getIsModified());
				stmt.setInt(8, filter.getIsRecovered());
			
			} else if (isCategoryPresent) {
				
				stmt.setInt(1,deviceID);
				stmt.setInt(2, filter.getCategoryID());
				stmt.setString(3, filter.getStartDateAsString());
				stmt.setString(4, filter.getEndDateAsString());
				stmt.setString(5, filter.getStartTime());
				stmt.setString(6, filter.getEndTime());
				stmt.setInt(7, filter.getIsModified());
				stmt.setInt(8, filter.getIsRecovered());
			
			} else {
				
				stmt.setInt(1,deviceID);
				stmt.setString(2, filter.getStartDateAsString());
				stmt.setString(3, filter.getEndDateAsString());
				stmt.setString(4, filter.getStartTime());
				stmt.setString(5, filter.getEndTime());
				stmt.setInt(6, filter.getIsModified());
				stmt.setInt(7, filter.getIsRecovered());
			}
			
		}
		
		return stmt;
		
	}
	
	private boolean isKeywordPresent(String keyword) {
		
		String emptyString = "";
		
		if (keyword.equals(emptyString))
			return false;
		else
			return true;
	}
	
	private boolean isCategoryPresent(String categoryName) {
		
		String emptyString = "";
		
		if (categoryName.equals(emptyString))
			return false;
		else
			return true;
		
	}
}
