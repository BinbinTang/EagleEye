package eagleeye.dbcontroller;

import java.sql.*;
import java.util.ArrayList;

import eagleeye.entities.Device;
import eagleeye.entities.File;

public class DBInsertController {

	protected int deviceID;
	protected DBInsertQueries queryMaker;
	
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
	
	public void insertNewDirectory(ArrayList<File> listOfDirectory, Statement stmt) throws Exception{
			
		for(File newDirectory : listOfDirectory){
			stmt.addBatch(queryMaker.insertNewDirectory(newDirectory, deviceID));
		}		
		stmt.executeBatch();
		stmt.clearBatch();
	}
	
	public void insertNewFile(ArrayList<File> listOfFiles, Statement stmt) throws Exception {
		
		for(File newFile : listOfFiles) {
			stmt.addBatch(queryMaker.insertNewFile(newFile, deviceID));
		}
		stmt.executeBatch();
		stmt.clearBatch();
	}
	
	public void insertNewFileExt(ArrayList<File> listOfFiles, Statement stmt) throws Exception {
		
		ArrayList<String> extList = getAllFileExt(listOfFiles);
				
		for(String ext: extList){
			stmt.addBatch(queryMaker.insertNewExt(ext, 6));
		}
		stmt.executeBatch();
		stmt.clearBatch();
	}
	
	public ArrayList<String> getAllFileExt(ArrayList<File> listOfFiles) {
		
		ArrayList<String> extList = new ArrayList<String> ();
		boolean isExtAdded = false;
		
		for(File newFile : listOfFiles){
			
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