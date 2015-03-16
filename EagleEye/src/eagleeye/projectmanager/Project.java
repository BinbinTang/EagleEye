package eagleeye.projectmanager;

import java.util.List;
import java.util.Map;

public class Project {
	private String projectPath;
	private int deviceID;
	private Map<String, List<List<String>>> markedItems;
	
	public Project(String path, int id, Map<String, List<List<String>>> items){
		setProjectPath (path);
		setDeviceID (id);	
		setMarkedItems(items);
	}
	
	/**
	 * <p>
	 * return projectfile's storing path. 
	 * <p>
	 * @return - String of path. COULD BE NULL
	 */
	public String getProjectPath() {
		return projectPath;
	}
	
	
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath; 
	}

	public int getDeviceID() {
		
		return deviceID;
	}

	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}

	public Map<String, List<List<String>>> getMarkedItems() {
		return markedItems;
	}

	public void setMarkedItems(Map<String, List<List<String>>> markedItems) {
		this.markedItems = markedItems;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("[Project] Path = "+projectPath+"\n");
		sb.append("[Project] DeviceID = "+deviceID+"\n");
		if(markedItems!=null){
			sb.append("[Project] Marked plugins = "+markedItems.size());
		}else{
			sb.append("[Project] Marked plugins = null");
		}
		
		return sb.toString();
		
	}
	
	
}
