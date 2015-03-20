package eagleeye.api.plugin;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

//TODO: change UI when marked
public abstract class MarkableItem<T>{
	private T item;
	private boolean isMarked;
	private String comment;
	private ContextMenu dropDownMenu;
	private MenuItem mark;
	private MenuItem addComment;
	private Node displayNode;
	
	
	
	public MarkableItem(T item, Node n){
		
		setItem(item);
		setComment("");
		setDefaultMenu();
		setMarked(false);
		setDisplayNode(n);
		setEventHandlers();
	}
	public T getItem() {
		return item;
	}
	public void setItem(T item) {
		this.item = item;
	}
	public boolean isMarked() {
		return isMarked;
	}
	public void setMarked(boolean isMarked) {
		this.isMarked = isMarked;
		if(this.isMarked){
			mark.setText("Un-mark");
			System.out.println(getItem()+" is marked");
		}else{
			if(!(getComment().trim().equals(""))) 
				setComment("");
			mark.setText("Mark");
			//System.out.println(getItem()+" is Un-marked");
		}
		setLookNFeel();
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public ContextMenu getDropDownMenu() {
		return dropDownMenu;
	}
	public void setDropDownMenu(ContextMenu dropDownMenu) {
		this.dropDownMenu = dropDownMenu;
	}
	public Node getDisplayNode() {
		return displayNode;
	}
	public void setDisplayNode(Node displayNode) {
		this.displayNode = displayNode;
	}
	public void setMenu(ContextMenu m){
		dropDownMenu = m;
	}
	protected void setDefaultMenu(){
		//System.out.println("menu set");
		dropDownMenu = new ContextMenu();
		mark = new MenuItem("Mark");
		addComment = new MenuItem("Edit Comment");
		dropDownMenu.getItems().addAll(mark,addComment);
	}
	public void useDefaultMouseEventBehaviour(MouseEvent event){
		if(event.getEventType() == MouseEvent.MOUSE_MOVED){
			dropDownMenu.hide();
		}
		if (event.getButton() == MouseButton.PRIMARY) {
			dropDownMenu.hide();
    	}
        if (event.getButton() == MouseButton.SECONDARY) {
        	mark.setOnAction(new EventHandler<ActionEvent>() {
    		    @Override 
    		    public void handle(ActionEvent e) {
    		    	setMarked(!isMarked());
    		    }
    		});
    		
    		addComment.setOnAction(new EventHandler<ActionEvent>() {
    		    @Override 
    		    public void handle(ActionEvent e) {	

	                ScrollPane commentInputContainer = new ScrollPane();
	                TextArea commentInput = new TextArea(getComment());

	                commentInputContainer.setContent(commentInput);
	       
	                Scene dialogScene = new Scene(commentInputContainer, 300, 200);
	                commentInput.autosize();
	                commentInputContainer.setFitToHeight(true);
	                commentInputContainer.setFitToWidth(true);
	                javafx.application.Platform.runLater(new Runnable() {
    			        @Override
    			        public void run() {
    			            Stage stage = new Stage();
    			            stage.setTitle("Edit Comment");
    			            stage.setScene(dialogScene);
    			            stage.setAlwaysOnTop(true);
    			            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {			                	
    		                    @Override
    		                    public void handle(WindowEvent t) {
    		                    	if(!commentInput.getText().equals("")){
    		                    		setComment(commentInput.getText());	
    		                    		setMarked(true);
    		                    	}
    		                    }
    		                });
    			            stage.show();
    			           
    			        }
    			    });

	              /*  
	                
	                if(item.getValue().getTooltip() != null){
	                	commentInput.setText(item.getValue().getTooltip().getText());
	                }
	                dialogVbox.getChildren().add(commentInputContainer);
	                
	                commentInputContainer.setFitToHeight(true);
	                commentInputContainer.setFitToWidth(true);
	                Scene dialogScene = new Scene(dialogVbox, 300, 200);
	                commentView.setScene(dialogScene);
	                commentView.show();
	                commentView.setOnCloseRequest(new EventHandler<WindowEvent>() {			                	
	                	//TODO: WRITE PROJECT FILE
	                    @Override
	                    public void handle(WindowEvent t) {
	                    	if(!commentInput.getText().equals("")){
		                    	item.getValue().setTooltip(new Tooltip(commentInput.getText()));			                    		
	                    	}
	                    }
	                });*/
    		    }
    		});
    		getDropDownMenu().show(getDisplayNode(), event.getScreenX(), event.getScreenY());
        }
	}
	abstract protected void setLookNFeel();
	abstract protected void setEventHandlers();
	
}