package eagleeye.api.plugin;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MarkableItems{
	
	//TODO: change UI when marked
	private class MarkableItem{
		private Node item;
		private boolean isMarked;
		private String comment;
		
		public MarkableItem(Node item){
			setItem(item);
			setMarked(false);
			setComment("");
			attachMenu();
		}
		public Node getItem() {
			return item;
		}
		public void setItem(Node item) {
			this.item = item;
		}
		public boolean isMarked() {
			return isMarked;
		}
		public void setMarked(boolean isMarked) {
			this.isMarked = isMarked;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		public void attachMenu(){
			item.setOnMouseClicked(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                if (event.getButton() == MouseButton.SECONDARY) {
	                	ContextMenu dropDownMenu = new ContextMenu();
	                	MenuItem mark = new MenuItem("Mark");
	            		mark.setOnAction(new EventHandler<ActionEvent>() {
	            		    @Override 
	            		    public void handle(ActionEvent e) {
	            		    	setMarked(true);
	            		        System.out.println(item+" is marked");
	            		    }
	            		});
	            		MenuItem unmark = new MenuItem("Unmark");
	            		unmark.setOnAction(new EventHandler<ActionEvent>() {
	            		    @Override 
	            		    public void handle(ActionEvent e) {
	            		    	setMarked(false);
	            		        System.out.println(item+" is Unmarked");
	            		    }
	            		});
	            		MenuItem comment = new MenuItem("Add Comment");
	            		comment.setOnAction(new EventHandler<ActionEvent>() {
	            		    @Override 
	            		    public void handle(ActionEvent e) {
	            		    	System.out.println(item+" set comment");
	            		    }
	            		});
	            		dropDownMenu.getItems().addAll(mark, unmark, comment);
	                    dropDownMenu.show(item, event.getScreenX(), event.getScreenY());
	                }
	            }
	        });
		}
		
	}
	//protected ContextMenu dropDownMenu;
	protected List<MarkableItem> items;
	public MarkableItems(){
		setItems(new ArrayList<MarkableItem>());
		
	}

	public void add(Node n){
		MarkableItem mi = new MarkableItem(n);
		items.add(mi);
		attachRightClickMenu(mi);
	}
	public List<Node> getMarkedItems(){
		List<Node> markedItems = new ArrayList<Node>();
		for(MarkableItem i: items){
			if(i.isMarked())
				markedItems.add(i.getItem());
		}
		return markedItems;
	}
	protected void attachRightClickMenu(MarkableItem mi){
		Node n = mi.getItem();
		
	}

	public List<MarkableItem> getItems() {
		return items;
	}

	public void setItems(List<MarkableItem> items) {
		this.items = items;
	}

}
