package eagleeye.dbcontroller;

import eagleeye.entities.*;
import eagleeye.api.dbcontroller.*;
import eagleeye.api.entities.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBQueryController implements DBController {

	protected int deviceID;
	protected DBQueries queryMaker;
	
	public DBQueryController() {
		System.out.println("new DB set");
		this.deviceID = -1;
		queryMaker = new DBQueries();
	}
	
	@Override
	public void setDeviceID(int deviceID){
		
		this.deviceID = deviceID;
	}
	
	@Override
	public int getDeviceID(){
	
		return deviceID;
	}
	
	@Override
	public ArrayList<EagleDevice> getAllDevices() {
		
		ArrayList<EagleDevice> listOfDevices = new ArrayList<EagleDevice> (); 
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllDevicesQuery());
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				Device device = new Device();
				device.modifyDeviceID(rs.getInt("DeviceID"));
				device.modifyDeviceName(rs.getString("DeviceName"));
				device.modifyDeviceOwner(rs.getString("DeviceOwner"));
				device.modifyContentSize(rs.getString("ContentSize"));
				device.modifyDateCreated(rs.getString("DateCreated"));
				device.modifyLastViewedOn(rs.getString("LastViewedOn"));
				device.modifyDeviceFolderPath(rs.getString("DeviceFolderPath"));
				
				listOfDevices.add(device);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return listOfDevices;
	}
	
	@Override
	public ArrayList<String> getAllDeviceNames() {
		
		ArrayList<String> listOfDeviceNames = new ArrayList<String> (); 
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllDeviceNamesQuery());
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				String deviceName = rs.getString("DeviceName");
				listOfDeviceNames.add(deviceName);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return listOfDeviceNames;
	}
	
	
	@Override
	//use for tree-view
	//Call this method if using tree
	public ArrayList<EagleDirectory> getAllDirectoriesAndFiles() {
		
		ArrayList<EagleDirectory> listOfDirectory = getAllDirectories();
		ArrayList<EagleFile> listOfFiles = getAllFiles();
		
		System.out.println("CaiJun: D_List Size =" + listOfDirectory.size());
		System.out.println("CaiJun: F_List Size =" + listOfFiles.size());
		
		ArrayList<EagleDirectory> organizedDirectoryList = this.organizeFilesAndDirectory(listOfDirectory, listOfFiles);
		return organizedDirectoryList;
		
	}
	
	@Override
	public ArrayList<EagleDirectory> getAllDirectories(){
		
		ArrayList<EagleDirectory> listOfDirectory = new ArrayList<EagleDirectory>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllDirectoriesQuery());
			stmt.setInt(1, deviceID);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				
				Directory d = new Directory();
				d.modifyDirectoryID(rs.getInt("DirectoryID"));
				d.modifyDirectoryName(rs.getString("DirectoryName"));
				d.modifyDeviceID(rs.getInt("DeviceID"));
				d.modifyParentDirectory(rs.getInt("ParentDirectoryID"));
				d.modifyDateCreated(rs.getString("DateCreated"));
				d.modifyDateAccessed(rs.getString("DateAccessed"));
				d.modifyDateModified(rs.getString("DateModified"));
				d.modifyIsRecovered(rs.getBoolean("IsRecovered"));
				
				listOfDirectory.add(d);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
				
		return listOfDirectory;
	}
	
	@Override
	public ArrayList<EagleFile> getAllFiles() {
		
		ArrayList<EagleFile> listOfFiles = new ArrayList<EagleFile>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllFilesQuery());
			stmt.setInt(1, deviceID);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				
				FileEntity f = new FileEntity();
				f.modifyDeviceID(deviceID);
				f.modifyFileName(rs.getString("FileName"));
				f.modifyFileID(rs.getInt("FileID"));
				f.modifyDirectoryID(rs.getInt("DirectoryID"));
				f.modifyDirectoryName(rs.getString("DirectoryName"));
				f.modifyFileExt(rs.getString("FileExt"));
				f.modifyFileExtID(rs.getInt("FileExtID"));
				f.modifyDateCreated(rs.getString("DateCreated"));
				f.modifyDateAccessed(rs.getString("DateAccessed"));
				f.modifyDateModified(rs.getString("DateModified"));
				f.modifyIsModified(rs.getBoolean("IsModified"));
				f.modifyContentType(rs.getString("ContentType"));
				f.modifyIsRecovered(rs.getBoolean("IsRecovered"));
				f.modifyFilePath(rs.getString("FilePath"));
				f.modifyCategory(rs.getString("ExtTypeName"));
				f.modifyIsDirectory(false);
								
				listOfFiles.add(f);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			System.out.println("Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return listOfFiles;
		
	}
	
	@Override
	public ArrayList<EagleFile> getFilteredFiles(EagleFilter filter) {
		
		boolean isKeywordPresent = isKeywordPresent(filter.getKeyword());
		ArrayList<EagleFile> listOfFiles = new ArrayList<EagleFile>();
		
		System.out.println("StartDateTime = " + filter.getStartDateTimeAsString());
		System.out.println("EndDateTime = " + filter.getEndDateTimeAsString());
		System.out.println("StartTimeDaily = " + filter.getStartTimeDaily());
		System.out.println("EndTimeDaily = " + filter.getEndTimeDaily());
		System.out.println("Keyword = " + filter.getKeyword());
		
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getFilteredFilesQuery(isKeywordPresent, filter));
			stmt = setFieldsForFilter(stmt,isKeywordPresent,filter);
			System.out.println("query is " + stmt.getMetaData());
			
			ResultSet rs = stmt.executeQuery();
			
			int count = 0;
			while(rs.next()){
				
				
				
				FileEntity f = new FileEntity();
				f.modifyDeviceID(deviceID);
				f.modifyFileName(rs.getString("FileName"));
				f.modifyFileID(rs.getInt("FileID"));
				f.modifyDirectoryID(rs.getInt("DirectoryID"));
				f.modifyDirectoryName(rs.getString("DirectoryName"));
				f.modifyFileExt(rs.getString("FileExt"));
				f.modifyFileExtID(rs.getInt("FileExtID"));
				f.modifyDateCreated(rs.getString("DateCreated"));
				f.modifyDateAccessed(rs.getString("DateAccessed"));
				f.modifyDateModified(rs.getString("DateModified"));
				f.modifyIsModified(rs.getBoolean("IsModified"));
				f.modifyContentType(rs.getString("ContentType"));
				f.modifyIsRecovered(rs.getBoolean("IsRecovered"));
				f.modifyFilePath(rs.getString("FilePath"));
				f.modifyCategory(rs.getString("ExtTypeName"));
								
				listOfFiles.add(f);
				count ++;
			}
			
			System.out.println("resultset count is " + count);
			conn.close();
		} catch (Exception e) {
			
			System.out.println("Filter Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		
		return listOfFiles;
		
	}
	
	@Override
	public String getDeviceRootPath() {
		
		if(deviceID == -1)
			return null;
		
		String deviceRootPath = "";
		Connection conn = DBConnection.dbConnector();
		
		try {
			
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getDeviceRootQuery());
			stmt.setInt(1, deviceID);
			
			ResultSet rs = stmt.executeQuery();
			
			int count = 0;
			while(rs.next()){
				
				deviceRootPath = rs.getString("DeviceFolderPath");
			}
			
			conn.close();
		
		} catch (Exception e) {
			
			System.out.println("Device Folder Path Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return deviceRootPath;
		
	}
	
	//EVENTS QUERY SECTION
	
	public List<Event> getAllEvents(int deviceID) {
		
		List<Event> eventsList = new ArrayList<Event>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllEventsQuery());
			stmt.setInt(1, deviceID);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				Event newEvent = new Event();
				newEvent.setEventID(rs.getInt("EventID"));
				newEvent.setPluginName(rs.getString("PluginName"));
				newEvent.setStartTime(rs.getString("StartTime"));
				newEvent.setEndTime(rs.getString("EndTime"));
				newEvent.setDetails(rs.getString("Details"));
				newEvent.setDeviceID(rs.getInt("DeviceID"));
				
				eventsList.add(newEvent);
			}
		
			conn.close();
		
		} catch (Exception e) {
			
			System.out.println("Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}	
		
		return eventsList;
	}
	
	public List<Event> getAllEventsWithTimeRange(int deviceID, String startTime, String endTime) {
		
		List<Event> eventsList = new ArrayList<Event>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllEventsWithTimeRangeQuery());
			stmt.setInt(1, deviceID);
			stmt.setString(2, startTime);
			stmt.setString(3, endTime);

			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				Event newEvent = new Event();
				newEvent.setEventID(rs.getInt("EventID"));
				newEvent.setPluginName(rs.getString("PluginName"));
				newEvent.setStartTime(rs.getString("StartTime"));
				newEvent.setEndTime(rs.getString("EndTime"));
				newEvent.setDetails(rs.getString("Details"));
				newEvent.setDeviceID(rs.getInt("DeviceID"));
				
				eventsList.add(newEvent);
			}
		
			conn.close();
		
		} catch (Exception e) {
			
			System.out.println("Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}	
		
		return eventsList;
	}
	
	public List<Event> getEventsByPluginName(int deviceID, String pluginName) {
	
		List<Event> eventsList = new ArrayList<Event>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getEventsByPluginNameQuery());
			stmt.setInt(1, deviceID);
			stmt.setString(2, pluginName);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				Event newEvent = new Event();
				newEvent.setEventID(rs.getInt("EventID"));
				newEvent.setPluginName(rs.getString("PluginName"));
				newEvent.setStartTime(rs.getString("StartTime"));
				newEvent.setEndTime(rs.getString("EndTime"));
				newEvent.setDetails(rs.getString("Details"));
				newEvent.setDeviceID(rs.getInt("DeviceID"));
				
				eventsList.add(newEvent);
			}
		
			conn.close();
		
		} catch (Exception e) {
			
			System.out.println("Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}	
		
		return eventsList;
	}
	
	public List<Event> getEventsByPluginNameWithTimeRange(int deviceID, String pluginName, String startTime, String endTime) {
		
		List<Event> eventsList = new ArrayList<Event>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getEventsByPluginNameWithTimeRangeQuery());
			stmt.setInt(1, deviceID);
			stmt.setString(2, pluginName);
			stmt.setString(3, startTime);
			stmt.setString(4, endTime);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				Event newEvent = new Event();
				newEvent.setEventID(rs.getInt("EventID"));
				newEvent.setPluginName(rs.getString("PluginName"));
				newEvent.setStartTime(rs.getString("StartTime"));
				newEvent.setEndTime(rs.getString("EndTime"));
				newEvent.setDetails(rs.getString("Details"));
				newEvent.setDeviceID(rs.getInt("DeviceID"));
				
				eventsList.add(newEvent);
			}
		
			conn.close();
		
		} catch (Exception e) {
			
			System.out.println("Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}	
		
		return eventsList;
	}
	
	public boolean isAnalyzedDataPresent(int deviceID, String pluginName) {
		
		List<Event> eventsList = new ArrayList<Event>();
		Connection conn = DBConnection.dbConnector();
		int count = 0;
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getIsAnalyzedEventPresentQuery());
			stmt.setInt(1, deviceID);
			stmt.setString(2, pluginName);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				
				count = rs.getInt(1);
			}
		
			conn.close();
		
		} catch (Exception e) {
			
			System.out.println("Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}	
		
		if(count == 0)
			return false;
		else
			return true;
		
	}
	
	
	//HELPER METHODS SECTION
	private ArrayList<EagleDirectory> organizeFilesAndDirectory (ArrayList<EagleDirectory> listOfDirectory, ArrayList<EagleFile> listOfFiles){
		
		for(EagleFile f : listOfFiles) {
			
			int fileDirectory = f.getDirectoryID();
			
			for(EagleDirectory d : listOfDirectory) {
				
				int directoryID = d.getDirectoryID();
				
				if(fileDirectory == directoryID) {
					d.addNewFile((FileEntity)f);
					break;
				}
			}
		}
		
		return listOfDirectory;
	}
	
	
	private PreparedStatement setFieldsForFilter(PreparedStatement stmt,boolean isKeywordPresent, EagleFilter filter) throws SQLException {
		
		if(isKeywordPresent) {
			
			stmt.setInt(1,deviceID);
			stmt.setString(2, filter.getKeyword());
			stmt.setString(3, filter.getStartDateTimeAsString());
			stmt.setString(4, filter.getEndDateTimeAsString());
			stmt.setString(5, filter.getStartTimeDaily());
			stmt.setString(6, filter.getEndTimeDaily());
		
		} else {
				
			stmt.setInt(1,deviceID);
			stmt.setString(2, filter.getStartDateTimeAsString());
			stmt.setString(3, filter.getEndDateTimeAsString());
			stmt.setString(4, filter.getStartTimeDaily());
			stmt.setString(5, filter.getEndTimeDaily());
		}
			
		return stmt;
		
	}
		
	private boolean isKeywordPresent(String keyword) {
		
		String emptyString = "";
		
		if (keyword.equals(emptyString))
			return false;
		else
			return true;
	}
	
	
}
