package eagleeye.dbcontroller;

import java.sql.*;

import eagleeye.entities.Device;
import eagleeye.entities.File;

public class DBInsertController {

	protected final int NO_SUCH_EXT_FOUND = -1;
	protected int deviceID;
	DBInsertQueries queryMaker;
	
	public DBInsertController(){
		
		deviceID = -1;
		queryMaker = new DBInsertQueries();
	}
	
	public int insertNewDevice(Device newDevice, Statement stmt) throws Exception{
		
		String query = queryMaker.insertNewDevice(newDevice);
		String getKey = queryMaker.getKey();
				
		stmt.execute(query);
		ResultSet resultKey = stmt.executeQuery(getKey);
			
		while(resultKey.next()){
				
			deviceID = resultKey.getInt(1);					
		}
				
		return deviceID;
	}
	
	public void insertNewRootDirectory(Statement stmt) throws Exception {
		
		String query = queryMaker.insertNewRootDirectory(deviceID);
		stmt.execute(query);
	}
	
	public void insertNewDirectory(File newDirectory, Statement stmt) throws Exception{
		
		String query = queryMaker.insertNewDirectory(newDirectory, this.deviceID);
		stmt.execute(query);
		
	}
	
	public void insertNewFile(File newFile, int directoryID, int fileExtID, Statement stmt) throws Exception {
		
		String query = queryMaker.insertNewFile(newFile, deviceID, fileExtID, directoryID);
		stmt.execute(query);
		
	}
	
	public int insertNewFileExt(String fileExtName, int extTypeID, Statement stmt) throws Exception {
		
		int fileExtID = -1;
		String query = queryMaker.insertNewExt(fileExtName, extTypeID);
		String getKey = queryMaker.getKey();
		stmt.execute(query);
		
		ResultSet resultKey = stmt.executeQuery(getKey);
		while(resultKey.next()){
				
			fileExtID = resultKey.getInt(1);					
		}
		
		return fileExtID;
		
	}
	
	public int getFileExtID(String fileExt, Statement stmt) throws Exception {
		
		String query = queryMaker.getFileExtID(fileExt);
		ResultSet resultKey = stmt.executeQuery(query);
		int fileExtID = -1;
		
		if(resultKey == null) {
			
			return NO_SUCH_EXT_FOUND;
			
		} else {
			
			while(resultKey.next()){
				
				fileExtID = resultKey.getInt(1);
			}
		}		
		return fileExtID;
	}
	
	public int getDirectoryID(int fileParentID, Statement stmt) throws Exception {
		
		String query = queryMaker.getDirectoryID(fileParentID, deviceID);
		ResultSet resultKey = stmt.executeQuery(query);
		int directoryID = -1;
		
		while(resultKey.next()){
			
			directoryID = resultKey.getInt(1);					
		}
		
		return directoryID;
	}
	
}