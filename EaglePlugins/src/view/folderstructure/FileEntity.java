package view.folderstructure;

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
	
	@Override
	public int getDeviceID(){
	
		return deviceID;
	}
	
	@Override
	public int getDirectoryID(){
		 
		return directoryID;
	}
 
	@Override
	public int getFileID(){
	 
		return fileID;
	}
		
	@Override
	public boolean getIsDirectory(){
		
		return isDirectory;
	}
	
	@Override
	public String getFileName(){
	 
		return fileName;
	}	
	
	@Override
	public String getFilePath(){
	 
		return filePath;
	}
	
	@Override
	public String getFileExt(){
		 
		return fileExt;
	}
	
	@Override
	public int getFileExtID(){
		 
		return fileExtID;
	}
		
	@Override
	public boolean getIsRecovered(){
	 
		return isRecovered;
	}

	@Override
	public boolean getIsModified(){
	 
		return isModified;
	}
	
	@Override
	public String getContentType(){
		
		return contentType;
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
	public String getCategory(){
		 
		return category;
	}
	
	
	public String getDirectoryName(){
		
		return directoryName;
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
	public void modifyFileID(int fileID){
		  
		this.fileID = fileID;
	}
	
	@Override
	public void modifyIsDirectory(boolean isDirectory){
		
		this.isDirectory = isDirectory;
	}
 
	@Override
	public void modifyFileName(String fileName){
	  
		this.fileName = fileName;
	}
		
	@Override
	public void modifyFilePath(String filePath){
	 
		this.filePath = filePath;
	}
	
	@Override
	public void modifyFileExt(String fileExt){
		 
		this.fileExt = fileExt;
	}
	
	@Override
	public void modifyFileExtID(int fileExtID){
		 
		this.fileExtID = fileExtID;
	}
	
	@Override
	public void modifyIsRecovered(boolean isRecovered){
	 
		this.isRecovered = isRecovered;
	}
	
	@Override
	public void modifyIsModified(boolean isModified){
	 
		this.isModified = isModified;
	}
	
	@Override
	public void modifyContentType(String contentType){
		
		this.contentType = contentType;
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
	public void modifyCategory(String category){
		
		this.category = category;
	}
	

	public void modifyDirectoryName(String directoryName) {
		
		this.directoryName = directoryName;
	}


}
