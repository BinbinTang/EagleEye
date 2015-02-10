package eagleeye.projectmanager;

import java.io.File;
import java.util.ArrayList;
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
	private String deviceName;
	
	public ProjectWriter(String path, String name)
	{
		filePath = path;
		deviceName = name;
	}
	
	public void writeFile(HashMap<String, ArrayList<ArrayList<String>>> markedItem)
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
	    try {
	    	documentBuilder = dbFactory.newDocumentBuilder();
	        Document doc = documentBuilder.newDocument();
	        Element mainRootElement = doc.createElement("Plugin-Marked-Items");
	        mainRootElement.setAttribute("Device-Name", deviceName);
	        doc.appendChild(mainRootElement);
	        
	        Iterator<String> iter = markedItem.keySet().iterator(); 
	        while (iter.hasNext()) {  
	            String key = iter.next(); 
	            ArrayList<ArrayList<String>> val = markedItem.get(key); 
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
	
	public Node getPluginContent (Document doc, String key, ArrayList<ArrayList<String>> content){
		Element plugin = doc.createElement("Plugin");
		plugin.setAttribute("Plugin-Name", key);
		
		for (int i=1; i<content.size(); i++)
		{
			plugin.appendChild(getFileContent(doc, plugin, i, content.get(0),content.get(i)));
		}
		return plugin;
	}
	
	public Node getFileContent (Document doc, Element plugin, Integer id, ArrayList<String> name, ArrayList<String> content )
	{
		Element file = doc.createElement("File");
		file.setAttribute("ID", id.toString());
		
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
	
	public static void main(String[] args)
	{
		ProjectWriter pw = new ProjectWriter("/Users/BinbinTang/Desktop","First");
		HashMap<String, ArrayList<ArrayList<String>>> markedItem = new HashMap<String, ArrayList<ArrayList<String>>> ();
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("Name");
		attributes.add("Content");
		attributes.add("FileMarked");
		
		ArrayList<String> file1 = new ArrayList<String>();
		file1.add("SMS");
		file1.add("Text");
		file1.add("Yes");
		
		ArrayList<String> file2 = new ArrayList<String>();
		file2.add("Call");
		file2.add("Number");
		file2.add("Yes");
		
		ArrayList<ArrayList<String>> plugin1 = new ArrayList<ArrayList<String>> ();
		plugin1.add(attributes);
		plugin1.add(file1);
		plugin1.add(file2);
		
		ArrayList<ArrayList<String>> plugin2 = new ArrayList<ArrayList<String>> ();
		plugin2.add(attributes);
		plugin2.add(file1);
		plugin2.add(file2);
		
		markedItem.put("Notes", plugin1);
		markedItem.put("Notes2", plugin2);
		
		pw.writeFile(markedItem);
	}
}
