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
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.PopupBuilder;
import javafx.stage.Stage;
import eagleeye.api.entities.EagleFile;
import eagleeye.pluginmanager.Plugin;

//extends Service<ArrayList<ArrayList<EagleFile>>>
public class AndroidYaffs2ImageExtractorPlugin extends Application implements Plugin{
	private List<FormatDescription> fds;
	//private Thread t;
	public ProgressBar pb;
	public Text mb;
	public List<List<EagleFile>> result;
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

		
		javafx.application.Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	            Stage stage = new Stage();
	            stage.setScene(new Scene(vbox));
	            stage.setAlwaysOnTop(true);
	            stage.show();
	        }
	    });

	    run();

		return result;
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
		String deviceName = (String) params.get(1);
		AndroidBootFormatIdentifier abi = new AndroidBootFormatIdentifier();
		YAFFS2FormatIdentifier yaff2i = new YAFFS2FormatIdentifier();
		File dir = new File(diskImgFolder);
		if(dir.exists() && dir.isDirectory()){
			
			File[] files = dir.listFiles();
			for (File f:files) {
				try {
					FormatDescription fd = abi.identify(f);

					if(fd!=null){
						fd.setDeviceName(deviceName);
						if(fd.getOperatingSystem().equalsIgnoreCase("android")){
							fds.add(fd);
							System.out.println(fd.getBinaryImageType());
						}
					}else{
						fd = yaff2i.identify(f);
						if(fd!=null){
							fd.setDeviceName(deviceName);
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
	//@Override
	public void run() {
		System.out.println("thread starts...");
		YAFFS2ImageUnpacker unpacker = new YAFFS2ImageUnpacker();
		result = new ArrayList<List<EagleFile>>();
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

				ArrayList<EagleFile> partition = task.get();
				if(partition!=null && partition.size()>0){
					result.add(partition);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		System.out.println("thread finished");
	}

	
	/*********************** for testing ************************/
	@Override
	public void start(Stage stage) throws Exception {
		
		//simulate main window
		ScrollPane sp = new ScrollPane();
	    stage.setScene(new Scene(sp));
	    stage.show();
		
		AndroidYaffs2ImageExtractorPlugin ex = new AndroidYaffs2ImageExtractorPlugin();
		
		String diskImgFolder = "D:/MyFolder/y4/CS3283_MediaTech_Project/DATA/Case2";
		String deviceName = "test";
		List params = new ArrayList<String>();
		params.add(diskImgFolder);
		params.add(deviceName);
		
		int status = ex.setParameter(params);
		
	    
		if(status==0){
			/*
			//execute the plugin in thread
			Thread t = new Thread(new Runnable() {
			    public void run() {

			    	List<List<EagleFile>> entityList = (List<List<EagleFile>>) ex.getResult();
			    	System.out.println("partition written = "+entityList.size());
			    	
			    	
			    }
			});
			
			//daemon thread will terminate on main thread termination
			t.setDaemon(true);
			t.start();
			*/
				
			Task task = new Task() {
			    @Override 
			    protected Object call() {
			    	System.out.println("called");
			    	List<List<EagleFile>> result = (List<List<EagleFile>>) ex.getResult();
			    	System.out.println("partition written = "+result.size());
					return null;
			    }
			};
			Thread th = new Thread(task);
	        th.setDaemon(true);
	        th.start();
			
		}
	}
	

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public Object getMarkedItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMarkedItems(Object arg0) {
		// TODO Auto-generated method stub
		
	}

}
