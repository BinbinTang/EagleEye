package view.folderstructure;

import static org.junit.Assert.*;


import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;


import org.junit.Test;
import org.junit.experimental.categories.Category;
//import org.loadui.testfx.framework.robot.impl.FxRobotImpl;
//import org.testfx.api.FxLifecycle;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.categories.TestFX;

import eagleeye.api.plugin.MarkableItem;
import view.folderstructure.FolderStructureTreePlugin;
import view.folderstructure.FolderStructureTreePlugin.MarkableMyTreeItem;
import view.folderstructure.FolderStructureTreePlugin.MyTreeItem;

@Category(TestFX.class)
public class testMarkableItemMenu extends GuiTest{
		public MarkableMyTreeItem mt;

	    @Override
	    protected Parent getRootNode() {
	    	
	    	//create GUI components
	    	Label l = new Label("haha");
	    	FolderStructureTreePlugin pl = new FolderStructureTreePlugin();
	    	MyTreeItem<Label> item = pl.new MyTreeItem<Label>(l);
	    	TreeView<Label> tree = new TreeView<Label>(item);
	    	mt = pl.new MarkableMyTreeItem(item,tree);
	    	
	    	
	    	//set test component IDs
	    	l.setId("markableLabel");
	    	tree.setId("treeview");    	
	    	
	        return tree;
	    }
	    @Test
	    public void testRightClickShow(){
	        Label l = find("#markableLabel");
	        ContextMenu cm = mt.getDropDownMenu();
	        assertFalse(cm.isShowing());
	        rightClick(l);
	        cm = mt.getDropDownMenu();
	        ObservableList<MenuItem> ms = cm.getItems();
	        assertTrue(ms.get(0).getText().equals("Mark"));
	        assertTrue(ms.get(1).getText().equals("Edit Comment"));
	        //verifyThat("#desktop", hasChildren(0, ".file"));
	        //assertTrue(cm.isShowing());
	    }
	    
	    @Test
	    public void testMark(){
	    	String unMarkedColor = "-fx-background-color: rgba(0, 0, 0, 0);";
			String markedColor = "-fx-background-color: rgba(0, 0, 0, 0.31);";
			
	    	Label l = find("#markableLabel");
	        ContextMenu cm = mt.getDropDownMenu();
	        assertFalse(cm.isShowing());
	        rightClick(l);
	    	moveBy(10,10);
	    	click();
	    	assertTrue(l.getStyle().equals(markedColor));
	    	assertTrue(mt.isMarked());

	    	rightClick(l);
	    	moveBy(10,10);
	    	click();
	    	assertTrue(l.getStyle().equals(unMarkedColor));
	    	assertTrue(!mt.isMarked());
	    }
	    
	    @Test
	    public void testComment(){

	    	System.out.println("test comment");
	    	Label l = find("#markableLabel");
	        ContextMenu cm = mt.getDropDownMenu();
	        assertFalse(cm.isShowing());
	        rightClick(l);
	    	moveBy(10,30);
	    	click();
	    	String typedComment = "this is a test comment\n haha";
	    	type(typedComment);
	    	closeCurrentWindow();
	    	assertTrue(mt.getComment().equals(typedComment));
	    	
	    }

}
