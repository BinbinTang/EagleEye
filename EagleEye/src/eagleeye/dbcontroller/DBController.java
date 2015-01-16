package eagleeye.dbcontroller;

import eagleeye.entities.*;

import java.util.ArrayList;


public interface DBController {

	public void setDeviceID(int deviceID);
	
	public int getDeviceID();
	
	public String getDeviceRootPath();
	
	public ArrayList<Device> getAllDevices();
		
	
	public ArrayList<String> getAllDeviceNames();
	
	//use for tree-view
	//Call this method if using tree
	public ArrayList<Directory> getAllDirectoriesAndFiles();
	
	public ArrayList<Directory> getAllDirectories();
	
	public ArrayList<FileEntity> getAllFiles();
		
	public ArrayList<FileEntity> getFilteredFiles(Filter filter);
	
	
}
