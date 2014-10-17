package eagleeye.pluginmanager;

import java.util.List;

public interface Plugin {
	
	public String pluginName();
	
	public String pluginFunction();
	
	//for host application to pass parameters to plugin
	public int passParam (List param);
	
	//For host application to retrieve plugin work result
	public Object getResult();
	
	//execute plugin
	//public void run();
	
	//Signals that previous call to the plugin was not successful
	//check before calling plugin
	public boolean hasError();
}
