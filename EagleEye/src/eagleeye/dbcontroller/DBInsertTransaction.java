package eagleeye.dbcontroller;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import eagleeye.entities.*;

public class DBInsertTransaction {

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
		
		System.out.println("FilesList size = " + FilesList.size());
		System.out.println("DirectoryList size = "+ listOfDirectory.size());
		System.out.println("newFileList size =" + listOfFiles.size());
		
		Connection conn = DBConnection.dbConnector();
		Statement stmt = null;
						
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			deviceID = controller.insertNewDevice(newDevice, stmt);
			System.out.println("Device insert success");
			
			controller.insertNewRootDirectory(stmt);
			System.out.println("Root Directory insert success");
			
			controller.insertNewDirectory(listOfDirectory, stmt);
			conn.commit();
			System.out.println("All Directories insert success");
			
			controller.insertNewFileExt(listOfFiles,stmt);
			conn.commit();
			System.out.println("All fileExt insert success");
			
			controller.insertNewFile(FilesList, stmt);
			conn.commit();
			System.out.println("All Files insert success");
			
			conn.close();
			return true;
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
			
			try {
				conn.rollback();
				conn.close();
				
			} catch (SQLException e2) {
				
				System.out.println("conn Rollback Failed");
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