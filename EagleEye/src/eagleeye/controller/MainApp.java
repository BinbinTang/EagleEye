package eagleeye.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import eagleeye.view.WorkBenchController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("EagleEye");
        
        try {
            // Load the root layout from the fxml file
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("../view/WorkBenchRootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            final double rem = Math.rint(new Text("").getLayoutBounds().getHeight());
            Scene scene = new Scene(rootLayout, 50 * rem, 40 * rem);
            //Scene scene = new Scene(rootLayout);

            primaryStage.setMinHeight(400);
            primaryStage.setMinWidth(820);
            primaryStage.setHeight(600);
            primaryStage.setWidth(820);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }

        showWorkBench();
    } 
   

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Shows the workbench scene.
     */
    public void showWorkBench() {
        try {
            // Load the fxml file and set into the center of the main layout
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("../view/WorkBench.fxml"));
            AnchorPane overviewPage = (AnchorPane) loader.load();
            rootLayout.setCenter(overviewPage);
            overviewPage.setMinHeight(400);
            overviewPage.setMinWidth(800);
            WorkBenchController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
    }
    
    // Method to obtain current case path
    public String getCasePath() {
    	String resultCasePath = "TestCaseForUI";
        return resultCasePath;
    }

    public static void main(String[] args) {
        launch(args);
    }
}