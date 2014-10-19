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
	
	public String insertNewDirectory(FileEntity newDirectory, int deviceID){
		
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
					 	+ dateAccessed + "','" + dateModified + "','" + isRecovered + "','" + dateDeleted + "')";
		
		return script;
	}
	
	public String insertNewFile(FileEntity newFile, int deviceID){
		
		String filePath = newFile.getFilePath();
		String fileName = newFile.getFileName();
		String fileExt = newFile.getFileExt();
		boolean isRecovered = newFile.getIsRecovered();
		String dateDeleted = newFile.getDateDeleted();
		boolean isModified = newFile.getIsModified();
		String modifiedExt = newFile.getModifiedExt();
		String dateCreated = newFile.getDateCreated();
		String dateAccessed = newFile.getDateAccessed();
		String dateModified = newFile.getDateModified();
		int directoryID = newFile.getDirectoryID();
		
		
		String script = "INSERT INTO File(FileName,FileExt,IsRecovered,DateDeleted,IsModified,ModifiedExt"
						+",DateCreated,DateAccessed,DateModified,FilePath,DirectoryID,DeviceID) "
						+"VALUES ('"+ fileName +"','"+ fileExt +"','"+ isRecovered + "','" + dateDeleted + "','"
						+ isModified +"','" + modifiedExt + "','" + dateCreated + "','" + dateAccessed + "','" + dateModified 
						+ "','" + filePath + "'," + directoryID + "," + deviceID +")";
		
		return script;
		
	}
	
	public String insertNewExt(String fileExtName, int extTypeID) {
		
		String script = "INSERT OR IGNORE INTO Extension(ExtName,ExtTypeID) "
						+ "VALUES ('"+ fileExtName +"',"+ extTypeID + ")";
		
		return script;
		
	}
	
	
	
}

