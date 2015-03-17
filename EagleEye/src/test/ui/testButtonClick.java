package test.ui;

import javafx.scene.Parent;

import javafx.scene.control.Button;



//import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
//import org.loadui.testfx.framework.robot.impl.FxRobotImpl;
//import org.testfx.api.FxLifecycle;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.categories.TestFX;


@Category(TestFX.class)
public class testButtonClick extends GuiTest {
    @Override
    protected Parent getRootNode() {
        final Button btn = new Button();
        btn.setId("btn");
        btn.setText("Hello World");
        btn.setOnAction((actionEvent)-> btn.setText( "was clicked" ));
        return btn;
    }
    
    @Test
    public void shouldClickButton(){
        final Button button = find( "#btn" );
        click(button);
        //verifyThat( "#btn", hasText("was clicked") );
    }
    
}

/*
public class testButtonClick extends GuiTest{

	@Override
	protected Parent getRootNode() {
		return new Button();
	}
	
	@Test
	public void clickSuccessfully()
	{
		click(".text-field").type("steve");
		//assertNodeExists("");
	}
}
*/