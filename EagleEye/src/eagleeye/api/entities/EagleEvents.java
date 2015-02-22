package eagleeye.api.entities;

public abstract interface EagleEvents {

	public void setDetails(String details);
	public void setDeviceID(int deviceID);
	public void setEventID(int eventID);
	public int getEventID();
	public String getDetails();
	public int getDeviceID();
}
