package eagleeye.model;
import java.util.ArrayList;
import java.util.List;

import eagleeye.entities.Device;

public interface RequestHandler {
	
	public ArrayList<eagleeye.entities.FileEntity> getFolderStructure();
	public ArrayList<Device> getExistingDevices();
	public String getFilePath(int id);
	public String getFileSize(int id);
	
}
