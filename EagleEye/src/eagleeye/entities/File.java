package eagleeye.entities;

public class File {
 protected String FileName;
 protected int FileID;
 protected String FilePath;
 protected String FileExt;
 protected boolean IsDeleted; 
 protected int DirectoryID;
 protected boolean IsModified;
 protected String DateDeleted;
 protected String DateCreated;
 protected String DateAccessed;
 protected int DeviceID;
 
 public int getDeviceID(){
	 return DeviceID;
 }
 
 public int getFileID(){
	 return FileID;
 }
 public String getFileName(){
	 return FileName;
 }
 public String getFilePath(){
	 return FilePath;
 }
 public int getDirectoryID(){
	 return DirectoryID;
 }
 public boolean getIsDeleted(){
	 return IsDeleted;
 }
 public String getFileExt(){
	 return FileExt;
 }
 public boolean getIsModified(){
	 return IsModified;
 }
 public String getDateDeleted(){
	 return DateDeleted;
 }
 public String getDateCreated(){
	 return DateCreated;
 }
 public String getDateAccessed(){
	 return DateAccessed;
 }
 
 public void ModifyDeviceID(int dvID){
	  DeviceID=dvID;
 }
 
 public void ModifyFileName(String fileName){
	  FileName=fileName;
 }
 public void ModifyFileID(int fileID){
	  FileID=fileID;
}
 public void ModifyFilePath(String filePath){
	  FilePath=filePath;
 }
 public void ModifyDirectoryID(int dirID){
	  DirectoryID=dirID;
 }
 public void ModifyIsDeleted(boolean isDeleted){
	  IsDeleted=isDeleted;
 }
 public void ModifyFileExt(String fileExt){
	  FileExt=fileExt;
 }
 public void ModifyIsModified(boolean isModified){
	  IsModified=isModified;
 }
 public void ModifyDateDeleted(String dateDeleted){
	  DateDeleted=dateDeleted;
 }
 public void ModifyDateCreated(String dateCreated){
	  DateCreated=dateCreated;
 }
 public void ModifyDateAccessed(String dateAccessed){
	 DateAccessed=dateAccessed;
 }
 
}
