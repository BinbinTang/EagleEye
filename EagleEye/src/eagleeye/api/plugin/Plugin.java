package eagleeye.api.plugin;

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
	
	/**
	 * <p>
	 * Returns plugin name as a String
	 * <p>
	 * @return - String of plugin name
	 */
	
	public String getName();
	/**
	 * <p>
	 * Returns plugin type.
	 * <p>
	 * @return - Type. Available types enums include:
	 * <p>
	 * <ul>
	 * 	<li> GUI_VIEW	- displayed in main window </li>
	 *	<li> GUI_POPUP	- displayed in popup window </li>
	 *	<li> GUI_FILTER - Not in use </li>
	 *	<li> READER		- reads a type of data </li>
	 *	<li> ANALYZER	- analyzes data </li>
	 *	<li> EXTRACTOR	- extracts raw device image </li>
	 *	</ul>
	 */
	public Type getType();
	
	/**
	 * <p>
	 * Specify other plugins that can be used by this plugin. 
	 * <p>
	 * @param plugins - List of all Plugin objects that can
	 * 					be used by this plugin and specified
	 * 					in the pluginConfig.txt file.
	 * @return - int, 0 for success.
	 */
	public int setAvailablePlugins(List<Plugin> plugins);
	
	/**
	 * <p>
	 * Set the parameters used by the plugin to perform its dedicated
	 * task. 
	 * <p>
	 * @param param - List of Objects, developer should validate
	 * 				  the content of this list before casting. 
	 * 				  The parameter structure of each plugin should
	 * 				  be documented in the pluginConfig.txt file. 
	 * @return - int, 0 for success. Developer can specify other return
	 * 			 values to indicate different types of errors.
	 */
	public int setParameter (List param);
	
	/**
	 * <p>
	 * Returns the plugin's run result. 
	 * <p>
	 * @return - Object, the return data structure should be documented
	 * 			 in the pluginConfig.txt file.
	 */
	public Object getResult();
	
	/**
	 * <p>
	 * Returns a list of marked itmes. Items may be markable by the user
	 * in the plugin so that the marked items can be saved into project file
	 * or printed on a report. 
	 * <p>
	 * 
	 * @return - Object. However, the actual structure of the object should
	 * 			 strictly be List<List<String>> where the first list of string
	 * 			 stores the file names while the following list of strings
	 * 			 stores the actual data of each field.
	 */
	public Object getMarkedItems();
	
	/**
	 * <p>
	 * Set the list of marked items read from a project file. This is to allow
	 * marked files in previous editing to be redisplayed after program restarts
	 * and the project file is loaded.
	 * <p>
	 * 
	 * @param markedItems - Object. However, the actual structure of teh object
	 * 					    strictly be List<List<String>> where the first list 
	 * 						of string stores the file names while the following 
	 * 						list of strings stores the actual data of each field.
	 */
	public void setMarkedItems(Object markedItems);
	
	/**
	 * <p>
	 * Returns true if there's an error in the plugin. This method is mainly for
	 * checking whether the plugin is in a good condition to run its dedicated
	 * task.
	 * <p>
	 * @return - true if has error. 
	 */
	public boolean hasError();
}
