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
		
		String script = "INSERT INTO Directory(DirectoryName, DeviceID, ParentObjectID, ObjectID) "
						+ "VALUES (?,?,?,?)";
		return script;
	}
	
	public String insertNewDirectory(){
		
		String script = "INSERT INTO Directory(DirectoryName, ObjectID, ParentObjectID, DeviceID, DateCreated, DateAccessed, DateModified, IsRecovered)"
					 	+ "VALUES (?,?,?,?,?,?,?,?)";
		
		return script;
	}
	
	public String updateDirectoryRoute(){
		
		String script = "UPDATE Directory Set ParentDirectoryID = (SELECT e.DirectoryID FROM Directory e WHERE e.ObjectID = Directory.ParentObjectID AND e.DeviceID = Directory.DeviceID AND e.DeviceID = ?) WHERE DeviceID = ?";
		
		return script;
	}
	
	public String insertNewExt() {
		
		String script = "INSERT OR IGNORE INTO Extension(ExtName,ExtTypeID) "
						+ "VALUES (?,?)";
		
		return script;		
	}
	
	public String insertNewFile(){
				
		String script = "INSERT INTO File(FileName,FileExt,DateCreated,DateAccessed,DateModified,IsModified,ContentType,IsRecovered,"
						+"FilePath,ObjectID,ParentObjectID) "
						+"VALUES (?,?,?,?,?,?,?,?,?,?,?)";	
		
		return script;
	}
	
	public String updateFileExtID() {
		
		String script = "UPDATE File SET FileExtID = (Select ExtID FROM Extension WHERE File.FileExt = Extension.ExtName)";
		
		return script;
	}
	
	public String updateFileDirectoryID() {
		
		String script = "UPDATE File SET DirectoryID = " +
						"(SELECT e.DirectoryID FROM Directory e WHERE e.ObjectID = File.ParentObjectID AND e.DeviceID = ?) " +
						"WHERE FileID IN " +
						"(SELECT FileID FROM Directory e WHERE e.ObjectID = File.ParentObjectID AND e.DeviceID = ?)";
		
		return script;
	}
	
}

