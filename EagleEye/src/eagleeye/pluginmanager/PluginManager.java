package eagleeye.pluginmanager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import eagleeye.api.*;
import eagleeye.api.entities.EagleFile;
import eagleeye.dbcontroller.DBInsertTransaction;
import eagleeye.entities.Device;

public class PluginManager {
	protected String pluginsDir;
	protected List<Plugin> plugins;
	
	public PluginManager (String _pluginsDir) {
		if (_pluginsDir!=null)
			pluginsDir = _pluginsDir;
		else
			pluginsDir = "PluginBinaries";

		plugins = new ArrayList<Plugin>();
		searchPlugins();

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
	public void searchPlugins(){
		File dir = new File(System.getProperty("user.dir") + File.separator + pluginsDir);
		if (dir.exists() && dir.isDirectory()) {
			String[] files = dir.list();
			for (int i=0; i<files.length; i++) {
				try {
					//load .jar file
					if (! files[i].endsWith(".jar"))
						continue;
					System.out.println("find Jar in \""+dir.getName()+"\": "+files[i]);
					loadJar(System.getProperty("user.dir") + File.separator + pluginsDir+ File.separator+files[i]);
					
				} catch (Exception ex) {
					ex.printStackTrace();
					System.err.println("File " + files[i] + " does not contain a valid Plugin class.");
				}
			}
		}
		broadCastAvailablePlugins();
	}
	
	public void broadCastAvailablePlugins(){
		for(Plugin pl: plugins){
			pl.setAvailablePlugins(plugins);
		}
	}

}
