package eagleeye.dbcontroller;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import eagleeye.entities.*;

public class DBInsertTransaction {

	protected final int NO_SUCH_EXT_FOUND = -1;
	protected final int OTHER_EXT_TYPE_ID = 6;
	protected int deviceID;
	protected DBInsertController controller;
	protected ArrayList<File> listOfDirectory;
	protected ArrayList<File> listOfFiles;
	
	public DBInsertTransaction(){
		
		listOfDirectory = new ArrayList<File>();
		listOfFiles = new ArrayList<File>();
		deviceID = -1;
		controller = new DBInsertController();
		
	}
	
	public boolean insertNewDeviceData(Device newDevice, ArrayList<File> FilesList) {
	
		separateFilesAndDirectory(FilesList);
		
		Connection conn = DBConnection.dbConnector();
		Statement stmt = null;
				
		try {
			stmt = conn.createStatement();
			deviceID = controller.insertNewDevice(newDevice, stmt);
			System.out.println("Device insert success");
			
			controller.insertNewRootDirectory(stmt);
			System.out.println("Root Directory insert success");
			
			for(File newDirectory : listOfDirectory){
				
				controller.insertNewDirectory(newDirectory, stmt);
			}
			System.out.println("All Directories insert success");
			
			for(File newFile : listOfFiles){
				
				int fileExt = controller.getFileExtID(newFile.getFileExt(), stmt);
				int directoryID = controller.getDirectoryID(newFile.getDirectoryID(), stmt);
				
				if(fileExt == NO_SUCH_EXT_FOUND) {
					//insert a new extension under others.
					fileExt = controller.insertNewFileExt(newFile.getFileExt(), OTHER_EXT_TYPE_ID, stmt);
					System.out.println("New ext insert success");
				}	
					
				controller.insertNewFile(newFile, directoryID, fileExt, stmt);
								
			}
			System.out.println("ALL files insert success");
			
			conn.commit();
			conn.close();
			
		} catch (Exception e) {
						
			try {
				conn.rollback();
				conn.close();
				
			} catch (SQLException e2) {
				
				System.out.println("Rollback Failed");
			}
		}
		return false;
	}
	
	//Helper Methods
	public void separateFilesAndDirectory(ArrayList<File> FilesList) {
		
		for(File file : FilesList){
			
			if(file.getIsDirectory()) {
				
				File newDirectory = file;
				listOfDirectory.add(newDirectory);
				
			} else {
				
				File newFile = file;
				listOfFiles.add(newFile);
				
			}
		}
		
		Collections.sort(listOfDirectory);
		Collections.sort(listOfFiles);		
	}
}
