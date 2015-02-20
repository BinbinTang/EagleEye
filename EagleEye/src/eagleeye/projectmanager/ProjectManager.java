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
	PluginManager pm;
	DBController dbc;
	
	
	public ProjectManager(){
		
	}
	public void setPluginManager(PluginManager pm){
		this.pm=pm;
	}
	public void setDBController(DBController dbc){
		this.dbc = dbc;
	}
	public void writeProjectFile(){
		System.out.println("collecting marked items");
		Map<String, List<List<String>>> markedItem = pm.getAllPluginMarkedItems();
		
		System.out.println("writing marked items");
		String projectPath = dbc.getDeviceRootPath(); //"D:\\MyFolder\\y4\\CS3283_MediaTech_Project\\code\\EagleEye\\EagleEye\\output\\PLUG";
		System.out.println(projectPath);
		String deviceName = "deviceName"; //TODO: add getDeviceName from dbcontroller
		
		ProjectWriter pw = new ProjectWriter(projectPath,deviceName);
		pw.writeFile(markedItem);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("marked items written "+dateFormat.format(date));
	}
	public void readProjectFile(){
		System.out.println("reading marked items");
		String projectPath = dbc.getDeviceRootPath(); //"D:\\MyFolder\\y4\\CS3283_MediaTech_Project\\code\\EagleEye\\EagleEye\\output\\PLUG";
		System.out.println(projectPath);
		
		ProjectReader pr = new ProjectReader(projectPath);
		Map<String, List<List<String>>> markedItems = pr.readFile();
		System.out.println(markedItems.size());
		pm.setAllPluginMarkedItems(markedItems);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("marked items read "+dateFormat.format(date));
	}
	public static void main(String[] args){
		PluginManager pm = new PluginManager("PluginBinaries");
		DBController dbc = new DBQueryController();
		ProjectManager projm = new ProjectManager();
		projm.setDBController(dbc);
		projm.setPluginManager(pm);
		
		projm.writeProjectFile();
		projm.readProjectFile();
	}
}
