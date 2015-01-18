package eagleeye.model;

import java.util.ArrayList;

import eagleeye.api.entities.*;
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
			
			ArrayList<EagleDirectory> TreeStructure = dbController.getAllDirectoriesAndFiles();
			
			System.out.println("The number of Directories: " + TreeStructure.size());
			
		}
		return folderStructure;
	}
	
	public ArrayList<Device> getExistingDevices(){
		DBQueryController dbController = new DBQueryController();
		ArrayList<Device> ds = new ArrayList<Device>();
		ArrayList<EagleDevice> eds = dbController.getAllDevices();
		for(EagleDevice ed: eds){
			ds.add((Device)ed);
		}
		return ds;
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

