package eagleeye.view;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import eagleeye.controller.MainAppFinal;
import eagleeye.datacarving.unpack.service.FileSystemFormatDescriptorService;
import eagleeye.datacarving.unpack.service.UnpackDirectoryService;
import eagleeye.dbcontroller.DBInsertTransaction;
import eagleeye.dbcontroller.DBQueryController;
import eagleeye.entities.Device;
import eagleeye.entities.Directory;
import eagleeye.entities.FileEntity;
import eagleeye.entities.Filter;
import eagleeye.filesystem.format.FormatDescription;
import eagleeye.model.RequestHandler;
import eagleeye.model.UIRequestHandler;
import eagleeye.pluginmanager.*;

public class WorkBenchControllerFinal {

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
	private VBox categoryViewPane;
	
	// result pane view
	private Filter filter = new Filter();
	final ObservableList<String> listItems = FXCollections.observableArrayList(); 
	
	@FXML
	private AnchorPane displayPane;
	
	@FXML
	private ScrollPane MainResultPane;
	
	@FXML
	private StackPane timeLineViewPane;
	
	//@FXML dynamically created
	private ListView resultListView;

	// SearchButton
	private final Image searchIcon = new Image(getClass().getResourceAsStream(
			"Icons/search icon1.png"), 16, 16, false, false);
	@FXML
	private Button searchButton;
	@FXML
	private Button resetSearchButton;

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

	private String startHourDaily = "00";
	private String startMinuteDaily = "00";
	private String endHourDaily = "23";
	private String endMinuteDaily = "59";
	@FXML
	private TextField startHourTf;
	@FXML
	private TextField startMinuteTf;
	@FXML
	private TextField endHourTf;
	@FXML
	private TextField endMinuteTf;
	@FXML
	private TextField startHourDailyTf;
	@FXML
	private TextField startMinuteDailyTf;
	@FXML
	private TextField endHourDailyTf;
	@FXML
	private TextField endMinuteDailyTf;
	@FXML
	private TextField keywordsTf;
	@FXML
	private CheckBox isDeletedCheckBox;
	@FXML
	private CheckBox isModifiedCheckBox;
	@FXML
	private CheckBox isOriginalCheckBox;
	

	// Menubar
	ArrayList<String> functionList;
	@FXML
	private MenuItem newClick;
	@FXML
	private Menu openMenu;
	@FXML
	private MenuItem exitClick;
	@FXML 
	private Menu analysisMenu;
	
	// Fucntion Hbox
	@FXML
	private HBox functionHBox;
	
	// Progress information label
	@FXML private Label progressLabel;

	// Reference to the main application.
	private MainAppFinal mainAppFinal;

	// Filter's items
	private String selectedCategory;
	private final String EMPTY_STRING = "";
	private final int CATEGORY_TAB_INDEX = 1;
	@FXML
	private TabPane leftTabPane;
	
	//plugin manager
	private PluginManager pm;
	
	
	/**
	 * The constructor.
	 */
	public WorkBenchControllerFinal() {
		functionList = new ArrayList(Arrays.asList("Tree View"));
		pm = new PluginManager("PluginBinaries");
		selectedCategory = EMPTY_STRING;
		resultListView = new ListView();
	}

	/**
	 * The initializer.
	 */

	@FXML
	private void initialize() {
		//add pluginsnames to functionList
		List<String> plnames=pm.getGUIPluginNames();
		System.out.println(plnames.size());
		for(String s : plnames){
			System.out.println("PLUG: "+ s);
			functionList.add(s);
		}
		
		
		// Initialize DB
		dbController = new DBQueryController();
		
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
			newBtn.setPrefWidth(80);
			newBtn.setPrefHeight(30);
			
			functionHBox.getChildren().add(newBtn);
			
			//System.out.println(functionName);
			if(functionName.equals("Tree View")){
				newBtn.setOnMouseClicked(new EventHandler<MouseEvent>(){
					@Override
					public void handle(MouseEvent event) {addDirectoryView();}
				});
			}

			else{
				Plugin pl = pm.getPluginWithName(functionName);
				if(pl!=null){
					newBtn.setOnMouseClicked(new EventHandler<MouseEvent>(){
						@Override
						public void handle(MouseEvent event) {addPluginView(pl);}	
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

		// Initialize for DatePicker
		startDatePicker.setValue(startDate);
		endDatePicker.setValue(endDate);

		startDatePicker.setOnAction(event -> {
			startDate = startDatePicker.getValue();
			System.out.println("Selected date: " + startDate);
		});

		endDatePicker.setOnAction(event -> {
			endDate = endDatePicker.getValue();
			System.out.println("Selected date: " + endDate);
		});


		// Time, start: 00:00-23:59. end: 00:00-23:59. start <= end check not implemented
		
		startHourTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!(newValue.matches("[0-9]*"))) {
					startHourTf.setText(newValue.substring(0,
							newValue.length() - 1));
					System.out.println("no match");
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
			}
		});
		
		endMinuteTf.focusedProperty().addListener(new ChangeListener<Boolean>() {
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

		// Daily Time
		startHourDailyTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!(newValue.matches("[0-9]*"))) {
					startHourDailyTf.setText(newValue.substring(0,
							newValue.length() - 1));
					System.out.println("no match");
				}
			}
		});
		
