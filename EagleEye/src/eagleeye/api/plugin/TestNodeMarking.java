package eagleeye.api.plugin;

import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TestNodeMarking extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		ScrollPane mainWindow = new ScrollPane();
		HBox hbox = new HBox();
		Button b = new Button("non-markable");
		Button b1 = new Button("markable1");
		Button b2 = new Button("markable2");
		hbox.getChildren().addAll(b, b1, b2);
		
		//simple code to add ui element to markable items list
		MarkableItems mis = new MarkableItems();
		mis.add(b1);
		mis.add(b2);
		
		
		
		
		mainWindow.setContent(hbox);
		mainWindow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                	List<Node> marked = mis.getMarkedItems();
                	System.out.println("===== marked items ======" );
                	for(Node n: marked)
                		System.out.println(n);
                }
            }
        });
		primaryStage.setScene(new Scene(mainWindow));
		primaryStage.show();
		
	}
	public static void main(String[] args){
		launch(args); 
	}
	
}
