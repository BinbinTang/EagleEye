package Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import eagleeye.pluginmanager.Plugin;



public class TestPlugin extends Application implements Plugin{
	private List params;

	public TestPlugin(){

	}
	
	@Override 
	  public void start(Stage stage) {	
		Node pc = (Node)getResult();
		ScrollPane sp = new ScrollPane();
		sp.setContent(pc);
	    stage.setScene(new Scene(sp));
	    stage.show();
	  }

	  
	@Override
	public Object getResult() {
		
		Node rootLayout = null;
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(new URL("file:Test.fxml"));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
	        System.out.println("FXML not finded");
		}
        try {
			rootLayout = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rootLayout;
	}
	
	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int setParameter(List argList) {
		// TODO Auto-generated method stub
		params=argList;
		return 0;
	}
	
	@Override
	public Type getType() {
		return Type.GUI_VIEW;
	}
	
	@Override
	public String getName() {
		return "Test";
	}
	
	public static void main(String[] args) { 
		launch(args); 
	}

	@Override
	public int setAvailablePlugins(List<Plugin> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}