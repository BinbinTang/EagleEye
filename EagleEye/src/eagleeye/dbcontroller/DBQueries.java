package eagleeye.dbcontroller;

public class DBQueries {

	protected int deviceID;
	protected String query;
	
	public DBQueries(int deviceID){
		
		this.deviceID = deviceID;
	}
	
	public String getDevice(){
		
		query = "SELECT * FROM Device WHERE DeviceID = " + deviceID;
		return query;
	}
	
	public String getAllDirectory(){
		
		query = "SELECT * FROM Directory WHERE DeviceID = " + deviceID;
		return query;
	}
		
	public String getAllPhotos(){
		
		query = "SELECT * FROM Photo INNER JOIN Directory ON Photo.DirectoryID = Directory.DirectoryID "
				+ "WHERE DeviceID = " + deviceID;
		return query;		
	}
	
	public String getAllVideos(){
		
		query = "SELECT * FROM Video INNER JOIN Directory ON Video.DirectoryID = Directory.DirectoryID "
				+ "WHERE DeviceID = " + deviceID;
		return query;
	}
	
	public String getAllAudios(){
		
		query = "SELECT * FROM Audio INNER JOIN Directory ON Audio.DirectoryID = Directory.DirectoryID "
				+ "WHERE DeviceID = " + deviceID;
		return query;
	}
	
	public String getAllWifi(){
		
		query = "SELECT * FROM Wifi INNER JOIN Directory ON Wifi.DirectoryID = Directory.DirectoryID "
				+ "WHERE DeviceID = " + deviceID;
		return query;
	}
	
	public String getAllContacts(){
		
		query = "SELECT * FROM Contacts INNER JOIN Directory ON Contacts.DirectoryID = Directory.DirectoryID "
				+ "WHERE DeviceID = " + deviceID;
		return query;
	}
	
	public String getAllSMS(){
		
		query = "SELECT * FROM SMS INNER JOIN Directory ON SMS.DirectoryID = Directory.DirectoryID "
				+ "WHERE DeviceID = " + deviceID;
		return query;
	}
	

	public String getAllGarbageWithDirectory(){
		
		query = "SELECT * FROM Garbage INNER JOIN Directory ON Garbage.DirectoryID = Directory.DirectoryID "
				+ "WHERE DeviceID = " + deviceID;
		return query;
	}
	
	public String getAllGarbageWithoutDirectory(){
		
		query = "SELECT * FROM Garbage WHERE DeviceID = " + deviceID + " "
				+ "AND DirectoryID is NULL";
		return query;
	}
	
	
	
}
