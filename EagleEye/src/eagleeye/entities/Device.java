package eagleeye.entities;

public class Device {
 protected String DeviceName;
 protected String ConentSize;
 protected String LastViewedOn;
 protected String LastViewedBy;
 protected String DateCreated;
 protected String DeviceOwner;
 protected int DeviceID;
 
 public String getDeviceName(){
	 return DeviceName;
 }
 
 public int getDeviceID(){
	 return DeviceID;
 }
 
 public String getConentSize(){
	 return ConentSize;
 }
 public String getLastViewedOn(){
	 return LastViewedOn;
 }
 public String getLastViewedBy(){
	 return LastViewedBy;
 }
 public String getDateCreated(){
	 return DateCreated;
 }
 public String getDeviceOwner(){
	 return DeviceOwner;
 }
 
 public void modifyDeviceName(String dvName){
	 DeviceName=dvName;
 }
 public void modifyDeviceID(int dvID){
	 DeviceID=dvID;
 }

 public void modifyConentSize(String contentSize){
	 ConentSize=contentSize;
 }

 public void modifyLastViewedOn(String lastViewedOn){
	 LastViewedOn=lastViewedOn;
 }
 public void modifyLastViewedBy(String lastViewedBy){
	 LastViewedBy=lastViewedBy;
 }
 public void  modifyDateCreated(String dateCreated){
	 DateCreated=dateCreated;
 }
 public void  modifyDeviceOwner(String deviceOwner){
	 DeviceOwner=deviceOwner;
 }

 
 
}
