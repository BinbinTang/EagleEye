package eagleeye.entities;

public class File {
	
	protected int deviceID;
	protected int directoryID;
	protected int fileID;
	protected String fileName;
	protected String filePath;
	protected String fileExt;
	protected boolean isRecovered; 
	protected String dateDeleted;
	protected boolean isModified;
	protected String modifiedExt;
	protected String dateCreated;
	protected String dateAccessed;
	protected String dateModified;
	
	public File() {
		
		isRecovered = false;
		isModified = false;
	}
	 
	public int getDeviceID(){
	
		return deviceID;
	}
	
	public int getDirectoryID(){
		 
		return directoryID;
	}
 
	public int getFileID(){
	 
		return fileID;
	}
	
	public String getFileName(){
	 
		return fileName;
	}	
	
	public String getFilePath(){
	 
		return filePath;
	}
	
	public String getFileExt(){
		 
		return fileExt;
	}
		
	public boolean isRecovered(){
	 
		return isRecovered;
	}
	
	public String getDateDeleted(){
		 
		return dateDeleted;
	}
		
	public boolean isModified(){
	 
		return isModified;
	}
	
	public String getModifiedExt(){
		
		return modifiedExt;
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
 
	public void modifyDeviceID(int deviceID){
	  
		this.deviceID = deviceID;
	}
	
	public void modifyDirectoryID(int directoryID){
		 
		this.directoryID = directoryID;
	}
	
	public void modifyFileID(int fileID){
		  
		this.fileID = fileID;
	}
 
	public void modifyFileName(String fileName){
	  
		this.fileName = fileName;
	}
		
	public void modifyFilePath(String filePath){
	 
		this.filePath = filePath;
	}
	
	public void modifyFileExt(String fileExt){
		 
		this.fileExt = fileExt;
	}
	
	public void modifyIsRecovered(boolean isRecovered){
	 
		this.isRecovered = isRecovered;
	}
	
	public void modifyDateDeleted(String dateDeleted){
		 
		this.dateDeleted = dateDeleted;
	}
		
	public void modifyIsModified(boolean isModified){
	 
		this.isModified = isModified;
	}
	
	public void modifyModifiedExt(String modifiedExt){
		
		this.modifiedExt = modifiedExt;
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
 
}
