package eagleeye.entities;

public class Directory {

	protected String directoryName;
	protected String previousDirectory;
	protected int deviceID;
	protected int directoryID;
	
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

	public String getDirectoryName(){

		return directoryName;
	}
	
	public String getPreviousDirectory(){
		
		return previousDirectory;
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
	
	public void modifyPreviousDirectory(String previousDirectory){
		
		this.previousDirectory= previousDirectory;
	}

 
}
