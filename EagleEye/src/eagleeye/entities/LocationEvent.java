package eagleeye.entities;

import eagleeye.api.entities.EagleEvents;
	
public class LocationEvent implements EagleEvents{

	private int locationEventID;
	private String longitude;
	private String latitude;
	private String details;
	private int deviceID;
	
	public LocationEvent(String longitude, String latitude, String details, int deviceID) {
		
		this.longitude = longitude;
		this.latitude = latitude;
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
		
		this.locationEventID = eventID;
	}

	@Override
	public int getEventID() {
		
		return locationEventID;
	}

	@Override
	public String getDetails() {
		
		return details;
	}

	@Override
	public int getDeviceID() {
	
		return deviceID;
	}
	
	public String getLongitude() {
		
		return longitude;
	}
	
	public void setLongitude(String longitude) {
		
		this.longitude = longitude;
	}

	public String getLatitude() {
		
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		
		this.latitude = latitude;
	}

	
}
