package eagleeye.pluginmanager;

import java.util.List;

public interface Plugin {
	public enum Type{
		GUI_VIEW,
		GUI_FILTER,
		ANALYZER,
		EXTRACTOR,
	}
	
	public String getName();
	
	public Type getType();
	
	//for host application to pass parameters to plugin
	public int setParameter (List param);
	
	//execute plugin and get result
	public Object getResult();

	//Signals that previous call to the plugin was not successful
	//check before calling plugin
	public boolean hasError();
}
