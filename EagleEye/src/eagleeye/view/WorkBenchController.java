package eagleeye.view;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import eagleeye.controller.MainApp;
import eagleeye.datacarving.unpack.AndroidBootImageUnpacker;
import eagleeye.datacarving.unpack.DiskImageUnpackerManager;
import eagleeye.datacarving.unpack.FAT32ImageUnpacker;
import eagleeye.datacarving.unpack.YAFFS2ImageUnpacker;
import eagleeye.dbcontroller.DBInsertTransaction;
import eagleeye.entities.Device;
import eagleeye.entities.Directory;
import eagleeye.filesystem.format.AndroidBootFormatIdentifier;
import eagleeye.filesystem.format.FAT32FormatIdentifier;
import eagleeye.filesystem.format.FormatDescription;
import eagleeye.filesystem.format.FormatIdentifierManager;
import eagleeye.filesystem.format.YAFFS2FormatIdentifier;
import eagleeye.model.WorkBench;

public class WorkBenchController {

	// File chooser
	private File file;
	private Label labelFilePath = new Label();
	private File directory;
	private Label labelDirPath = new Label();

	// Path to identify current case
	private String casePath;;

	// TreeView
	private final Node rootIcon = new ImageView(new Image(getClass()
			.getResourceAsStream("Icons/treeViewRootFolderIcon.png")));
	private final Image fileIcon = new Image(getClass().getResourceAsStream(
			"Icons/fileIcon.jpg"));
	List<MyFile> myFiles = Arrays
			.<MyFile> asList(new MyFile("200482583232.6910771", ".jpg", false,
					false, "/UI Test"), 
					new MyFile("CS3283 meeting notes",".txt", false, false, "/UI Test"),
					new MyFile(
					"fdcbcc689c21421c9e5abb6868884fd8", ".jpg", false, false,"/UI Test"),
					new MyFile(
					"Game Design Strategies for Collectivist Persuasion", ".pdf", false,false, "/UI Test"), 
					//new MyFile("When you are gone", ".flv",false, false, "/UI Test"), 
					new MyFile("Ó£»¨2", ".jpg", false,false, "/UI Test"));
	TreeItem<String> rootNode = new TreeItem<String>("MyFiles", rootIcon);

	@FXML
	private StackPane treeViewPane;

	// UI elements
	@FXML
	private GridPane topGridPane;
	// DatePicker
	private LocalDate startDate = LocalDate.parse("1992-05-08");
	private LocalDate endDate = LocalDate.now();
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
	private MenuItem openClick;
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

