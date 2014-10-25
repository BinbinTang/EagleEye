package eagleeye.dbcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import eagleeye.entities.*;

public class DBInsertTransaction {

	protected int deviceID;
	protected DBInsertController controller;
	protected DBInsertQueries queryMaker;
	protected ArrayList<FileEntity> listOfDirectory;
	protected ArrayList<FileEntity> listOfFiles;
	
	public DBInsertTransaction(){
		
		listOfDirectory = new ArrayList<FileEntity>();
		listOfFiles = new ArrayList<FileEntity>();
		deviceID = -1;
		controller = new DBInsertController();
		queryMaker = new DBInsertQueries();
		
	}
	
	public boolean insertNewDeviceData(Device newDevice, ArrayList<FileEntity> FilesList) {
	
		separateFilesAndDirectory(FilesList);
		
		System.out.println("FilesList size = " + FilesList.size());
		System.out.println("DirectoryList size = "+ listOfDirectory.size());
		System.out.println("newFileList size =" + listOfFiles.size());
		
		Connection conn = DBConnection.dbConnector();
		PreparedStatement stmt = null;
		Statement qstmt = null;
		
		try {
			conn.setAutoCommit(false);
			qstmt = conn.createStatement();
			stmt = conn.prepareStatement(queryMaker.insertNewDevice());
			deviceID = controller.insertNewDevice(newDevice, stmt, qstmt);
			System.out.println("Device insert success");
			
			stmt = conn.prepareStatement(queryMaker.insertNewRootDirectory());
			controller.insertNewRootDirectory(stmt);
			System.out.println("Root Directory insert success");
			
			stmt = conn.prepareStatement(queryMaker.insertNewDirectory());
			controller.insertNewDirectory(listOfDirectory, stmt);
			conn.commit();
			System.out.println("All Directories insert success");
			
			stmt = conn.prepareStatement(queryMaker.updateDirectoryRoute());
			controller.updateDirectoryRoute(stmt);
			conn.commit();
			System.out.println("Directories route success");
						
			stmt = conn.prepareStatement(queryMaker.insertNewExt());
			controller.insertNewFileExt(listOfFiles,stmt);
			conn.commit();
			System.out.println("All fileExt insert success");
			
			stmt = conn.prepareStatement(queryMaker.insertNewFile());
			controller.insertNewFile(listOfFiles, stmt);
			conn.commit();
			System.out.println("All Files insert success");
			
			stmt = conn.prepareStatement(queryMaker.updateFileExtID());
			controller.updateFileExtID(stmt);
			conn.commit();
			System.out.println("All fileExtID updated");
			
			stmt = conn.prepareStatement(queryMaker.updateFileDirectoryID());
			controller.updateFileDirectoryID(stmt);
			conn.commit();
			System.out.println("All Files' directoryID updated");
			
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
	public void separateFilesAndDirectory(ArrayList<FileEntity> FilesList) {
		
		for(FileEntity file : FilesList){
			
			if(file.getIsDirectory()) {
				
				FileEntity newDirectory = file;
				listOfDirectory.add(newDirectory);
				
			} else {
				
				FileEntity newFile = file;
				listOfFiles.add(newFile);
				
			}
		}
		
		Collections.sort(listOfDirectory);
		Collections.sort(listOfFiles);		
	}
}
