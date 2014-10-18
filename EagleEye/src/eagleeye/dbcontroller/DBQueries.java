package eagleeye.dbcontroller;

public class DBQueries {

	public String getAllDevices() {
		
		String query = "SELECT * FROM Device";
		return query;
	}
	
	public String getAllDirectories(int deviceID) {
		
		String query = "SELECT * FROM Directory WHERE DeviceID = " + deviceID;
		return query;
		
	}
	
	public String getAllFiles(int deviceID) {
		
		String query = "SELECT FileName, FileExt, File.IsRecovered, File.DateDeleted, File.IsModified,"
					   + "ModifiedExt, File.DateCreated, File.DateAccessed, File.DateModified, FilePath, File.DirectoryID,"
					   + "File.DeviceID, ExtTypeName, DirectoryName FROM File "
					   + "INNER JOIN Directory ON Directory.OriginDirectory = File.DirectoryID " 
					   + "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
					   + "INNER JOIN Extension ON Extension.ExtName = File.FileExt "
					   + "WHERE File.DeviceID = " + deviceID + " AND File.DeviceID = Directory.DeviceID";
				
		return query;		
		
	}
}
