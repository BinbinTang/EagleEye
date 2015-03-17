package eagleeye.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import eagleeye.api.dbcontroller.DBController;
import eagleeye.api.entities.EagleDevice;
import eagleeye.api.entities.EagleFile;
import eagleeye.api.plugin.Plugin;
import eagleeye.dbcontroller.DBInsertTransaction;
import eagleeye.dbcontroller.DBQueryController;
import eagleeye.entities.Device;
import eagleeye.main.MainApp;
import eagleeye.pluginmanager.*;
import eagleeye.projectmanager.Project;
import eagleeye.projectmanager.ProjectManager;
import eagleeye.report.ReportGenerator;

public class WorkBenchControllerFinal {		
	// DataBase
	private DBController dbController;
	
	// File chooser
	private File file;
	private Label labelFilePath = new Label();
	private File directory;
	private Label labelDirPath = new Label();

	// Path to identify current case
	private String casePath = "";
	private int currentCaseID = -1;
	private int currentDirID = 0;
	private String currentDir = "";	
	private Project currentProject = null;
	
	@FXML
	private ScrollPane MainResultPane;	
	@FXML
	private StackPane timeLineViewPane;

	// Menubar
	ArrayList<String> functionList;
	@FXML MenuItem openProject;
	@FXML MenuItem saveProject;
	@FXML MenuItem saveAsProject;
	@FXML MenuItem generateReport;
	
	@FXML
	private MenuItem newDevice;
	@FXML
	private Menu existDeviceMenu;
	@FXML 
	private Menu analysisMenu;
	
	// Fucntion Hbox
	@FXML
	private HBox functionHBox;
	
	// Progress information label
	@FXML private Label progressLabel;

	// Reference to the main application.
	private MainApp mainAppFinal;
		
	//plugin manager
	private PluginManager pm;
	
	//project manager
	private ProjectManager projm;
	
	private ProgressBar importProgressIndicator;
	
	/**
	 * The constructor.
	 */
	public WorkBenchControllerFinal() {
		
		functionList = new ArrayList<String>();
	}

	/**
	 * The initializer.
	 */

