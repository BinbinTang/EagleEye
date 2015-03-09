package eagleeye.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import eagleeye.api.entities.EagleFilter;
import eagleeye.dbcontroller.DBQueries;
import eagleeye.entities.Filter;

public class testDBQueries {
	
	@Test
	public void testFilteredQueriesWithAllCategories() {
		
		EagleFilter filter = new Filter();
		boolean isKeywordPresent = false;
		DBQueries queryMaker = new DBQueries();
		
		String expectedQuery = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, "
				+ "FileExt,FileExtID,File.DateCreated,File.DateAccessed,"
				+ "File.DateModified,File.IsModified,ContentType,File.IsRecovered,"
				+ "FilePath,ExtTypeName, Extension.ExtTypeID FROM File "
				+ "INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
				+ "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
				+ "INNER JOIN Extension ON Extension.ExtID = File.FileExtID WHERE Directory.DeviceID = ? "
				+ "AND File.DateCreated >= ? AND File.DateCreated <= ? AND time(File.DateCreated) >= time(?) "
				+ "AND time(File.DateCreated) <= time(?)AND ( Extension.ExtTypeID = 8 ) ";
		
		String query = queryMaker.getFilteredFilesQuery(isKeywordPresent, filter);
		
		assertEquals(expectedQuery, query);
	}

	@Test
	public void testFilteredQueriesWithImageAndDocumentCategories() {
		
		EagleFilter filter = new Filter();
		boolean isKeywordPresent = false;
		DBQueries queryMaker = new DBQueries();
		ArrayList<String> categoryFilter = new ArrayList<String>();
		categoryFilter.add("Image"); categoryFilter.add("Document");
		filter.setCategoryFilter(categoryFilter);
		
		
		String expectedQuery = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, "
				+ "FileExt,FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
				+ "File.IsModified,ContentType,File.IsRecovered,FilePath,ExtTypeName, "
				+ "Extension.ExtTypeID FROM File INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
				+ "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
				+ "INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
				+ "WHERE Directory.DeviceID = ? AND File.DateCreated >= ? AND File.DateCreated <= ? "
				+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)"
				+ "AND ( Extension.ExtTypeID = 1 OR Extension.ExtTypeID = 4 ) "; 

		
		String query = queryMaker.getFilteredFilesQuery(isKeywordPresent, filter);
		
