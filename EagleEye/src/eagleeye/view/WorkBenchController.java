package eagleeye.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import eagleeye.controller.MainApp;
import eagleeye.model.WorkBench;

public class WorkBenchController {
	// Path to identify current case
	@FXML 
	private GridPane topGridPane;
	@FXML
    private String casePath;
	// DatePicker
	@FXML
	private LocalDate startDate, endDate;
	@FXML
	private DatePicker startDatePicker = new DatePicker(LocalDate.parse("1992-05-08"));		
	@FXML
	private DatePicker endDatePicker = new DatePicker(LocalDate.now());
	
	// Functions
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
		
		startDatePicker.setOnAction(event -> {
    		startDate = startDatePicker.getValue();
            System.out.println("Selected date: " + startDate);
        });
		
    	startDatePicker.setConverter(new StringConverter<LocalDate>()
    			{
    	    private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");

    	    @Override
    	    public String toString(LocalDate localDate)
    	    {
    	        if(localDate==null)
    	            return "";
    	        return dateTimeFormatter.format(localDate);
    	    }

    	    @Override
    	    public LocalDate fromString(String dateString)
    	    {
    	        if(dateString==null || dateString.trim().isEmpty())
    	        {
    	            return null;
    	        }
    	        return LocalDate.parse(dateString,dateTimeFormatter);
    	    }
    	});
    	
    	endDatePicker.setOnAction(event -> {
    		endDate = startDatePicker.getValue();
            System.out.println("Selected date: " + endDate);
        });
    	
    	endDatePicker.setConverter(new StringConverter<LocalDate>()
    			{
    	    private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");

    	    @Override
    	    public String toString(LocalDate localDate)
    	    {
    	        if(localDate==null)
    	            return "";
    	        return dateTimeFormatter.format(localDate);
    	    }

    	    @Override
    	    public LocalDate fromString(String dateString)
    	    {
    	        if(dateString==null || dateString.trim().isEmpty())
    	        {
    	            return null;
    	        }
    	        return LocalDate.parse(dateString,dateTimeFormatter);
    	    }
    	});
    	
	}
	
	/*
	 * Methods of workbench
	 */
	
	// DatePicker
	
	//private void handleStartDateClick
		
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // obtain current case path
        casePath = (mainApp.getCasePath());
    }
}
