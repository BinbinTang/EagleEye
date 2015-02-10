package eagleeye.projectmanager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ProjectReader {
	
	private String filePath;
	private HashMap<String, List<List<String>>> markedItem;
	
	public ProjectReader(String path)
	{
		filePath = path;
		markedItem = new HashMap<String, List<List<String>>>();
	}
	
	public HashMap<String, List<List<String>>> readFile() {
		
		
		File fXmlFile = new File(filePath+File.separator+"markedFile.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nl = doc.getElementsByTagName("Plugin-Name");
			if (doc.hasChildNodes())
				executeRootNodes(doc.getChildNodes().item(0));
			
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return markedItem;
	}
	
	public void executeRootNodes(Node root)
	{
		NodeList nl = root.getChildNodes();
		Element plugin;
		for (int i=0; i<nl.getLength(); i++)
		{
			plugin = (Element) nl.item(i);
			String name = plugin.getAttribute("Plugin-Name");
			ArrayList<List<String>> var = executePlugin(plugin);
			markedItem.put(name, var);
		}
	}
	
	public ArrayList<List<String>> executePlugin(Element plugin)
	{
		NodeList nl = plugin.getChildNodes();
		Element file;
		if (nl.getLength() == 0)
			return null;
		ArrayList<List<String>> markedFile = new ArrayList<List<String>>();
		markedFile.add(retrieveAttributesName((Element) nl.item(0)));
		for (int i=0; i<nl.getLength();i++)
		{
			file = (Element) nl.item(i);
			markedFile.add(retrieveFileContent(file));
		}
		return markedFile;
	}
	
	public ArrayList<String> retrieveAttributesName(Element file)
	{
		ArrayList<String> attributesName = new ArrayList<String>();
		NodeList nl = file.getChildNodes();
		
		for (int i=0; i<nl.getLength(); i++)
		{
			Node attributes = nl.item(i);
			attributesName.add(attributes.getNodeName());
		}
		return attributesName;
	}
	
	public ArrayList<String> retrieveFileContent(Element file)
	{
		ArrayList<String> fileContent = new ArrayList<String>();
		NodeList nl = file.getChildNodes();
		
		for (int i=0; i<nl.getLength(); i++)
		{
			Node attribute = nl.item(i);
			fileContent.add(attribute.getTextContent());
		}
		return fileContent;
	}
	
	public static void main(String[] args)
	{
		ProjectReader reader = new ProjectReader("/Users/BinbinTang/Desktop");
		HashMap<String, List<List<String>>> markedItem = reader.readFile();
		
		Iterator<String> iter = markedItem.keySet().iterator(); 
        while (iter.hasNext()) {  
            String key = iter.next(); 
            System.out.println("Key1 = "+key);
            List<List<String>> val = markedItem.get(key);
            System.out.println(val.toString());
        }
	}
}
