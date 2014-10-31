package eagleeye.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import eagleeye.controller.MainApp;
import eagleeye.datacarving.unpack.service.UnpackDirectoryService;
import eagleeye.dbcontroller.DBQueryController;
import eagleeye.entities.Device;
import eagleeye.entities.Directory;
import eagleeye.entities.FileEntity;
import eagleeye.entities.Filter;
import eagleeye.model.RequestHandler;
import eagleeye.model.UIRequestHandler;

public class WorkBenchController {

	// DataBase
	private DBQueryController dbController;
	
	// File chooser
	private File file;
	private Label labelFilePath = new Label();
	private File directory;
	private Label labelDirPath = new Label();

	// Path to identify current case
	private String casePath = "";
	private int currentCaseID = -1;
	private String currentDir = "";
	ArrayList<eagleeye.entities.FileEntity> folderStructure;
	ArrayList<Directory> TreeStructure;

	// TreeView & CategoryVuew
	private final Node rootIcon = new ImageView(new Image(getClass()
			.getResourceAsStream("Icons/treeViewRootFolderIcon.png")));
	private final Image fileIcon = new Image(getClass().getResourceAsStream(
			"Icons/fileIcon.jpg"));
	TreeItem<String> rootNode;
	TreeItem<String> rootNodeC;
	ArrayList<String> categoryList = new ArrayList(Arrays.asList("Image","Video","Audio","Document","Database", "Compressed Folder", "Others"));

	@FXML
	private StackPane treeViewPane;
	@FXML
	private VBox categoryViewPane;

	// result pane view
	private Filter filter = new Filter();
	final ObservableList<String> listItems = FXCollections.observableArrayList("For testing purpose"); 
	@FXML
	private ListView resultListView;

	// SearchButton
	private final Image searchIcon = new Image(getClass().getResourceAsStream(
			"Icons/search icon1.png"), 16, 16, false, false);
	@FXML
	private Button searchButton;

	// DatePicker
	private LocalDate startDate = LocalDate.parse("1992-05-08");
	private LocalDate endDate = LocalDate.now();
	private final Image calendarIcon = new Image(getClass()
			.getResourceAsStream("Icons/fileIcon.jpg"));
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;

	// Time
	private String startHour = "00";
	private String startMinute = "00";
	private String endHour = "23";
	private String endMinute = "59";
	@FXML
	private TextField startHourTf;
	@FXML
	private TextField startMinuteTf;
	@FXML
	private TextField endHourTf;
	@FXML
	private TextField endMinuteTf;

	// Menu
	@FXML
	private MenuItem newClick;
	@FXML
	private Menu openMenu;
	@FXML
	private MenuItem saveClick;
	@FXML
	private MenuItem exitClick;

	// Function Buttons
	@FXML
	private Button contactHistoryBtn;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor.
	 */
	public WorkBenchController() {
	}

	/**
	 * The initializer.
	 */

	@FXML
	private void initialize() {

		// Initialize for DatePicker
		startDatePicker.setValue(startDate);
		endDatePicker.setValue(endDate);

		startDatePicker.setOnAction(event -> {
			startDate = startDatePicker.getValue();
			System.out.println("Selected date: " + startDate);
		});

		/*
		startDatePicker.setConverter(new StringConverter<LocalDate>() {
			private DateTimeFormatter dateTimeFormatter = DateTimeFormatter
					.ofPattern("yyyy/MM/dd");

			@Override
			public String toString(LocalDate localDate) {
				if (localDate == null)
					return "";
				startDate = localDate;
				return dateTimeFormatter.format(localDate);
			}

			@Override
			public LocalDate fromString(String dateString) {
				if (dateString == null || dateString.trim().isEmpty()) {
					return null;
				}
				startDate = LocalDate.parse(dateString, dateTimeFormatter);
				return LocalDate.parse(dateString, dateTimeFormatter);
			}
		});
		*/

		endDatePicker.setOnAction(event -> {
			endDate = endDatePicker.getValue();
			System.out.println("Selected date: " + endDate);
		});


		// Time, start: 00:00-23:59. end: 00:00-23:59. start <= end
		startHourTf.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!(newValue.matches("[0-9]*"))) {
					startHourTf.setText(newValue.substring(0,
							newValue.length() - 1));
					System.out.println("no match");
				}

