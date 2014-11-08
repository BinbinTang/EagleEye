package eagleeye.controller;

import java.io.IOException;

import eagleeye.entities.Device;
import eagleeye.view.NewDeviceDialogController;
import eagleeye.view.WorkBenchControllerFinal;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainAppFinal extends Application {

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
            final double rem = Math.rint(new Text(" ").getLayoutBounds().getHeight());
            Scene scene = new Scene(rootLayout, 50 * rem, 40 * rem);
            //Scene scene = new Scene(rootLayout);

            primaryStage.setMinHeight(400);
            primaryStage.setMinWidth(820);
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
            FXMLLoader loader = new FXMLLoader(MainAppFinal.class.getResource("../view/WorkBenchFinal.fxml"));
            AnchorPane overviewPage = (AnchorPane) loader.load();
            rootLayout.setCenter(overviewPage);
            WorkBenchControllerFinal controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
    }
    
    public Device showNewDeviceDialogDialog() {
    	  try {
    		  
    	    FXMLLoader loader = new FXMLLoader(MainAppFinal.class.getResource("../view/NewDeviceDialog.fxml"));
    	    AnchorPane page = (AnchorPane) loader.load();
    	    Stage dialogStage = new Stage();
    	    dialogStage.setTitle("Import a New Device Image");
    	    dialogStage.initModality(Modality.WINDOW_MODAL);
    	    dialogStage.initOwner(primaryStage);
    	    Scene scene = new Scene(page);
    	    dialogStage.setScene(scene);
    	  
    	    NewDeviceDialogController controller = loader.getController();
    	    controller.setDialogStage(dialogStage);
    	    dialogStage.showAndWait();
    	    
    	    if(controller.isImportClickedSuccess())
    	    	return controller.getDevice();
    	    
    	    else
    	    	return null;
    	    
    	  } catch (IOException e) {
    	    e.printStackTrace();
    	    return null;
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