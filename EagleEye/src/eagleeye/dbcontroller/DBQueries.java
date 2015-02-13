package eagleeye.dbcontroller;

import java.util.ArrayList;



import eagleeye.api.entities.EagleFilter;
import eagleeye.entities.Filter;

public class DBQueries {

	public String getAllDevicesQuery() {
		
		String query = "SELECT * FROM Device";
		return query;
	}
	
	public String getAllDeviceNamesQuery() {
		
		String query = "SELECT DeviceName FROM Device";
		return query;
	}
	
	public String getAllDirectoriesQuery() {
		
		String query = "SELECT * FROM Directory WHERE DeviceID = ?";
		return query;
		
	}
	
	public String getAllFilesQuery() {
		
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
	
	public String getFilteredFilesQuery(boolean isKeywordPresent, EagleFilter filter) {
		
		String query;
		
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
		
		ArrayList<String> categoryFilter = filter.getCategoryFilter();
		String categoryQuery = "AND (";
		
		for(String c : categoryFilter )
		{
			if(c.equals("All")) { 
				categoryQuery = "";
				break;
			}
			if(c.equals("Image"))
				categoryQuery += " Extension.ExtTypeID = 1 ";
			if(c.equals("Video"))
				categoryQuery += " Extension.ExtTypeID = 2 ";
			if(c.equals("Audio"))
				categoryQuery += " Extension.ExtTypeID = 3 ";
			if(c.equals("Document"))
				categoryQuery += " Extension.ExtTypeID = 4 ";
			if(c.equals("Database"))
				categoryQuery += " Extension.ExtTypeID = 5 ";
			if(c.equals("Compressed Folder"))
				categoryQuery += " Extension.ExtTypeID = 6 ";
			if(c.equals("Others"))
				categoryQuery += " Extension.ExtTypeID = 7 ";
			
			categoryQuery += "OR";
		}
		
		if(categoryFilter.size() == 0){
			categoryQuery += " Extension.ExtTypeID = 8 OR";
		}
		
		if(!categoryQuery.equals("")) {
			
			categoryQuery = categoryQuery.substring(0, categoryQuery.length()-2);
			categoryQuery += ") ";
			
			query = query + categoryQuery;
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
			query = query + " AND ((File.IsModified = 0 AND File.IsRecovered = 0) OR File.IsRecovered = 1)";
		if(checkBoxCombi.equals("110"))
			query = query + " AND ((File.IsModified = 0 AND File.IsRecovered = 0) OR File.IsModified = 1)";
		if(checkBoxCombi.equals("101"))
			query = query + " AND (File.IsModified = 1 OR File.IsRecovered = 1)";
		
		System.out.println("Query is " + query);
		
		return query;
	}
	
	public String getDeviceRootQuery () {
		
		String query = "SELECT DeviceFolderPath FROM Device WHERE DeviceID = ?";
		
		return query;
		
	}
}
