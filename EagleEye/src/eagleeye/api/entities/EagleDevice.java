package eagleeye.api.entities;
import java.io.File;

public interface EagleDevice {
	
	public String getDeviceFolderPath();
	
	public void modifyDeviceFolderPath(String deviceFolderPath);
	
	public File getDeviceImageFolder();
 
	public int getDeviceID();
	
	public String getDeviceName();
 
	public String getContentSize();
	public String getLastViewedOn();
	
	public String getDateCreated();
	
	public String getDeviceOwner();
 
	public void modifiyDeviceImageFolder(File deviceImageFolder);
	
	public void modifyDeviceID(int deviceID);
	
	public void modifyDeviceName(String deviceName);
 
	public void modifyContentSize(String contentSize);

	public void modifyLastViewedOn(String lastViewedOn);
	
	public void  modifyDateCreated(String dateCreated);
 
	public void  modifyDeviceOwner(String deviceOwner);
}
