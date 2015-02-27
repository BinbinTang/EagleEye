package eagleeye.entities;

import eagleeye.api.entities.EagleEvent;

public class Event implements EagleEvent {

	private int eventID;
	private int deviceID;
	private String startTime;
	private String endTime;
	private String pluginName;
	private String details;
	
	public Event() {
		this.deviceID = -1;
		this.pluginName = "";
		this.startTime = "";
		this.endTime = "";
		this.details = "";
	}

	public Event(int deviceID, String pluginName, String startTime, String endTime, String details) {
		
		this.deviceID = deviceID;
		this.pluginName = pluginName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.details = details;
	}
	
	@Override
	public int getEventID() {
		return eventID;
	}

	@Override
	public void setEventID(int eventID) {
		this.eventID = eventID;
	}

	@Override
	public int getDeviceID() {
		return deviceID;
	}

	@Override
	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}

	@Override
	public String getPluginName() {
		return pluginName;
	}

	@Override
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@Override
	public String getStartTime() {
		return startTime;
	}

	@Override
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Override
	public String getEndTime() {
		return endTime;
	}

	@Override
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String getDetails() {
		return details;
	}

	@Override
	public void setDetails(String details) {
		this.details = details;
	}

	
	
}
