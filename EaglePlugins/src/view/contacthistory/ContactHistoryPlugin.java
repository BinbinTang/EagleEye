package view.contacthistory;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import eagleeye.pluginmanager.Plugin;



public class ContactHistoryPlugin extends Application implements Plugin{
	private List params;

	public ContactHistoryPlugin(){

	}
	
	@Override 
	  public void start(Stage stage) {	
		Node pc = (Node)getResult();
		ScrollPane sp = new ScrollPane();
		sp.setContent(pc);
	    stage.setScene(new Scene(sp));
	    stage.show();
	  }

	  
	@Override
	public Object getResult() {
		NumberAxis xAxis = new NumberAxis(1, 31, 1);
        NumberAxis yAxis = new NumberAxis();
		AreaChart<Number,Number> ac = 
            new AreaChart<Number,Number>(xAxis,yAxis);
        ac.setTitle("Temperature Monitoring (in Degrees C)");
 
        XYChart.Series seriesApril= new XYChart.Series();
        seriesApril.setName("April");
        seriesApril.getData().add(new XYChart.Data(1, 4));
        seriesApril.getData().add(new XYChart.Data(3, 10));
        seriesApril.getData().add(new XYChart.Data(6, 15));
        seriesApril.getData().add(new XYChart.Data(9, 8));
        seriesApril.getData().add(new XYChart.Data(12, 5));
        seriesApril.getData().add(new XYChart.Data(15, 18));
        seriesApril.getData().add(new XYChart.Data(18, 15));
        seriesApril.getData().add(new XYChart.Data(21, 13));
        seriesApril.getData().add(new XYChart.Data(24, 19));
        seriesApril.getData().add(new XYChart.Data(27, 21));
        seriesApril.getData().add(new XYChart.Data(30, 21));
        
        XYChart.Series seriesMay = new XYChart.Series();
        seriesMay.setName("May");
        seriesMay.getData().add(new XYChart.Data(1, 20));
        seriesMay.getData().add(new XYChart.Data(3, 15));
        seriesMay.getData().add(new XYChart.Data(6, 13));
        seriesMay.getData().add(new XYChart.Data(9, 12));
        seriesMay.getData().add(new XYChart.Data(12, 14));
        seriesMay.getData().add(new XYChart.Data(15, 18));
        seriesMay.getData().add(new XYChart.Data(18, 25));
        seriesMay.getData().add(new XYChart.Data(21, 25));
        seriesMay.getData().add(new XYChart.Data(24, 23));
        seriesMay.getData().add(new XYChart.Data(27, 26));
        seriesMay.getData().add(new XYChart.Data(31, 26));
        
        ac.getData().addAll(seriesApril, seriesMay);
        ac.setTitle("Contact History");
	    System.out.println("chart initialized");
		return ac;
	}
	
	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int setParameter(List argList) {
		// TODO Auto-generated method stub
		params=argList;
		return 0;
	}
	
	@Override
	public Type getType() {
		return Type.GUI_VIEW;
	}
	
	@Override
	public String getName() {
		return "Contact History";
	}
	
	public static void main(String[] args) { 
		launch(args); 
	}
}