package view.folderstructure;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import eagleeye.api.entities.*;
import eagleeye.pluginmanager.Plugin;

public class FolderStructureTreePlugin extends Application implements Plugin{
	
	// Declare primary stage
	private Stage primaryStage;
    private AnchorPane rootLayout;
    
    // Filter
 	private EagleFilter filter = new EagleFilter();
 	final ObservableList<String> listItems = FXCollections.observableArrayList(); 
	
	private TreeView<String> tree;
	private Node rootIcon;
	private Image fileIcon;
	private TreeItem<String> rootNode;
	private TreeItem<String> rootNodeC;
	
	private String casePath = "";
	private int currentCaseID = -1;
	private String currentDir = "";
	private ArrayList<Directory> TreeStructure;
	private ArrayList<FileEntity> allFiles;
	
	public FolderStructureTreePlugin(){
		/*rootIcon = new ImageView(new Image(getClass().getResourceAsStream("Icons/treeViewRootFolderIcon.png")));
		fileIcon = new Image(getClass().getResourceAsStream("Icons/fileIcon.jpg"));*/
		TreeStructure = new ArrayList<Directory>();
		allFiles = new ArrayList<FileEntity>();
		initTreeView();
	}
	
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public int setAvailablePlugins(List<Plugin> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getName() {
		return "Folder Structure";
	}

	@Override
	public Object getResult() {
		return tree;
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

	@Override
	public void start(Stage stage) throws Exception {
		
		this.primaryStage = stage;
		
		Node view = (Node)getResult();
		BorderPane bp = new BorderPane();
		bp.setCenter(view);
		stage.setTitle(this.getName());
	    stage.setScene(new Scene(bp)); 
	    stage.show();	
	    
	    // Load FXML
	    try {
            // Load the root layout from the fxml file
            FXMLLoader loader = new FXMLLoader(FolderStructureTreePlugin.class.getResource("FolderStructure.fxml"));
            rootLayout = (AnchorPane) loader.load();
            final double rem = Math.rint(new Text(" ").getLayoutBounds().getHeight());
            Scene scene = new Scene(rootLayout, 50 * rem, 40 * rem);
            //Scene scene = new Scene(rootLayout);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
	    
	    // Set On Close Window
	    this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
	}
	
	// Add files into directory
	public void addFiles(EagleDirectory dir, TreeItem<String> node) {
		for (EagleFile file : dir.getFiles()) {
			TreeItem<String> newItem = new TreeItem<String>(file.getFileName()
					+ "." + file.getFileExt(), new ImageView(fileIcon));
			node.getChildren().add(newItem);
		}

	}
	// Find Directory according to ID
	public EagleDirectory findDir(ArrayList<EagleDirectory> db, int ID) {
		for (EagleDirectory checkParent : db) {
			if (checkParent.getDirectoryID() == ID) {
				return checkParent;
			}
		}
		return null;
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
	
	public void initTreeView(){
		// Check if DB empty
		if (TreeStructure.size() != 0) {
			// TreeView
			rootNode = new TreeItem<String>(TreeStructure.get(0)
					.getDirectoryName(), rootIcon);
			ArrayList<EagleDirectory> CopyTreeStructure = new ArrayList<EagleDirectory>(
					TreeStructure);
			tree = new TreeView<String>(rootNode);

			// Force root node ID to be 0
			//TreeStructure.get(0).modifyDirectoryID(0);

			int rootDirID = TreeStructure.get(0).getDirectoryID();
			// Whenever a directory found its parent, we remove it from copied
			// list
			
			while (CopyTreeStructure.size() > 0) {
				int startSize = CopyTreeStructure.size();
				for (EagleDirectory dir : CopyTreeStructure) {
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

					EagleDirectory parent = findDir(TreeStructure,dir.getParentDirectory());

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
					for (EagleDirectory dir : CopyTreeStructure) {
						System.out.println(dir.getDirectoryName()
								+ " needed parent not found: "
								+ dir.getParentDirectory());
					}
					break;
				}
			}
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

}
