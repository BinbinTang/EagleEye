package eagleeye.projectmanager;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ProjectWriter {
	private String filePath;
	
	public ProjectWriter(String path)
	{
		filePath = path;
	}
	
	public void writeFile(HashMap<String, List<List<String>>> markedItem)
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
	    try {
	    	documentBuilder = dbFactory.newDocumentBuilder();
	        Document doc = documentBuilder.newDocument();
	        Element mainRootElement = doc.createElement("Plugin Marked Items");
	        doc.appendChild(mainRootElement);
	        
	        Iterator<String> iter = markedItem.keySet().iterator(); 
	        while (iter.hasNext()) {  
	            String key = iter.next(); 
	            List<List<String>> val = markedItem.get(key); 
	            mainRootElement.appendChild(getPluginContent(doc,key,val));
	        }   
	        
	        Transformer transformer = TransformerFactory.newInstance().newTransformer(); 
	        DOMSource source = new DOMSource(doc);
	        StreamResult result = new StreamResult(new File(filePath+File.separator+"markedFile.xml"));
	        transformer.transform(source, result);
	        
	    } catch (Exception e) {
	        	e.printStackTrace();
	    }   
	
	   
	}
	
	public Node getPluginContent (Document doc, String key, List<List<String>> content){
		Element plugin = doc.createElement("Plugin");
		
		plugin.setAttribute("Plugin Name", key);
		for (int i=1; i<content.size(); i++)
		{
			plugin.appendChild(getFileContent(doc, plugin, i, content.get(0),content.get(i)));
		}
		return plugin;
	}
	
	public Node getFileContent (Document doc, Element plugin, Integer id, List<String> name, List<String> content )
	{
		Element file = doc.createElement("File");
		plugin.setAttribute("ID", id.toString());
		
		for (int i=0; i<name.size(); i++)
		{
			file.appendChild(getFileElement(doc, file, name.get(i),content.get(i)));
		}
		return file;
	}
	
	public Node getFileElement (Document doc, Element file, String attributeName, String attributeContent)
	{
		Element node = doc.createElement(attributeName);
		node.appendChild(doc.createTextNode(attributeContent));
		return node;
	}
}
