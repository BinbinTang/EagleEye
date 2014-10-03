package eagleeye.entities;

public class Video {
 protected String VideoName;
 protected String VideoPath;
 protected int DirectoryID;
 protected Boolean IsRecovered;
 protected String DateTaken;
 protected String DateDeleted;
 protected int DeviceID;
 protected int VideoID;

 public int getDeviceID(){
	 return DeviceID;
 }
 
 public int getVideoID(){
	 return VideoID;
 }
 public String getVideoName(){
	 return VideoName;
 }
 public String getVideoPath(){
	 return VideoPath;
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
	 return VideoName;
 }
 public void modifyDeviceID(int deviceID){
	 DeviceID=deviceID;
 }

 public void modifyVideoName(String videoName){
	VideoName=videoName;
 }
 public void modifyVideoPath(String videoPath){
	 VideoPath=videoPath;
 }
 public void modifyDirectoryID(int dirID){
	 DirectoryID=dirID;
 }
 public void  modifyIsRecovered(boolean isRec){
	 IsRecovered=isRec;
 }
 public void  modifyVideoID(int videoID){
     VideoID=videoID;
}
 public void  modifyDateTaken(String dateTaken){
	 DateTaken=dateTaken;
 }
 public void modifyDateDeleted(String dateDeleted){
	 VideoName=dateDeleted;
 }

 
 
}
