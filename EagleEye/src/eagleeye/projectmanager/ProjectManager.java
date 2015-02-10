package eagleeye.projectmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eagleeye.projectmanager.ProjectReader;
import eagleeye.projectmanager.ProjectWriter;
import eagleeye.pluginmanager.PluginManager;

public class ProjectManager {
	PluginManager pm;
	String projectPath;
	String deviceName;
	HashMap<String, List<List<String>>> markedItem;
	
	public ProjectManager(String path, String name)
	{
		projectPath = path;
		deviceName = name;
	}
	
	public void writeProjectFile(){
		System.out.println("writing marked items");
		
		ProjectWriter pw = new ProjectWriter(projectPath,deviceName);
		pw.writeFile(markedItem);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("marked items written "+dateFormat.format(date));
	}
	public void readProjectFile(){
		System.out.println("reading marked items");
		
		ProjectReader pr = new ProjectReader(projectPath);
		markedItem = pr.readFile();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("marked items read "+dateFormat.format(date));
	}
	public static void main(String[] args){
		ProjectManager projm = new ProjectManager("Path","DeviceName");
		projm.writeProjectFile();
		projm.readProjectFile();
	}
}
