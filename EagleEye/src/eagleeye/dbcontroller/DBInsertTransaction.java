package eagleeye.dbcontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eagleeye.entities.*;
import eagleeye.api.entities.*;

public class DBInsertTransaction {

	protected int deviceID;
	protected DBInsertController controller;
	protected DBInsertQueries queryMaker;
	protected ArrayList<EagleFile> listOfDirectory;
	protected ArrayList<EagleFile> listOfFiles;
	
	public DBInsertTransaction(){
		
		listOfDirectory = new ArrayList<EagleFile>();
		listOfFiles = new ArrayList<EagleFile>();
		deviceID = -1;
		controller = new DBInsertController();
		queryMaker = new DBInsertQueries();
		
	}
	
	public boolean insertNewDeviceData(Device newDevice, List<List<EagleFile>> filesLists) {
	
		Connection conn = DBConnection.dbConnector();
		PreparedStatement stmt = null;
		Statement qstmt = null;

		int markerLower = 0;
		int markerUpper = 0;

		try {
			conn.setAutoCommit(false);
			qstmt = conn.createStatement();
			stmt = conn.prepareStatement(queryMaker.insertNewDevice());
			deviceID = controller.insertNewDevice(newDevice, stmt, qstmt);
			System.out.println("Device insert success");
			
			stmt = conn.prepareStatement(queryMaker.getDirectoryAutoIncrementMarker());
			markerLower = controller.getDirectoryAutoIncrementMarker(stmt);
			conn.commit();
		
			stmt = conn.prepareStatement(queryMaker.insertNewRootDirectory());
			controller.insertNewRootDirectory(stmt);
			System.out.println("Root Directory insert success");
		
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
		
			try {
				conn.rollback();
			
			} catch (SQLException e2) {
			
				System.out.println("conn Rollback Failed");
			}
		}
		
		for(List<EagleFile> filesList : filesLists) {
			
			separateFilesAndDirectory(filesList);
		
			System.out.println("FilesList size = " + filesList.size());
			System.out.println("DirectoryList size = "+ listOfDirectory.size());
			System.out.println("newFileList size =" + listOfFiles.size());
				
			try {

				markerUpper = markerLower + listOfDirectory.size();
				stmt = conn.prepareStatement(queryMaker.insertNewDirectory());
				controller.insertNewDirectory(listOfDirectory, stmt);
				conn.commit();
				System.out.println("All Directories insert success");
			
				stmt = conn.prepareStatement(queryMaker.updateDirectoryRoute());
				controller.updateDirectoryRoute(stmt, markerLower, markerUpper);
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
				controller.updateFileDirectoryID(stmt, markerLower, markerUpper);
				conn.commit();
				System.out.println("All Files' directoryID updated");
				
				stmt = conn.prepareStatement(queryMaker.getDirectoryAutoIncrementMarker());
				markerLower = controller.getDirectoryAutoIncrementMarker(stmt);
				conn.commit();
										
			} catch (Exception e) {
			
				System.out.println(e.getMessage());
				e.printStackTrace();
			
				try {
					conn.rollback();
					conn.close();
					return false;
				} catch (SQLException e2) {
				
					System.out.println("conn Rollback Failed");
				}
			}
		}
		try {
			conn.close();
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
		
			try {
				conn.rollback();
				conn.close();
				return false;
			} catch (SQLException e2) {
			
				System.out.println("conn Rollback Failed");
			}
		} 
		return true;
		
	}
	
	//Helper Methods
	public void separateFilesAndDirectory(List<EagleFile> FilesList) {
		
		listOfDirectory = new ArrayList<EagleFile>();
		listOfFiles = new ArrayList<EagleFile>();
		
		for(EagleFile file : FilesList){
			
			if(file.getIsDirectory()) {
				
				EagleFile newDirectory = file;
				listOfDirectory.add(newDirectory);
				
			} else {
				
				EagleFile newFile = file;
				listOfFiles.add(newFile);
				
			}
		}
					
	}
	
	public int getDeviceID () {
		
		return this.deviceID;
	}
}
