package extractor.photorec;

import java.util.ArrayList;

import eagleeye.api.entities.EagleDirectory;
import eagleeye.api.entities.EagleFile;


public class PhotoRecDirectory implements EagleDirectory{
	protected String directoryName;		//Name of the directory
	protected int parentDirectory; 		//parent Object ID
	protected int deviceID;			
	protected int directoryID; 			//Own Object ID
	protected String dateCreated;
	protected String dateAccessed;
	protected String dateModified;
	protected boolean isRecovered;
	protected String dateDeleted;
	protected ArrayList<EagleFile> fileList;
	
	public PhotoRecDirectory(){
		
		isRecovered = false;
		fileList = new ArrayList<EagleFile>();
	}
	
	public PhotoRecDirectory(int _deviceID, int _directoryID, String _directoryName,
			int _parentDirectory, String _dateCreated, String _dateAccessed, String _dateModified,
			String _dateDeleted, boolean _isRecovered){
		
		modifyDeviceID(_deviceID);	
		modifyDirectoryID(_directoryID);
		modifyDirectoryName(_directoryName);
		modifyParentDirectory(_parentDirectory);
		modifyDateCreated(_dateCreated);
		modifyDateAccessed(_dateAccessed);
		modifyDateModified(_dateModified);
		modifyDateDeleted(_dateDeleted);
		modifyIsRecovered(_isRecovered);
		fileList = new ArrayList<EagleFile>();
	}

	public int getDeviceID(){

		return deviceID;
	}
 
	public int getDirectoryID(){

		return directoryID;
	}
	
	public String getDirectoryName(){

		return directoryName;
	}
	
	public int getParentDirectory(){
		
		return parentDirectory;
	}
	
	public String getDateCreated(){
		 
		return dateCreated;
	}
	
	public String getDateAccessed(){
	 
		return dateAccessed;
	}
	
	public String getDateModified(){
		
		return dateModified;
	}
	
	public boolean getIsRecovered(){
		
		return isRecovered;
	}
	
	public String getDateDeleted(){
		
		return dateDeleted;
	}
	
	public ArrayList<EagleFile> getFiles(){
		
		return fileList;
	}

	public void modifyDeviceID(int deviceID){
		
		this.deviceID = deviceID;
	}
	
	public void modifyDirectoryID(int directoryID){
		
		this.directoryID = directoryID;
	}
	
	public void modifyDirectoryName(String directoryName){
		
		this.directoryName = directoryName;
	}
	
	public void modifyParentDirectory(int parentDirectory){
		
		this.parentDirectory= parentDirectory;
	}
	
	public void modifyDateCreated(String dateCreated){
		 
		this.dateCreated = dateCreated;
	}
	
	public void modifyDateAccessed(String dateAccessed){
	 
		this.dateAccessed = dateAccessed;
	}
	
	public void modifyDateModified(String dateModified){
		
		this.dateModified = dateModified;		
	}
	
	public void modifyDateDeleted(String dateDeleted){
		
		this.dateDeleted = dateDeleted;
	}
	
	public void modifyIsRecovered(boolean isRecovered){
		
		this.isRecovered = isRecovered;
	}
	
	public void addNewFile(EagleFile newFile){
		this.fileList.add(newFile);
	}
}
