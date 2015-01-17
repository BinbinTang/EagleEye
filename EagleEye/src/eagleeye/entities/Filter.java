package eagleeye.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TimeZone;

import eagleeye.api.entities.EagleFilter;

public class Filter implements EagleFilter{

	private String keyword;
	private LocalDate startDate;
	private LocalDate endDate;
	private String startTime;
	private String endTime;
	private String startTimeDaily;
	private String endTimeDaily;
	private int isModified;
	private int isRecovered;
	private int isOriginal;
	private ArrayList<String> categoryFilter;
	
	
	public Filter() {
		
		keyword = "";
		startDate = LocalDate.of(1970, 1, 1);
		endDate = LocalDate.now();
		startTime = "00:00:00";
		endTime = "23:59:00";
		startTimeDaily = "00:00:00";
		endTimeDaily = "23:59:00";
		isModified = 1;
		isRecovered = 1;
		isOriginal = 1;
		categoryFilter = new ArrayList<String>();
	}
	
	public String getStartTimeDaily() {
		
		return startTimeDaily;
	}
	
	public void modifyStartTimeDaily(String startTimeDaily) {
		
		this.startTimeDaily = startTimeDaily + ":00";
	}
	
	public String getEndTimeDaily() {
		
		return endTimeDaily;
	}
	
	public void modifyEndTimeDaily(String endTimeDaily) {
		
		this.endTimeDaily = endTimeDaily + ":00";
	}
	
	public String getKeyword() {
		
		return keyword;
	}

	public void modifyKeyword(String keyword) {
		
		this.keyword = "%" + keyword + "%";
	}

	public LocalDate getStartDate() {
		
		return startDate;
	}

	public void modifyStartDate(LocalDate startDate) {
		
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		
		return endDate;
	}

	public void modifyEndDate(LocalDate endDate) {
		
		this.endDate = endDate;
	}

	public String getStartTime() {
		
		return startTime;
	}

	public void modifyStartTime(String startTime) {
		
		this.startTime = startTime + ":00";
	}

	public String getEndTime() {
		
		return endTime;
	}

	public void modifyEndTime(String endTime) {
		
		this.endTime = endTime + ":00";
	}

	public int getIsModified() {
		
		return isModified;
	}

	public void modifyIsModified(boolean isModified) {
		
		if(isModified)
			this.isModified = 1;
		else
			this.isModified = 0;
	}

	public int getIsRecovered() {
		
		return isRecovered;
	}

	public void modifyIsRecovered(boolean isRecovered) {
		
		if(isRecovered)
			this.isRecovered = 1;
		else
			this.isRecovered = 0;
	}
	
	public int getIsOriginal()  {
		
		return isOriginal;
	}
	
	public void modifiyIsOriginal(boolean isOriginal) {
		
		if(isOriginal)
			this.isOriginal = 1;
		else
			this.isOriginal = 0;
	}
	
	public String getStartDateTimeAsString() {
		
		return startDate.toString() + " " + startTime;
	}
	
	public String getEndDateTimeAsString() {
		
		return endDate.toString() + " " + endTime;
	}
	
	public String getCheckBoxCombination() {
		
		String combination = isModified + "" + isOriginal + "" + isRecovered;
		
		return combination;
	}
	
	public ArrayList<String> getCategoryFilter () {
		
		return categoryFilter;
	}
	
	public void setCategoryFilter(ArrayList<String> categoryFilter) {
		
		this.categoryFilter = categoryFilter;
	}
}


