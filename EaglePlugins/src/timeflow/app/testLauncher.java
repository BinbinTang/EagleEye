package timeflow.app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import timeflow.model.Display;

public class testLauncher {
	public static void main(String[] args) throws Exception
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "TimeFlow");
		System.out.println("Running "+Display.version());
		
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (Exception e) {
	       System.out.println("Can't set system look & feel");
	    }		
		
		final testApp t=new testApp();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try
				{
					t.init();
					t.setVisible(true);				
				}
				catch (Exception e)
				{
					e.printStackTrace(System.out);
				}
				/*t.splash.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent e) {
						t.splash.setVisible(false);
					}}
				);
				t.splash(false);*/
				//t.splash.message=t.model.getDisplay().version();
			}});
		
	}
}
