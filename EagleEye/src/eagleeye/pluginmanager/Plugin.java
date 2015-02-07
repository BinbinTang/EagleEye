package eagleeye.pluginmanager;

import java.util.List;

public interface Plugin {
	public enum Type{
		GUI_VIEW,
		GUI_POPUP,
		GUI_FILTER,
		READER,
		ANALYZER,
		EXTRACTOR
	}
	
	public String getName();
	
	public Type getType();
	
	//for plugin manager to pass available plugins
	public int setAvailablePlugins(List<Plugin> plugins);
	
	//for host application to pass parameters to plugin
	public int setParameter (List param);
	
	//execute plugin and get result
	public Object getResult();
	
	public Object getMarkedItems();
	
	public void setMarkedItems(Object i);
	
	//Signals that previous call to the plugin was not successful
	//check before calling plugin
	public boolean hasError();
}
