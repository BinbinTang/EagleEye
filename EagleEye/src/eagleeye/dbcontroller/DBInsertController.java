package eagleeye.dbcontroller;

import java.sql.*;
import java.util.ArrayList;

import eagleeye.entities.Device;
import eagleeye.entities.FileEntity;

public class DBInsertController {

	private final int OTHER_EXT_TYPE_ID = 7;
	protected int deviceID;
	protected DBInsertQueries queryMaker;
	
	public DBInsertController(){
		
		deviceID = -1;
		queryMaker = new DBInsertQueries();
	}
	
	public int insertNewDevice(Device newDevice, PreparedStatement stmt, Statement qstmt) throws Exception{
		
		stmt.setString(1, newDevice.getDeviceName());
		stmt.setString(2, newDevice.getDeviceOwner());
		stmt.setString(3, newDevice.getContentSize());
		
		stmt.execute();

		String getKey = queryMaker.getKey();
		ResultSet resultKey = qstmt.executeQuery(getKey);
			
		while(resultKey.next()){
				
			deviceID = resultKey.getInt(1);					
		}
				
		return deviceID;
	}
	
	public void insertNewRootDirectory(PreparedStatement stmt) throws Exception {
		
		String rootName = "root";
		int rootParent = -1;
		int rootID = 0;
		
		stmt.setString(1, rootName);
		stmt.setInt(2, deviceID);
		stmt.setInt(3, rootParent);
		stmt.setInt(4, rootID);
		stmt.execute();
	}
	
	public void insertNewDirectory(ArrayList<FileEntity> listOfDirectory, PreparedStatement stmt) throws Exception{
			
		for(FileEntity newDirectory : listOfDirectory){
			
			stmt.setString(1, newDirectory.getFileName());
			stmt.setInt(2, newDirectory.getFileID());
			stmt.setInt(3, newDirectory.getDirectoryID());
			stmt.setInt(4, deviceID);
			stmt.setString(5, newDirectory.getDateCreated());
			stmt.setString(6, newDirectory.getDateAccessed());
			stmt.setString(7, newDirectory.getDateModified());
			stmt.setBoolean(8, newDirectory.getIsRecovered());
			
			stmt.addBatch();
		}		
		stmt.executeBatch();
		stmt.clearBatch();
	}
	
	public void updateDirectoryRoute(PreparedStatement stmt) throws Exception {

		stmt.setInt(1, deviceID);
		stmt.setInt(2, deviceID);
		stmt.execute();
		
	}
	
	public void insertNewFileExt(ArrayList<FileEntity> listOfFiles, PreparedStatement stmt) throws Exception {
		
		ArrayList<String> extList = getAllFileExt(listOfFiles);
		int newExtTypeID = OTHER_EXT_TYPE_ID;
		
		for(String ext: extList){
			
			stmt.setString(1,ext);
			stmt.setInt(2, newExtTypeID);
			
			stmt.addBatch();
		}
		stmt.executeBatch();
		stmt.clearBatch();
	}
	
	public void insertNewFile(ArrayList<FileEntity> listOfFiles, PreparedStatement stmt) throws Exception {
		
		for(FileEntity newFile : listOfFiles) {
			
			stmt.setString(1, newFile.getFileName());
			stmt.setString(2, newFile.getFileExt());
			stmt.setString(3, newFile.getDateCreated());
			stmt.setString(4, newFile.getDateAccessed());
			stmt.setString(5, newFile.getDateModified());
			stmt.setBoolean(6, newFile.getIsModified());
			stmt.setString(7, newFile.getContentType());
			stmt.setBoolean(8, newFile.getIsRecovered());
			stmt.setString(9, newFile.getFilePath());
			stmt.setInt(10, newFile.getFileID());
			stmt.setInt(11, newFile.getDirectoryID());
			
			stmt.addBatch();
		}
		stmt.executeBatch();
		stmt.clearBatch();
	}
	
	public void updateFileExtID(PreparedStatement stmt) throws Exception {
		
		stmt.execute();
	}

	public void updateFileDirectoryID(PreparedStatement stmt) throws Exception {

		stmt.setInt(1, deviceID);
		stmt.setInt(2, deviceID);
		stmt.execute();
	}

	
	//Helper method
	public ArrayList<String> getAllFileExt(ArrayList<FileEntity> listOfFiles) {
		
		ArrayList<String> extList = new ArrayList<String> ();
		boolean isExtAdded = false;
		
		for(FileEntity newFile : listOfFiles){
			
			isExtAdded = false;
			String fileExt = newFile.getFileExt();
			
			for(String ext : extList){
				if(ext.equals(fileExt)){
					isExtAdded = true;
					break;
				}
			}
			
			if(!isExtAdded)
				extList.add(fileExt);
		}
		
		return extList;
	}
	

	
}