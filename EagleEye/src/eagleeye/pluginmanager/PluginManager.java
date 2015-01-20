package eagleeye.pluginmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import eagleeye.api.*;
import eagleeye.api.entities.EagleFile;
import eagleeye.dbcontroller.DBInsertTransaction;
import eagleeye.entities.Device;

public class PluginManager {
	protected String pluginsDir;
	protected List<Plugin> plugins;
	protected Map<String, List<String>> configList;
	
	public PluginManager (String _pluginsDir) {
		if (_pluginsDir!=null)
			pluginsDir = _pluginsDir;
		else
			pluginsDir = "PluginBinaries";

		plugins = new ArrayList<Plugin>();
		loadPlugins();

		//System.setSecurityManager(new PluginSecurityManager(pluginsDir));
	}
	public Plugin getPluginWithName(String name){
		for(Plugin pl : plugins){
			if(pl.getName().equals(name)){
				return pl;
			}
		}
		return null;
	}
	public Plugin getPluginWithClassName(String className){
		for(Plugin pl : plugins){
			String []tokens = pl.getClass().getName().split("\\.");
			if(tokens[tokens.length-1].equals(className)){
				return pl;
			}
		}
		return null;
	}
	public List<Plugin> getPlugins(){
		return plugins;
	}
	public List<String> getGUIViewPluginNames(){
		List<String> ls = new ArrayList<String>();
		for(Plugin pl: plugins){
			if(pl.getType()==Plugin.Type.GUI_VIEW){
				System.out.println(pl.getName());
				ls.add(pl.getName());
			}
		}
		return ls;
	}
	public List<String> getGUIPopupPluginNames(){
		List<String> ls = new ArrayList<String>();
		for(Plugin pl: plugins){
			if(pl.getType()==Plugin.Type.GUI_POPUP){
				System.out.println(pl.getName());
				ls.add(pl.getName());
			}
		}
		return ls;
	}
	public List<String> getAnalyzerPluginNames(){
		List<String> ls = new ArrayList<String>();
		for(Plugin pl: plugins){
			if(pl.getType()==Plugin.Type.ANALYZER){
				System.out.println(pl.getName());
				ls.add(pl.getName());
			}
		}
		return ls;
	}
	public void readConfig(){
		configList = new HashMap<String,List<String>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("pluginconfig.txt"));
			String line = br.readLine();
			String lastReadPlugin="";
			while(line!=null){
				int commentIdx = line.indexOf('#');
				if(commentIdx!=-1){
					line = line.substring(0,commentIdx);
				}
				String[] tokens = line.split("\\:");
				if(tokens[0].trim().equalsIgnoreCase("plugindir")){
					pluginsDir=tokens[1].trim();
				}else if(tokens[0].trim().equalsIgnoreCase("name")){
					lastReadPlugin = tokens[1].trim();
					configList.put(lastReadPlugin, new ArrayList<String>());
				}else if(tokens[0].trim().equalsIgnoreCase("uses")){
					List<String> pls = configList.get(lastReadPlugin);
					for(int i=1; i<tokens.length; i++){
						pls.add(tokens[i].trim());
					}
				}
				line = br.readLine();
			}
			
			//print plugin config
			Iterator it = configList.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        System.out.println(pairs.getKey() + " : " );
		        List<String> pls = (List<String>)pairs.getValue();
		        System.out.println ("use = "+pls);
		    }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: [EagleEye.PluginManager] cannot read \"pluginconfig.txt\"");
			e.printStackTrace();
		}
	}
	public void loadJar(String pathToJar) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		JarFile jarFile = new JarFile(pathToJar);
		Enumeration e = jarFile.entries();

		URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
		URLClassLoader cl = URLClassLoader.newInstance(urls);

	    while (e.hasMoreElements()) {
	        JarEntry je = (JarEntry) e.nextElement();
	        if(je.isDirectory() || !je.getName().endsWith(".class")){
	        	//System.out.println(je.toString());
	            continue;
	        }
		    String className = je.getName().substring(0,je.getName().indexOf("."));
		    className = className.replace('/', '.');
		    //System.out.println(className);
		    Class c = cl.loadClass(className);
		    Class[] intf = c.getInterfaces();

			for (int j=0; j<intf.length; j++) {
				if (intf[j].getName().equals(Plugin.class.getName())) {
					// the following line assumes that PluginFunction has a no-argument constructor
					Plugin pf = (Plugin) c.newInstance();
					plugins.add(pf);
					System.out.println("added to plugin list: "+pf.getName());
				}
			}
		}
	}	
	public void loadPlugins(){
		//read configuration of plugin relationships
		readConfig();
		
		//load plugins
		File dir = new File(pluginsDir);
		if (dir.exists() && dir.isDirectory()) {
			String[] files = dir.list();
			for (int i=0; i<files.length; i++) {
				if (! files[i].endsWith(".jar"))
					continue;
				String[] tokens = files[i].split("\\.");
				if(configList.get(tokens[0])!=null){
					try {
						//load .jar pluginfile
						System.out.println("find Jar in \""+dir.getName()+"\": "+files[i]);
						loadJar(pluginsDir+ File.separator+files[i]);
						
					} catch (Exception ex) {
						ex.printStackTrace();
						System.err.println("File " + files[i] + " does not contain a valid Plugin class.");
					}
				}
			}
		}
		
		
		connectPlugins();
	}
	
	//for each plugin, set up/down stream plugins
	public void connectPlugins(){
		for(Plugin pl: plugins){
			String[] tokens = pl.getClass().getName().split("\\.");
			System.out.println("---"+tokens[tokens.length-1]+"---");
			List<String>pls = configList.get(tokens[tokens.length-1]);
			List<Plugin> use = new ArrayList<Plugin>();
			for(String p: pls){
				Plugin pa = getPluginWithClassName(p);
				if(pa!=null){
					use.add(pa);
					System.out.println("use : "+p);
				}
			}
			pl.setAvailablePlugins(use);
		}
	}
	
	/****************** for test ***************************/
	public static void main(String[] args){
		
		PluginManager pm = new PluginManager("");
	}
}
