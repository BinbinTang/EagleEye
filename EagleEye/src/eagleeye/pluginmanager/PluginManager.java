package eagleeye.pluginmanager;

import java.io.BufferedReader;
import java.io.File;
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
import eagleeye.api.plugin.Plugin;

public class PluginManager {
	protected String pluginsDir;
	protected List<Plugin> plugins;
	protected Map<String, List<String>> configList;
	
	public PluginManager (String _pluginsDir) {
		plugins = new ArrayList<Plugin>();
		configList = new HashMap<String, List<String>>();
		setPluginsDir(_pluginsDir);
			
		//loadPlugins();

		//System.setSecurityManager(new PluginSecurityManager(pluginsDir));
	}
	public void clearData(){
		plugins.clear();
		configList.clear();
	}
	public void setPluginsDir(String _pluginsDir){
		clearData();
		if(_pluginsDir!=null){
			File dir = new File(_pluginsDir);
			if (dir.exists() && dir.isDirectory()){
				pluginsDir = _pluginsDir;
			}else{
				pluginsDir = null;
			}
		}else{
			pluginsDir = null;
		}
	}
	public List<Plugin> getPlugins(){
		return plugins;
	}
	public Map<String, List<String>> getConfigList(){
		return configList; 
	}
	public String getPluginsDir(){
		return pluginsDir;
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
	public List<Plugin> getExtractorPlugins(){
		List<Plugin> pls = new ArrayList<Plugin>();
		for(Plugin p : plugins){
			if(p.getType()==Plugin.Type.EXTRACTOR){
				pls.add(p);
			}
		}
		return pls;
	}
	public Map<String, List<List<String>>> getAllPluginMarkedItems(){
		
		Map<String, List<List<String>>> pluginsMarkedItems = new HashMap <String, List<List<String>>>();
		for(Plugin p: plugins){
			System.out.println(p);
			Object o = p.getMarkedItems();
			if(o!=null){
				try{
					List<List<String>> markedItems = (List<List<String>>) o;
					if(markedItems!=null && markedItems.size()!=0){
						String []tokens = p.getClass().getName().split("\\.");
						String pluginClassName = tokens[tokens.length-1];
						pluginsMarkedItems.put(pluginClassName, markedItems);
						System.out.println("[Plugin Manager] collected: "+p.getName()+" "+markedItems.size()+" item(s)");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return pluginsMarkedItems;
		
	}
	public void setAllPluginMarkedItems(Map<String, List<List<String>>> pluginsMarkedItems){

		for(Plugin p: plugins){
			String []tokens = p.getClass().getName().split("\\.");
			String pluginClassName = tokens[tokens.length-1];
			List<List<String>> markedItems = null;
			if(pluginsMarkedItems!=null){
				markedItems = pluginsMarkedItems.get(pluginClassName);
			}
			if(markedItems==null){
				markedItems = new ArrayList<List<String>>();
			}
			p.setMarkedItems(markedItems);
			System.out.println("[Plugin Manager] distributed: "+p.getName()+" "+markedItems.size()+" items");
		}

	}
	public boolean readConfig(){
		if(getPluginsDir()==null){
			System.out.println("ERROR: [EagleEye.PluginManager] plugin directory does not exist");
			return false;
		}else{
			try {
				BufferedReader br = new BufferedReader(new FileReader(pluginsDir+File.separator+"pluginconfig.txt"));
				String line = br.readLine();
				clearData();
				String lastReadPlugin="";
				while(line!=null){
					int commentIdx = line.indexOf('#');
					if(commentIdx!=-1){
						line = line.substring(0,commentIdx);
					}
					String[] tokens = line.split("\\:");
					if(tokens.length>0){
						if(tokens[0].trim().equalsIgnoreCase("name")){
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
				}
				
				//print plugin config
				Iterator it = configList.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
			        System.out.println(pairs.getKey() + " : " );
			        List<String> pls = (List<String>)pairs.getValue();
			        System.out.println ("use = "+pls);
			    }
			    return true;
				
			} catch (IOException e) {
				System.out.println("ERROR: [EagleEye.PluginManager.readConfig] cannot read \"pluginconfig.txt\"");
				return false;
			}
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
	public boolean loadPlugins(){
		//read configuration of plugin relationships
		if(readConfig()){

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
				connectPlugins();
				return true;
			}
			
			
			return false;
		}else{
			System.out.println("ERROR: [EagleEye.PluginManager.loadPlugins] readConfig failed");
			return false;
		}
	}
	
	//for each plugin, set up/down stream plugins
	public boolean connectPlugins(){
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
			if(pl.setAvailablePlugins(use)!=0){
				System.out.println("ERROR: [EagleEye.PluginManager.connectPlugins] configured connection was wrong");
			}
		}
		return true;
	}
	
	/****************** for test ***************************/
	public static void main(String[] args){
		
		PluginManager pm = new PluginManager("");
	}
}
