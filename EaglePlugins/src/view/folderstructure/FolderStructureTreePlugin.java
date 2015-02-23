package view.folderstructure;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JRootPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import eagleeye.api.dbcontroller.DBController;
import eagleeye.api.entities.*;
import eagleeye.dbcontroller.DBQueryController;
//import eagleeye.entities.Directory;
//import eagleeye.entities.FileEntity;
import eagleeye.entities.Filter;
import eagleeye.pluginmanager.Plugin;
import eagleeye.pluginmanager.PluginManager;
//import eagleeye.utils.FileFormatIdentifier;

public class FolderStructureTreePlugin extends Application implements Plugin{
	
	// Neeeded Reader-type plugin
	private List<Plugin> readers;
	private List<Plugin> popUpViews;
	
	// DataBase
	private DBController dbController;
	
	// Path to identify current case
	private String casePath = "";
	private int currentCaseID = -1;
	private int currentDirID = 0;
	private String currentDir = "";	
	
	// Primary stage
	private Stage primaryStage;
    private AnchorPane rootLayout;   
    
    // Node
    private Node myNode;
    
    // Marked files
    private List<List<String>> markedFilesResult;
    private ArrayList<MyTreeItem> markedFilesCashe;

	// Colors
	private Color originalColor = Color.web("#2e00ff");
	private Color isRecoveredColor = Color.web("#025013");
	private Color isModifiedColor = Color.web("#f42929");	
	private String unMarkedColor = "-fx-background-color: rgba(0, 0, 0, 0);";
	private String markedColor = "-fx-background-color: rgba(0, 0, 0, 0.31);";
	
	//@FXML dynamically created
	private ListView resultListView;	
	    
    // Filter
 	private Filter filter;
 	final ObservableList<String> listItems = FXCollections.observableArrayList(); 
 	
	// Filter's items
	ArrayList<String> categoryFilter;
	private final String EMPTY_STRING = "";
	
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

	ArrayList<EagleFile> allFiles;
	
	ArrayList<EagleDirectory> TreeStructure;
	
	@FXML
	private AnchorPane treeFilterPane;	
	@FXML
	private VBox categoryViewPane;
	@FXML
	private VBox filterPane;	
	@FXML
	private ScrollPane MainResultPane;
	
	// SearchButton
	private final Image searchIcon = new Image("file:Icons/search icon1.png", 16, 16, false, false);
	@FXML
	private Button searchButton;
	@FXML
	private Button resetSearchButton;
	
