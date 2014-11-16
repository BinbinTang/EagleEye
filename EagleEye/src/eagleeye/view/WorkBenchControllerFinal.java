package eagleeye.view;

import java.io.File;
import java.text.NumberFormat;
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
import javafx.concurrent.Task;
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
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import eagleeye.controller.MainAppFinal;
import eagleeye.datacarving.unpack.FileSystemFormatDescriptorService;
import eagleeye.datacarving.unpack.UnpackDirectoryService;
import eagleeye.dbcontroller.DBInsertTransaction;
import eagleeye.dbcontroller.DBQueryController;
import eagleeye.entities.Device;
import eagleeye.entities.Directory;
import eagleeye.entities.FileEntity;
import eagleeye.entities.Filter;
import eagleeye.fileReader.fileLoader;
//import eagleeye.fileReader.fileReader;
import eagleeye.filesystem.format.FormatDescription;
import eagleeye.model.RequestHandler;
import eagleeye.model.UIRequestHandler;
import eagleeye.pluginmanager.*;

public class WorkBenchControllerFinal {
	
	// Predfined fixed numbers
	private Color isRecoveredColor = Color.web("#23ff23");
	private Color isModifiedColor = Color.web("#f42929");

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
	private int currentDirID = 0;
	private String currentDir = "";
	ArrayList<eagleeye.entities.FileEntity> folderStructure;
	ArrayList<Directory> TreeStructure;

	// TreeView & CategoryVuew
	private final Node rootIcon = new ImageView(new Image(getClass()
			.getResourceAsStream("Icons/folder.png"), 16, 16, false, false));
	private final Image fileIcon = new Image(getClass().getResourceAsStream(
			"Icons/file.png"), 16, 16, false, false);
	private final Image docIcon = new Image(getClass().getResourceAsStream(
			"Icons/doc.png"), 16, 16, false, false);
	private final Image imageIcon = new Image(getClass().getResourceAsStream(
			"Icons/photo icon.png"), 16, 16, false, false);
	private final Image videoIcon = new Image(getClass().getResourceAsStream(
			"Icons/video icon.png"), 16, 16, false, false);
	private final Image audioIcon = new Image(getClass().getResourceAsStream(
			"Icons/audio icon.png"), 16, 16, false, false);
	private final Image othersIcon = new Image(getClass().getResourceAsStream(
			"Icons/unknown.png"), 16, 16, false, false);
	private final Image dbIcon = new Image(getClass().getResourceAsStream(
			"Icons/DB.png"), 16, 16, false, false);
	MyTreeItem<Label> rootNode;
	MyTreeItem<String> rootNodeC;
	ArrayList<String> categoryList = new ArrayList(Arrays.asList("All", "Image","Video","Audio","Document","Database", "Compressed Folder", "Others"));

	ArrayList<FileEntity> allFiles;
	
	@FXML
	private AnchorPane treeFilterPane;
	
	
	@FXML
	private VBox categoryViewPane;
	@FXML
	private VBox filterPane;
	
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
	ArrayList<String> categoryFilter;
	private final String EMPTY_STRING = "";
	
	
	@FXML
	private TabPane leftTabPane;
	
	//plugin manager
	private PluginManager pm;
	
	private ProgressBar importProgressIndicator;
	
	/**
	 * The constructor.
	 */
	public WorkBenchControllerFinal() {
		functionList = new ArrayList(Arrays.asList("Folder Structure"));
		pm = new PluginManager("PluginBinaries");
		resultListView = new ListView();
	}

	/**
	 * The initializer.
	 */

	@FXML
	private void initialize() {
		// Hide the filter of folder structure
		treeFilterPane.setVisible(false);
		
		//add pluginsnames to functionList
		List<String> plnames=pm.getGUIPluginNames();
		System.out.println(plnames.size());
		for(String s : plnames){
			if(s.equals("Folder Structure")){
				continue;
			}
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
		

		// Category View
		categoryViewPane.setSpacing(5);
		categoryViewPane.setPadding(new Insets(5,5,5,5));
		categoryViewPane.getChildren().clear();
		
		
		for (String category : categoryList){
			CheckBox ckb = new CheckBox(category);
			ckb.setPrefHeight(30);
			ckb.setSelected(true);
			ckb.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	
	            	if(category.equals("All") && ckb.isSelected())
	            	{
	            		for(Node N : categoryViewPane.getChildren())
	            		{
	            			CheckBox cb = (CheckBox) N;
	            			cb.setSelected(true);
	            		}
	            	
	            	}else if(category.equals("All") && !ckb.isSelected())
	            	{
	            		for(Node N : categoryViewPane.getChildren())
	            		{
	            			CheckBox cb = (CheckBox) N;
	            			cb.setSelected(false);
	            		}
	            	
	            	}else if(!ckb.isSelected())
	            	{
	            		for(Node N : categoryViewPane.getChildren())
	            		{
	            			CheckBox cb = (CheckBox) N;
	            			if(cb.getText().equals("All")){
	            				cb.setSelected(false);
	            			}
	            		}
	            	
	            	}
	            }
	        });
			categoryViewPane.getChildren().add(ckb);
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
			//newBtn.setPrefWidth(80);
			newBtn.setPrefHeight(30);
			
