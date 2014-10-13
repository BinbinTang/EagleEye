package eagleeye.entities;

public class Directory {

	protected String directoryName;		//Name of the directory
	protected String previousDirectory; //parent Object Name (Use for Easy UI display)
	protected int deviceID;			
	protected int directoryID; 			//database assign ID (when query)
	protected int originDirectory; 		//ObjectID (whenInput)
	protected String dateCreated;
	protected String dateAccessed;
	protected String dateModified;
	
	public Directory(String directoryName, String previousDirectory){
		
		this.directoryName = directoryName;
		this.previousDirectory = previousDirectory;
	}

	public int getDeviceID(){

		return deviceID;
	}

	public int getDirectoryID(){

		return directoryID;
	}
	
	public int originDirectory(){
		
		return originDirectory;
	}

	public String getDirectoryName(){

		return directoryName;
	}
	
	public String getPreviousDirectory(){
		
		return previousDirectory;
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
	
	public void modifyOriginDirectory(int originDirectory){
		
		this.originDirectory = originDirectory;
	}
	
	public void modifyDirectoryName(String directoryName){
		
		this.directoryName = directoryName;
	}
	
	public void modifyPreviousDirectory(String previousDirectory){
		
		this.previousDirectory= previousDirectory;
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
