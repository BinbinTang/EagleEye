package eagleeye.pluginmanager;

public class HostProgram {

	public static void main(String[] args)
	{
		String pluginFolder="PluginBinaries";
		PluginManager demo = new PluginManager(pluginFolder);
		demo.getPlugins();
		demo.runPlugins();
	}
	
}

