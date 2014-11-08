package eagleeye.pluginmanager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import eagleeye.api.entities.EagleFile;

public class HostProgram {

	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		String pluginFolder="PluginBinaries";
		String diskimgPath = "D:/MyFolder/y4/CS3283_MediaTech_Project/Case2/mtd8.dd";
		String outputPath = "D:/MyFolder/y4/CS3283_MediaTech_Project/CodeX/EagleEye/EagleEye/Output";
		
		PluginManager demo = new PluginManager(pluginFolder);
		demo.searchPlugins();
		//demo.extractFiles(diskimgPath,outputPath);
		demo.loadView();
	}

}

