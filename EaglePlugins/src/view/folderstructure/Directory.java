package view.folderstructure;

import java.util.ArrayList;

import eagleeye.api.entities.EagleDirectory;
import eagleeye.api.entities.EagleFile;

//This class is used when querying
public class Directory implements EagleDirectory{

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
	@Override
	public int getDeviceID(){

		return deviceID;
	}
	@Override
	public int getDirectoryID(){

		return directoryID;
	}
	@Override
	public String getDirectoryName(){

		return directoryName;
	}
	@Override
	public int getParentDirectory(){
		
		return parentDirectory;
	}
	@Override
	public String getDateCreated(){
		 
		return dateCreated;
	}
	@Override
	public String getDateAccessed(){
	 
		return dateAccessed;
	}
	@Override
	public String getDateModified(){
		
		return dateModified;
	}
	@Override
	public boolean getIsRecovered(){
		
		return isRecovered;
	}
	@Override
	public ArrayList<EagleFile> getFiles(){
		
		return fileList;
	}
	@Override
	public void modifyDeviceID(int deviceID){
		
		this.deviceID = deviceID;
	}
	@Override
	public void modifyDirectoryID(int directoryID){
		
		this.directoryID = directoryID;
	}
	@Override
	public void modifyDirectoryName(String directoryName){
		
		this.directoryName = directoryName;
	}
	@Override
	public void modifyParentDirectory(int parentDirectory){
		
		this.parentDirectory= parentDirectory;
	}
	@Override
	public void modifyDateCreated(String dateCreated){
		 
		this.dateCreated = dateCreated;
	}
	@Override
	public void modifyDateAccessed(String dateAccessed){
	 
		this.dateAccessed = dateAccessed;
	}
	@Override
	public void modifyDateModified(String dateModified){
		
		this.dateModified = dateModified;		
	}
	@Override
	public void modifyIsRecovered(boolean isRecovered){
		
		this.isRecovered = isRecovered;
	}
	@Override
	public void addNewFile(EagleFile newFile){
		
		this.fileList.add(newFile);
	}
 
}
