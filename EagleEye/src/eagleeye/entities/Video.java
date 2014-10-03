package eagleeye.entities;

public class Video {
	
	protected int deviceID;
	protected int directoryID;
	protected int videoID;
	protected String videoName;
	protected String videoPath;
	protected boolean isRecovered;
	protected String dateDeleted;
	protected boolean isModified;
	protected String modifiedExt;
	protected String dateCreated;
	protected String dateAccessed;
	protected String dateModified;
	
	public Video() {
		
		isRecovered = false;
		isModified = false;
	}
	
	public int getDeviceID(){
		
		return deviceID;
	}
	
	public int getDirectoryID(){
		
		return directoryID;
	}
		
	public int getVideoID(){
		
		return videoID;
	}

	public String getVideoName(){
		
		return videoName;
	}
	
	public String getVideoPath(){
	
		return videoPath;
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
	
	public void modifyVideoID(int videoID){
		
		this.videoID = videoID;
	}

	public void modifyVideoName(String videoName){
		
		this.videoName = videoName;
	}
	
	public void modifyVideoPath(String videoPath){
		
		this.videoPath = videoPath;
	}
		
	public void  modifyIsRecovered(boolean isRecovered){
		
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
	
	public void  modifyDateCreated(String dateCreated){
		
		this.dateCreated = dateCreated;
	}
	
	public void modifyDateAccessed(String dateAccessed){
		 
		this.dateAccessed = dateAccessed;
	}
	
	public void modifyDateModified(String dateModified){
		
		this.dateModified = dateModified;		
	}
}
