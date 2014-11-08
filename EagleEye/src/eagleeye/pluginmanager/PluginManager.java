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
	protected List plugins;
	
	public PluginManager (String _pluginsDir) {
		if (_pluginsDir!=null)
			pluginsDir = _pluginsDir;
		else
			pluginsDir = "PluginBinaries";

		plugins = new ArrayList();
		searchPlugins();

		//System.setSecurityManager(new PluginSecurityManager(pluginsDir));
	}
	public Plugin getPluginWithName(String name){
		for(Object o : plugins){
			Plugin pl = (Plugin)o;
			if(pl.getName().equals(name)){
				return pl;
			}
		}
		return null;
	}
	public List getPlugins(){
		return plugins;
	}
	public List<String> getGUIPluginNames(){
		List<String> ls = new ArrayList<String>();
		for(Object o: plugins){
			Plugin pl = (Plugin) o;
			if(pl.getType()==Plugin.Type.GUI_VIEW){
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
	}
	/*
	public int extractFiles(String diskimgPath, String outputPath){
		ArrayList<EagleFile> result = new ArrayList<EagleFile>();
		
		System.out.println("==============================");
		System.out.println("running dataextraction plugin....");
		Iterator iter = plugins.iterator();
		while (iter.hasNext()) {
			Plugin pf = (Plugin) iter.next();
			try {
				
				//only try execute image unpacker class
				if(pf.getType()==Plugin.Type.EXTRACTOR){	
					System.out.println(pf.getName());
					
					//pass parameters to plugin
					List params = new ArrayList();
					params.add(diskimgPath);
					params.add(outputPath);
					pf.setParameter(params);
					
					if (pf.hasError()) {
						System.out.println("there was an error during plugin initialization");
						continue;
					}
					result = (ArrayList<EagleFile>) pf.getResult();
					if (pf.hasError())
						System.out.println("there was an error during plugin execution");
					else
						System.out.println("successfully extracted "+result.size()+"files");
					
					ArrayList<eagleeye.entities.FileEntity> fs = new ArrayList<eagleeye.entities.FileEntity>();
					for(EagleFile f: result){
						eagleeye.entities.FileEntity ef = new eagleeye.entities.FileEntity();
						ef.modifyDeviceID(f.getDeviceID());
						ef.modifyDirectoryID(f.getDirectoryID());
						ef.modifyFileID(f.getFileID());
						ef.modifyIsDirectory(f.getIsDirectory());
						ef.modifyFileName(f.getFileName());
						ef.modifyFilePath(f.getFilePath());
						ef.modifyFileExt(f.getFileExt());
						ef.modifyFileExtID(f.getFileExtID());
						ef.modifyIsRecovered(f.getIsRecovered());
						ef.modifyIsModified(f.getIsModified());
						ef.modifyContentType(f.getContentType());
						ef.modifyDateCreated(f.getDateCreated());
						ef.modifyDateAccessed(f.getDateAccessed());
						ef.modifyDateModified(f.getDateModified());
						ef.modifyCategory(f.getCategory());
						fs.add(ef);
					}
					
					DBInsertTransaction transaction = new DBInsertTransaction();
					//transaction.insertNewDeviceData(new Device("Test Device2", "100GB", "Li"), fs);
					
					break;
				}

			} catch (SecurityException secEx) {
				System.err.println("plugin '"+pf.getClass().getName()+"' tried to do something illegal");
			}
		}
		System.out.println("finished");
		System.out.println("==============================");
		
		return 0;
	}*/
}
