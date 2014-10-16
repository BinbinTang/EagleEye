package eagleeye.dbcontroller;

public class DBQueries {

	String getAllDirectories(int deviceID) {
		
		String query = "SELECT * FROM Directory WHERE DeviceID = " + deviceID;
		return query;
		
	}
	
	String getAllFiles(int deviceID) {
		
		String query = "SELECT FileName, FileExt, Files.IsRecovered, Files.DateDeleted,	Files.IsModified,"
					   + "ModifiedExt, Files.DateCreated, Files.DateAccessed, Files.DateModified, FilePath, Files.DirectoryID,"
					   + "Files.DeviceID, ExtTypeName, DirectoryName FROM Files "
					   + "INNER JOIN Directory ON Directory.OriginDirectory = Files.DirectoryID " 
					   + "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
					   + "INNER JOIN Extension ON Extension.ExtName = Files.FileExt "
					   + "WHERE Files.DeviceID = " + deviceID + " AND Files.DeviceID = Directory.DeviceID";
				
		return query;		
		
	}
}
