package eagleeye.entities;

import eagleeye.api.entities.EagleEvents;

public class TimeEvent implements EagleEvents{

	private int timeEventID;
	private String eventTime;
	private String details;
	private int deviceID;
	
	public TimeEvent(String eventTime, String details, int deviceID) {
		
		this.eventTime = eventTime;
		this.details = details;
		this.deviceID = deviceID;
	}
	
	
	@Override
	public void setDetails(String details) {
		
		this.details = details;
	}

	@Override
	public void setDeviceID(int deviceID) {
		
		this.deviceID = deviceID;
	}
	
	@Override
	public void setEventID(int eventID) {
		
		this.timeEventID = eventID;
	}

	@Override
	public int getEventID() {
		
		return timeEventID;
	}

	@Override
	public String getDetails() {
		
		return details;
	}

	@Override
	public int getDeviceID() {
	
		return deviceID;
	}
	
	public void setEventTime(String eventTime) {
		
		this.eventTime = eventTime;
	}
	
	public String getEventTime() {
		
		return eventTime;
	}

}
