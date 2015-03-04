package view.contacthistory;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import eagleeye.api.plugin.MarkableItem;
import eagleeye.api.plugin.MarkableItemManager;
import eagleeye.api.plugin.Plugin;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TestNodeMarking extends Application implements Plugin{
	public class MarkableControl extends MarkableItem<Control>{

		public MarkableControl(Control item, Node n) {
			super(item, n);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void setLookNFeel() {
			String unMarkedColor = "-fx-background-color: rgba(0, 0, 0, 0);";
			String markedColor = "-fx-background-color: rgba(0, 0, 0, 0.31);";
			if(getItem()==null){
				return;
			}else{
				System.out.println("change look and feel");
				if(isMarked()) getItem().setStyle(markedColor);
				else getItem().setStyle(unMarkedColor);
			}
			
		}

		@Override
		protected void setEventHandlers() {
			getItem().addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>(){

				@Override
				public void handle(MouseEvent event) {
					if(event.getClickCount() == 2){
						try {
							Desktop.getDesktop().open(new File("D:\\MyFolder\\y4\\CS3283_MediaTech_Project\\Forensics Suite.pdf"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					useDefaultMouseEventBehaviour(event);
					
				}
				
			});
			
		}
		
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		ScrollPane mainWindow = new ScrollPane();
		HBox hbox = new HBox();
		Button b = new Button("non-markable");
		Button b1 = new Button("markable1");
		Button b2 = new Button("markable2");
		//hbox.getChildren().addAll(b, b1, b2);
		/*
		ObservableList<Button> bList = FXCollections.observableArrayList(); 
		bList.addAll(b,b1,b2);
		ListView lv = new ListView();
		lv.setItems(bList);
		*/
		
		/*
		TreeItem<Button> root = new TreeItem<Button>();
		root.setValue(b);
		TreeItem<Button> t1 = new TreeItem<Button>();
		t1.setValue(b1);
		TreeItem<Button> t2 = new TreeItem<Button>();
		t2.setValue(b2);
		root.getChildren().addAll(t1,t2);
		
		TreeView<Button> tree = new TreeView<Button>(root);

		//simple code to add ui element to markable items list
		MarkableItems mis = new MarkableItems();
		mis.add(b1);
		mis.add(b2);
		*/
		
		Label l =new Label("root");
		Label l1 =new Label("item1");
		Label l2 =new Label("item2");
		TreeItem<Label> root = new TreeItem<Label>(l);
		TreeItem<Label> t1 = new TreeItem<Label>(l1);
		TreeItem<Label> t2 = new TreeItem<Label>(l2);
		root.getChildren().addAll(t1,t2);
		TreeView<Label> ltree = new TreeView<Label>(root);
		
		MarkableControl mc = new MarkableControl(l1,ltree);
		MarkableControl mc2 = new MarkableControl(l2,ltree);
		MarkableItemManager mis = new MarkableItemManager();
		mis.add(mc);
		mis.add(mc2);
		
		mainWindow.setContent(ltree);
		mainWindow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                	List<MarkableItem> marked = mis.getMarkedItems();
                	System.out.println("===== marked items ======" );
                	for(MarkableItem n: marked)
                		System.out.println(n.getItem());
                }
            }
        });
		primaryStage.setScene(new Scene(mainWindow));
		primaryStage.show();
		
	}
	public static void main(String[] args){
		launch(args); 
	}
	@Override
	public String getName() {
		return "Test Markable Tree";
	}
	@Override
	public Type getType() {
		return Type.GUI_VIEW;
	}
	@Override
	public int setAvailablePlugins(List<Plugin> plugins) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int setParameter(List param) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Object getResult() {
		Button b = new Button("non-markable");
		Button b1 = new Button("markable1");
		Button b2 = new Button("markable2");
		
		Label l =new Label("root");
		Label l1 =new Label("item1");
		Label l2 =new Label("item2");
		TreeItem<Label> root = new TreeItem<Label>(l);
		TreeItem<Label> t1 = new TreeItem<Label>(l1);
		TreeItem<Label> t2 = new TreeItem<Label>(l2);
		root.getChildren().addAll(t1,t2);
		TreeView<Label> ltree = new TreeView<Label>(root);
		
		MarkableControl mc = new MarkableControl(l1,ltree);
		MarkableControl mc2 = new MarkableControl(l2,ltree);
		MarkableItemManager mis = new MarkableItemManager();
		mis.add(mc);
		mis.add(mc2);
		return ltree;
	}
	@Override
	public Object getMarkedItems() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setMarkedItems(Object markedItems) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
