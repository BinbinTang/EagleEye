package eagleeye.entities;

import eagleeye.api.entities.EagleFile;

public class FileEntity implements EagleFile {
	
	protected int deviceID;
	//parent object ID when inserted; directoryID when queried
	protected int directoryID;
	//object ID when inserted; fileID when queried
	protected int fileID; 			 
	protected boolean isRecovered;
	protected boolean isModified;
	protected boolean isDirectory;
	//Can be a filename or a directory name 
	protected String fileName;
	protected String filePath;
	protected String fileExt; 		// example gif, jpg without .
	protected int fileExtID; 		
	protected String contentType;
	protected String dateCreated;
	protected String dateAccessed;
	protected String dateModified;
	protected String category;
	protected String directoryName;
		
	public FileEntity() {
		
		isRecovered = false;
		isModified = false;
		isDirectory = false;
		fileName = "";
		filePath = "";
		fileExt = "";
		contentType = "";
		dateCreated = "";
		dateAccessed = "";
		dateModified = "";
		category = "";
	}
	/*new*/
	public FileEntity(int _deviceID,
	 			int _directoryID,
	 			int _fileID,
				boolean _isRecovered,
				boolean _isModified,
				boolean _isDirectory,
				String _fileName,
				String _filePath,
				String _fileExt,
				String _contentType,
				String _dateCreated,
				String _dateAccessed,
				String _dateModified,
				String _category) {
		
		modifyDeviceID(_deviceID);
		modifyDirectoryID(_directoryID);
		modifyFileID(_fileID);
		modifyIsRecovered(_isRecovered);
		modifyIsModified(_isModified);
		modifyIsDirectory(_isDirectory); 
		modifyFileName (_fileName);
		modifyFilePath (_filePath);
		modifyFileExt (_fileExt);
		modifyContentType (_contentType);
		modifyDateCreated (_dateCreated);
		modifyDateAccessed (_dateAccessed);
		modifyDateModified (_dateModified);
		modifyCategory(_category);
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
		
	public boolean getIsModified(){
	 
		return isModified;
	}
	
	public String getContentType(){
		
		return contentType;
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
	
	public String getCategory(){
		 
		return category;
	}
	
	public String getDirectoryName(){
		
		return directoryName;
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
	
	public void modifyIsModified(boolean isModified){
	 
		this.isModified = isModified;
	}
	
	public void modifyContentType(String contentType){
		
		this.contentType = contentType;
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
	
	public void modifyCategory(String category){
		
		this.category = category;
	}
	
	public void modifyDirectoryName(String directoryName) {
		
		this.directoryName = directoryName;
	}


}