		assertEquals(expectedQuery, query);
	}
	
	@Test
	public void testFilteredQueriesWithDatabaseAndOthersCategories() {
		
		EagleFilter filter = new Filter();
		boolean isKeywordPresent = false;
		DBQueries queryMaker = new DBQueries();
		ArrayList<String> categoryFilter = new ArrayList<String>();
		categoryFilter.add("Database"); categoryFilter.add("Others");
		filter.setCategoryFilter(categoryFilter);
	

		String expectedQuery = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, "
				+ "FileExt,FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
				+ "File.IsModified,ContentType,File.IsRecovered,FilePath,ExtTypeName, "
				+ "Extension.ExtTypeID FROM File INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
				+ "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
				+ "INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
				+ "WHERE Directory.DeviceID = ? AND File.DateCreated >= ? AND File.DateCreated <= ? "
				+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)"
				+ "AND ( Extension.ExtTypeID = 5 OR Extension.ExtTypeID = 7 ) "; 

		
		String query = queryMaker.getFilteredFilesQuery(isKeywordPresent, filter);
		assertEquals(expectedQuery, query);
	}
	
	@Test
	public void testFilteredQueriesWithFourCategories() {
		
		EagleFilter filter = new Filter();
		boolean isKeywordPresent = false;
		DBQueries queryMaker = new DBQueries();
		ArrayList<String> categoryFilter = new ArrayList<String>();
		categoryFilter.add("Database"); categoryFilter.add("Others");
		categoryFilter.add("Image"); categoryFilter.add("Compressed Folder");
		filter.setCategoryFilter(categoryFilter);
	

		String expectedQuery = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, "
				+ "FileExt,FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
				+ "File.IsModified,ContentType,File.IsRecovered,FilePath,ExtTypeName, "
				+ "Extension.ExtTypeID FROM File INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
				+ "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
				+ "INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
				+ "WHERE Directory.DeviceID = ? AND File.DateCreated >= ? AND File.DateCreated <= ? "
				+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)"
				+ "AND ( Extension.ExtTypeID = 5 OR Extension.ExtTypeID = 7 OR Extension.ExtTypeID = 1 "
				+ "OR Extension.ExtTypeID = 6 ) "; 

		
		String query = queryMaker.getFilteredFilesQuery(isKeywordPresent, filter);
		assertEquals(expectedQuery, query);
	}
	
	@Test
	public void testFilteredQueriesWithKeywords() {
		
		EagleFilter filter = new Filter();
		boolean isKeywordPresent = true;
		DBQueries queryMaker = new DBQueries();
		
		String expectedQuery = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, "
				+ "FileExt,FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
				+ "File.IsModified,ContentType,File.IsRecovered,FilePath,ExtTypeName, "
				+ "Extension.ExtTypeID FROM File INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
				+ "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
				+ "INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
				+ "WHERE Directory.DeviceID = ? AND FileName LIKE ? AND File.DateCreated >= ? AND File.DateCreated <= ? "
				+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)AND ( Extension.ExtTypeID = 8 ) "; 
		
		String query = queryMaker.getFilteredFilesQuery(isKeywordPresent, filter);
		assertEquals(expectedQuery, query);
	}
	
	@Test
	public void testFilteredQueriesWithKeywordsAndFourCategories() {
		
		EagleFilter filter = new Filter();
		boolean isKeywordPresent = true;
		DBQueries queryMaker = new DBQueries();
		ArrayList<String> categoryFilter = new ArrayList<String>();
		categoryFilter.add("Database"); categoryFilter.add("Video");
		categoryFilter.add("Image"); categoryFilter.add("Others");
		filter.setCategoryFilter(categoryFilter);
				
		String expectedQuery = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, "
				+ "FileExt,FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
				+ "File.IsModified,ContentType,File.IsRecovered,FilePath,ExtTypeName, "
				+ "Extension.ExtTypeID FROM File INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
				+ "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
				+ "INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
				+ "WHERE Directory.DeviceID = ? AND FileName LIKE ? AND File.DateCreated >= ? AND File.DateCreated <= ? "
				+ "AND time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)AND ( Extension.ExtTypeID = 5 "
				+ "OR Extension.ExtTypeID = 2 OR Extension.ExtTypeID = 1 OR Extension.ExtTypeID = 7 ) "; 
		
		String query = queryMaker.getFilteredFilesQuery(isKeywordPresent, filter);
		assertEquals(expectedQuery, query);
	}
	
	@Test
	public void testFilteredQueriesWithOriginalFiles() {
	
		EagleFilter filter = new Filter();
		boolean isKeywordPresent = false;
		DBQueries queryMaker = new DBQueries();
		boolean isOriginal = true;	filter.modifiyIsOriginal(isOriginal);
		boolean isRecovered = false; filter.modifyIsRecovered(isRecovered);
		boolean isModified = false;	filter.modifyIsModified(isModified);
		
		String expectedQuery = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, "
				+ "FileExt,FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
				+ "File.IsModified,ContentType,File.IsRecovered,FilePath,ExtTypeName, Extension.ExtTypeID FROM File "
				+ "INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
				+ "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
				+ "INNER JOIN Extension ON Extension.ExtID = File.FileExtID WHERE Directory.DeviceID = ? "
				+ "AND File.DateCreated >= ? AND File.DateCreated <= ? AND time(File.DateCreated) >= time(?) "
				+ "AND time(File.DateCreated) <= time(?)AND ( Extension.ExtTypeID = 8 )  "
				+ "AND File.IsRecovered = 0 AND File.IsModified = 0";
		
		String query = queryMaker.getFilteredFilesQuery(isKeywordPresent, filter);
		assertEquals(expectedQuery, query);
	}
	
	@Test
	public void testFilteredQueriesWithRecoveredAndOriginalFiles() {
	
		EagleFilter filter = new Filter();
		boolean isKeywordPresent = false;
		DBQueries queryMaker = new DBQueries();
		boolean isOriginal = true;	filter.modifiyIsOriginal(isOriginal);
		boolean isRecovered = true; filter.modifyIsRecovered(isRecovered);
		boolean isModified = false;	filter.modifyIsModified(isModified);
		
		String expectedQuery = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, "
				+ "FileExt,FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,"
				+ "File.IsModified,ContentType,File.IsRecovered,FilePath,ExtTypeName, "
				+ "Extension.ExtTypeID FROM File INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
				+ "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
				+ "INNER JOIN Extension ON Extension.ExtID = File.FileExtID WHERE Directory.DeviceID = ? AND "
				+ "File.DateCreated >= ? AND File.DateCreated <= ? AND time(File.DateCreated) >= time(?) AND "
				+ "time(File.DateCreated) <= time(?)AND ( Extension.ExtTypeID = 8 )  AND ((File.IsModified = 0 AND "
				+ "File.IsRecovered = 0) OR File.IsRecovered = 1)";

		
		String query = queryMaker.getFilteredFilesQuery(isKeywordPresent, filter);
		assertEquals(expectedQuery, query);
	}
	
	@Test
	public void testFilteredQueriesWithRecoveredAndModifiedFiles() {
	
		EagleFilter filter = new Filter();
		boolean isKeywordPresent = false;
		DBQueries queryMaker = new DBQueries();
		boolean isOriginal = false;	filter.modifiyIsOriginal(isOriginal);
		boolean isRecovered = true; filter.modifyIsRecovered(isRecovered);
		boolean isModified = true;	filter.modifyIsModified(isModified);
		
		String expectedQuery = "SELECT FileID, FileName, File.DirectoryID, DirectoryName, "
				+ "FileExt,FileExtID,File.DateCreated,File.DateAccessed,File.DateModified,File.IsModified,"
				+ "ContentType,File.IsRecovered,FilePath,ExtTypeName, Extension.ExtTypeID FROM File "
				+ "INNER JOIN Directory ON Directory.DirectoryID = File.DirectoryID "
				+ "INNER JOIN ExtensionType ON Extension.ExtTypeID = ExtensionType.ExtTypeID "
				+ "INNER JOIN Extension ON Extension.ExtID = File.FileExtID "
				+ "WHERE Directory.DeviceID = ? AND File.DateCreated >= ? AND File.DateCreated <= ? AND "
				+ "time(File.DateCreated) >= time(?) AND time(File.DateCreated) <= time(?)AND ( Extension.ExtTypeID = 8 )  "
				+ "AND (File.IsModified = 1 OR File.IsRecovered = 1)";
	
		String query = queryMaker.getFilteredFilesQuery(isKeywordPresent, filter);
		assertEquals(expectedQuery, query);
	}
	
		
}
