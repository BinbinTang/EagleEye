package eagleeye.dbcontroller;

import eagleeye.entities.*;

public class DBInsertQueries {

	public String getKey() {
		
		String script = "SELECT last_insert_rowid()";
		
		return script;
	}
	
	public String insertNewDevice(Device newDevice) {
		
		String deviceName = newDevice.getDeviceName();
		String deviceOwner = newDevice.getDeviceOwner();
		String contentSize = newDevice.getContentSize();
	
		String script = "INSERT INTO Device(DeviceName, DeviceOwner, ContentSize, DateCreated, LastViewedOn) "
						+ "VALUES('" + deviceName +"','"+ deviceOwner + "','" + contentSize + "',datetime('now'),"
						+ "datetime('now'))";
		
		return script;
	}
	
	public String insertNewRootDirectory(int deviceID) {
		
		String script = "INSERT INTO Directory(DirectoryName, DeviceID, PreDirectory, OriginDirectory) "
						+ "VALUES ('root'," + deviceID + ",-2,-1)";
		return script;
	}
	
	public String insertNewDirectory(File newDirectory, int deviceID){
		
		String directoryName = newDirectory.getFileName();
		int originFileID = newDirectory.getFileID(); 
		String dateCreated = newDirectory.getDateCreated();
		String dateAccessed = newDirectory.getDateAccessed();
		String dateModified = newDirectory.getDateModified();
		boolean isRecovered = newDirectory.getIsRecovered();
		String dateDeleted = newDirectory.getDateDeleted();
		int parentDirectoryID = newDirectory.getDirectoryID();
		
		String script = "INSERT INTO Directory(DirectoryName, DeviceID, PreDirectory, OriginDirectory, DateCreated, DateAccessed, DateModified, IsRecovered, DateDeleted) "
					 	+ "VALUES ('" + directoryName + "'," + deviceID + ","+ parentDirectoryID + "," + originFileID +",'" + dateCreated +"','"
					 	+ dateAccessed + "','" + dateModified + "'," + isRecovered + ",'" + dateDeleted + "')";
		
		return script;
	}
	
	public String insertNewFile(File newFile, int deviceID, int fileExtID, int directoryID){
		
		String filePath = newFile.getFilePath();
		String fileName = newFile.getFileName();
		boolean isRecovered = newFile.getIsRecovered();
		String dateDeleted = newFile.getDateDeleted();
		boolean isModified = newFile.getIsModified();
		String modifiedExt = newFile.getModifiedExt();
		String dateCreated = newFile.getDateCreated();
		String dateAccessed = newFile.getDateAccessed();
		String dateModified = newFile.getDateModified();
		
		String script = "INSERT INTO Files(FileName,FileExt,IsRecovered,DateDeleted,IsModified,ModifiedExt"
						+",DateCreated,DateAccessed,DateModified,FilePath,DirectoryID,DeviceID) "
						+"VALUES ('"+ fileName +"',"+ fileExtID +","+ isRecovered + ",'" + dateDeleted + "',"
						+ isModified +",'" + modifiedExt + "','" + dateCreated + "','" + dateAccessed + "','" + dateModified 
						+ "','" + filePath + "'," + directoryID + "," + deviceID +")";
		
		return script;
		
	}
	
	public String insertNewExt(String fileExtName, int extTypeID) {
		
		String script = "INSERT INTO Extension(ExtName,ExtTypeID) "
						+ "VALUES ('"+ fileExtName +"',"+ extTypeID + ")";
		
		return script;
		
	}
	
	//Helper methods
	public String getFileExtID(String fileExt){
		
		String script = "SELECT ExtID FROM Extension WHERE ExtName = '" + fileExt + "'";
		return script;
		
	}
	
	public String getDirectoryID(int fileParentID, int deviceID){
		
		String script = "SELECT DirectoryID FROM Directory WHERE OriginDirectory = " + fileParentID
						+ " AND DeviceID = "+ deviceID;
		return script;
	}
	
	
	
}

