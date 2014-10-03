package eagleeye.entities;

public class Directory {
 protected String DirectoryName;
 protected String PreviousDirectory;
 protected int DeviceID;
 protected int DirectoryID;

 public int getDeviceID(){
	 return DeviceID;
 }
 
 public int getDirectoryID(){
	 return DirectoryID;
 }
 
 public String getDirectoryName(){
	 return DirectoryName;
 }
 public String getPreviousDirectory(){
	 return PreviousDirectory;
 }
 
 public void modifyDeviceID(int dvID){
      DeviceID=dvID;
 }
 public void modifyDirectoryID(int dirID){
     DirectoryID=dirID;
}
 public void modifyDirectoryName(String dirName){
     DirectoryName=dirName;
 }
 public void modifyPreviousDirectory(String pdirID){
	 PreviousDirectory= pdirID;
 }
 
 
}
