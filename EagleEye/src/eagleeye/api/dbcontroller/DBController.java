package eagleeye.api.dbcontroller;

import eagleeye.api.entities.*;
import java.util.ArrayList;


public interface DBController {

	public void setDeviceID(int deviceID);
	
	public int getDeviceID();
	
	public String getDeviceRootPath();
	
	public ArrayList<EagleDevice> getAllDevices();
		
	public ArrayList<String> getAllDeviceNames();
	
	//use for tree-view
	//Call this method if using tree
	public ArrayList<EagleDirectory> getAllDirectoriesAndFiles();
	
	public ArrayList<EagleDirectory> getAllDirectories();
	
	public ArrayList<EagleFile> getAllFiles();
		
	public ArrayList<EagleFile> getFilteredFiles(EagleFilter filter);
	
	
}
