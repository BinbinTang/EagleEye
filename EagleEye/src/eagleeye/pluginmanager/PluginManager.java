package eagleeye.pluginmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

		System.setSecurityManager(new PluginSecurityManager(pluginsDir));
	}

	public void getPlugins() {
		File dir = new File(System.getProperty("user.dir") + File.separator + pluginsDir);
		ClassLoader cl = new PluginClassLoader(dir,Plugin.class.getPackage().getName());
		if (dir.exists() && dir.isDirectory()) {
			// we'll only load classes directly in this directory -
			// no subdirectories, and no classes in packages are recognized
			String[] files = dir.list();
			for (int i=0; i<files.length; i++) {
				try {
					// only consider files ending in ".class"
					//TODO: load .jar file?
					if (! files[i].endsWith(".class"))
						continue;
					System.out.println("find Class in \""+dir.getName()+"\": "+files[i]);
					Class c = cl.loadClass(files[i].substring(0, files[i].indexOf(".")));
					Class[] intf = c.getInterfaces();
					for (int j=0; j<intf.length; j++) {
						if (intf[j].getName().equals(Plugin.class.getName())) {
							// the following line assumes that PluginFunction has a no-argument constructor
							Plugin pf = (Plugin) c.newInstance();
							plugins.add(pf);	
							continue;
						}
					}
				} catch (Exception ex) {
					System.err.println(ex);
					System.err.println("File " + files[i] + " does not contain a valid Plugin class.");
				}
			}
		}
	}
	/*protected void runPlugins() {
		System.out.println("==============================");
		System.out.println("running plugin....");
		int count = 1;
		Iterator iter = plugins.iterator();
		while (iter.hasNext()) {
			Plugin pf = (Plugin) iter.next();
			try {
				pf.passParam(count);
				System.out.println(pf.pluginName());
				//System.out.print(" ( "+count+" ) = ");
				if (pf.hasError()) {
					System.out.println("there was an error during plugin initialization");
					continue;
				}
				int result = (Integer) pf.getResult();
				if (pf.hasError())
					System.out.println("there was an error during plugin execution");
				else
					System.out.println(result);
				count++;
			} catch (SecurityException secEx) {
				System.err.println("plugin '"+pf.getClass().getName()+"' tried to do something illegal");
			}
		}
		System.out.println("finished");
		System.out.println("==============================");
	}*/
	public int extractFiles(String diskimgPath, String outputPath){
		ArrayList<EagleFile> result = new ArrayList<EagleFile>();
		
		System.out.println("==============================");
		System.out.println("running dataextraction plugin....");
		Iterator iter = plugins.iterator();
		while (iter.hasNext()) {
			Plugin pf = (Plugin) iter.next();
			try {
				
				//only try execute image unpacker class
				if(pf.pluginFunction()=="ImageUnpacker"){	
					System.out.println(pf.pluginName());
					
					//pass parameters to plugin
					List params = new ArrayList();
					params.add(diskimgPath);
					params.add(outputPath);
					pf.passParam(params);
					
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
						ef.modifyDateDeleted(f.getDateDeleted());
						ef.modifyIsModified(f.getIsModified());
						ef.modifyModifiedExt(f.getModifiedExt());
						ef.modifyDateCreated(f.getDateCreated());
						ef.modifyDateAccessed(f.getDateAccessed());
						ef.modifyDateModified(f.getDateModified());
						ef.modifyCategory(f.getCategory());
						fs.add(ef);
					}
					
					DBInsertTransaction transaction = new DBInsertTransaction();
					transaction.insertNewDeviceData(new Device("Test Device2", "100GB", "Li"), fs);
					
					break;
				}

			} catch (SecurityException secEx) {
				System.err.println("plugin '"+pf.getClass().getName()+"' tried to do something illegal");
			}
		}
		System.out.println("finished");
		System.out.println("==============================");
		
		return 0;
	}
}
