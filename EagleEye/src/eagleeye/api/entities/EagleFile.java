package eagleeye.api.entities;

public interface EagleFile {
	
	public int getDeviceID();
	
	public int getDirectoryID();
 
	public int getFileID();
		
	public boolean getIsDirectory();
	
	public String getFileName();
	
	public String getFilePath();
	
	public String getFileExt();
	
	public int getFileExtID();
		
	public boolean getIsRecovered();
	
	public String getDateDeleted();
		
	public boolean getIsModified();
	
	public String getModifiedExt();

	public String getDateCreated();
	
	public String getDateAccessed();
	
	public String getDateModified();
	
	public String getCategory();
 
	public void modifyDeviceID(int deviceID);
	
	public void modifyDirectoryID(int directoryID);
	
	public void modifyFileID(int fileID);
	
	public void modifyIsDirectory(boolean isDirectory);
 
	public void modifyFileName(String fileName);
		
	public void modifyFilePath(String filePath);
	
	public void modifyFileExt(String fileExt);
	
	public void modifyFileExtID(int fileExtID);
	
	public void modifyIsRecovered(boolean isRecovered);
	
	public void modifyDateDeleted(String dateDeleted);
		
	public void modifyIsModified(boolean isModified);
	
	public void modifyModifiedExt(String modifiedExt);
		
	public void modifyDateCreated(String dateCreated);
	
	public void modifyDateAccessed(String dateAccessed);
	
	public void modifyDateModified(String dateModified);
	
	public void modifyCategory(String category);

	public int compareTo(EagleFile anotherFile);
}