			functionHBox.getChildren().add(newBtn);
			
			//System.out.println(functionName);
			if(functionName.equals("Folder Structure")){
				newBtn.setOnMouseClicked(new EventHandler<MouseEvent>(){
					@Override
					public void handle(MouseEvent event) {
						if(dbController.getDeviceID() != -1){
							treeFilterPane.setVisible(true);
							addDirectoryView(null);
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
								addPluginView(pl);
								treeFilterPane.setVisible(false);
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
				System.exit(0);
			}
		});

		// Search
		//searchButton.setGraphic(new ImageView(searchIcon));
	}

	/*
	 *Visualization methods 
	 * 
	 */
	
	private void addPluginView(Plugin pl){
		Object plview = pl.getResult();
		//System.out.println(plview.getClass().getName());
		
		MainResultPane.setContent((Node)plview);
	}
	
	private void addDirectoryView(Filter filter){
		TreeStructure = dbController.getAllDirectoriesAndFiles();
		if(filter == null){
			allFiles = dbController.getAllFiles();
		}else{
			allFiles = dbController.getFilteredFiles(filter);
		}
		
		buildTree();
	}

	
	/*
	 * Methods of workbench
	 */
	//retrieve device list from db through RequestHandler
	private ArrayList<Device> getExisitingDevices(){
		ArrayList<Device> DeviceList;
		RequestHandler rh= new UIRequestHandler();
		DeviceList = rh.getExistingDevices();
		return DeviceList;
		
	}
	
	private void loadExistingDevice(int deviceID){
		refreshCase(deviceID);
	}
	
	// Build treeView
	private void buildTree(){
		if (TreeStructure.size() != 0) {
			// TreeView
			rootNode = new MyTreeItem<Label>(new Label(TreeStructure.get(0)
					.getDirectoryName()), rootIcon);
			rootNode.setDirectoryEntity(TreeStructure.get(0));
			ArrayList<Directory> CopyTreeStructure = new ArrayList<Directory>(
					TreeStructure);
			TreeView<Label> tree = new TreeView<Label>(rootNode);

			// Force root node ID to be 0
			//TreeStructure.get(0).modifyDirectoryID(0);

			int rootDirID = TreeStructure.get(0).getDirectoryID();
			// Whenever a directory found its parent, we remove it from copied
			// list
			
			while (CopyTreeStructure.size() > 0) {
				int startSize = CopyTreeStructure.size();
				for (Directory dir : CopyTreeStructure) {
					MyTreeItem<Label> targetParent = null;
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

					MyTreeItem<Label> newItem = new MyTreeItem<Label>(
							new Label(dir.getDirectoryName()));
					newItem.setDirectoryEntity(dir);

					Directory parent = findDir(TreeStructure,dir.getParentDirectory());

					if (parent != null) {
						targetParent = findItem(rootNode,parent.getDirectoryName());
					} else {
						//System.out.println("Cannot find parent" + dir.getParentDirectory());
					}

					if (targetParent != null) {
						// System.out.println("parent found");
						targetParent.getChildren().add(newItem);

						// Record current expanded path, such that it wont refresh after filter
						if(currentDirID == newItem.getDirectory().getDirectoryID()){
							MyTreeItem<Label> temp = newItem;
							while(temp.getParent() != null){
								temp.setExpanded(true);
								temp = (MyTreeItem<Label>) temp.getParent();
							}
							temp.setExpanded(true);
						}
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

			tree.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {

					MyTreeItem<Label> item = (MyTreeItem<Label>) tree.getSelectionModel().getSelectedItem();
					if(item.getDirectory() != null){
						currentDirID = item.getDirectory().getDirectoryID();
					}
					System.out.println("Selected Text : " + item.getValue().getText());
					
					// Change Result pane view if a folder
					/*
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
					*/
					
					if (mouseEvent.getClickCount() == 2) {
						// Check if it is a file, and open						
						if (item.isLeaf()) {
							String filePath = item.getFileEntity().getFilePath() + file.separator + item.getFileEntity().getFileName() + "." + item.getFileEntity().getFileExt();
							//File currentFile = new File(filePath);
							//Desktop.getDesktop().open(currentFile);
							fileLoader fd = new fileLoader();
							fd.start(filePath);
						}
						
					}
				}
			});
		    MainResultPane.setContent(tree);
		}
		    return;
	}
	
	// Refresh Case that Loaded in View
	private void refreshCase(int deviceID){
		//dbController = new DBQueryController();
		if(functionHBox.getChildren().size() != 0){
			Label noDevice = new Label("No function has been chosen.");
			MainResultPane.setContent(noDevice);
		}
		dbController.setDeviceID(deviceID);
		System.out.println("new device: " +deviceID);
		TreeStructure = dbController.getAllDirectoriesAndFiles();
		allFiles = dbController.getAllFiles();
		
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
		ArrayList<Device> devices = dbController.getAllDevices();
		openMenu.getItems().clear();
		for (Device device : devices){
			int ID = device.getDeviceID();
			MenuItem newItem = new MenuItem(device.getDeviceName() + " [" + device.getDeviceOwner() + "]");
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
	private void displayResult(ObservableList<String> list, String type){
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
	private ObservableList<String> filterResult(){

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
	private MyTreeItem<Label> findItem(MyTreeItem<Label> root, String target) {
		MyTreeItem<Label> result = null;

		if (root.getValue().getText() == target) {
			return root;
		} else if (root.getChildren().size() != 0) {
			for (TreeItem<Label> sub : root.getChildren()) {
				MyTreeItem<Label> subResult = findItem((MyTreeItem)sub, target);
				if (subResult != null) {
					return subResult;
				}
			}
		}

		return result;
	}

	// Find Directory according to ID
	private Directory findDir(ArrayList<Directory> db, int ID) {
		for (Directory checkParent : db) {
			if (checkParent.getDirectoryID() == ID) {
				return checkParent;
			}
		}
		return null;
	}

	// Add files into directory
	private void addFiles(Directory dir, MyTreeItem<Label> node) {
		for (eagleeye.entities.FileEntity file : allFiles) {
			if(file.getDirectoryID() == dir.getDirectoryID()){
				Label itemName = new Label(""+ file.getFileName() + "." + file.getFileExt());
				MyTreeItem<Label> newItem = new MyTreeItem<Label>(itemName);
				newItem.setFileEntity(file);
				//"All", "Image","Video","Audio","Document","Database", "Compressed Folder", "Others"
				switch(file.getCategory()){
					case("Image"): 
						newItem.setGraphic(new ImageView(imageIcon));
					break;
					case("Video"): 
						newItem.setGraphic(new ImageView(videoIcon));
					break;
					case("Audio"): 
						newItem.setGraphic(new ImageView(audioIcon));
					break;
					case("Document"): 
						newItem.setGraphic(new ImageView(docIcon));
					break;
					case("Database"): 
						newItem.setGraphic(new ImageView(dbIcon));
					break;
					case("Compressed Folder"): 
					break;
					case("Others"): 
						newItem.setGraphic(new ImageView(othersIcon));
					break;
					
					default:
						newItem.setGraphic(new ImageView(fileIcon));
					break;
					
				}
				
				if(newItem.getFileEntity().getIsRecovered()){		
					newItem.getValue().setTextFill(isRecoveredColor);
					System.out.println("Recovered: "+ newItem.getValue());
				}else if(newItem.getFileEntity().getIsModified()){
					newItem.getValue().setTextFill(isModifiedColor);
					System.out.println("Modified: "+ newItem.getValue());
				}
			
				node.getChildren().add(newItem);
			}
		}

	}
	
	// change progressLabel
	public void updateProgress(String progress){
		progressLabel.setText(progress);
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
		
		Service<?> fsService = new FileSystemFormatDescriptorService(newDevice.getDeviceImageFolder());
		
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
	
	//Filters Controller Function
	@FXML 
	public void handleFilter() {
		
		this.filter = new Filter();
		categoryFilter = new ArrayList<String>();
		
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

		for(Node N : categoryViewPane.getChildren())
		{
			CheckBox cb = (CheckBox) N;
			if(cb.isSelected())
				categoryFilter.add(cb.getText());
		}
		
		
		
		
		filter.setCategoryFilter(categoryFilter);
		//displayResult(listItems,"category");
		addDirectoryView(filter);
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
		keywordsTf.setText("");
		isDeletedCheckBox.setSelected(true);
		isModifiedCheckBox.setSelected(true);
		isOriginalCheckBox.setSelected(true);
		
		for (Node N : categoryViewPane.getChildren())
		{
			CheckBox cb = (CheckBox) N;
			cb.setSelected(true);
		}
		categoryFilter = new ArrayList<String> ();
		
		handleFilter();
		
	}
	
	public boolean validationOfFilters () {
		
		return true;
	}
	
	public class MyTreeItem<T> extends TreeItem<T>{
		
		private FileEntity file;
		private Directory dir;
		
		public void setFileEntity(FileEntity file){
			this.file = file;
		}
		
		public void setDirectoryEntity(Directory dir){
			this.dir = dir;
		}
		
		public FileEntity getFileEntity(){
			return file;
		}
		
		public Directory getDirectory(){
			return dir;
		}
		
		MyTreeItem(){
			super();
		}
		MyTreeItem(T value){
			super(value);
		}
		MyTreeItem(T value, Node graphic){
			super(value,graphic);
		}
	}

	
}

