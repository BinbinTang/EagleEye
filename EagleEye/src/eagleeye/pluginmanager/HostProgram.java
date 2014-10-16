package eagleeye.pluginmanager;

import java.util.List;

import eagleeye.api.entities.EagleFile;

public class HostProgram {

	public static void main(String[] args)
	{
		String pluginFolder="PluginBinaries";
		String diskimgPath = "D:/MyFolder/y4/CS3283_MediaTech_Project/Case2/mtd8.dd";
		String outputPath = "D:/MyFolder/y4/CS3283_MediaTech_Project/CodeX/EagleEye/EagleEye/Output";
		PluginManager demo = new PluginManager(pluginFolder);
		demo.getPlugins();
		//demo.runPlugins();
		demo.extractFiles(diskimgPath,outputPath);
	}
	
}

