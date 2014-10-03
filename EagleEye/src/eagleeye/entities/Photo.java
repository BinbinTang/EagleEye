package eagleeye.entities;

public class Photo {
 protected String PhotoName;
 protected String PhotoPath;
 protected int DirectoryID;
 protected Boolean IsRecovered;
 protected String DateTaken;
 protected String DateDeleted;
 protected int DeviceID;
 protected int PhotoID;
 
 public int getDeviceID(){
	 return DeviceID;
 }
 public int getPhotoID(){
	 return PhotoID;
 }
 
 public String getPhotoName(){
	 return PhotoName;
 }
 public String getPhotoPath(){
	 return PhotoPath;
 }
 public int getDirectoryID(){
	 return DirectoryID;
 }
 public Boolean IsRecovered(){
	 return IsRecovered;
 }
 public String DateTaken(){
	 return DateTaken;
 }
 public String DateDeleted(){
	 return PhotoName;
 }
 public void modifyDeviceID(int deviceID){
	 DeviceID=deviceID;
 }

 public void modifyPhotoName(String photoName){
	PhotoName=photoName;
 }
 public void modifyPhotoPath(String photoPath){
	 PhotoPath=photoPath;
 }
 public void modifyDirectoryID(int dirID){
	 DirectoryID=dirID;
 }
 public void modifyPhotoID(int photoID){
	 PhotoID=photoID;
 }
 public void  modifyIsRecovered(boolean isRec){
	 IsRecovered=isRec;
 }
 public void  modifyDateTaken(String dateTaken){
	 DateTaken=dateTaken;
 }
 public void modifyDateDeleted(String dateDeleted){
	 PhotoName=dateDeleted;
 }

 
 
}
