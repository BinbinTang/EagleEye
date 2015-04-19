package eagleeye.report;

import java.util.Date;
import java.util.Calendar;


public class TimeLineEventNode implements Comparable<TimeLineEventNode> {

	private int key;
	private String deviceID;
	private String fileID;
	private String eventID;
	private String timeValue;
	private String event;
	private String comment;
	private String date;
	private int dateMonth;
	private int timeHour;
	private int year;
	
	public TimeLineEventNode () {
				
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		
		this.date = date;
	}
	
	public int getDateMonth() {
		return dateMonth;
	}

	public void setDateMonth(int month) {
		
		this.dateMonth = month;
		
	}
		
	public String getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(String timeValue) {
		this.timeValue = timeValue;
	}
	
	public void setHour(int hour) {
		this.timeHour = hour;
	}
	
	public int getHour() {
		return timeHour;
	}
	
	public void setYear(int year){
		this.year=year;
	}
	
	public int getYear(){
		return year;
	}
	
	public int compareTo(TimeLineEventNode node) {
		
		if(this.getYear() < node.getYear())
			return -1;
		else if(this.getYear() > node.getYear())
			return 1;
		else
		{
			if(this.getKey() >= node.getKey())
				return 1;
			else
				return -1;
		}
	}
	
	
}
