package reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import eagleeye.pluginmanager.Plugin;



public class ImageReaderPlugin implements Plugin{
	private String filepath;

	public ImageReaderPlugin(){

	}
	
	@Override
	public Object getResult() {
		File img = new File(filepath);
		return img;
	}
	
	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int setParameter(List argList) {
		// TODO Auto-generated method stub
		
		filepath=(String)argList.get(0);
		System.out.println(filepath);
		return 0;
	}
	
	@Override
	public Type getType() {
		return Type.READER;
	}
	
	@Override
	public String getName() {
		return "Image Reader";
	}
	
	public static void main(String[] args) { 
		ImageReaderPlugin ip = new ImageReaderPlugin();
		List paths = new ArrayList();
		paths.add(".."+File.separator+"EagleEye"+File.separator+"output2"+File.separator+"JPG"+File.separator+"f0495561.jpg");
		ip.setParameter(paths);
		ip.getResult();
	}
}