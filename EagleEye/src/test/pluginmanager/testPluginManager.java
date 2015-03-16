package test.pluginmanager;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eagleeye.pluginmanager.PluginManager;
public class testPluginManager{
	PluginManager pm,pm_noConfig,pm_wrongConfig,pm_noBinary,pm_null,pm_bad;
	String testResourcePath;
	@Before
	public void init() {
		testResourcePath = "src"+File.separator+"test"+File.separator+"pluginmanager"+File.separator+"dummy";
		pm 				= new PluginManager(testResourcePath+File.separator+"good");
		pm_noConfig 	= new PluginManager(testResourcePath+File.separator+"noconfig");
		pm_wrongConfig 	= new PluginManager(testResourcePath+File.separator+"wrongconfig");
		pm_noBinary 	= new PluginManager(testResourcePath+File.separator+"nobinary");
		pm_null 		= new PluginManager(null);
		pm_bad 			= new PluginManager(testResourcePath+File.separator+"badPath:!@#$$%");
	}
	
	@Test
	public void testReadConfig() {
		assertTrue(pm.readConfig());
		assertTrue(pm_wrongConfig.readConfig());
		assertTrue(pm_noBinary.readConfig());
		
		assertFalse(pm_noConfig.readConfig());
		assertFalse(pm_null.readConfig());
		assertFalse(pm_bad.readConfig());
		assertTrue(pm_noConfig.getPlugins().isEmpty());
		assertTrue(pm_null.getPlugins().isEmpty());
		assertTrue(pm_bad.getPlugins().isEmpty());
	}
	
	@Test 
	public void testLoadPlugins(){
		assertTrue(pm.loadPlugins());
		assertTrue(pm_wrongConfig.loadPlugins());
		assertTrue(pm_noBinary.loadPlugins());
		
		assertFalse(pm_noConfig.loadPlugins());
		assertFalse(pm_null.loadPlugins());
		assertFalse(pm_bad.loadPlugins());
		assertTrue(pm_noConfig.getPlugins().isEmpty());
		assertTrue(pm_null.getPlugins().isEmpty());
		assertTrue(pm_bad.getPlugins().isEmpty());
	}
	
	@Test
	public void testConnectPlugins(){
		assertTrue(pm.connectPlugins());
		assertTrue(pm_wrongConfig.connectPlugins());
		assertTrue(pm_noBinary.connectPlugins());
	
	}

}
