package eagleeye.dbcontroller;

import eagleeye.entities.Filter;

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
					   +"File.IsModified,ContentType,File.IsRecovered,FilePath,"
					   +"ExtTypeName FROM File "
					   +"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
					   +"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
					   +"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
					   +"Where Directory.DeviceID = ? ORDER BY File.FileID";
		
		return query;		
		
	}
	
	public String getFilteredFiles(boolean isKeywordPresent, boolean isCategoryPresent, Filter filter) {
		
		String query;
		int optionSelected = 1;
		
		if(isKeywordPresent && isCategoryPresent) {
			
			query = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, FileExt,"
					+"FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
					+"File.IsModified,ContentType,File.IsRecovered,FilePath,"
					+"ExtTypeName, Extension.ExtTypeID FROM File "
					+"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
					+"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
					+"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
					+ "WHERE Directory.DeviceID = ? AND FileName LIKE ? " 
					+ "AND Extension.ExtTypeID = ? AND File.DateCreated >= ? AND File.DateCreated <= ? "
					+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)";
					
		} else {
			
			if(isKeywordPresent) {
				
				query = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, FileExt,"
						+"FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
						+"File.IsModified,ContentType,File.IsRecovered,FilePath,"
						+"ExtTypeName, Extension.ExtTypeID FROM File "
						+"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
						+"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
						+"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
						+ "WHERE Directory.DeviceID = ? AND FileName LIKE ? " 
						+ "AND File.DateCreated >= ? AND File.DateCreated <= ? "
						+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)";
						
				
			} else if (isCategoryPresent) {
				
				query = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, FileExt,"
						+"FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
						+"File.IsModified,ContentType,File.IsRecovered,FilePath,"
						+"ExtTypeName, Extension.ExtTypeID FROM File "
						+"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
						+"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
						+"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
						+ "WHERE Directory.DeviceID = ? " 
						+ "AND Extension.ExtTypeID = ? AND File.DateCreated >= ? AND File.DateCreated <= ? "
						+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)";
						
			} else {
				
				query = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, FileExt,"
						+"FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
						+"File.IsModified,ContentType,File.IsRecovered,FilePath,"
						+"ExtTypeName, Extension.ExtTypeID FROM File "
						+"INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
						+"INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
						+"INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
						+ "WHERE Directory.DeviceID = ? " 
						+ "AND File.DateCreated >= ? AND File.DateCreated <= ? "
						+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)";
						
				
			}
		}
		
		String checkBoxCombi = filter.getCheckBoxCombination();
		
		if(checkBoxCombi.equals("000"))
			query = query + " AND File.IsModified = 1 AND File.IsRecovered = 0 AND File.IsModified = 1 AND File.IsRecovered = 1";
		if(checkBoxCombi.equals("001"))
			query = query + " AND File.IsRecovered = 1";
		if(checkBoxCombi.equals("010"))
			query = query + " AND File.IsRecovered = 0 AND File.IsModified = 0";
		if(checkBoxCombi.equals("100"))
			query = query + " AND File.IsModified= 1";
		if(checkBoxCombi.equals("011"))
			query = query + " AND (File.IsModified = 0 AND File.IsRecovered = 0) OR File.IsRecovered = 1";
		if(checkBoxCombi.equals("110"))
			query = query + " AND (File.IsModified = 0 AND File.IsRecovered = 0) OR File.IsModified = 1";
		if(checkBoxCombi.equals("101"))
			query = query + " AND File.IsModified = 1 OR File.IsRecovered = 1";
		
		System.out.println("Query is " + query);
		
		return query;
	}
}
