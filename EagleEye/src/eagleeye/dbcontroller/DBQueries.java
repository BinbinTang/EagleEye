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
	
	public String getFilteredFiles(boolean isKeywordPresent, boolean isCategoryPresent) {
		
		String query;
		
		if(isKeywordPresent && isCategoryPresent) {
			
			query = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, FileExt,"
					+"FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
					+"File.IsModified,ModifiedExt,File.IsRecovered,File.DateDeleted,FilePath,"
					+"ExtTypeName, Extension.ExtTypeID FROM File "
					+"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
					+"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
					+"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
					+ "WHERE Directory.DeviceID = ? AND FileName LIKE ? " 
					+ "AND Extension.ExtTypeID = ? AND File.DateCreated >= ? AND File.DateCreated <= ? "
					+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?) "
					+ "AND File.IsModified = ? AND File.IsRecovered = ?";
		} else {
			
			if(isKeywordPresent) {
				
				query = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, FileExt,"
						+"FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
						+"File.IsModified,ModifiedExt,File.IsRecovered,File.DateDeleted,FilePath,"
						+"ExtTypeName, Extension.ExtTypeID FROM File "
						+"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
						+"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
						+"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
						+ "WHERE Directory.DeviceID = ? AND FileName LIKE ? " 
						+ "AND File.DateCreated >= ? AND File.DateCreated <= ? "
						+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?) "
						+ "AND File.IsModified = ? AND File.IsRecovered = ?";
				
			} else if (isCategoryPresent) {
				
				query = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, FileExt,"
						+"FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
						+"File.IsModified,ModifiedExt,File.IsRecovered,File.DateDeleted,FilePath,"
						+"ExtTypeName, Extension.ExtTypeID FROM File "
						+"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
						+"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
						+"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
						+ "WHERE Directory.DeviceID = ? " 
						+ "AND Extension.ExtTypeID = ? AND File.DateCreated >= ? AND File.DateCreated <= ? "
						+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?) "
						+ "AND File.IsModified = ? AND File.IsRecovered = ?";
			} else {
				
				query = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, FileExt,"
						+"FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
						+"File.IsModified,ModifiedExt,File.IsRecovered,File.DateDeleted,FilePath,"
						+"ExtTypeName, Extension.ExtTypeID FROM File "
						+"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
						+"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
						+"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
						+ "WHERE Directory.DeviceID = ? " 
						+ "AND File.DateCreated >= ? AND File.DateCreated <= ? "
						+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?) "
						+ "AND File.IsModified = ? AND File.IsRecovered = ?";
				
			}
		}
		
		return query;
	}
}
