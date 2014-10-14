package eagleeye.entities;

public class File implements Comparable<File> {
	
	protected int deviceID;
	protected int directoryID; 		//object parentID
	protected int fileID; 			//object fileID
	protected boolean isDirectory;
	protected String fileName;
	protected String filePath;
	protected String fileExt; 		// example .gif, .jpg 
	protected int fileExtID; 		// currently not used
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
		isDirectory = false;
		fileName = "";
		filePath = "";
		fileExt = "";
		modifiedExt = "";
		dateDeleted = "";
		dateCreated = "";
		dateAccessed = "";
		dateModified = "";
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
		
	public boolean getIsDirectory(){
		
		return isDirectory;
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
	
	public int getFileExtID(){
		 
		return fileExtID;
	}
		
	public boolean getIsRecovered(){
	 
		return isRecovered;
	}
	
	public String getDateDeleted(){
		 
		return dateDeleted;
	}
		
	public boolean getIsModified(){
	 
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
	
	public void modifyIsDirectory(boolean isDirectory){
		
		this.isDirectory = isDirectory;
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
	
	public void modifyFileExtID(int fileExtID){
		 
		this.fileExtID = fileExtID;
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

	@Override
	public int compareTo(File anotherFile) {
		
		int anotherFileDirectory = anotherFile.getDirectoryID();
		return this.getDirectoryID() - anotherFileDirectory;
	}



 
}
