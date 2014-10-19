package eagleeye.pluginmanager;
import eagleeye.api.entities.EagleFile;

public class PhotoRecFile implements EagleFile{

	protected int deviceID;
	protected int directoryID; 		//object parentID
	protected int fileID; 			//object fileID
	protected boolean isRecovered;
	protected boolean isModified;
	protected boolean isDirectory;
	protected String fileName;
	protected String filePath;
	protected String fileExt; 		// example gif, jpg without .
	protected int fileExtID; 		// currently not used
	protected String dateDeleted;
	protected String modifiedExt;
	protected String dateCreated;
	protected String dateAccessed;
	protected String dateModified;
	protected String category;
	
	public PhotoRecFile() {
		
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
		category = "";
	}

	public PhotoRecFile(int _deviceID,
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
		modifyModifiedExt (_modifiedExt);
		modifyDateDeleted (_dateDeleted);
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
	public String getDateDeleted(){
		 
		return dateDeleted;
	}
	
	@Override
	public boolean getIsModified(){
	 
		return isModified;
	}
	
	@Override
	public String getModifiedExt(){
		
		return modifiedExt;
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
	public void modifyDateDeleted(String dateDeleted){
		 
		this.dateDeleted = dateDeleted;
	}
	
	@Override	
	public void modifyIsModified(boolean isModified){
	 
		this.isModified = isModified;
	}
	
	@Override
	public void modifyModifiedExt(String modifiedExt){
		
		this.modifiedExt = modifiedExt;
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

	@Override
	public int compareTo(EagleFile anotherFile) {
		
		int anotherFileDirectory = anotherFile.getDirectoryID();
		return this.getDirectoryID() - anotherFileDirectory;
	}

}