		startDatePicker.setConverter(new StringConverter<LocalDate>() {
			private DateTimeFormatter dateTimeFormatter = DateTimeFormatter
					.ofPattern("dd/MM/yyyy");

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

		endDatePicker.setOnAction(event -> {
			endDate = endDatePicker.getValue();
			System.out.println("Selected date: " + endDate);
		});

		endDatePicker.setConverter(new StringConverter<LocalDate>() {
			private DateTimeFormatter dateTimeFormatter = DateTimeFormatter
					.ofPattern("dd/MM/yyyy");

			@Override
			public String toString(LocalDate localDate) {
				if (localDate == null)
					return "";
				endDate = localDate;
				return dateTimeFormatter.format(localDate);
			}

			@Override
			public LocalDate fromString(String dateString) {
				if (dateString == null || dateString.trim().isEmpty()) {
					return null;
				}
				endDate = LocalDate.parse(dateString, dateTimeFormatter);
				return LocalDate.parse(dateString, dateTimeFormatter);
			}
		});

		// Time
		startHourTf.focusedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean oldPropertyValue, Boolean newPropertyValue) {
						if (newPropertyValue) {
							System.out.println("Textfield on focus");
						} else {
							startHour = startHourTf.getText();
							System.out.println("startHour is " + startHour);
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
							startMinute = startMinuteTf.getText();
							System.out.println("startMinute is " + startMinute);
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
					endHour = endHourTf.getText();
					System.out.println("endHour is " + endHour);
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
							endMinute = endMinuteTf.getText();
							System.out.println("endMinute " + endMinute);
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

		// directory chooser
		openClick.setOnAction(this::handleOpenDirectory);

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

		// TreeView
		rootNode.setExpanded(true);

		for (MyFile file : myFiles) {
			TreeItem<String> empLeaf = new TreeItem<String>(file.getName(),new ImageView(fileIcon));
			boolean foundPath = false;
			boolean foundDep = false;
			for (TreeItem<String> pathNode : rootNode.getChildren()) {
				for (TreeItem<String> depNode : pathNode.getChildren()) {
					// Check if path match
					if (pathNode.getValue().contentEquals(file.getPath())) {
						// Check if format match
						if (depNode.getValue().contentEquals(file.getFormat())) {
							depNode.getChildren().add(empLeaf);
							foundDep = true;
							break;
						}
						if (!foundDep) {
							TreeItem<String> newDepNode = new TreeItem<String>(
									file.getFormat());
							pathNode.getChildren().add(newDepNode);
							newDepNode.getChildren().add(empLeaf);
						}
						foundPath = true;
						break;
					}
					if (!foundPath) {
						TreeItem<String> newPathNode = new TreeItem<String>(
								file.getPath());
						TreeItem<String> newDepNode = new TreeItem<String>(
								file.getFormat());
						rootNode.getChildren().add(newPathNode);
						newPathNode.setExpanded(true);
						newPathNode.getChildren().add(newDepNode);
						newDepNode.getChildren().add(empLeaf);
					}
				}
			}
			if (!foundPath && !foundDep){
				System.out.println("first time");
				TreeItem<String> newPathNode = new TreeItem<String>(
						file.getPath());
				TreeItem<String> newDepNode = new TreeItem<String>(
						file.getFormat());
				rootNode.getChildren().add(newPathNode);
				newPathNode.setExpanded(true);
				newPathNode.getChildren().add(newDepNode);
				newDepNode.getChildren().add(empLeaf);
			}
		}

		TreeView<String> tree = new TreeView<String>(rootNode);
		tree.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					TreeItem<String> item = tree.getSelectionModel()
							.getSelectedItem();
					System.out.println("Selected Text : " + item.getValue());

					// Check if it is a file, and open
					if (item.isLeaf()) {
						String filePath = item.getParent().getParent().getValue() +"/" + item.getValue()
								+ item.getParent().getValue();
						System.out.println("Selected File : " + filePath);
						String location = Paths.get(".").toAbsolutePath().normalize().toString();
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
		treeViewPane.getChildren().add(tree);
		tree.setPrefWidth(400);
	}

	/*
	 * Methods of workbench
	 */

	// private void handleStartDateClick

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// obtain current case path
		casePath = (mainApp.getCasePath());
	}

	private void handleOpenDirectory(ActionEvent event) {
		DirectoryChooser dirChooser = new DirectoryChooser();

		// Show open file dialog
		file = dirChooser.showDialog(null);

		labelDirPath.setText(file.getPath());
		System.out.println(labelDirPath);

		try {
			loadDirectory(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadDirectory(File directory) throws Exception {
		if (!directory.isDirectory()) {
			throw new Exception("The provided path is not a directory.");
		}

		// Assuming files are all in main directory
		File[] files = directory.listFiles();

		/*
		 * STEP 02 - FILE SYSTEM LAYER FILES ARE TAGGED TO CERTAIN TYPES TO
		 * PREPARE FOR DATA CARVING
		 */
		FormatIdentifierManager formatIdentifierManager = new FormatIdentifierManager();

		// Simulate plug ins
		formatIdentifierManager.load(new AndroidBootFormatIdentifier());
		formatIdentifierManager.load(new YAFFS2FormatIdentifier());
		formatIdentifierManager.load(new FAT32FormatIdentifier());

		ArrayList<FormatDescription> formatDescriptions = new ArrayList<FormatDescription>();

		Arrays.sort(files);

		if (files.length > 0) {
			System.out.println("-----------------");
			System.out.println("File System Layer");
			System.out.println("-----------------");
			System.out.printf("%-25s\t%-20s\t%15s%n", "Name",
					"Binary Image Type", "Size");

			for (File file : files) {
				FormatDescription formatDescription = formatIdentifierManager
						.identify(file);
				String binaryImageType = "-";

				if (formatDescription != null) {
					formatDescriptions.add(formatDescription);
					binaryImageType = formatDescription.getBinaryImageType();
				}

				System.out.printf("%-25s\t%-20s\t%12s KB%n", file.getName(),
						binaryImageType, file.length());
			}
		}

		System.out.println();

		/*
		 * STEP 03 - DATA CARVING BASED ON DATA FROM FILE SYSTEM LAYER, CARVE
		 * OUT DATA FROM FILE
		 */

		DiskImageUnpackerManager diskImageUnpackerManager = new DiskImageUnpackerManager();

		// Simulate plug ins
		diskImageUnpackerManager.load(new AndroidBootImageUnpacker());
		diskImageUnpackerManager.load(new YAFFS2ImageUnpacker());
		diskImageUnpackerManager.load(new FAT32ImageUnpacker());

		if (formatDescriptions.size() > 0) {
			System.out.println("------------------");
			System.out.println("Data Carving Layer");
			System.out.println("------------------");
			System.out.println();

			// Always unpack OS images first

			for (FormatDescription formatDescription : formatDescriptions) {
				if (formatDescription.getOperatingSystem() == null) {
					continue;
				}

				if (diskImageUnpackerManager.unpack(formatDescription) == null) {
					break;
				}
			}

			ArrayList<eagleeye.entities.File> fileList = null;

			for (FormatDescription formatDescription : formatDescriptions) {
				if (formatDescription.getOperatingSystem() != null) {
					continue;
				}

				fileList = diskImageUnpackerManager.unpack(formatDescription);
			}
			
			DBInsertTransaction transaction = new DBInsertTransaction();
			transaction.insertNewDeviceData(new Device("Test Device", "100GB", "Dennis"), fileList);
		}
	}

	// Classes
	// TreeView File
	public static class MyFile {

		private String name;
		private String format;
		private boolean isDeleted;
		private boolean isModified;
		private String path;

		private MyFile(String name, String format, boolean isDeleted,
				boolean isModified, String path) {
			this.name = name;
			this.format = format;
			this.isDeleted = isDeleted;
			this.isModified = isModified;
			this.path = path;
		}

		public String getName() {
			return name;
		}

		public void setName(String fName) {
			name = fName;
		}

		public String getFormat() {
			return format;
		}

		public void setFormat(String fName) {
			format = fName;
		}

		public boolean getIsDeleted() {
			return isDeleted;
		}

		public void setIsDeleted(boolean tf) {
			isDeleted = tf;
		}

		public boolean getIsModified() {
			return isModified;
		}

		public void setIsModified(boolean tf) {
			isModified = tf;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String fName) {
			path = fName;
		}
	}

}
