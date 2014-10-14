package eagleeye.entities;

public class Device {

	protected int deviceID;
	protected String deviceName;
	protected String contentSize;
	protected String lastViewedOn;
	protected String dateCreated;
	protected String deviceOwner;
	
	public Device(String deviceName, String contentSize, String deviceOwner){
		
		this.deviceName = deviceName;
		this.contentSize = contentSize;
		this.deviceOwner = deviceOwner;
	}
 
	public int getDeviceID(){
		
		return deviceID;
	}
	
	public String getDeviceName(){
		
		return deviceName;
	}
 
	public String getContentSize(){
		
		return contentSize;
	}
	
	public String getLastViewedOn(){
	 
		return lastViewedOn;
	}
	
	public String getDateCreated(){
	 
		return dateCreated;
	}
	
	public String getDeviceOwner(){
	 
		return deviceOwner;
	}
 
	public void modifyDeviceID(int deviceID){
	
		this.deviceID = deviceID;
	}
	
	public void modifyDeviceName(String deviceName){
		 
		this.deviceName = deviceName;
	}
 
	public void modifyConentSize(String contentSize){
	 
		this.contentSize = contentSize;
	}

	public void modifyLastViewedOn(String lastViewedOn){
		
		this.lastViewedOn = lastViewedOn;
	}
	
	public void  modifyDateCreated(String dateCreated){
	
		this.dateCreated = dateCreated;
	}
 
	public void  modifyDeviceOwner(String deviceOwner){
	
		this.deviceOwner = deviceOwner;
	}

 
 
}
