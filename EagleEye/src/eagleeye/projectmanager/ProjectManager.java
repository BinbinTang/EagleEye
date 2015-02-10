package eagleeye.projectmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import eagleeye.projectmanager.ProjectReader;
import eagleeye.projectmanager.ProjectWriter;
import eagleeye.pluginmanager.PluginManager;

public class ProjectManager {
	PluginManager pm;
	String projectPath;
	public void writeProjectFile(){
		System.out.println("writing marked items");
		
		ProjectWriter pw = new ProjectWriter(projectPath);
		
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("marked items written "+dateFormat.format(date));
	}
	public void readProjectFile(){
		System.out.println("reading marked items");
		
		ProjectReader pw = new ProjectReader(projectPath);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("marked items read "+dateFormat.format(date));
	}
	public static void main(String[] args){
		ProjectManager projm = new ProjectManager();
		projm.writeProjectFile();
		projm.readProjectFile();
	}
}