		startHourDailyTf.focusedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							// Check range
							if (Integer.parseInt(startHourDailyTf.getText()) > 23) {
								System.out.println("too large");
								startHourDailyTf.setText("23");
							} else if (Integer.parseInt(startHourDailyTf.getText()) < 0) {
								System.out.println("too small");
								startHourDailyTf.setText("0");
							}
							// Check length
							if (startHourDailyTf.getText().length() > 2) {
								System.out.println("too long");
								startHourDailyTf.setText("23");
							} else if (startHourDailyTf.getText().length() == 1) {
								System.out.println("1 degit");
								startHourDailyTf.setText("0" + startHourDailyTf.getText());
							} else if (startHourDailyTf.getText().length() == 0) {
								System.out.println("no input");
								startHourDailyTf.setText("00");
							}
							startHourDaily = startHourDailyTf.getText();
							System.out.println("startHourDaily is " + startHourDaily);
						}
					}
				});

		startMinuteDailyTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!(newValue.matches("[0-9]*"))) {
					startMinuteDailyTf.setText(newValue.substring(0,
							newValue.length() - 1));
					System.out.println("no match");
				}
			}
		});
		
		startMinuteDailyTf.focusedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							// Check range
							if (Integer.parseInt(startMinuteDailyTf.getText()) > 59) {
								System.out.println("too large");
								startMinuteDailyTf.setText("59");
							} else if (Integer.parseInt(startMinuteDailyTf.getText()) < 0) {
								System.out.println("too small");
								startMinuteDailyTf.setText("0");
							}
							// Check length
							if (startMinuteDailyTf.getText().length() > 2) {
								System.out.println("too long");
								startMinuteDailyTf.setText("59");
							} else if (startMinuteDailyTf.getText().length() == 1) {
								System.out.println("1 degit");
								startMinuteDailyTf.setText("0"
										+ startMinuteDailyTf.getText());
							} else if (startMinuteDailyTf.getText().length() == 0) {
								System.out.println("no input");
								startMinuteDailyTf.setText("00");
							}
							startMinuteDaily = startMinuteDailyTf.getText();
							System.out.println("startMinuteDaily is " + startMinuteDaily);
						}
					}
				});
		
		endHourDailyTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!(newValue.matches("[0-9]*"))) {
					endHourDailyTf.setText(newValue.substring(0,
							newValue.length() - 1));
					System.out.println("no match");
				}
			}
		});
		
		endHourDailyTf.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					System.out.println("Textfield on focus");
				} else {
					// Check range
					if (Integer.parseInt(endHourDailyTf.getText()) > 23) {
						System.out.println("too large");
						endHourDailyTf.setText("23");
					} else if (Integer.parseInt(endHourDailyTf.getText()) < 0) {
						System.out.println("too small");
						endHourDailyTf.setText("0");
					}
					// Check length
					if (endHourDailyTf.getText().length() > 2) {
						System.out.println("too long");
						endHourDailyTf.setText("23");
					} else if (endHourDailyTf.getText().length() == 1) {
						System.out.println("1 degit");
						endHourDailyTf.setText("0" + endHourDailyTf.getText());
					} else if (endHourDailyTf.getText().length() == 0) {
						System.out.println("no input");
						endHourDailyTf.setText("00");
					}
					endHourDaily = endHourDailyTf.getText();
					System.out.println("endHourDaily is " + endHourDaily);
				}
			}
		});
		
		endMinuteDailyTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (!(newValue.matches("[0-9]*"))) {
					endMinuteDailyTf.setText(newValue.substring(0,
							newValue.length() - 1));
					System.out.println("no match");
				}
			}
		});
		
		endMinuteDailyTf.focusedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							// Check range
							if (Integer.parseInt(endMinuteDailyTf.getText()) > 59) {
								System.out.println("too large");
								endMinuteDailyTf.setText("59");
							} else if (Integer.parseInt(endMinuteDailyTf.getText()) < 0) {
								System.out.println("too small");
								endMinuteDailyTf.setText("0");
							}
							// Check length
							if (endMinuteDailyTf.getText().length() > 2) {
								System.out.println("too long");
								endMinuteDailyTf.setText("59");
							} else if (endMinuteDailyTf.getText().length() == 1) {
								System.out.println("1 degit");
								endMinuteDailyTf.setText("0" + endMinuteDailyTf.getText());
							} else if (endMinuteDailyTf.getText().length() == 0) {
								System.out.println("no input");
								endMinuteDailyTf.setText("00");
							}
							endMinuteDaily = endMinuteDailyTf.getText();
							System.out.println("endMinuteDaily is " + endMinuteDaily);
						}
					}
				});
		
		
		
		// menubar new/open device 
		// new device
		newClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				// Show open file dialog
				file = fileChooser.showOpenDialog(null);

				labelFilePath.setText(file.getPath());
				System.out.println(labelFilePath);
			}
		});

		newClick.setOnAction(this::handleNewDirectory);
		
		// open
		refreshDeviceList();

		// exit
		exitClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});

		// Search
		//searchButton.setGraphic(new ImageView(searchIcon));
	}

	/*
	 *Visualization methods 
	 * 
	 */
	
	public void addPluginView(Plugin pl){
		Object plview = pl.getResult();
		//System.out.println(plview.getClass().getName());
		
		MainResultPane.setContent((Node)plview);
	}
	
	public void addDirectoryView(){
		if(dbController.getDeviceID() == -1){
			Label noDevice = new Label("No device has been chosen.");
			MainResultPane.setContent(noDevice);
		}
		
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
					//System.out.println("Current remaining Size: "
					//		+ CopyTreeStructure.size());
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
						//System.out.println("Cannot find parent" + dir.getParentDirectory());
					}

					if (targetParent != null) {
						// System.out.println("parent found");
						targetParent.getChildren().add(newItem);
						this.addFiles(dir, newItem);
						CopyTreeStructure.remove(dir);
						break;
					} else {
						//System.out.println("cannot find parent");
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

			/*
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
			*/
			//treeViewPane.getChildren().add(tree);
		    MainResultPane.setContent(tree);
		}
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
		//dbController = new DBQueryController();
		if(functionHBox.getChildren().size() != 0){
			Label noDevice = new Label("No function has been chosen.");
			MainResultPane.setContent(noDevice);
		}
		dbController.setDeviceID(deviceID);
		System.out.println("new device: " +deviceID);
		ArrayList<Directory> TreeStructure = dbController
				.getAllDirectoriesAndFiles();
		ArrayList<FileEntity> allFiles = dbController.getAllFiles();

		// Check if DB empty
		if (TreeStructure.size() != 0) {
			// Category View
			categoryViewPane.setSpacing(5);
			categoryViewPane.setPadding(new Insets(5,5,5,5));
			categoryViewPane.getChildren().clear();
			for (String category : categoryList){
				Button btn = new Button(category);
				btn.setPrefHeight(40);
				btn.setPrefWidth(130);
				btn.setOnAction(new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {
		                listItems.clear();
		                filter.modifyCategoryName(category);
		                displayResult(listItems, "category");
		                
		                selectedCategory = category;
		                
		            }
		        });
				categoryViewPane.getChildren().add(btn);
			}
			
		}
		
		if(functionHBox.getChildren().size() != 0){
			Label noDevice = new Label("No function has been chosen.");
			MainResultPane.setContent(noDevice);
		}
		//refreshDevice();
		//refreshDeviceList();
	}
	
	// Refresh Device in Menu List
	public void refreshDeviceList(){
		System.out.println("refreshDeviceList");
		//dbController = new DBQueryController();
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
			MainResultPane.setContent(resultListView);
		}else if(type == "tree"){
			//resultListView.setItems(list);
			//MainResultPane.setContent(resultListView);
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
        	resultList.add(name + "." + ext);
        }
        
		return resultList;
	}

	// Find whether a target is inside the tree of root, by recursion
	public TreeItem<String> findItem(TreeItem<String> root, String target) {
		TreeItem<String> result = null;

		if (root.getValue() == target) {
			return root;
		} else if (root.getChildren().size() != 0) {
			for (TreeItem<String> sub : root.getChildren()) {
				TreeItem<String> subResult = findItem(sub, target);
				if (subResult != null) {
					return subResult;
				}
			}
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

	public void setMainApp(MainAppFinal mainAppFinal) {
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

		Stage dialog = this.createProgressDialog();
	
		Service<?> service = new FileSystemFormatDescriptorService(newDevice.getDeviceImageFolder());
		
		ChangeListener<State> handleServiceChange =	new ChangeListener<State>()
		{
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue)
			{
				if (newValue == State.CANCELLED || newValue == State.FAILED)
				{
					dialog.close();
				}
			}
		};

		service.stateProperty().addListener(handleServiceChange);

		dialog.setOnHidden(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent arg0)
			{
				service.cancel();
			}
		});
		
		EventHandler<WorkerStateEvent> handleUnpackServiceSucceed = new EventHandler<WorkerStateEvent>()
		{
			@Override
			public void handle(WorkerStateEvent e)
			{
				@SuppressWarnings("unchecked")
				ArrayList<ArrayList<FileEntity>> entityList = (ArrayList<ArrayList<FileEntity>>)(e.getSource().getValue());
				
				if(entityList.size() > 0)
				{
					dialog.close();
					DBInsertTransaction transaction = new DBInsertTransaction();
					transaction.insertNewDeviceData(newDevice, entityList);
					refreshCase(transaction.getDeviceID());
				}
			}
		};

		EventHandler<WorkerStateEvent> handleFSServiceSucceed = new EventHandler<WorkerStateEvent>()
		{
			@Override
			public void handle(WorkerStateEvent e)
			{
				@SuppressWarnings("unchecked")
				ArrayList<FormatDescription> formatDescriptions = (ArrayList<FormatDescription>)(e.getSource().getValue());
				
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
				
				Service<?> service = new UnpackDirectoryService(formatDescriptions);
				service.setOnSucceeded(handleUnpackServiceSucceed);
				service.stateProperty().addListener(handleServiceChange);
				service.start();

				dialog.setOnHidden(new EventHandler<WindowEvent>()
				{
					@Override
					public void handle(WindowEvent arg0)
					{
						service.cancel();
					}
				});
			}
		};

		service.setOnSucceeded(handleFSServiceSucceed);
		
		service.start();
		dialog.show();
	}
	
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

		ProgressBar indicator = new ProgressBar();

		root.getChildren().add(indicator);

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
	
	//Filters Controller Function
	@FXML 
	public void handleFilter() {
		
		this.filter = new Filter();
		
		if(!startDatePicker.getValue().equals(null))
			filter.modifyStartDate(startDatePicker.getValue());
		if(!endDatePicker.getValue().equals(null))
			filter.modifyEndDate(endDatePicker.getValue());
		
		String startTimeDaily = startHourDailyTf.getText() + ":" + startMinuteDailyTf.getText();
		String endTimeDaily = endHourDailyTf.getText() + ":" + endMinuteDailyTf.getText();
		String startTime = startHourTf.getText() +":"+ startMinuteTf.getText();
		String endTime = endHourTf.getText() + ":" + endMinuteTf.getText();
		
		
		filter.modifyStartTimeDaily(startTimeDaily);
		filter.modifyEndTimeDaily(endTimeDaily);
		filter.modifyStartTime(startTime);
		filter.modifyEndTime(endTime);
		
		
		if(!keywordsTf.getText().equals(EMPTY_STRING))
			filter.modifyKeyword(keywordsTf.getText());
		
		filter.modifyIsModified(isModifiedCheckBox.isSelected());
		filter.modifyIsRecovered(isDeletedCheckBox.isSelected());
		filter.modifiyIsOriginal(isOriginalCheckBox.isSelected());
		
		displayResult(listItems,"category");
	}
	
	@FXML 
	public void resetFilter() {
		startDate = LocalDate.parse("1992-05-08");
		endDate = LocalDate.now();
		startHourTf.setText("00");
		startMinuteTf.setText("00");
		endHourTf.setText("23");
		endMinuteTf.setText("59");
		startHourDailyTf.setText("00");
		startMinuteDailyTf.setText("00");
		endHourDailyTf.setText("23");
		endMinuteDailyTf.setText("59");
		handleFilter();
	}
	
	public boolean validationOfFilters () {
		
		return true;
	}
	
	
	
}