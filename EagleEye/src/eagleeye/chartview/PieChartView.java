package eagleeye.chartview;

import javafx.application.Application;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.stage.Stage;



public class PieChartView extends Application {
  @Override 
  public void start(Stage stage) {
    final PieChart chart = new PieChart(
      FXCollections.observableArrayList(
        new PieChart.Data("China",         1344.0),
        new PieChart.Data("India",         1241.0),
        new PieChart.Data("United States",  310.5)
      )      
    );
    chart.setTitle("Population 2011");

    stage.setScene(new Scene(chart));
    stage.show();
  }

  public static void main(String[] args) { launch(args); }
}