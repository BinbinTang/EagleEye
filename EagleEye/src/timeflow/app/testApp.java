package timeflow.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

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
		/*final QuitAction quitAction=new QuitAction(this, model);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quitAction.quit();
			}
			public void windowStateChanged(WindowEvent e) {
				repaint();
			}
		});		
		/*Image icon = Toolkit.getDefaultToolkit().getImage("images/icon.gif");
		setIconImage(icon);

		// read example directory
		String[] ex=getVisibleFiles("settings/examples");
		int n=ex.length;
		examples=new String[n][2];
		for (int i=0; i<n; i++)
		{
			String s=ex[i];
			int dot=s.lastIndexOf('.');
			if (dot>=0 && dot<s.length()-1);
				s=s.substring(0,dot);
			examples[i][0]=s;
			examples[i][1]="settings/examples/"+ex[i];
		}
		templates=getVisibleFiles("settings/templates");
		fileChooser=new JFileChooser(state.getCurrentFile());
		*/
		getContentPane().setLayout(new BorderLayout());	
		/*
		// left tab area, with vertical gray divider.
		JPanel leftHolder=new JPanel();
		getContentPane().add(leftHolder, BorderLayout.WEST);
		
		leftHolder.setLayout(new BorderLayout());
		JPanel pad=new Pad(3,3);
		pad.setBackground(Color.gray);
		leftHolder.add(pad, BorderLayout.EAST);
		
		leftPanel=new LinkTabPane();//JTabbedPane();
		leftHolder.add(leftPanel, BorderLayout.CENTER);
		
		JPanel configPanel=new JPanel();
		configPanel.setLayout(new BorderLayout());		
		filterMenu=new JMenu("Filters");
		filterControlPanel=new FilterControlPanel(model, filterMenu);*/
		final GlobalDisplayPanel displayPanel=new GlobalDisplayPanel(model, filterControlPanel);
		/*configPanel.add(displayPanel, BorderLayout.NORTH);
		
		JPanel legend=new JPanel();
		legend.setLayout(new BorderLayout());
		configPanel.add(legend, BorderLayout.CENTER);	
		legend.add(new SizeLegendPanel(model), BorderLayout.NORTH);
		legend.add(new ColorLegendPanel(model), BorderLayout.CENTER);		
		leftPanel.addTab(configPanel, "Display", true);

		leftPanel.addTab(filterControlPanel, "Filter", true);
		*/
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
		/*
		// start off with intro screen
		center.setCurrentName(intro.getName());
		displayPanel.showLocalControl(intro.getName());
		*/
		// but then, once data is loaded, switch directly to the timeline view.
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
/*
		JMenuBar menubar=new JMenuBar();
		setJMenuBar(menubar);
		
		JMenu fileMenu=new JMenu("File");
		menubar.add(fileMenu);
			
		fileMenu.add(new NewDataAction(this));
		fileMenu.add(new CopySchemaAction(this));
		
		JMenu templateMenu=new JMenu("New From Template");
		fileMenu.add(templateMenu);
		for (int i=0; i<templates.length; i++)
		{
			JMenuItem t=new JMenuItem(templates[i]);
			final String fileName="settings/templates/"+templates[i];
			templateMenu.add(t);
			t.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					load(fileName, FileExtensionCatalog.get(fileName), true);
				}});
		}
		
		fileMenu.addSeparator();

		
		JMenuItem open=new JMenuItem("Open...");
		fileMenu.add(open);
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				load(new TimeflowFormat(), false);
			}});
		
		
		fileMenu.add(openRecent);
		makeRecentFileMenu();		
		fileMenu.addSeparator();		
		fileMenu.add(new ImportFromPasteAction(this));
		
		JMenuItem impDel=new JMenuItem("Import CSV/TSV...");
		fileMenu.add(impDel);
		impDel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkSaveStatus())
					importDelimited();
			}});

		fileMenu.addSeparator();
		
		fileMenu.add(save);
		save.setAccelerator(KeyStroke.getKeyStroke('S',
			    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		save.setEnabled(false);
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save(model.getDbFile());
				
			}});
		model.addListener(new TFListener() {
			@Override
			public void note(TFEvent e) {
				save.setEnabled(!model.getReadOnly());
			}});
		
		JMenuItem saveAs=new JMenuItem("Save As...");
		fileMenu.add(saveAs);
		saveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}});	
		
		fileMenu.addSeparator();
		
		JMenuItem exportTSV=new JMenuItem("Export TSV...");
		fileMenu.add(exportTSV);
		exportTSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportDelimited('\t');
			}});			
		JMenuItem exportCSV=new JMenuItem("Export CSV...");
		fileMenu.add(exportCSV);
		exportCSV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportDelimited(',');
			}});	
		JMenuItem exportHTML=new JMenuItem("Export HTML...");
		fileMenu.add(exportHTML);
		exportHTML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportHtml();
			}});	
		fileMenu.addSeparator();		
		//fileMenu.add(quitAction);
		
		JMenu editMenu=new JMenu("Edit");
		menubar.add(editMenu);
		editMenu.add(new AddRecordAction(this));
		editMenu.addSeparator();
		editMenu.add(new DateFieldAction(this));
		editMenu.add(new AddFieldAction(this));
		editMenu.add(new RenameFieldAction(this));
		editMenu.add(new DeleteFieldAction(this));
		editMenu.add(new ReorderFieldsAction(this));
		editMenu.addSeparator();
		editMenu.add(new EditSourceAction(this));		
		editMenu.addSeparator();		
		editMenu.add(new DeleteSelectedAction(this));
		editMenu.add(new DeleteUnselectedAction(this));

		menubar.add(filterMenu);
		model.addListener(filterMenuMaker);

	
		JMenu exampleMenu=new JMenu("Examples");
		menubar.add(exampleMenu);
		
		for (int i=0; i<examples.length; i++)
		{
			JMenuItem example=new JMenuItem(examples[i][0]);
			exampleMenu.add(example);
			final String file=examples[i][1];
			example.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					load(file, FileExtensionCatalog.get(file), true);
				}});
		}
		
		JMenu helpMenu=new JMenu("Help");
		menubar.add(helpMenu);
		
		helpMenu.add(new WebDocAction(this));
		
		JMenuItem about=new JMenuItem("About TimeFlow");
		helpMenu.add(about);
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				splash(true);
			}});
		
		model.addListener(new TFListener() {

			@Override
			public void note(TFEvent e) {
				if (e.type==TFEvent.Type.DATABASE_CHANGE)
				{
					String name=model.getDbFile();
					int n=Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
					if (n>0)
						name=name.substring(n+1);
					setTitle(name);
				}
			}});*/
	}
}
