package eagleeye.entities;

public class File implements Comparable<File> {
	
	protected int deviceID;
	protected int directoryID; //original parentID (when inserting)
	protected int fileID;
	protected boolean isRecovered;
	protected boolean isModified;
	protected boolean isDirectory;
	protected String fileName;
	protected String filePath;
	protected String fileExt; // example gif, jpg without the .
	protected String dateDeleted;
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
	/*new*/
	public File(int _deviceID,
	 			int _directoryID,
	 			int _fileID,
				boolean _isRecovered,
				boolean _isModified,
				boolean _isDirectory,
				String _fileName,
				String _filePath,
				String _fileExt,
				String _modifiedExt,
				String _dateDeleted,
				String _dateCreated,
				String _dateAccessed,
				String _dateModified) {
		
		modifyDeviceID(_deviceID);
		modifyDirectoryID(_directoryID);
		modifyFileID(_fileID);
		modifyIsRecovered(_isRecovered);
		modifyIsModified(_isModified);
		modifyIsDirectory(_isDirectory); 
		modifyFileName (_fileName);
		modifyFilePath (_filePath);
		modifyFileExt (_fileExt);
		modifyModifiedExt (_modifiedExt);
		modifyDateDeleted (_dateDeleted);
		modifyDateCreated (_dateCreated);
		modifyDateAccessed (_dateAccessed);
		modifyDateModified (_dateModified);
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
