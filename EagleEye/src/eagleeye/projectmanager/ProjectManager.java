package eagleeye.projectmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eagleeye.api.dbcontroller.DBController;
import eagleeye.dbcontroller.DBQueryController;
import eagleeye.projectmanager.ProjectReader;
import eagleeye.projectmanager.ProjectWriter;
import eagleeye.pluginmanager.PluginManager;

public class ProjectManager {

	Project openedProject;
	
	public ProjectManager(){
		openedProject = null;
	}
	
	public void setProject(Project p){
		openedProject = p;
	}
	public Project getProject(){
		return openedProject;
	}
	public void writeProjectFile(String path, Map<String, List<List<String>>> markedItems){
		if(openedProject == null){
			System.out.println("[Project Manager] no opened project");
			return;
		}
		
		openedProject.setMarkedItems(markedItems);
		
		System.out.println("[Project Manager] writing project ... ");
		
		String projectPath; 
		if(path==null){
			projectPath = openedProject.getProjectPath();
		}else{
			projectPath = path;
		}
		
		String deviceID = ""+openedProject.getDeviceID(); 
		
		ProjectWriter pw = new ProjectWriter(projectPath,deviceID);
		pw.writeFile(openedProject.getMarkedItems());
		
		System.out.println(openedProject);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("[Project Manager] project written successfully "+dateFormat.format(date));
	}
	public Project readProjectFile(String path){
		
		System.out.println("[Project Manager] reading project ...");
		ProjectReader pr = new ProjectReader(path);
		openedProject = pr.readFile();
		
		
		if(openedProject==null){
			System.out.println("[Project Manager] FAILED project read");
		}else{
			System.out.println(openedProject);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println("[Project Manager] project read successfully "+dateFormat.format(date));
		}
		
		return openedProject;
	}
	public static void main(String[] args){

		ProjectManager projm = new ProjectManager();
		
		
		
		projm.readProjectFile("output/PLUG/markedFile.xml");
		
		HashMap<String, List<List<String>>> markedItem = new HashMap<String, List<List<String>>> ();
		List<String> attributes = new ArrayList<String>();
		attributes.add("Name");
		attributes.add("Content");
		attributes.add("FileMarked");
		
		List<String> file1 = new ArrayList<String>();
		file1.add("SMS");
		file1.add("Text");
		file1.add("Yes");
		
		List<String> file2 = new ArrayList<String>();
		file2.add("Call");
		file2.add("Number");
		file2.add("Yes");
		
		List<List<String>> plugin1 = new ArrayList<List<String>> ();
		plugin1.add(attributes);
		plugin1.add(file1);
		plugin1.add(file2);
		
		List<List<String>> plugin2 = new ArrayList<List<String>> ();
		plugin2.add(attributes);
		plugin2.add(file1);
		plugin2.add(file2);
		
		markedItem.put("Notes", plugin1);
		markedItem.put("Notes2", plugin2);
		
		projm.writeProjectFile("output/test.xml",markedItem);
		
	}
}
