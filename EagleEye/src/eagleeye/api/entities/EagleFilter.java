package eagleeye.api.entities;

import java.time.LocalDate;
import java.util.ArrayList;

public interface EagleFilter {
	
	public String getStartTimeDaily();
	
	public void modifyStartTimeDaily(String startTimeDaily);
	
	public String getEndTimeDaily();
	
	public void modifyEndTimeDaily(String endTimeDaily);
	
	public String getKeyword();

	public void modifyKeyword(String keyword);

	public LocalDate getStartDate();

	public void modifyStartDate(LocalDate startDate);

	public LocalDate getEndDate();

	public void modifyEndDate(LocalDate endDate);

	public String getStartTime();

	public void modifyStartTime(String startTime);

	public String getEndTime();

	public void modifyEndTime(String endTime);

	public int getIsModified();

	public void modifyIsModified(boolean isModified);

	public int getIsRecovered();

	public void modifyIsRecovered(boolean isRecovered);
	
	public int getIsOriginal();
	
	public void modifiyIsOriginal(boolean isOriginal);
	
	public String getStartDateTimeAsString();
	
	public String getEndDateTimeAsString();
	
	public String getCheckBoxCombination();
	
	public ArrayList<String> getCategoryFilter();
	
	public void setCategoryFilter(ArrayList<String> categoryFilter);
}
