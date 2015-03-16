package test.projectmanager;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eagleeye.projectmanager.ProjectManager;
import eagleeye.projectmanager.Project;
public class testProjectManager{

	ProjectManager pm;
	public Map<String, List<List<String>>> markedItems_original;
	public Map<String, List<List<String>>> markedItems_updated;
	public Map<String, List<List<String>>> markedItems_null; 
	public Map<String, List<List<String>>> markedItems_zero;
	String testResourcePath;
	String originalProjectPath;
	String newProjectPath;
	
	@Before
	//this is called before every tests
	public void init() {
		testResourcePath = "src"+File.separator+"test"+File.separator+"projectmanager";
		originalProjectPath = testResourcePath+File.separator+"originalPath.xml";
		newProjectPath = testResourcePath+File.separator+"newPath.xml";

		List<String> attributes = new ArrayList<String>();
		attributes.add("Name");
		attributes.add("Content");
		attributes.add("FileMarked");
		
		List<String> file1 = new ArrayList<String>();
		file1.add("SMS");
		file1.add("Text");
		file1.add("Yes");
		
		List<String> file2 = new ArrayList<String>();
		file2.add("Call");
		file2.add("Number");
		file2.add("Yes");
		
		List<List<String>> plugin1 = new ArrayList<List<String>> ();
		plugin1.add(attributes);
		plugin1.add(file1);
		plugin1.add(file2);
		
		List<List<String>> plugin2 = new ArrayList<List<String>> ();
		plugin2.add(attributes);
		plugin2.add(file1);
		plugin2.add(file2);
		
		markedItems_null = null;
		markedItems_zero = new HashMap<String, List<List<String>>>();
		markedItems_original = new HashMap<String, List<List<String>>>();
		markedItems_updated = new HashMap<String, List<List<String>>>();
		
		markedItems_original.put("Notes", plugin1);
		markedItems_updated.put("Notes", plugin1);
		markedItems_updated.put("Notes2", plugin2);

		pm = new ProjectManager();
		
		
	}

	
	@Test
	//case : SAVE AS
	public void testWriteProjectFile_good() {
		Project proj = new Project(originalProjectPath,100,markedItems_original); 
		pm.setProject(proj);
		
		assertTrue(pm.writeProjectFile(newProjectPath , markedItems_updated));
		File writtenFile = new File (newProjectPath);
		assertTrue(writtenFile.exists());
		BufferedReader br;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(newProjectPath));
			String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        br.close();
		} catch (Exception e1) {
		} 
		
		
		String fileContent = sb.toString();
		
		String correctOutput = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<Plugin-Marked-Items Device-Name=\"100\">"
		+ 	"<Plugin Plugin-Name=\"Notes2\">"
		+ 			"<File ID=\"1\">"
		+ 				"<Name>SMS</Name>"
		+ 				"<Content>Text</Content>"
		+ 				"<FileMarked>Yes</FileMarked>"
		+ 			"</File>"
		+ 			"<File ID=\"2\">"
		+ 				"<Name>Call</Name>"
		+ 				"<Content>Number</Content>"
		+ 				"<FileMarked>Yes</FileMarked>"
		+ 			"</File>"
		+ 	"</Plugin>"
		+ 	"<Plugin Plugin-Name=\"Notes\">"
		+ 			"<File ID=\"1\">"
		+ 				"<Name>SMS</Name>"
		+ 				"<Content>Text</Content>"
		+ 				"<FileMarked>Yes</FileMarked>"
		+ 			"</File><File ID=\"2\">"
		+ 				"<Name>Call</Name>"
		+ 				"<Content>Number</Content>"
		+ 				"<FileMarked>Yes</FileMarked>"
		+ 			"</File>"
		+ 	"</Plugin>"
		+ "</Plugin-Marked-Items>\n";
		assertTrue(fileContent.trim().equals(correctOutput.trim()));
		assertTrue(pm.getProject().getMarkedItems() == markedItems_updated);
		assertTrue(pm.getProject().getProjectPath().equals(newProjectPath));
	}
	
	@Test
	//case :  SAVE 
	public void testWriteProjectFile_inputPathIsNull() {
		Project proj = new Project(originalProjectPath,100,markedItems_original); 
		pm.setProject(proj);	
		
		assertTrue(pm.writeProjectFile(null, markedItems_updated));
		File writtenFile = new File (originalProjectPath);
		assertTrue(writtenFile.exists());
		BufferedReader br;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(originalProjectPath));
			String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        br.close();
		} catch (Exception e1) {
		} 
		
		
		String fileContent = sb.toString();
		
		String correctOutput = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<Plugin-Marked-Items Device-Name=\"100\">"
		+ 	"<Plugin Plugin-Name=\"Notes2\">"
		+ 			"<File ID=\"1\">"
		+ 				"<Name>SMS</Name>"
		+ 				"<Content>Text</Content>"
		+ 				"<FileMarked>Yes</FileMarked>"
		+ 			"</File>"
		+ 			"<File ID=\"2\">"
		+ 				"<Name>Call</Name>"
		+ 				"<Content>Number</Content>"
		+ 				"<FileMarked>Yes</FileMarked>"
		+ 			"</File>"
		+ 	"</Plugin>"
		+ 	"<Plugin Plugin-Name=\"Notes\">"
		+ 			"<File ID=\"1\">"
		+ 				"<Name>SMS</Name>"
		+ 				"<Content>Text</Content>"
		+ 				"<FileMarked>Yes</FileMarked>"
		+ 			"</File><File ID=\"2\">"
		+ 				"<Name>Call</Name>"
		+ 				"<Content>Number</Content>"
		+ 				"<FileMarked>Yes</FileMarked>"
		+ 			"</File>"
		+ 	"</Plugin>"
		+ "</Plugin-Marked-Items>\n";
		System.out.println("|"+fileContent+"|");
		System.out.println("|"+correctOutput+"|");
		assertTrue(fileContent.trim().equals(correctOutput.trim()));
		assertTrue(pm.getProject().getMarkedItems() == markedItems_updated);
		assertTrue(pm.getProject().getProjectPath().equals(originalProjectPath));
	}
	
	@Test
	public void testWriteProjectFile_markedItemsIsNull() {
		Project proj = new Project(originalProjectPath,100,markedItems_original); 
		pm.setProject(proj);
		assertFalse(pm.writeProjectFile(testResourcePath+File.separator+"markedItemsIsNull.xml", null));
		File writtenFile = new File (testResourcePath+File.separator+"markedItemsIsNull.xml");
		assertFalse(writtenFile.exists());
	}
	
	@Test
	public void testWriteProjectFile_projectIsNull() {
		pm.setProject(null);
		assertFalse(pm.writeProjectFile(testResourcePath+File.separator+"projectIsNull.xml", markedItems_original));
		File writtenFile = new File (testResourcePath+File.separator+"projectIsNull.xml");
		assertFalse(writtenFile.exists());
	}
	
	@After
	//this is called after every test
	public void clearUpWrittenXML(){
		
		File dir = new File(testResourcePath);
		if (dir.exists() && dir.isDirectory()) {
			String[] files = dir.list();
			for (int i=0; i<files.length; i++) {
				//ensure there was no previous leftover project files in the directory
				if(files[i].endsWith(".xml")){
					try{
						System.out.println("existed: "+files[i]);
						 
			    		File file = new File(testResourcePath+File.separator+files[i]);
			 
			    		if(file.delete()){
			    			System.out.println(file.getName() + " is deleted!");
			    		}else{
			    			System.out.println("Delete operation is failed.");
			    		}
			 
			    	}catch(Exception e){
			 
			    		e.printStackTrace();
			 
			    	}
				}
			}
		}
	}


}
