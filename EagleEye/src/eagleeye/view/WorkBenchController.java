package eagleeye.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;




import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import eagleeye.controller.MainApp;
import eagleeye.entities.Directory;
import eagleeye.model.WorkBench;

public class WorkBenchController {

	// File chooser
	private File file;
	private Label labelFilePath = new Label();
	private Directory directory;
	private Label labelDirPath = new Label();

	// Path to identify current case
	private String casePath;

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
		startHourTf.focusedProperty().addListener(new ChangeListener<Boolean>()
				{
				    @Override
				    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
				    {
				        if (newPropertyValue)
				        {
				            System.out.println("Textfield on focus");
				        }
				        else
				        {
				        	startHour = startHourTf.getText();
				        	System.out.println("startHour is " + startHour);
				        }
				    }
				});
		startMinuteTf.focusedProperty().addListener(new ChangeListener<Boolean>()
				{
				    @Override
				    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
				    {
				        if (newPropertyValue)
				        {
				            System.out.println("Textfield on focus");
				        }
				        else
				        {
				        	startMinute = startMinuteTf.getText();
				        	System.out.println("startMinute is " + startMinute);
				        }
				    }
				});
		endHourTf.focusedProperty().addListener(new ChangeListener<Boolean>()
				{
				    @Override
				    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
				    {
				        if (newPropertyValue)
				        {
				            System.out.println("Textfield on focus");
				        }
				        else
				        {
				        	endHour = endHourTf.getText();
				        	System.out.println("endHour is " + endHour);
				        }
				    }
				});
		endMinuteTf.focusedProperty().addListener(new ChangeListener<Boolean>()
				{
				    @Override
				    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
				    {
				        if (newPropertyValue)
				        {
				            System.out.println("Textfield on focus");
				        }
				        else
				        {
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
		openClick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser dirChooser = new DirectoryChooser();

				// Show open file dialog
				file = dirChooser.showDialog(null);

				labelDirPath.setText(file.getPath());
				System.out.println(labelDirPath);
			}
		});

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
}