	// DatePicker
	private LocalDate startDate = LocalDate.parse("1992-05-08");
	private LocalDate endDate = LocalDate.now();
	private final Image calendarIcon = new Image("file:Icons/fileIcon.jpg");
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;

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
		
	
	@Override
	public void start(Stage stage) throws Exception {
		System.out.println("folder structure started");
		this.primaryStage = stage;

		// Load the root layout from the fxml file
        //Scene scene = new Scene(rootLayout);
		Node view = (Node)getResult();
		BorderPane viewContainer = new BorderPane();
		viewContainer.setCenter(view);
		primaryStage.setTitle(this.getName());
	    primaryStage.setScene(new Scene(viewContainer)); 

        //primaryStage.setScene(scene);
        primaryStage.show();

	    // Set On Close Window
	    this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });	
	}
	
	public FolderStructureTreePlugin(){
		myNode = new SwingNode();
		resultListView = new ListView();
		readers = new ArrayList<Plugin>();
		popUpViews = new ArrayList<Plugin>();
		System.out.println("Folder Structure Plugin Loaded");
	}
	
	@FXML
	private void initialize() {
		initializeCategoryFilter();
		initializeDateTimePicker();	
		addDirectoryView(null);
		setAvailablePlugins(null);
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

	// Build treeView
	private void buildTree(){
		if (TreeStructure.size() != 0) {
			// TreeView
			rootNode = new MyTreeItem<Label>(new Label(TreeStructure.get(0)
					.getDirectoryName()), rootIcon);
			rootNode.setDirectoryEntity(TreeStructure.get(0));
			ArrayList<EagleDirectory> CopyTreeStructure = new ArrayList<EagleDirectory>(
					TreeStructure);
			TreeView<Label> tree = new TreeView<Label>(rootNode);

			int rootDirID = TreeStructure.get(0).getDirectoryID();
			// Whenever a directory found its parent, we remove it from copied list
			
			while (CopyTreeStructure.size() > 0) {
				int startSize = CopyTreeStructure.size();
				for (EagleDirectory dir : CopyTreeStructure) {
					MyTreeItem<Label> targetParent = null;
					// check if it is root
					if (dir.getDirectoryID() == rootDirID) {
						casePath = dir.getDirectoryName();
						this.addFiles(dir, rootNode);
						CopyTreeStructure.remove(dir);
						break;
					}else{
						//System.out.println("not root");
					}

					MyTreeItem<Label> newItem = new MyTreeItem<Label>(
							new Label(dir.getDirectoryName()));
					newItem.setDirectoryEntity(dir);

					EagleDirectory parent = findDir(TreeStructure,dir.getParentDirectory());

					if (parent != null) {
						targetParent = findItem(rootNode,parent.getDirectoryName());
					} else {
						//System.out.println("Cannot find parent" + dir.getParentDirectory());
					}

					if (targetParent != null) {
						//System.out.println("parent found");
						targetParent.getChildren().add(newItem);

						// Record current expanded path, such that it wont refresh after filter
						if(currentDirID == newItem.getDirectory().getDirectoryID()){
							MyTreeItem<Label> tempItem = newItem;
							while(tempItem.getParent() != null){
								tempItem.setExpanded(true);
								tempItem = (MyTreeItem<Label>) tempItem.getParent();
							}
							tempItem.setExpanded(true);
						}
						this.addFiles(dir, newItem);
						CopyTreeStructure.remove(dir);
						break;
					} else {
						//System.out.println("cannot find parent");
					}
				}
				int endSize = CopyTreeStructure.size();
				// check if no change in size, then we print out remaining list and exit
				if (startSize == endSize) {
					System.out.println("Remaining Directories:");
					for (EagleDirectory dir : CopyTreeStructure) {
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
					
					if (mouseEvent.getButton() == MouseButton.SECONDARY) {
						
						// Mark file
						if(mouseEvent.getClickCount() > 0){
							item.setMark(!item.getMark());
							if(!item.getMark()){
								item.getValue().setStyle(unMarkedColor);
								markedFilesCashe.remove(item);
							}else{
								item.getValue().setStyle(markedColor);
								markedFilesCashe.add(item);
							}
							System.out.println("number of files marked = "+markedFilesCashe.size());
							for(MyTreeItem i : markedFilesCashe){
								System.out.println(i.file.getFileName()+" "+i.mark);
							}
						}
						
						// Input comments
						if(mouseEvent.getClickCount() == 2){
							final Stage commentView = new Stage();
			                VBox dialogVbox = new VBox();
			                ScrollPane commentInputContainer = new ScrollPane();
			                TextArea commentInput = new TextArea();
			                
			                commentView.initModality(Modality.APPLICATION_MODAL);
			                commentView.initOwner(primaryStage);
			                dialogVbox.getChildren().add(new Text("My comments."));
			                commentInputContainer.setContent(commentInput);
			                commentInput.autosize();
			                
			                if(item.getValue().getTooltip() != null){
			                	commentInput.setText(item.getValue().getTooltip().getText());
			                }
			                dialogVbox.getChildren().add(commentInputContainer);
			                
			                commentInputContainer.setFitToHeight(true);
			                commentInputContainer.setFitToWidth(true);
			                Scene dialogScene = new Scene(dialogVbox, 300, 200);
			                commentView.setScene(dialogScene);
			                commentView.show();
			                commentView.setOnCloseRequest(new EventHandler<WindowEvent>() {			                	
			                	//TODO: WRITE PROJECT FILE
			                    @Override
			                    public void handle(WindowEvent t) {
			                    	if(!commentInput.getText().equals("")){
				                    	item.getValue().setTooltip(new Tooltip(commentInput.getText()));			                    		
			                    	}
			                    }
			                });
						}
						
					}else if (mouseEvent.getClickCount() == 2) {
						// Check if it is a file, and open						
						if (item.isLeaf()) {
							String filePath = item.getFileEntity().getFilePath() + File.separator + 
									item.getFileEntity().getFileName() + "." + item.getFileEntity().getFileExt();
							
							List params = new ArrayList();
							params.add(filePath);
							for(Plugin pl: popUpViews){
								if(pl.setParameter(params)==0){
									Stage stage = new Stage();
									double WindowWidth = 600;
									double WindowHeight = 400;
									stage.setWidth(WindowWidth);
								    stage.setHeight(WindowHeight);
								   //get display content
									Node fileContentView = (Node)pl.getResult();
									
									//put content in new window
									ScrollPane container = new ScrollPane();
									container.setContent(fileContentView);
									stage.setScene(new Scene(container));
								    stage.setTitle(new File(filePath).getName());
								    stage.show();						
								    
								}
							}
						}	
					}
				}
			});
		    MainResultPane.setContent(tree);
		}
		    return;
	}
	
	// Method to Decide What to Show in Result Pane View
	private void displayResult(ObservableList<String> list, String type){
		if(type == "category"){
			ObservableList<String> resultList = filterResult();
			resultListView.setItems(resultList);
			MainResultPane.setContent(resultListView);
		}else if(type == "tree"){
		}
	}
		
	// Filters 
	private ObservableList<String> filterResult(){

		ObservableList<String> localResultList = FXCollections.observableArrayList(); 
		
        ArrayList<EagleFile> dbFilteredResult = dbController.getFilteredFiles(filter);
        System.out.println("now: " + dbController.getDeviceID());
        System.out.println("filtered result size: " + dbFilteredResult.size());
        
        for (EagleFile file : dbFilteredResult){
        	String name = file.getFileName();
        	String ext = file.getFileExt();
        	localResultList.add(name + "." + ext);
        }
        
		return localResultList;
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
	private EagleDirectory findDir(ArrayList<EagleDirectory> db, int ID) {
		for (EagleDirectory checkParent : db) {
			if (checkParent.getDirectoryID() == ID) {
				return checkParent;
			}
		}
		return null;
	}

	// Add files into directory
	private void addFiles(EagleDirectory dir, MyTreeItem<Label> node) {
		for (EagleFile file : allFiles) {
			if(file.getDirectoryID() == dir.getDirectoryID()){
				Label itemName = new Label(""+ file.getFileName() + "." + file.getFileExt() + " [" + file.getDateCreated() + "]");
				MyTreeItem<Label> newItem = new MyTreeItem<Label>(itemName);
				newItem.setFileEntity(file);
				//Types are: "All", "Image","Video","Audio","Document","Database", "Compressed Folder", "Others"
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
				
				for(List<String> i : markedFilesResult){
					if((newItem.getFileEntity().getDeviceID()+"").equals(i.get(0)) && (newItem.getFileEntity().getFileID()+"").equals(i.get(1))){
						System.out.println("["+getName()+"] mark:"+i.get(0)+" "+i.get(1));
						newItem.setMark(true);
						newItem.getValue().setStyle(markedColor);
						if(!i.get(i.size()-1).equals("")){
							newItem.getValue().setTooltip(new Tooltip(i.get(i.size()-1)));
						}
						markedFilesCashe.add(newItem);
					}
				}
				
				if(newItem.getFileEntity().getIsRecovered()){		
					newItem.getValue().setTextFill(isRecoveredColor);
				}else if(newItem.getFileEntity().getIsModified()){
					newItem.getValue().setTextFill(isModifiedColor);
				}else{
					newItem.getValue().setTextFill(originalColor);
				}
			
				node.getChildren().add(newItem);
				TreeItem<Label> nodeCopy = node;
				do{
					nodeCopy.setExpanded(true);
					nodeCopy = nodeCopy.getParent();
				}while(nodeCopy!= null);
				
			}
		}

	}
	
	private void initializeCategoryFilter() {
		// Category View
		categoryViewPane.setSpacing(5);
		categoryViewPane.setPadding(new Insets(5,5,5,5));
		categoryViewPane.getChildren().clear();
		
		for (String category : categoryList){
			CheckBox categoryBox = new CheckBox(category);
			categoryBox.setSelected(true);
			categoryBox.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	
	            	if(category.equals("All") && categoryBox.isSelected()){
	            		for(Node N : categoryViewPane.getChildren()){
	            			CheckBox checkBoxContainer = (CheckBox) N;
	            			checkBoxContainer.setSelected(true);
	            		}	            	
	            	}else if(category.equals("All") && !categoryBox.isSelected()){
	            		for(Node N : categoryViewPane.getChildren()){
	            			CheckBox cb = (CheckBox) N;
	            			cb.setSelected(false);
	            		}	            	
	            	}else if(!categoryBox.isSelected()){
	            		for(Node N : categoryViewPane.getChildren()){
	            			CheckBox categoryCheckBox = (CheckBox) N;
	            			if(categoryCheckBox.getText().equals("All")){
	            				categoryCheckBox.setSelected(false);
	            			}
	            		}	            	
	            	}
	            }
	        });
			categoryViewPane.getChildren().add(categoryBox);
		}
	}

	private void initializeDateTimePicker() {
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
				checkNumber(newValue,startHourTf);
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
							checkHour(startHourTf); 
							
							startHour = startHourTf.getText();
							System.out.println("startHour is " + startHour);
						}
					}
		});

		startMinuteTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				checkNumber(newValue,startMinuteTf);
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
							checkMinute(startMinuteTf);
							
							startMinute = startMinuteTf.getText();
							System.out.println("startMinute is " + startMinute);
						}
					}
				});
		
		endHourTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				checkNumber(newValue, endHourTf);
			}
		});
		
		endHourTf.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					System.out.println("Textfield on focus");
				} else {
					checkHour(endHourTf);
					endHour = endHourTf.getText();
					System.out.println("endHour is " + endHour);
				}
			}
		});
		
		endMinuteTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				checkNumber(newValue,endMinuteTf);
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
							checkMinute(endMinuteTf);
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
				checkNumber(newValue,startHourDailyTf);
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
							checkHour(startHourDailyTf);
							startHourDaily = startHourDailyTf.getText();
							System.out.println("startHourDaily is " + startHourDaily);
						}
					}
				});

		startMinuteDailyTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				checkNumber(newValue,startMinuteDailyTf);
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
							checkMinute(startMinuteDailyTf);
							startMinuteDaily = startMinuteDailyTf.getText();
							System.out.println("startMinuteDaily is " + startMinuteDaily);
						}
					}
				});
		
		endHourDailyTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				checkNumber(newValue,endHourDailyTf);
			}
		});
		
		endHourDailyTf.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					System.out.println("Textfield on focus");
				} else {
					checkHour(endHourDailyTf);
					endHourDaily = endHourDailyTf.getText();
					System.out.println("endHourDaily is " + endHourDaily);
				}
			}
		});
		
		endMinuteDailyTf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				checkNumber(newValue,endMinuteDailyTf);
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
							checkMinute(endMinuteDailyTf);
							endMinuteDaily = endMinuteDailyTf.getText();
							System.out.println("endMinuteDaily is " + endMinuteDaily);
						}
					}
		});
	}
	
	
	public static void main(String[] args){
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		launch(args);
	}
	
	@Override
	public int setAvailablePlugins(List<Plugin> pls) {
		if(pls == null) return 0;
		popUpViews = pls;
		/*for(Plugin pl: pls){
			if(pl.getType().equals(Plugin.Type.READER)){
				readers.add(pl);
				System.out.println(getName()+": Find "+pl.getName());
			} 
		}*/
		return 0;
	}
	
	@Override
	public String getName() {
		return "Folder Structure";
	}

	@Override
	public Object getResult() {
    	System.out.println("loading faml");
        FXMLLoader loader = null;
		loader = new FXMLLoader(FolderStructureTreePlugin.class.getResource("FolderStructure.fxml"));
		loader.setController(this);
        try {
			rootLayout = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //myNode = (Node)rootLayout;
		return rootLayout;
	}

	@Override
	public Type getType() {
		return Type.GUI_VIEW;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int setParameter(List arg0) {
		dbController = (DBController) arg0.get(0);
		//dbc.getDeviceRootPath();
		// TODO Get List of Files and Directories from host application
		// Avoid getting DB as the parameter
		/*
		if(dbController.getDeviceID() == -1){
			Label noDevice = new Label("No device has been chosen.");
			MainResultPane.setContent(noDevice);
		}
		
		ArrayList<Directory> TreeStructure = dbController
				.getAllDirectoriesAndFiles();
		ArrayList<FileEntity> allFiles = dbController.getAllFiles();
		*/
		
		//tmp create dummy folders and files

		return 0;
	}	
	
	// Add files into directory
	public void addFiles(Directory dir, TreeItem<String> node) {
		for (EagleFile file : dir.getFiles()) {
			TreeItem<String> newItem = new TreeItem<String>(file.getFileName()
					+ "." + file.getFileExt(), new ImageView(fileIcon));
			node.getChildren().add(newItem);
		}

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
	
	// Refresh Case that Loaded in View
	private void refreshCase(int deviceID){
		dbController.setDeviceID(deviceID);
		TreeStructure = dbController.getAllDirectoriesAndFiles();
		allFiles = dbController.getAllFiles();
	}
	
	private void checkNumber(String newValue, TextField input){
		if (!(newValue.matches("[0-9]*"))) {
			input.setText(newValue.substring(0,
					newValue.length() - 1));
			System.out.println("no match");
		}
	}
	
	private void checkHour(TextField hourInput){
		// Check length
		if (hourInput.getText().length() > 2) {
			System.out.println("too long");
			hourInput.setText("23");
		} else if (hourInput.getText().length() == 1) {
			System.out.println("1 degit");
			hourInput.setText("0" + hourInput.getText());
		} else if (hourInput.getText().length() == 0){
			System.out.println("no input");
			hourInput.setText("00");
		}
		// Check range
		if (Integer.parseInt(hourInput.getText()) > 23) {
			System.out.println("too large");
			hourInput.setText("23");
		} else if (Integer.parseInt(hourInput.getText()) < 0) {
			System.out.println("too small");
			hourInput.setText("0");
		} 
	}
	
	private void checkMinute(TextField minuteInput){
		// Check length
		if (minuteInput.getText().length() > 2) {
			System.out.println("too long");
			minuteInput.setText("59");
		} else if (minuteInput.getText().length() == 1) {
			System.out.println("1 degit");
			minuteInput.setText("0"
					+ minuteInput.getText());
		} else if (minuteInput.getText().length() == 0) {
			System.out.println("no input");
			minuteInput.setText("00");
		}
		// Check range
		if (Integer.parseInt(minuteInput.getText()) > 59) {
			System.out.println("too large");
			minuteInput.setText("59");
		} else if (Integer.parseInt(minuteInput.getText()) < 0) {
			System.out.println("too small");
			minuteInput.setText("0");
		}
	}
	
	public class MyTreeItem<T> extends TreeItem<T>{
		
		private boolean mark = false;
		private EagleFile file;
		private EagleDirectory dir;
		
		public void setMark(boolean isMark){
			mark = isMark;
		}
		
		public boolean getMark(){
			return mark;
		}
		
		public void setFileEntity(EagleFile file){
			this.file = file;
		}
		
		public void setDirectoryEntity(EagleDirectory dir){
			this.dir = dir;
		}
		
		public EagleFile getFileEntity(){
			return file;
		}
		
		public EagleDirectory getDirectory(){
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

	@Override
	public Object getMarkedItems() {
		if(markedFilesCashe.size()==0) return null;
		
		markedFilesResult = new ArrayList<List<String>>();
		List<String> headers = new ArrayList<String>();
		headers.add("DeviceID");
		headers.add("FileID");
		headers.add("Name");
		headers.add("Time");
		headers.add("Comment");
		markedFilesResult.add(headers);
		
		for(MyTreeItem<Label> treeItem:markedFilesCashe){
			EagleFile file = treeItem.getFileEntity();
			List<String> mark = new ArrayList<String>();
			mark.add(file.getDeviceID()+"");
			mark.add(file.getFileID()+"");
			mark.add(file.getFileName());
			mark.add(""+file.getDateModified());
			if(treeItem.getValue().getTooltip()!=null){
				mark.add(treeItem.getValue().getTooltip().getText());
			}else{
				mark.add("");
			}
			markedFilesResult.add(mark);
			//markedFilesResult.add(Arrays.asList((file.getDeviceID()+""),(file.getFileID()+""),file.getFileName(),(""+file.getDateModified())));
		}
		return (Object)markedFilesResult;
	}

	@Override
	public void setMarkedItems(Object items) {
			markedFilesResult = new ArrayList<List<String>>();
			markedFilesCashe = new ArrayList<MyTreeItem>();
			if(items!=null){
				System.out.println(markedFilesResult.getClass().getName()+ " vs "+items.getClass().getName());
				if(items.getClass().equals(markedFilesResult.getClass()))
					markedFilesResult = (List<List<String>>) items;
			}
				

	}

}
