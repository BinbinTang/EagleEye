package eagleeye.entities;

import java.util.ArrayList;

import eagleeye.api.entities.EagleDirectory;
import eagleeye.api.entities.EagleFile;

//This class is used when querying
public class Directory implements EagleDirectory {

	protected String directoryName;		//Name of the directory
	protected int parentDirectory; 		//parent Object ID
	protected int deviceID;			
	protected int directoryID; 			//Own Object ID
	protected String dateCreated;
	protected String dateAccessed;
	protected String dateModified;
	protected boolean isRecovered;
	protected ArrayList<EagleFile> fileList;
	
	public Directory(){
		
		isRecovered = false;
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
	
	public void modifyIsRecovered(boolean isRecovered){
		
		this.isRecovered = isRecovered;
	}
	
	public void addNewFile(EagleFile newFile){
		
		this.fileList.add(newFile);
	}
 
}