				if (startHourTf.getText().length() == 0) {
					System.out.println("no input");
					startHourTf.setText("00");
				}

			}
		});
		startHourTf.focusedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							// Check range
							if (Integer.parseInt(startHourTf.getText()) > 23) {
								System.out.println("too large");
								startHourTf.setText("23");
							} else if (Integer.parseInt(startHourTf.getText()) < 0) {
								System.out.println("too small");
								startHourTf.setText("0");
							}
							// Check length
							if (startHourTf.getText().length() > 2) {
								System.out.println("too long");
								startHourTf.setText("23");
							} else if (startHourTf.getText().length() == 1) {
								System.out.println("1 degit");
								startHourTf.setText("0" + startHourTf.getText());
							} else if (startHourTf.getText().length() == 0) {
								System.out.println("no input");
								startHourTf.setText("00");
							}
							startHour = startHourTf.getText();
							System.out.println("startHour is " + startHour);
						}
					}
				});

		startMinuteTf.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!(newValue.matches("[0-9]*"))) {
					startMinuteTf.setText(newValue.substring(0,
							newValue.length() - 1));
					System.out.println("no match");
				}

				if (startMinuteTf.getText().length() == 0) {
					System.out.println("no input");
					startMinuteTf.setText("00");
				}

			}
		});
		startMinuteTf.focusedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							// Check range
							if (Integer.parseInt(startMinuteTf.getText()) > 59) {
								System.out.println("too large");
								startMinuteTf.setText("59");
							} else if (Integer.parseInt(startMinuteTf.getText()) < 0) {
								System.out.println("too small");
								startMinuteTf.setText("0");
							}
							// Check length
							if (startMinuteTf.getText().length() > 2) {
								System.out.println("too long");
								startMinuteTf.setText("59");
							} else if (startMinuteTf.getText().length() == 1) {
								System.out.println("1 degit");
								startMinuteTf.setText("0"
										+ startMinuteTf.getText());
							} else if (startMinuteTf.getText().length() == 0) {
								System.out.println("no input");
								startMinuteTf.setText("00");
							}
							startMinute = startMinuteTf.getText();
							System.out.println("startMinute is " + startMinute);
						}
					}
				});
		endHourTf.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!(newValue.matches("[0-9]*"))) {
					endHourTf.setText(newValue.substring(0,
							newValue.length() - 1));
					System.out.println("no match");
				}

				if (startHourTf.getText().length() == 0) {
					System.out.println("no input");
					endHourTf.setText("00");
				}

			}
		});
		endHourTf.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					System.out.println("Textfield on focus");
				} else {
					// Check range
					if (Integer.parseInt(endHourTf.getText()) > 23) {
						System.out.println("too large");
						endHourTf.setText("23");
					} else if (Integer.parseInt(endHourTf.getText()) < 0) {
						System.out.println("too small");
						endHourTf.setText("0");
					}
					// Check length
					if (endHourTf.getText().length() > 2) {
						System.out.println("too long");
						endHourTf.setText("23");
					} else if (endHourTf.getText().length() == 1) {
						System.out.println("1 degit");
						endHourTf.setText("0" + endHourTf.getText());
					} else if (endHourTf.getText().length() == 0) {
						System.out.println("no input");
						endHourTf.setText("00");
					}
					endHour = endHourTf.getText();
					System.out.println("endHour is " + endHour);
				}
			}
		});
		endMinuteTf.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!(newValue.matches("[0-9]*"))) {
					endMinuteTf.setText(newValue.substring(0,
							newValue.length() - 1));
					System.out.println("no match");
				}

				if (endMinuteTf.getText().length() == 0) {
					System.out.println("no input");
					endMinuteTf.setText("00");
				}

			}
		});
		endMinuteTf.focusedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							// Check range
							if (Integer.parseInt(endMinuteTf.getText()) > 59) {
								System.out.println("too large");
								endMinuteTf.setText("59");
							} else if (Integer.parseInt(endMinuteTf.getText()) < 0) {
								System.out.println("too small");
								endMinuteTf.setText("0");
							}
							// Check length
							if (endMinuteTf.getText().length() > 2) {
								System.out.println("too long");
								endMinuteTf.setText("59");
							} else if (endMinuteTf.getText().length() == 1) {
								System.out.println("1 degit");
								endMinuteTf.setText("0" + endMinuteTf.getText());
							} else if (endMinuteTf.getText().length() == 0) {
								System.out.println("no input");
								endMinuteTf.setText("00");
							}
							endMinute = endMinuteTf.getText();
							System.out.println("endMinute is " + endMinute);
						}
					}
				});

		// file chooser
		newClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();

				// Set extension filter
				// FileChooser.ExtensionFilter extFilter = new
				// FileChooser.ExtensionFilter("AVI files (*.avi)", "*.avi");
				// fileChooser.getExtensionFilters().add(extFilter);

				// Show open file dialog
				file = fileChooser.showOpenDialog(null);

				labelFilePath.setText(file.getPath());
				System.out.println(labelFilePath);

			}
		});

		// new device
		newClick.setOnAction(this::handleNewDirectory);
		
		// open
		refreshDeviceList();

		// save
		saveClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

			}
		});

		// exit
		exitClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});

		// Search
		searchButton.setGraphic(new ImageView(searchIcon));

			
			/*
			rootNodeC = new TreeItem<String>(TreeStructure.get(0)
					.getDirectoryName(), rootIcon);
			TreeView<String> treeC = new TreeView<String>(rootNodeC);
			
			for (FileEntity file : allFiles) {
				TreeItem<String> empLeaf = new TreeItem<String>(
						file.getFileName(), new ImageView(fileIcon));
				boolean foundPath = false;
				boolean foundDep = false;
				for (TreeItem<String> pathNode : rootNodeC.getChildren()) {
					for (TreeItem<String> depNode : pathNode.getChildren()) {
						// Check if path match
						if (pathNode.getValue().contentEquals(file.getCategory())) {
							// Check if format match
							if (depNode.getValue().contentEquals(
									file.getFileExt())) {
								depNode.getChildren().add(empLeaf);
								foundDep = true;
								break;
							}
							if (!foundDep) {
								TreeItem<String> newDepNode = new TreeItem<String>(
										file.getFileExt());
								pathNode.getChildren().add(newDepNode);
								newDepNode.getChildren().add(empLeaf);
							}
							foundPath = true;
							break;
						}
						if (!foundPath) {
							TreeItem<String> newPathNode = new TreeItem<String>(
									file.getCategory());
							TreeItem<String> newDepNode = new TreeItem<String>(
									file.getFileExt());
							rootNodeC.getChildren().add(newPathNode);
							newPathNode.setExpanded(true);
							newPathNode.getChildren().add(newDepNode);
							newDepNode.getChildren().add(empLeaf);
						}
					}
				}
				if (!foundPath && !foundDep) {
					System.out.println("first time");
					TreeItem<String> newPathNode = new TreeItem<String>(
							file.getCategory());
					TreeItem<String> newDepNode = new TreeItem<String>(
							file.getFileExt());
					rootNodeC.getChildren().add(newPathNode);
					newPathNode.setExpanded(true);
					newPathNode.getChildren().add(newDepNode);
					newDepNode.getChildren().add(empLeaf);
				}
				
				tree.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent mouseEvent) {
						if (mouseEvent.getClickCount() == 2) {
							TreeItem<String> item = tree.getSelectionModel()
									.getSelectedItem();
							System.out.println("Selected Text : " + item.getValue());

							// Check if it is a file, and open
							if (item.isLeaf()) {
								String filePath = item.getValue();
								item = item.getParent();
								while (item instanceof TreeItem) {
									filePath = item.getValue() + "/" + filePath;
									item = item.getParent();
								}
								System.out.println("Selected File : " + filePath);
								String location = Paths.get(".").toAbsolutePath()
										.normalize().toString();
								File currentFile = new File(location + filePath);
								try {
									Desktop.getDesktop().open(currentFile);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				});
				categoryViewPane.getChildren().add(treeC);
			}
			*/
	}

	/*
	 * Methods of workbench
	 */
	
	//retrieve device list from db through RequestHandler
	public ArrayList<Device> getExisitingDevices(){
		ArrayList<Device> DeviceList;
		RequestHandler rh= new UIRequestHandler();
		DeviceList = rh.getExistingDevices();
		return DeviceList;
		
	}
	
	public void loadExistingDevice(int deviceID){
		refreshCase(deviceID);
	}
	
	// Refresh Case that Loaded in View
	public void refreshCase(int deviceID){
		dbController = new DBQueryController();
		dbController.setDeviceID(deviceID);
		System.out.println("new device: " +deviceID);
		ArrayList<Directory> TreeStructure = dbController
				.getAllDirectoriesAndFiles();
		ArrayList<FileEntity> allFiles = dbController.getAllFiles();

		// Check if DB empty
		if (TreeStructure.size() != 0) {
			// TreeView
			rootNode = new TreeItem<String>(TreeStructure.get(0)
					.getDirectoryName(), rootIcon);
			ArrayList<Directory> CopyTreeStructure = new ArrayList<Directory>(
					TreeStructure);
			TreeView<String> tree = new TreeView<String>(rootNode);

			// Force root node ID to be 0
			//TreeStructure.get(0).modifyDirectoryID(0);

			int rootDirID = TreeStructure.get(0).getDirectoryID();
			// Whenever a directory found its parent, we remove it from copied
			// list
			
			while (CopyTreeStructure.size() > 0) {
				int startSize = CopyTreeStructure.size();
				for (Directory dir : CopyTreeStructure) {
					TreeItem<String> targetParent = null;
					System.out.println("Current remaining Size: "
							+ CopyTreeStructure.size());
					// check if it is root
					if (dir.getDirectoryID() == rootDirID) {
						casePath = dir.getDirectoryName();
						this.addFiles(dir, rootNode);
						CopyTreeStructure.remove(dir);
						// System.out.println("root met, removed");
						break;
					}

					TreeItem<String> newItem = new TreeItem<String>(
							dir.getDirectoryName());

					Directory parent = findDir(TreeStructure,dir.getParentDirectory());

					if (parent != null) {
						targetParent = findItem(rootNode,parent.getDirectoryName());
					} else {
						System.out.println("Cannot find parent" + dir.getParentDirectory());
					}

					if (targetParent != null) {
						// System.out.println("parent found");
						targetParent.getChildren().add(newItem);
						this.addFiles(dir, newItem);
						CopyTreeStructure.remove(dir);
						break;
					} else {
						System.out.println("cannot find parent");
					}
				}
				int endSize = CopyTreeStructure.size();
				// check if no change in size, then we print out remaining list
				// and exit
				if (startSize == endSize) {
					System.out.println("Remaining Directories:");
					for (Directory dir : CopyTreeStructure) {
						System.out.println(dir.getDirectoryName()
								+ " needed parent not found: "
								+ dir.getParentDirectory());
					}
					break;
				}
			}

			tree.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {

					TreeItem<String> item = tree.getSelectionModel().getSelectedItem();
					System.out.println("Selected Text : " + item.getValue());
					
					// Change Result pane view if a folder
					if (!item.isLeaf()){
						listItems.clear();
						for(TreeItem<String> subFile : item.getChildren()){
							if (subFile.isLeaf()){
								listItems.add("File:" + subFile.getValue());
							}else{
								listItems.add("Folder: " + subFile.getValue());
							}
						}

						displayResult(listItems, "tree");
					}
					
					if (mouseEvent.getClickCount() == 2) {
						// Check if it is a file, and open
						/*
						if (item.isLeaf()) {
							String filePath = item.getValue();
							item = item.getParent();
							while (item instanceof TreeItem) {
								filePath = item.getValue() + "/" + filePath;
								item = item.getParent();
							}
							System.out.println("Selected File : " + filePath);
							String location = Paths.get(".").toAbsolutePath()
									.normalize().toString();
							File currentFile = new File(location + filePath);
							try {
								Desktop.getDesktop().open(currentFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						*/
					}
				}
			});
			treeViewPane.getChildren().add(tree);

			// Category View
			categoryViewPane.setSpacing(5);
			categoryViewPane.setPadding(new Insets(5,5,5,5));
			for (String category : categoryList){
				Button btn = new Button(category);
				btn.setPrefHeight(40);
				btn.setPrefWidth(130);
				btn.setOnAction(new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {
		                listItems.clear();
		                filter.modifyCategoryName(category);
		                System.out.println(filter.getCategoryName());
		                System.out.println(filter.getCategoryID());
		                System.out.println(filter.getStartDateAsString());
		                System.out.println(filter.getEndDateAsString());
		                System.out.println(filter.getStartTime());
		                System.out.println(filter.getEndTime());
		                System.out.println("Getting filter category: "+ category);
		                displayResult(listItems, "category");
		            }
		        });
				categoryViewPane.getChildren().add(btn);
			}
			
		}
		//refreshDevice();
	}
	
	// Refresh Device in Menu List
	public void refreshDeviceList(){
		dbController = new DBQueryController();
		ArrayList<Device> devices = dbController.getAllDevices();
		openMenu.getItems().clear();
		for (Device device : devices){
			int ID = device.getDeviceID();
			MenuItem newItem = new MenuItem(device.getDeviceName());
			openMenu.getItems().add(newItem);
			newItem.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent e) {
					currentCaseID = ID;
	                refreshCase(ID);
	            }
			});
		}
	}
	
	// Method to Decide What to Show in Result Pane View
	public void displayResult(ObservableList<String> list, String type){
		if(type == "category"){
			ObservableList<String> resultList = filterResult();
			resultListView.setItems(resultList);
		}else if(type == "tree"){
			resultListView.setItems(list);
		}
	}
	
	// Filters 
	public ObservableList<String> filterResult(){

		ObservableList<String> resultList = FXCollections.observableArrayList(); 
		
        ArrayList<FileEntity> results = dbController.getFilteredFiles(filter);
        System.out.println("now: " + dbController.getDeviceID());
        System.out.println("filtered result size: " + results.size());
        
        for (FileEntity resultFile : results){
        	String name = resultFile.getFileName();
        	String ext = resultFile.getFileExt();
        	resultList.add(name + ext);
        }
        
		return resultList;
	}

	// Find whether a target is inside the tree of root, by recursion
	public TreeItem<String> findItem(TreeItem<String> root, String target) {
		// System.out.println("I want: " + target + " current: "
		// +root.getValue());
		TreeItem<String> result = null;

		if (root.getValue() == target) {
			// System.out.println("found: " + target + " current: "+
			// root.getValue());
			return root;
		} else if (root.getChildren().size() != 0) {
			for (TreeItem<String> sub : root.getChildren()) {
				TreeItem<String> subResult = findItem(sub, target);
				if (subResult != null) {
					return subResult;
				}
			}
		} else {
			// System.out.println("cannot find: " + target +
			// " current: "+root.getValue());
		}

		return result;
	}

	// Find Directory according to ID
	public Directory findDir(ArrayList<Directory> db, int ID) {
		for (Directory checkParent : db) {
			if (checkParent.getDirectoryID() == ID) {
				return checkParent;
			}
		}
		return null;
	}

	// Add files into directory
	public void addFiles(Directory dir, TreeItem<String> node) {
		for (eagleeye.entities.FileEntity file : dir.getFiles()) {
			TreeItem<String> newItem = new TreeItem<String>(file.getFileName()
					+ "." + file.getFileExt(), new ImageView(fileIcon));
			node.getChildren().add(newItem);
		}

	}

	// private void handleStartDateClick

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// obtain current case path
		casePath = (mainApp.getCasePath());
	}
	
	private void handleNewDirectory(ActionEvent event) {
		DirectoryChooser dirChooser = new DirectoryChooser();

		// Show open file dialog
		file = dirChooser.showDialog(null);

		labelDirPath.setText(file.getPath());
		System.out.println(labelDirPath);

		UnpackDirectoryService service = new UnpackDirectoryService();
		service.setDirectory(file);

		Stage dialog = this.createProgressDialog(service);
		
		service.setOnSucceeded(new EventHandler<WorkerStateEvent>()
		{
			
			@Override
			public void handle(WorkerStateEvent e)
			{
				int deviceId = (int) e.getSource().getValue();
				
				refreshCase(deviceId);
			}
		});

		service.start();
		dialog.show();
	}

	private Stage createProgressDialog(final Service<Integer> service) {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setTitle("Import in Progress...");
		dialog.setWidth(150);
		dialog.setHeight(70);
		dialog.setResizable(false);

		VBox root = new VBox();
		root.setMaxWidth(Double.MAX_VALUE);

		Scene scene = new Scene(root);
		dialog.setScene(scene);

		final ProgressBar indicator = new ProgressBar();
		indicator.setMaxWidth(Double.MAX_VALUE);

		indicator.progressProperty().bind(service.progressProperty());
		indicator.setPrefHeight(35);
		root.getChildren().add(indicator);

		service.stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> observable,
					State oldValue, State newValue) {
				if (newValue == State.CANCELLED || newValue == State.FAILED
						|| newValue == State.SUCCEEDED) {
					dialog.hide();
				}
			}
		});

		Button cancel = new Button("Cancel");
		cancel.setPrefHeight(35);
		cancel.setMaxWidth(Double.MAX_VALUE);
		root.getChildren().add(cancel);

		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				service.cancel();
			}
		});

		return dialog;
	}
}


