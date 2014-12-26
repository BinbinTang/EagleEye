package view.folderstructure;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import eagleeye.api.entities.*;
import eagleeye.pluginmanager.Plugin;

public class FolderStructureTreePlugin extends Application implements Plugin{
	
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
		Node view = (Node)getResult();
		BorderPane bp = new BorderPane();
		bp.setCenter(view);
		stage.setTitle(this.getName());
	    stage.setScene(new Scene(bp)); 
	    stage.show();	
		
	}
	
	// Add files into directory
	public void addFiles(Directory dir, TreeItem<String> node) {
		for (EagleFile file : dir.getFiles()) {
			TreeItem<String> newItem = new TreeItem<String>(file.getFileName()
					+ "." + file.getFileExt(), new ImageView(fileIcon));
			node.getChildren().add(newItem);
		}

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
			ArrayList<Directory> CopyTreeStructure = new ArrayList<Directory>(
					TreeStructure);
			tree = new TreeView<String>(rootNode);

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
		}
	}
	public static void main(String[] args){
		launch(args);
	}
	@Override
	public int setAvailablePlugins(List<Plugin> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
