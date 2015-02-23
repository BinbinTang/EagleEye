package eagleeye.api.entities;

public interface EagleEvent {

	public int getEventID();
	public void setEventID(int eventID);
	public int getDeviceID();
	public void setDeviceID(int deviceID);
	public String getPluginName();
	public void setPluginName(String pluginName);
	public String getStartTime();
	public void setStartTime(String startTime);
	public String getEndTime();
	public void setEndTime(String endTime);
	public String getDetails();
	public void setDetails(String details);
	
}
