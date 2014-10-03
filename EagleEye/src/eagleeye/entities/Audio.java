package eagleeye.entities;

public class Audio {
 protected String AudioName;
 protected String AudioPath;
 protected int DirectoryID;
 protected Boolean IsRecovered;
 protected String DateTaken;
 protected String DateDeleted;
 protected int DeviceID;
 
 public int getDeviceID(){
	 return DeviceID;
 }
 
 public String getAudioName(){
	 return AudioName;
 }
 public String getAudioPath(){
	 return AudioPath;
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
	 return AudioName;
 }

 public void modifyDeviceID(int deviceID){
	 DeviceID=deviceID;
 }

 public void modifyAudioName(String audioName){
	AudioName=audioName;
 }
 public void modifyAudioPath(String audioPath){
	 AudioPath=audioPath;
 }
 public void modifyDirectoryID(int dirID){
	 DirectoryID=dirID;
 }
 public void  modifyIsRecovered(boolean isRec){
	 IsRecovered=isRec;
 }
 public void  modifyDateTaken(String dateTaken){
	 DateTaken=dateTaken;
 }
 public void modifyDateDeleted(String dateDeleted){
	 AudioName=dateDeleted;
 }

 
 
}
