package eagleeye.dbcontroller;

import java.sql.*;
import java.util.ArrayList;

import eagleeye.entities.Device;
import eagleeye.entities.FileEntity;

public class DBInsertController {

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
		int rootParent = -2;
		int rootID = -1;
		
		stmt.setString(1, rootName);
		stmt.setInt(2, deviceID);
		stmt.setInt(3, rootParent);
		stmt.setInt(4, rootID);
		stmt.execute();
	}
	
	public void insertNewDirectory(ArrayList<FileEntity> listOfDirectory, PreparedStatement stmt) throws Exception{
			
		for(FileEntity newDirectory : listOfDirectory){
			
			stmt.setString(1, newDirectory.getDirectoryName());
			stmt.setInt(2, deviceID);
			stmt.setInt(3, newDirectory.getDirectoryID());
			stmt.setInt(4, newDirectory.getFileID());
			stmt.setString(5, newDirectory.getDateCreated());
			stmt.setString(6, newDirectory.getDateAccessed());
			stmt.setString(7, newDirectory.getDateModified());
			stmt.setBoolean(8, newDirectory.getIsRecovered());
			stmt.setString(9, newDirectory.getDateDeleted());
			
			stmt.addBatch();
		}		
		stmt.executeBatch();
		stmt.clearBatch();
	}
	
	public void insertNewFile(ArrayList<FileEntity> listOfFiles, PreparedStatement stmt) throws Exception {
		
		for(FileEntity newFile : listOfFiles) {
			
			stmt.setString(1, newFile.getFileName());
			stmt.setString(2, newFile.getFileExt());
			stmt.setBoolean(3, newFile.getIsRecovered());
			stmt.setString(4, newFile.getDateDeleted());
			stmt.setBoolean(5, newFile.getIsModified());
			stmt.setString(6, newFile.getModifiedExt());
			stmt.setString(7, newFile.getDateCreated());
			stmt.setString(8, newFile.getDateAccessed());
			stmt.setString(9, newFile.getDateModified());
			stmt.setString(10, newFile.getFilePath());
			stmt.setInt(11, newFile.getDirectoryID());
			stmt.setInt(12, deviceID);
			
			stmt.addBatch();
		}
		stmt.executeBatch();
		stmt.clearBatch();
	}
	
	public void insertNewFileExt(ArrayList<FileEntity> listOfFiles, PreparedStatement stmt) throws Exception {
		
		ArrayList<String> extList = getAllFileExt(listOfFiles);
		int newExtTypeID = 6;
		
		for(String ext: extList){
			
			stmt.setString(1,ext);
			stmt.setInt(2, newExtTypeID);
			
			stmt.addBatch();
		}
		stmt.executeBatch();
		stmt.clearBatch();
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