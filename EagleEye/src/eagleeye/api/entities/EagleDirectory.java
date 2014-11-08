package eagleeye.api.entities;

import java.util.ArrayList;

import eagleeye.entities.FileEntity;

public interface EagleDirectory {
	public int getDeviceID();
	public int getDirectoryID();	
	public String getDirectoryName();	
	public int getParentDirectory();	
	public String getDateCreated();	
	public String getDateAccessed();	
	public String getDateModified();	
	public boolean getIsRecovered();	
	public ArrayList<EagleFile> getFiles();
	public void modifyDeviceID(int deviceID);	
	public void modifyDirectoryID(int directoryID);	
	public void modifyDirectoryName(String directoryName);	
	public void modifyParentDirectory(int parentDirectory);	
	public void modifyDateCreated(String dateCreated);	
	public void modifyDateAccessed(String dateAccessed);	
	public void modifyDateModified(String dateModified);	
	public void modifyIsRecovered(boolean isRecovered);	
	public void addNewFile(EagleFile newFile);
}
