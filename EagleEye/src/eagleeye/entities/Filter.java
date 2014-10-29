package eagleeye.entities;

import java.time.LocalDate;
import java.util.TimeZone;

public class Filter {

	private String keyword;
	private String categoryName;
	private int categoryID;
	private LocalDate startDate;
	private LocalDate endDate;
	private String startTime;
	private String endTime;
	private int isModified;
	private int isRecovered;
	
	public Filter() {
		
		keyword = "";
		categoryName = "";
		categoryID = -1;
		startDate = LocalDate.of(1970, 1, 1);
		endDate = LocalDate.now();
		startTime = "00:00:00";
		endTime = "23:59:00";
		isModified = 0;
		isRecovered = 0;
	}
	
	public String getKeyword() {
		
		return keyword;
	}

	public void modifyKeyword(String keyword) {
		
		this.keyword = "%" + keyword + "%";
	}

	public String getCategoryName() {
		
		return categoryName;
	}

	public void modifyCategoryName(String categoryName) {
		
		this.categoryName = categoryName;
		
		if(categoryName.equals("Image"))
			this.categoryID = 1;
		if(categoryName.equals("Video"))
			this.categoryID = 2;
		if(categoryName.equals("Audio"))
			this.categoryID = 3;
		if(categoryName.equals("Document"))
			this.categoryID = 4;
		if(categoryName.equals("Database"))
			this.categoryID = 5;
		if(categoryName.equals("Compressed Folder"))
			this.categoryID = 6;
		if(categoryName.equals("Others"))
			this.categoryID = 7;
		
	}

	public int getCategoryID() {
		
		return categoryID;
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
	
	public String getStartDateAsString() {
		
		return startDate.toString();
	}
	
	public String getEndDateAsString() {
		
		return endDate.toString();
	}
	
	
}


