package eagleeye.dbcontroller;

public class DBInsertQueries {

	public String getKey() {
		
		String script = "SELECT last_insert_rowid()";
		
		return script;
	}
	
	public String insertNewDevice() {
		
		String script = "INSERT INTO Device(DeviceName, DeviceOwner, ContentSize, DateCreated, LastViewedOn) "
						+ "VALUES(?,?,?,datetime('now'),datetime('now'))";
		
		return script;
	}
	
	public String insertNewRootDirectory() {
		
		String script = "INSERT INTO Directory(DirectoryName, DeviceID, PreDirectory, OriginDirectory) "
						+ "VALUES (?,?,?,?)";
		return script;
	}
	
	public String insertNewDirectory(){
		
		String script = "INSERT INTO Directory(DirectoryName, DeviceID, PreDirectory, OriginDirectory, DateCreated, DateAccessed, DateModified, IsRecovered, DateDeleted) "
					 	+ "VALUES (?,?,?,?,?,?,?,?,?)";
		
		return script;
	}
	
	public String insertNewFile(){
				
		String script = "INSERT INTO File(FileName,FileExt,IsRecovered,DateDeleted,IsModified,ModifiedExt"
						+",DateCreated,DateAccessed,DateModified,FilePath,DirectoryID,DeviceID) "
						+"VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";	
		
		return script;
	}
	
	public String insertNewExt() {
		
		String script = "INSERT OR IGNORE INTO Extension(ExtName,ExtTypeID) "
						+ "VALUES (?,?)";
		
		return script;
		
	}
	
}

