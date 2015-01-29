package extractor.yaffs2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import eagleeye.api.entities.EagleFile;
import eagleeye.pluginmanager.Plugin;

//extends Service<ArrayList<ArrayList<EagleFile>>>
public class AndroidYaffs2ImageExtractorPlugin extends Application implements Plugin, Runnable {
	private List<FormatDescription> fds;
	private Thread t;
	public ProgressBar pb;
	public Text mb;
	@Override
	public String getName() {
		return "Android Yaffs2 Extractor";
	}
	
	@Override
	public Object getResult() {
		VBox vbox = new VBox();
		//HBox hbox = new HBox();
		pb = new ProgressBar(0);
		pb.setMinSize(500, 30);
		mb = new Text();
		vbox.getChildren().addAll(pb,mb);
		vbox.setPadding(new Insets(15, 12, 15, 12));
		
		if(t==null){	
			 t = new Thread (this, getName());
			 t.start (); 
		}

		return vbox;
	}

	@Override
	public Type getType() {
		return Type.EXTRACTOR;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int setAvailablePlugins(List<Plugin> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setParameter(List params) {
		fds = new ArrayList<FormatDescription>();
		
		String diskImgFolder = (String) params.get(0);
		
		AndroidBootFormatIdentifier abi = new AndroidBootFormatIdentifier();
		YAFFS2FormatIdentifier yaff2i = new YAFFS2FormatIdentifier();
		File dir = new File(diskImgFolder);
		if(dir.exists() && dir.isDirectory()){
			
			File[] files = dir.listFiles();
			for (File f:files) {
				try {
					FormatDescription fd = abi.identify(f);
					if(fd!=null){
						if(fd.getOperatingSystem().equalsIgnoreCase("android")){
							fds.add(fd);
							System.out.println(fd.getBinaryImageType());
						}
					}else{
						fd = yaff2i.identify(f);
						if(fd!=null){
							fds.add(fd);
							System.out.println(fd.getBinaryImageType());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else{
			System.out.println("ERROR: input path does not exist or is not a directory");
		}
		
		if (fds.size()>0){
			System.out.println("["+getName()+"] test successful");
			return 0;
		}else{
			System.out.println("["+getName()+"] test fail");
			return 1;
		}
	}
	@Override
	public void run() {
		System.out.println("extraction starts...");
		YAFFS2ImageUnpacker unpacker = new YAFFS2ImageUnpacker();
		for(FormatDescription fd: fds){
			unpacker.setFormatDescription(fd);
			try {
				Task<ArrayList<EagleFile>> task = unpacker.createTask();
				task.progressProperty().addListener(new ChangeListener<Number>(){
					@Override
					public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue){
						pb.setProgress(newValue.doubleValue());
					}
				});
				task.messageProperty().addListener(new ChangeListener<String>(){
					@Override
					public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue){
						mb.setText(newValue);
					}
				});
				task.run();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("extraction finished");
		
	}
/*
	@Override
	protected Task<ArrayList<ArrayList<EagleFile>>> createTask() {
		return new Task<ArrayList<ArrayList<EagleFile>>>() {
			@Override
			protected ArrayList<ArrayList<EagleFile>> call()
			{
				ArrayList<ArrayList<EagleFile>> ls = new ArrayList<ArrayList<EagleFile>>();
				YAFFS2ImageUnpacker unpacker = new YAFFS2ImageUnpacker();
				for(FormatDescription fd: fds){
					if(fd.getBinaryImageType().equalsIgnoreCase("YAFFS2")){
						unpacker.setFormatDescription(fd);
						try {
							ArrayList<EagleFile> files = unpacker.createTask();
							if(files!=null){
								ls.add(files);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				return ls;
			}
		};
	}*/

	
	/*********************** for testing ************************/
	@Override
	public void start(Stage stage) throws Exception {
		AndroidYaffs2ImageExtractorPlugin ex = new AndroidYaffs2ImageExtractorPlugin();
		
		String diskImgFolder = "D:/MyFolder/y4/CS3283_MediaTech_Project/DATA/Case2";
		List params = new ArrayList<String>();
		params.add(diskImgFolder);
		
		int status = ex.setParameter(params);
		
	    
		if(status==0){
			
			Node pc = (Node) ex.getResult();
			ScrollPane sp = new ScrollPane();
			sp.setContent(pc);
		    stage.setScene(new Scene(sp));
		    stage.show();
		    
		}
	}
	

	public static void main(String[] args){
		
		launch(args);
	
	}

}
