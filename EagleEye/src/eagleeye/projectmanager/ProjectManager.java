package eagleeye.projectmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import eagleeye.pluginmanager.PluginManager;

public class ProjectManager {
	PluginManager pm;
	public void writeProjectFile(){
		System.out.println("writing marked items");
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("marked items written "+dateFormat.format(date));
	}
	public void readProjectFile(){
		System.out.println("reading marked items");
		
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
