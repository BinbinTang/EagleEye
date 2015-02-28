package eagleeye.api.dbcontroller;

import eagleeye.api.entities.*;
import java.util.ArrayList;


public interface DBController {

	/**
	 * <p>
	 * Set the device id which will be used to query the database.
	 * <p>
	 * @param deviceID - int which corresponds to a device.
	 */
	public void setDeviceID(int deviceID);
	/**
	 * <p>
	 * Returns the device id used.
	 * <p>
	 * @return - int which corresponds to the device ID. -1 if not set.
	 */
	public int getDeviceID();
	/**
	 * <p>
	 * Returns the location on local disk space where the device's
	 * dataFiles are installed.
	 * <p>
	 * @return - String of path.
	 */
	public String getDeviceRootPath();
	/**
	 * <p>
	 * Returns a list of all devices stored in the database.
	 * <p>
	 * @return - List of EagleDevice object
	 */
	public ArrayList<EagleDevice> getAllDevices();
	/**
	 * <p>
	 * Returns a list of all device names stored in the database.
	 * <p>
	 * @return - List of String each corresponds to a device name.
	 */
	public ArrayList<String> getAllDeviceNames();
	/**
	 * <p>
	 * Returns a list of all directories stored in the database for
	 * the device ID set in {@link #setDeviceID(int) setDeviceID}. 
	 * Used for tree-view. Call this method if using tree.
	 * <p>
	 * @return - List of EagleDirectory object. The list is organized
	 * in a tree structure.
	 */
	public ArrayList<EagleDirectory> getAllDirectoriesAndFiles();
	/**
	 * <p>
	 * Returns a list of all directories stored in the database for
	 * the device ID set in {@link #setDeviceID(int) setDeviceID}.
	 * <p>
	 * @return - List of EagleDirectory object
	 */
	public ArrayList<EagleDirectory> getAllDirectories();
	/**
	 * <p>
	 * Returns a list of all files in in the database for
	 * the device ID set in {@link #setDeviceID(int) setDeviceID}. 
	 * <p>
	 * @return - List of EagleFile object
	 */
	public ArrayList<EagleFile> getAllFiles();
	/**
	 * <p>
	 * Returns a list of files in the database for
	 * the device ID set in {@link #setDeviceID(int) setDeviceID} 
	 * after applying a filter given as input parameter.
	 * <p>
	 * @param filter - EagleFilter object
	 * @return - List of EagleFile object
	 */
	public ArrayList<EagleFile> getFilteredFiles(EagleFilter filter);
	
	
}