	@FXML
	private void initialize() {	
		pm = new PluginManager("PluginBinaries");
		pm.loadPlugins();
		dbController = new DBQueryController();
		projm = new ProjectManager();
		
		//add pluginsnames to functionList
		List<String> plnames=pm.getGUIViewPluginNames();
		for(String s : plnames){
			System.out.println("TOOL: "+ s);
			functionList.add(s);
		}
		
		
		
		
		// Check device ID
		if(dbController.getDeviceID() == -1){
			Label noDevice = new Label("No device has been chosen.");
			MainResultPane.setContent(noDevice);
		}			
		
		// Add bonding to functions checked dynamically and functions visible
		for(String functionName : functionList){
			// create menuItem
			CheckMenuItem newFuctionCheck = new CheckMenuItem(functionName);
			newFuctionCheck.setSelected(true);
			analysisMenu.getItems().add(newFuctionCheck);			

			// create function button
			Button newBtn = new Button(functionName);
			newBtn.setTooltip(new Tooltip(functionName));
			newBtn.setPrefHeight(40);
			newBtn.setStyle("-fx-font-size: 16;");
			
			functionHBox.getChildren().add(newBtn);
			
			//System.out.println(functionName);
			if(functionName.equals("")){
				newBtn.setOnMouseClicked(new EventHandler<MouseEvent>(){
					@Override
					public void handle(MouseEvent event) {
						if(dbController.getDeviceID() != -1){
						}else{
							Label noDevice = new Label("No device has been chosen.");
							MainResultPane.setContent(noDevice);
						}
						updateProgress("Current selected tool: " +functionName );
					}
				});
			}

			else{
				Plugin pl = pm.getPluginWithName(functionName);
				if(pl!=null){
					newBtn.setOnMouseClicked(new EventHandler<MouseEvent>(){
						@Override
						public void handle(MouseEvent event) {
							if(dbController.getDeviceID() != -1){
								System.out.println("[set event]"+ pl.getClass().getName());
								List params = new ArrayList();
								params.add((DBController) dbController);
								pl.setParameter(params);
								addPluginView(pl);
								
							}else{
								Label noDevice = new Label("No device has been chosen.");
								MainResultPane.setContent(noDevice);
							}
							updateProgress("Current selected tool: " +functionName );
						}	
					});
				}
			}
			
			// set binding dynamically
			newFuctionCheck.setOnAction(event -> {
				if(newFuctionCheck.isSelected()){
					functionHBox.getChildren().add(newBtn);
				}else{
					functionHBox.getChildren().remove(newBtn);
				}
			});
		}
		// menu bar
		//open Project
		openProject.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				FileChooser projFileChooser = new FileChooser();
				projFileChooser.setTitle("Select Project File");
				projFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
				File projFile = projFileChooser.showOpenDialog(null);

				if(projFile!=null){
					System.out.println(projFile.getName());
					currentProject = projm.readProjectFile(projFile.getAbsolutePath());
					pm.setAllPluginMarkedItems(currentProject.getMarkedItems());	
					refreshCase(currentProject.getDeviceID());
				}
				else
					System.out.println("No project file chosen");
			}
		});

		//save Project
		saveProject.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Project p = projm.getProject();
				if(p.getProjectPath()==null){
					FileChooser projFileChooser = new FileChooser();
					projFileChooser.setTitle("Save Project File As");
					projFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
					File projFile = projFileChooser.showSaveDialog(null);
					
					if(projFile!=null){
						projm.writeProjectFile(projFile.getAbsolutePath(),pm.getAllPluginMarkedItems());
					}else{
						System.out.println("no save file path specified");
					}
				}else{
					projm.writeProjectFile(null,pm.getAllPluginMarkedItems());
				}
			}
		});
		//save as Project
		saveAsProject.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				FileChooser projFileChooser = new FileChooser();
				projFileChooser.setTitle("Save Project File As");
				projFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
				File projFile = projFileChooser.showSaveDialog(null);
				
				if(projFile!=null){
					projm.writeProjectFile(projFile.getAbsolutePath(),pm.getAllPluginMarkedItems());
				}else{
					System.out.println("no save file path specified");
				}
				
			}
		});
		
		// new device
		newDevice.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				// Show open file dialog
				file = fileChooser.showOpenDialog(null);

				labelFilePath.setText(file.getPath());
				System.out.println(labelFilePath);
			}
		});

		newDevice.setOnAction(this::handleNewDirectory);
		
		//generate report
		generateReport.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				
				
				ReportGenerator RG = new ReportGenerator();
				
				List<List<String>> reportData = pm.getAllPluginMarkedItems().get("FolderStructureTreePlugin");
				Project currentProject = projm.getProject();
				String projectPath = null;
				EagleDevice currentDevice=null;
				if(currentProject!=null){
					projectPath = currentProject.getProjectPath();
					int dvID = currentProject.getDeviceID();
					ArrayList<EagleDevice> devices = dbController.getAllDevices();
					for(EagleDevice dv: devices ){
						System.out.println("device id = "+ dv.getDeviceID());
						if(dv.getDeviceID()==dvID){
							currentDevice = dv;
						}
					}
				}
				System.out.println("reportdata ="+reportData);
				System.out.println("current device = " + currentDevice);
				System.out.println("project path ="+projectPath);
				try {
					if(RG.generateTableStyleReport(reportData,currentDevice,projectPath)){
						System.out.println("SUCCESSFUL: report generated");
					}else{
						System.out.println("UNSUCCESSFUL: report not generated");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		// on start
		refreshDeviceList();

	}

	/*
	 *Visualization methods 
	 * 
	 */
	
	private void addPluginView(Plugin pl){
		Object plview = pl.getResult();
		
		MainResultPane.setContent((Node)plview);
		MainResultPane.setFitToHeight(true);
		MainResultPane.setFitToWidth(true);
	}

	
	/*
	 * Methods of workbench
	 */
	//retrieve device list from db through RequestHandler
	private ArrayList<EagleDevice> getExisitingDevices(){
		ArrayList<EagleDevice> DeviceList;
		//RequestHandler rh= new UIRequestHandler();
		
		DeviceList = dbController.getAllDevices();
		return DeviceList;
		
	}
	
	private void loadExistingDevice(int deviceID){
		refreshCase(deviceID);
	}
	
	// Refresh Case that Loaded in View
	private void refreshCase(int deviceID){
		System.out.println("refreshCase "+deviceID);
		//dbController = new DBQueryController();
		if(functionHBox.getChildren().size() != 0){
			Label noDevice = new Label("No function has been chosen.");
			MainResultPane.setContent(noDevice);
		}
		dbController.setDeviceID(deviceID);		
		if(functionHBox.getChildren().size() != 0){
			Label noDevice = new Label("No function has been chosen.");
			MainResultPane.setContent(noDevice);
		}
		//refreshDevice();
		refreshDeviceList();
	}
	
	// Refresh Device in Menu List
	private void refreshDeviceList(){
		System.out.println("refreshDeviceList");
		//dbController = new DBQueryController();
		ArrayList<EagleDevice> devices = dbController.getAllDevices();
		existDeviceMenu.getItems().clear();
		for (EagleDevice device : devices){
			int ID = device.getDeviceID();
			MenuItem newItem = new MenuItem(device.getDeviceName() + " [" + device.getDeviceOwner() + "]");
			existDeviceMenu.getItems().add(newItem);
			newItem.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					currentCaseID = ID;
					refreshCase(currentCaseID);
					Project p = new Project(null, currentCaseID, null);
					projm.setProject(p);
					pm.setAllPluginMarkedItems(null);
	            }
			});
		}
	}
	
	// change progressLabel
	public void updateProgress(String progress){
		progressLabel.setText(progress);
	}

	// private void handleStartDateClick

	public void setMainApp(MainApp mainAppFinal) {
		this.mainAppFinal = mainAppFinal;

		// obtain current case path
		casePath = (mainAppFinal.getCasePath());
	}
	
	private void handleNewDirectory(ActionEvent event) {
		
		Device newDevice = mainAppFinal.showNewDeviceDialogDialog();
		
		if(newDevice == null)
		{
			return;
		}
		
		String diskImgFolder = newDevice.getDeviceImageFolder().toString();
		String deviceName = newDevice.getDeviceName();
		System.out.println("new device folder = "+diskImgFolder);
		List params = new ArrayList<String>();
		params.add(diskImgFolder);
		params.add(deviceName);
		

		List<Plugin> extractors = pm.getExtractorPlugins();
		for(Plugin p: extractors){	
			int status = p.setParameter(params);
			if(status==0){
				
				Task<Integer> task = new Task<Integer>() {
				    @Override 
				    protected Integer call() {
				    	//extract
				    	List<List<EagleFile>> entityList = (List<List<EagleFile>>) p.getResult();
				    	System.out.println("partition written = "+entityList.size());
				    	
				    	//insert to db
				    	if(entityList.size() > 0)
						{
							DBInsertTransaction transaction = new DBInsertTransaction();
							transaction.insertNewDeviceData(newDevice, entityList);
							System.out.println("inserted new device to db");
							return transaction.getDeviceID();
						}
				    	
						return -1;
				    }
				};
				task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			        @Override
			        public void handle(WorkerStateEvent t)
			        {
			            try {
			            	currentCaseID = task.get();
			            	Project p = new Project(null, currentCaseID, null);
							projm.setProject(p);
							pm.setAllPluginMarkedItems(null);
							refreshCase(currentCaseID);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    });
				Thread th = new Thread(task);
		        th.setDaemon(true);
		        th.start();
		        
				
				break;
			}
		}
		

		
		/*Stage dialog = this.createProgressDialog();
	
		Service<?> fsService = new FileSystemFormatDescriptorService(newDevice.getDeviceImageFolder(),newDevice.getDeviceName());
		
		ChangeListener<State> handleServiceChange = new ChangeListener<State>()
		{
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue)
			{
				if (newValue == State.FAILED)
				{
					updateProgress("Import of new device has failed.");
				}
				else if(newValue == State.CANCELLED)
				{
					updateProgress("Import of new device has been cancelled.");
				}
				
				if(newValue == State.FAILED || newValue == State.CANCELLED)
				{
					dialog.close();
				}
			}
		};

		fsService.stateProperty().addListener(handleServiceChange);

		dialog.setOnHidden(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent arg0)
			{
				updateProgress("Import of new device has been cancelled.");
				fsService.cancel();
			}
		});
		
		EventHandler<WorkerStateEvent> handleUnpackServiceSucceed = new EventHandler<WorkerStateEvent>()
		{
			@Override
			public void handle(WorkerStateEvent e)
			{
				importProgressIndicator.progressProperty().unbind();
				progressLabel.textProperty().unbind();
				
				updateProgress("Writing database entries for new device...");
				
				importProgressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
				
				@SuppressWarnings("unchecked")
				ArrayList<ArrayList<FileEntity>> entityList = (ArrayList<ArrayList<FileEntity>>)(e.getSource().getValue());
				
				Task<Void> task = new Task<Void>()
				{
					
					@Override
					protected Void call() throws Exception
					{
						if(entityList.size() > 0)
						{
							DBInsertTransaction transaction = new DBInsertTransaction();
							transaction.insertNewDeviceData(newDevice, entityList);
							refreshCase(transaction.getDeviceID());
						}
						
						return null;
					}
				};
				
				task.setOnSucceeded(new EventHandler<WorkerStateEvent>()
				{
					
					@Override
					public void handle(WorkerStateEvent arg0)
					{
						dialog.close();
						WorkBenchControllerFinal.this.updateProgress("Import of new device complete.");
					}
				});
				
				task.setOnFailed(new EventHandler<WorkerStateEvent>()
				{
					@Override
					public void handle(WorkerStateEvent arg0)
					{
						dialog.close();
						WorkBenchControllerFinal.this.updateProgress("Import of new device failed.");
					}
				});
				
				task.setOnCancelled(new EventHandler<WorkerStateEvent>()
				{
					
					@Override
					public void handle(WorkerStateEvent arg0)
					{
						dialog.close();
						WorkBenchControllerFinal.this.updateProgress("Import of new device cancelled.");
					}
				});
				
				task.run();
			}
		};

		EventHandler<WorkerStateEvent> handleFSServiceSucceed = new EventHandler<WorkerStateEvent>()
		{
			@Override
			public void handle(WorkerStateEvent e)
			{
				@SuppressWarnings("unchecked")
				ArrayList<FormatDescription> formatDescriptions = (ArrayList<FormatDescription>)(e.getSource().getValue());
				
				if(formatDescriptions.size() == 0)
				{
					dialog.close();
					JOptionPane.showMessageDialog(null, "No device images were found in the folder provided.", "New device not imported.", JOptionPane.INFORMATION_MESSAGE);
					updateProgress("Device images were not found in folder provided.");
				}

				long contentSize = 0;
				
				for(FormatDescription formatDescription : formatDescriptions)
				{
					contentSize += formatDescription.getFile().length();
				}
				
				String contentSizeString = "";
				
				int unit = 1024;
				
				if(contentSize < unit)
				{
					contentSizeString = contentSize + " B";
				}
				else
				{
					int exp = (int) (Math.log(contentSize) / Math.log(unit));
					

				    char pre = ("KMGTPE").charAt(exp-1);
				    contentSizeString = String.format("%.1f %sB", contentSize / Math.pow(unit, exp), pre);
				}
				
				newDevice.modifyContentSize(contentSizeString);
				
				updateProgress("Total disk image size: " + contentSizeString);
				
				Service<?> unpackService = new UnpackDirectoryService(formatDescriptions);
				unpackService.setOnSucceeded(handleUnpackServiceSucceed);
				unpackService.stateProperty().addListener(handleServiceChange);

				updateProgress("Unpacking of raw disk images on the device has started...");
				
				NumberFormat format = NumberFormat.getPercentInstance();
				
				unpackService.progressProperty().addListener
				(
					new ChangeListener<Number>()
					{
						@Override
						public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
						{
							double value = newValue.doubleValue();
							importProgressIndicator.setProgress(value);
							updateProgress(unpackService.getMessage().toString() + ".. " + format.format(value));
						}
					}
				);

				dialog.setOnHidden(new EventHandler<WindowEvent>()
				{
					public void handle(WindowEvent arg0)
					{
						unpackService.cancel();
						importProgressIndicator.progressProperty().unbind();
						progressLabel.textProperty().unbind();
						updateProgress("Import of new device has been cancelled.");
					}
				});
				
				unpackService.start();

			}
		};

		fsService.setOnSucceeded(handleFSServiceSucceed);
		updateProgress("Analysing file system on disk images...");
		fsService.start();
		dialog.show();*/
	}
/*	
	private Stage createProgressDialog()
	{
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setTitle("Import in Progress...");
		dialog.setWidth(150);
		dialog.setHeight(70);
		dialog.setResizable(false);

		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(root);
		dialog.setScene(scene);

		importProgressIndicator = new ProgressBar();
		root.getChildren().add(importProgressIndicator);

		Button cancel = new Button("Cancel");
		root.getChildren().add(cancel);
		
		cancel.setOnAction
		(
			new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					dialog.close();
				}
			}
		);

		return dialog;
	}
	*/
	public boolean validationOfFilters () {
		
		return true;
	}
}

