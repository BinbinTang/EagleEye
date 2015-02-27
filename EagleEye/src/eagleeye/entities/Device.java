package eagleeye.entities;

import java.io.File;

import eagleeye.api.entities.EagleDevice;

public class Device implements EagleDevice{

	protected int deviceID;
	protected String deviceName;
	protected String contentSize;
	protected String lastViewedOn;
	protected String dateCreated;
	protected String deviceOwner;
	protected File deviceImageFolder;
	protected String deviceFolderPath;
	
	public Device() {
		modifiyDeviceImageFolder	(null);
		modifyDeviceID				(-1);
		modifyDeviceName			(""); 
		modifyContentSize			("");
		modifyLastViewedOn			("");
		modifyDateCreated			("");
		modifyDeviceOwner			("");
	}
	
	public Device(String deviceName, String contentSize, String deviceOwner){
		modifiyDeviceImageFolder	(null);
		modifyDeviceID				(-1);
		modifyDeviceName			(deviceName); 
		modifyContentSize			(contentSize);
		modifyLastViewedOn			("");
		modifyDateCreated			("");
		modifyDeviceOwner			(deviceOwner);
	}
	
	public String getDeviceFolderPath() {
		
		return deviceFolderPath;
	}
	
	public void modifyDeviceFolderPath(String deviceFolderPath) {
		
		this.deviceFolderPath = deviceFolderPath;
	}
	
	public File getDeviceImageFolder() {
		
		return deviceImageFolder;
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
 
	public void modifiyDeviceImageFolder(File deviceImageFolder){
		
		this.deviceImageFolder = deviceImageFolder;
	}
	
	public void modifyDeviceID(int deviceID){
	
		this.deviceID = deviceID;
	}
	
	public void modifyDeviceName(String deviceName){
		 
		this.deviceName = deviceName;
	}
 
	public void modifyContentSize(String contentSize){
	 
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
