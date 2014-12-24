package view.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import eagleeye.pluginmanager.Plugin;

public class TextViewPlugin extends Application implements Plugin{
	private class CustomTextView{
		private String path;
		private CustomTextView(String path){
			this.path = path;
		}
		private Node initView(){
			Text t = new Text();
			t.setTranslateX(5.0);	//to indent a little
			BufferedReader br;
			try {
				File f = new File(path);
				System.out.println(f.getName());
				FileReader fr = new FileReader(path);
				br = new BufferedReader(fr);
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				while (line != null) {
					sb.append(line);
					sb.append("\n");
					line = br.readLine();
				}
				br.close();
				t.setText(sb.toString());
			} catch(Exception e){
				e.printStackTrace();
			}
			return t;
		}
	}
	private CustomTextView ctv;
	public TextViewPlugin(){
		
	}

	@Override
	public String getName() {
		return "Text View";
	}

	@Override
	public Object getResult() {
		return ctv.initView();
	}

	@Override
	public Type getType() {
		return Type.GUI_POPUP;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int setParameter(List argList) {
		String textPath = (String)argList.get(0);
		ctv = new CustomTextView(textPath);
		return 0;
	}
	/******************For Test**********************/
	@Override
	public void start(Stage stage) throws Exception {
		String path = ".."+File.separator+"EagleEye"+File.separator+"output2"+File.separator+"TXT"+File.separator+"f0002112.txt";
		
		double WindowWidth = 600;
		double WindowHeight = 400;
		double hBorder = 600-584.0;
		double vBorder = 400-362.0;	//TODO: automate to be platform-independent
		
		//set plugin input params
		List params = new ArrayList();
		params.add(path);
		params.add(WindowWidth-hBorder);
		params.add(WindowHeight-vBorder);
		setParameter (params);
		
		//get result
		Node pc = (Node)getResult();
		ScrollPane sp = new ScrollPane();
		sp.setContent(pc);
		
		stage.setScene(new Scene(sp));
		stage.setTitle(new File(path).getName());
		stage.setWidth(WindowWidth);
		stage.setHeight(WindowHeight);
		stage.show();
		
	}
	public static void main(String[] args) { 
		launch(args); 
	}
	/******************End Test**********************/
}
