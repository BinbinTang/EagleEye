package eagleeye.model;

import java.util.Calendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

public class WorkBench {
	
	final DatePicker startDatePicker = new DatePicker(LocalDate.parse("1992-05-08"));	
	final DatePicker endDatePicker = new DatePicker(LocalDate.now());
	
	private Calendar startDateC, endDateC;	
	private LocalDate startDate, endDate;
	
	
	/**
	 * default constructor
	 */
	
	public WorkBench(){
		
	}
	
	/**
	 * UI internal functions
	 */
	
	//Date picker methods
	public String pickStartDate(){
		String resultDate = "";
		
		return resultDate;
	}
	
	public String pickEndDate(){
		String resultDate = "";
		
		return resultDate;
	}
	

	
	/**
	 * UI binding functions
	 */
	
	
	/**
	 * Data Retrieving functions 
	 */
		
}
