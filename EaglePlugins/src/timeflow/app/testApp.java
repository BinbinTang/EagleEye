package timeflow.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import timeflow.app.actions.CopySchemaAction;
import timeflow.app.actions.NewDataAction;
import timeflow.app.actions.QuitAction;
import timeflow.app.ui.GlobalDisplayPanel;
import timeflow.app.ui.LinkTabPane;
import timeflow.app.ui.filter.FilterControlPanel;
import timeflow.model.Display;
import timeflow.model.TFEvent;
import timeflow.model.TFListener;
import timeflow.model.TFModel;
import timeflow.views.AbstractView;
import timeflow.views.BarGraphView;
import timeflow.views.CalendarView;
import timeflow.views.DescriptionView;
import timeflow.views.IntroView;
import timeflow.views.ListView;
import timeflow.views.SummaryView;
import timeflow.views.TableView;
import timeflow.views.TimelineView;
import timeflow.app.actions.QuitAction.*;
import timeflow.data.db.ActDB;
import timeflow.format.file.FileExtensionCatalog;
import timeflow.format.file.Import;
import timeflow.format.file.TimeflowFormat;

public class testApp extends JFrame{
	public TFModel model=new TFModel();
	public JFileChooser fileChooser;
	
	AboutWindow splash;
	String[][] examples;
	String[] templates;

	AppState state=new AppState();
	JMenu openRecent=new JMenu("Open Recent");
	public JMenu filterMenu;
	JMenuItem save=new JMenuItem("Save");
	FilterControlPanel filterControlPanel;
	LinkTabPane leftPanel;
	
	public void init() throws Exception
	{
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0,0,Math.min(d.width, 1200), Math.min(d.height, 900));
		setTitle(Display.version());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		fileChooser=new JFileChooser(state.getCurrentFile());
		getContentPane().setLayout(new BorderLayout());	

		final GlobalDisplayPanel displayPanel=new GlobalDisplayPanel(model, filterControlPanel);
		
		JMenuBar menubar=new JMenuBar();
		setJMenuBar(menubar);
		
		JMenu fileMenu=new JMenu("File");
		menubar.add(fileMenu);
		
		JMenuItem open=new JMenuItem("Open...");
		fileMenu.add(open);
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load(new TimeflowFormat(), false);
			}});
		fileMenu.addSeparator();
		
		
		// center tab area

		final LinkTabPane center=new LinkTabPane();
		getContentPane().add(center, BorderLayout.CENTER);
		
		center.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				displayPanel.showLocalControl(center.getCurrentName());
			}
		});		
		

		
		final IntroView intro=new IntroView(model); // we refer to this a bit later.
		final TimelineView timeline=new TimelineView(model);
		AbstractView[] views={
				timeline,
				new CalendarView(model),
				new ListView(model),
				new TableView(model),
				new BarGraphView(model),
				intro,
				new DescriptionView(model),
				new SummaryView(model),
		};

		for (int i=0; i<views.length; i++)
		{
			center.addTab(views[i], views[i].getName(), i<5);
			displayPanel.addLocalControl(views[i].getName(), views[i].getControls());
		}

		model.addListener(new TFListener() {
			@Override
			public void note(TFEvent e) {
				if (e.type==e.type.DATABASE_CHANGE)
				{
					if (center.getCurrentName().equals(intro.getName()))
					{
						center.setCurrentName(timeline.getName());
						displayPanel.showLocalControl(timeline.getName());
					}
				}
			}});
		this.addKeyListener(new KeyListener(){
			@Override
			public void keyTyped(KeyEvent e) {
				 System.out.println(e.getKeyCode()+" key typed");
				 if(e.getKeyCode()==KeyEvent.VK_O){
					 System.out.println("Key Stroke O");
				 }
				 
				 String testFile="testdata/monet.time";
	    	     load(testFile, FileExtensionCatalog.get(testFile), false);
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				//System.out.println("key pressed");
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				//System.out.println("key released");
			}
		});
	}
	public void noteFileUse(String file)
	{
		System.out.println("ok");
		state.setCurrentFile(new File(file));
		state.save();
		//makeRecentFileMenu();
		System.out.println("ok");
	}
	void load(Import importer, boolean readOnly)
	{
		if (!checkSaveStatus())
			return;
        try {
    	    //int retval = fileChooser.showOpenDialog(this);
    	    //if (retval == fileChooser.APPROVE_OPTION)
    	    {
    	    	String testFile="testdata/monet.time";
    	    	load(testFile, importer, readOnly);
    	    	//load(fileChooser.getSelectedFile().getAbsolutePath(), importer, readOnly);
    	    	//noteFileUse(fileChooser.getSelectedFile().getAbsolutePath());
     	    }
        } catch (Exception e) {
        	//showUserError("Couldn't read file.");
            System.out.println(e);
        }
	}
	void load(final String fileName, final Import importer, boolean readOnly)
	{
		System.out.println(fileName);
		if (!checkSaveStatus())
			return;
		try
		{
			final File f=new File(fileName);
			ActDB db=importer.importFile(f);	
			model.setDB(db, fileName, readOnly, testApp.this);
			if (!readOnly)
				noteFileUse(fileName);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			//showUserError("Couldn't read file.");
			model.noteError(this);
		}
	}
	public boolean checkSaveStatus()
	{
		boolean needSave=model.isChangedSinceSave();
		if (!needSave)
			return true;

		Object[] options=null;
		if (model.isReadOnly())
			options= new Object[] {"Save As", "Discard Changes", "Cancel"};
		else
			options= new Object[] {"Save", "Save As", "Discard Changes", "Cancel"};
		int n = JOptionPane.showOptionDialog(
				this, 
		    "The current data set has unsaved changes that will be lost.\n"+
		    "Would you like to save them before continuing?",
		    "Save Before Closing?",
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    model.isReadOnly() ? "Save As" : "Save");
		Object result=options[n];
		if ("Discard Changes".equals(result))
			return true;
		if ("Cancel".equals(result))
			return false;
		if ("Save".equals(result))
		{
			return save(model.getDbFile());
		}
		
		// we are now at "save as..."
		return saveAs();
	}
	public boolean save(String fileName)
	{
		try
		{
			FileWriter fw=new FileWriter(fileName);
			BufferedWriter out=new BufferedWriter(fw);
			new TimeflowFormat().export(model, out);
			out.close();
			fw.close();
			noteFileUse(fileName);
			if (!fileName.equals(model.getDbFile()))
				model.setDbFile(fileName, false, this);
			model.setChangedSinceSave(false);
			model.setReadOnly(false);
			save.setEnabled(true);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			//showUserError("Couldn't save file: "+e);
			return false;
		}
	}
	public boolean saveAs()
	{
		File current=fileChooser.getSelectedFile();
		if (current!=null)
			fileChooser.setSelectedFile(new File(current.getAbsolutePath()+" (copy)"));
		int retval = fileChooser.showSaveDialog(this);
	    if (retval == fileChooser.APPROVE_OPTION)
	    {   	    	
	    	String fileName=fileChooser.getSelectedFile().getAbsolutePath();
	    	model.setReadOnly(false);
	    	save.setEnabled(true);
	    	return save(fileName);
	    }
	    else
	    	return false;
 	}
	
}
