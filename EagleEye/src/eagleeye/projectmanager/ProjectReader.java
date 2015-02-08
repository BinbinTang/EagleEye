package eagleeye.projectmanager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ProjectReader {
	private String filePath;
	public ProjectReader(String path)
	{
		filePath = path;
	}
	
	public HashMap<String, List<List<String>>> readFile() {
		HashMap<String, List<List<String>>> markedItem = new HashMap<String, List<List<String>>>();
		
		File fXmlFile = new File(filePath+File.separator+"markedFile.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			if (doc.hasChildNodes())
				executeChildNodes(doc.getChildNodes());
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
	
	public void executeChildNodes(NodeList nodeList)
	{
		
	}
}
