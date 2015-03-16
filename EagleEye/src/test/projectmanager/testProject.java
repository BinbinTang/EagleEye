package test.projectmanager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import eagleeye.projectmanager.Project;
public class testProject{

	public Project proj;
	public Project proj_nullMarked;
	public Project proj_zeroMarked;
	public Map<String, List<List<String>>> markedItems_good;
	public Map<String, List<List<String>>> markedItems_null; 
	public Map<String, List<List<String>>> markedItems_zero;
	public String testResourcePath;

	@Before
	public void init() {
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
		markedItems_good = new HashMap<String, List<List<String>>>();
		
		markedItems_good.put("Notes", plugin1);
		markedItems_good.put("Notes2", plugin2);

		proj = new Project("path",100,markedItems_good);
		proj_nullMarked = new Project("",-1,markedItems_null);
		proj_zeroMarked = new Project(null,10,markedItems_zero);
		
	}
	
	@Test
	public void testToString(){

		System.out.println(proj.toString());
		System.out.println(proj_nullMarked.toString());
		System.out.println(proj_zeroMarked.toString());
		assertTrue(true);
	}


}
