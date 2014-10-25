package eagleeye.dbcontroller;

public class DBQueries {

	public String getAllDevices() {
		
		String query = "SELECT * FROM Device";
		return query;
	}
	
	public String getAllDirectories() {
		
		String query = "SELECT * FROM Directory WHERE DeviceID = ?";
		return query;
		
	}
	
	public String getAllFiles() {
		
		String query = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, FileExt,"
					   +"FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
					   +"File.IsModified,ModifiedExt,File.IsRecovered,File.DateDeleted,FilePath,"
					   +"ExtTypeName FROM File "
					   +"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
					   +"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
					   +"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
					   +"Where Directory.DeviceID = ? ORDER BY File.FileID";
		
		return query;		
		
	}
}
