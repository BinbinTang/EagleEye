package eagleeye.dbcontroller;

public class DBInsertQueries {

	public String getKey() {
		
		String script = "SELECT last_insert_rowid()";
		
		return script;
	}
	
	public String getInsertNewDeviceQuery() {
		
		String script = "INSERT INTO Device(DeviceName, DeviceOwner, ContentSize, DateCreated, LastViewedOn, DeviceFolderPath) "
						+ "VALUES(?,?,?,datetime('now'),datetime('now'), ?)";
		
		return script;
	}
	
	public String getInsertNewRootDirectoryQuery() {
		
		String script = "INSERT INTO Directory(DirectoryName, DeviceID, ParentObjectID, ObjectID) "
						+ "VALUES (?,?,?,?)";
		return script;
	}
	
	public String getInsertNewDirectoryQuery(){
		
		String script = "INSERT INTO Directory(DirectoryName, ObjectID, ParentObjectID, DeviceID, DateCreated, DateAccessed, DateModified, IsRecovered)"
					 	+ "VALUES (?,?,?,?,?,?,?,?)";
		
		return script;
	}
	
	public String getUpdateDirectoryRouteQuery(){
		
		String script = "UPDATE Directory Set ParentDirectoryID = (SELECT e.DirectoryID FROM Directory e WHERE e.ObjectID = Directory.ParentObjectID AND e.DeviceID = Directory.DeviceID AND e.DeviceID = ? AND ((e.DirectoryID > ? AND e.DirectoryID <= ?) OR e.DirectoryName ='root')) WHERE DeviceID = ? "
						+ "AND Directory.ParentDirectoryID IS NULL";
		
		return script;
	}
	
	public String getInsertNewExtQuery() {
		
		String script = "INSERT OR IGNORE INTO Extension(ExtName,ExtTypeID) "
						+ "VALUES (?,?)";
		
		return script;		
	}
	
	public String getInsertNewFileQuery(){
				
		String script = "INSERT INTO File(FileName,FileExt,DateCreated,DateAccessed,DateModified,IsModified,ContentType,IsRecovered,"
						+"FilePath,ObjectID,ParentObjectID) "
						+"VALUES (?,?,?,?,?,?,?,?,?,?,?)";	
		
		return script;
	}
	
	public String getUpdateFileExtIDQuery() {
		
		String script = "UPDATE File SET FileExtID = (Select ExtID FROM Extension WHERE File.FileExt = Extension.ExtName)";
		
		return script;
	}
	
	public String getUpdateFileDirectoryIDQuery() {
		
		String script = "UPDATE File SET DirectoryID = " +
						"(SELECT e.DirectoryID FROM Directory e WHERE e.ObjectID = File.ParentObjectID AND e.DeviceID = ? AND e.DirectoryID > ? AND e.DirectoryID <= ?) " +
						"WHERE FileID IN " +
						"(SELECT FileID FROM Directory e WHERE e.ObjectID = File.ParentObjectID AND e.DeviceID = ? AND e.DirectoryID > ? AND e.DirectoryID <= ?) AND "
						+"File.DirectoryID IS NULL";
		
		return script;
	}
	
	public String getDirectoryAutoIncrementMarker() {
		
		String script = "SELECT seq FROM sqlite_sequence WHERE name= 'Directory'";
		
		return script;
		
	}
	
	public String getInsertLocationEventsQuery() {
		
		String script = "INSERT INTO LocationEvents(Longitude, Latitude, Details, DeviceID) VALUES (?,?,?,?)";
		
		return script;
	}
	
	public String getInsertTimeEventsQuery() {
		
		String script = "INSERT INTO TimeEvents(Time, Details, DeviceID) VALUES (?,?,?)";
		
		return script;
	}
}

