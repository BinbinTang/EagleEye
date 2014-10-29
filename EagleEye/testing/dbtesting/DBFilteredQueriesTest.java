package dbtesting;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Test;

import eagleeye.dbcontroller.DBQueryController;
import eagleeye.entities.*;


//**Only run this class after population of table.**
public class DBFilteredQueriesTest {

	@Test
	public void testFilteredWithoutKeywordsAndCategory() {
		
		//condition one: date and time set 1
		Filter filter = new Filter();
		LocalDate startDate = LocalDate.of(2011, 5, 10);
		LocalDate endDate = LocalDate.of(2011, 8, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("07:00");
		filter.modifyEndTime("17:00");
		
		DBQueryController dbController = new DBQueryController();
		dbController.setDeviceID(1);
		
		ArrayList<FileEntity> listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),83);
		
		//condition two: date and time set 2
		filter = new Filter();
		startDate = LocalDate.of(2011, 5, 5);
		endDate = LocalDate.of(2011, 7, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("09:00");
		filter.modifyEndTime("17:00");
		
		listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),481);
	}
	
	@Test
	public void testFilteredWithoutKeywords() {
		
		//condition one: category = Image
		Filter filter = new Filter();
		LocalDate startDate = LocalDate.of(2011, 5, 5);
		LocalDate endDate = LocalDate.of(2011, 7, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("09:00");
		filter.modifyEndTime("17:00");
		filter.modifyCategoryName("Image");		
		
		DBQueryController dbController = new DBQueryController();
		dbController.setDeviceID(1);
		
		ArrayList<FileEntity> listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),57);
		
		//condition two: category = Others
		filter = new Filter();
		startDate = LocalDate.of(2011, 5, 5);
		endDate = LocalDate.of(2011, 7, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("09:00");
		filter.modifyEndTime("17:00");
		filter.modifyCategoryName("Others");		
		
		dbController = new DBQueryController();
		dbController.setDeviceID(1);
		
		listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),381);
	}
	
	@Test
	public void testFilteredWithoutCategory() {
		
		//condition one: keyword = data
		Filter filter = new Filter();
		LocalDate startDate = LocalDate.of(2011, 5, 5);
		LocalDate endDate = LocalDate.of(2011, 7, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("09:00");
		filter.modifyEndTime("17:00");
		filter.modifyKeyword("data");		
		
		DBQueryController dbController = new DBQueryController();
		dbController.setDeviceID(1);
		
		ArrayList<FileEntity> listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),41);
		
		//condition two: keyword = com
		filter = new Filter();
		startDate = LocalDate.of(2011, 5, 5);
		endDate = LocalDate.of(2011, 7, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("09:00");
		filter.modifyEndTime("17:00");
		filter.modifyKeyword("com");		
		
		dbController = new DBQueryController();
		dbController.setDeviceID(1);
		
		listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),75);
	}
	
	@Test
	public void testFilteredWithCategoryAndKeyWords() {
		
		//condition one: 
		Filter filter = new Filter();
		LocalDate startDate = LocalDate.of(2011, 5, 5);
		LocalDate endDate = LocalDate.of(2011, 7, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("09:00");
		filter.modifyEndTime("17:00");
		filter.modifyKeyword("com");
		filter.modifyCategoryName("Document");
		
		DBQueryController dbController = new DBQueryController();
		dbController.setDeviceID(1);
		
		ArrayList<FileEntity> listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),5);
		
		//condition two: 
		filter = new Filter();
		startDate = LocalDate.of(2011, 5, 5);
		endDate = LocalDate.of(2011, 7, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("09:00");
		filter.modifyEndTime("17:00");
		filter.modifyKeyword("Goods");
		filter.modifyCategoryName("Image");
		
		dbController = new DBQueryController();
		dbController.setDeviceID(1);
		
		listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),57);
	}
	
	@Test
	public void testFilteredWithIsRecovered() {
		
		//condition one: with keywords
		Filter filter = new Filter();
		LocalDate startDate = LocalDate.of(2011, 5, 5);
		LocalDate endDate = LocalDate.of(2011, 7, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("08:00");
		filter.modifyEndTime("17:00");
		filter.modifyKeyword("set");
		filter.modifyIsRecovered(true);
		
		DBQueryController dbController = new DBQueryController();
		dbController.setDeviceID(1);
		
		ArrayList<FileEntity> listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),33);
		
		//condition two: with category type
		filter = new Filter();
		startDate = LocalDate.of(2011, 5, 5);
		endDate = LocalDate.of(2011, 7, 10);
		filter.modifyStartDate(startDate);
		filter.modifyEndDate(endDate);
		filter.modifyStartTime("08:00");
		filter.modifyEndTime("17:00");
		filter.modifyCategoryName("Document");
		filter.modifyIsRecovered(true);
		
		dbController = new DBQueryController();
		dbController.setDeviceID(1);
		
		listOfFiles = dbController.getFilteredFiles(filter);
		assertEquals(listOfFiles.size(),26);
	}
	
	
	

}
