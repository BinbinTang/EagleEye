package eagleeye.entities;

public class Photo {
	
	protected int deviceID;
	protected int directoryID;
	protected int photoID;
	protected String photoName;
	protected String photoPath;
	protected boolean isRecovered;
	protected String dateDeleted;
	protected boolean isModified;
	protected String modifiedExt;
	protected String dateCreated;
	protected String dateAccessed;
	protected String dateModified;
	
	public Photo() {
		
		isRecovered = false;
		isModified = false;
	}

	public int getDeviceID(){
		
		return deviceID;
	}
	
	public int getDirectoryID(){
		
		return directoryID;
	}
		
	public int getPhotoID(){
		
		return photoID;
	}

	public String getPhotoName(){
		
		return photoName;
	}
	
	public String getPhotoPath(){
	
		return photoPath;
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
	
	public void modifyPhotoID(int photoID){
		
		this.photoID = photoID;
	}

	public void modifyPhotoName(String photoName){
		
		this.photoName = photoName;
	}
	
	public void modifyPhotoPath(String photoPath){
		
		this.photoPath = photoPath;
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
