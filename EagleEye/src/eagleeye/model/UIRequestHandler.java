package eagleeye.model;

import java.util.ArrayList;

import eagleeye.dbcontroller.DBQueryController;
import eagleeye.entities.*;

public class UIRequestHandler implements RequestHandler {
	private ArrayList<eagleeye.entities.FileEntity> folderStructure;
	
	@Override
	/*
	/*Return a list of File Object, as defined by entities package
	*/
	public ArrayList<eagleeye.entities.FileEntity> getFolderStructure() {
		if(folderStructure==null){
			folderStructure=new ArrayList<eagleeye.entities.FileEntity>();
		
			// TODO Query database and populate folderStructure
			
			//WenXia, Use the following method:
			DBQueryController dbController = new DBQueryController();
			dbController.setDeviceID(1);
			
			ArrayList<Directory> TreeStructure = dbController.getAllDirectoriesAndFiles();
			
			System.out.println("The number of Directories: " + TreeStructure.size());
			
		}
		return folderStructure;
	}
	
	public ArrayList<Device> getExistingDevices(){
		DBQueryController dbController = new DBQueryController();
		return dbController.getAllDevices();
	}

	@Override
	public String getFilePath(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileSize(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}

