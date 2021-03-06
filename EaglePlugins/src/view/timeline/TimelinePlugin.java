package view.timeline;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import analyzer.*;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import reader.SQLiteReaderPlugin;
import tempDBcontroller.DBQueryController;
import timeflow.app.actions.AddFieldAction;
import timeflow.app.actions.AddRecordAction;
import timeflow.app.actions.CopySchemaAction;
import timeflow.app.actions.DateFieldAction;
import timeflow.app.actions.DeleteFieldAction;
import timeflow.app.actions.DeleteSelectedAction;
import timeflow.app.actions.DeleteUnselectedAction;
import timeflow.app.actions.EditSourceAction;
import timeflow.app.actions.ImportFromPasteAction;
import timeflow.app.actions.NewDataAction;
import timeflow.app.actions.QuitAction;
import timeflow.app.actions.RenameFieldAction;
import timeflow.app.actions.ReorderFieldsAction;
import timeflow.app.actions.WebDocAction;
import timeflow.app.ui.ColorLegendPanel;
import timeflow.app.ui.GlobalDisplayPanel;
import timeflow.app.ui.LinkTabPane;
import timeflow.app.ui.SizeLegendPanel;
import timeflow.app.ui.filter.FilterControlPanel;
import timeflow.data.db.ActDB;
import timeflow.format.file.FileExtensionCatalog;
import timeflow.format.file.Import;
import timeflow.format.file.TimeflowFormat;
import timeflow.model.Display;
import timeflow.model.TFEvent;
import timeflow.model.TFListener;
import timeflow.model.TFModel;
import timeflow.util.Pad;
import timeflow.views.AbstractView;
import timeflow.views.AnalyzerView;
import timeflow.views.BarGraphView;
import timeflow.views.CalendarView;
import timeflow.views.DescriptionView;
import timeflow.views.IntroView;
import timeflow.views.ListView;
import timeflow.views.SummaryView;
import timeflow.views.TableView;
import timeflow.views.TimelineView;
import eagleeye.api.dbcontroller.DBController;
import eagleeye.api.plugin.Plugin;

public class TimelinePlugin extends Application implements Plugin{
	//timeline view attributes
	public TFModel model;
	private  SwingNode timelineNode;
	private List<Plugin> analyzers;
	private List<Plugin> showAnalyzers;
	public TimelinePlugin(){
		model = new TFModel();
		timelineNode = new SwingNode();
		analyzers = new ArrayList<Plugin>();
	}
	public void initTimelineView(){
		JRootPane jf = new JRootPane();

		// left tab area, with vertical gray divider.
		jf.setLayout(new BorderLayout());	
		
		JPanel rightHolder=new JPanel();
		jf.add(rightHolder, BorderLayout.EAST);
		
		rightHolder.setLayout(new BorderLayout());
		JPanel pad=new Pad(3,3);
		pad.setBackground(Color.gray);
		rightHolder.add(pad, BorderLayout.WEST);
		
		LinkTabPane rightPanel=new LinkTabPane();//JTabbedPane();
		rightHolder.add(rightPanel, BorderLayout.CENTER);
		
		JPanel configPanel=new JPanel();
		configPanel.setLayout(new BorderLayout());		
		JMenu filterMenu=new JMenu("Filters");
		FilterControlPanel filterControlPanel=new FilterControlPanel(model, filterMenu);
		final GlobalDisplayPanel displayPanel=new GlobalDisplayPanel(model, filterControlPanel);
		configPanel.add(displayPanel, BorderLayout.NORTH);
		
		JPanel legend=new JPanel();
		legend.setLayout(new BorderLayout());
		configPanel.add(legend, BorderLayout.CENTER);	
		legend.add(new SizeLegendPanel(model), BorderLayout.NORTH);
		legend.add(new ColorLegendPanel(model), BorderLayout.CENTER);		
		rightPanel.addTab(configPanel, "Display", true);

		rightPanel.addTab(filterControlPanel, "Filter", true);
		
		//Timeline view
		LinkTabPane center=new LinkTabPane();
		jf.add(center, BorderLayout.CENTER);
		
		TimelineView timeline=new TimelineView(model);
		AnalyzerView startView = new AnalyzerView(model, showAnalyzers);
		
		//create tab list	
		AbstractView[] views={
				startView,
				timeline,
				new CalendarView(model),
				//new timeflow.views.ListView(model),
				new TableView(model),
				new BarGraphView(model),
				//new DescriptionView(model),
				new SummaryView(model),
		};
		for (int i=0; i<views.length; i++)
		{
			//add tabs
			center.addTab(views[i], views[i].getName(), i<6);
			//add local controls
			displayPanel.addLocalControl(views[i].getName(), views[i].getControls());
		}
		//show local controls listener
		center.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				displayPanel.showLocalControl(center.getCurrentName());
			}
		});	
		
		// start off with startView
		center.setCurrentName(startView.getName());
		displayPanel.showLocalControl(startView.getName());
				
		// but then, once data is loaded, switch directly to the timeline view.
		model.addListener(new TFListener() {
			@Override
			public void note(TFEvent e) {
				if (e.type==e.type.DATABASE_CHANGE)
				{
					if (center.getCurrentName().equals(startView.getName()))
					{
						center.setCurrentName(timeline.getName());
						displayPanel.showLocalControl(timeline.getName());
					}
				}
			}
		});
		
		//displayPanel.showLocalControl(timeline.getName());
		timelineNode.setContent(jf);
	}
	
	//No Right Panel version
	/*
	public void initTimelineView(){

		//Timeline view
		LinkTabPane center=new LinkTabPane();
		TimelineView timeline=new TimelineView(model);
		AbstractView[] views={
				timeline,
				new CalendarView(model),
				//new timeflow.views.ListView(model),
				new TableView(model),
				new BarGraphView(model),
				//new DescriptionView(model),
				new SummaryView(model),
		};
		for (int i=0; i<views.length; i++)
		{
			center.addTab(views[i], views[i].getName(), i<5);
		}	
		timelineNode.setContent(center);
	}
	*/
	
	/*
	 * For Data loading. Now moved to AnalyzerView instead.
	 * 
	public void loadData(String data){
		load(data, FileExtensionCatalog.get(data), false); //set read only
	}
	
	void load(final String fileName, final Import importer, boolean readOnly)
	{
		try
		{
			final File f=new File(fileName);
			ActDB db=importer.importFile(f);	
			model.setDB(db, fileName, readOnly, this);

		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			//showUserError("Couldn't read file.");
			model.noteError(this);
		}
	}
	*/
	

	@Override
	public String getName() {
		return "Time Line";
	}

	@Override
	public Object getResult() {
		//loadData((dataFilePath==null)?"testdata/data.time":dataFilePath);
		return timelineNode;
	}

	@Override
	public Type getType() {
		return Type.GUI_VIEW;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int setParameter(List params) {
		showAnalyzers = new ArrayList<Plugin>();
		if(params.size()!=1){
			return 1;
		}	
		Object p = params.get(0);
		Class[] interfaces = p.getClass().getInterfaces();
		if(interfaces[0].equals(DBController.class)){
			DBController dbc = (DBController) p;
			String deviceRoot = dbc.getDeviceRootPath();
			String [] tokens = deviceRoot.split("\\"+File.separator);
			String outputPath = "analysis"+File.separator+tokens[tokens.length-1];
			File f = new File(outputPath);
			if (!f.exists()) {
			    System.out.println("creating directory: " + outputPath);
			    try{
			    	f.mkdir();
			        System.out.println("Created directory: "+ outputPath);
			    } catch(SecurityException se){
			    	System.out.println("Failed to create directory: "+ outputPath);
			    }
			}
			
			List params2 = new ArrayList();
			params2.add(deviceRoot+File.separator+"mtd8.dd"+File.separator+"mtd8.dd");
			params2.add(outputPath);
			for(int i=0; i<analyzers.size(); i++){
				if(analyzers.get(i).setParameter(params2)==0){
					showAnalyzers.add(analyzers.get(i));
				}
			}
			initTimelineView();
			return 0;
		}else{
			System.out.println("ERROR: ["+getName()+"] cannot recognize input parameters");
		}
		return 1;
		
	}
	
	@Override
	public int setAvailablePlugins(List<Plugin> pls) {
		analyzers = pls;
		return 0;
	}
	
	@Override
	public Object getMarkedItems() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setMarkedItems(Object arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/***********************start of test ************************/
	@Override
	public void start(Stage stage) throws Exception {
		TimelinePlugin tp = new TimelinePlugin();
		List<Plugin> pls = new ArrayList<Plugin>();
		pls.add(tp);
		//pls.add(new IOSWhatsAppAnalyzerPlugin());
		//pls.add(new TestAnalyzerPlugin());
		pls.add(new SQLiteReaderPlugin());
		//pls.add(new IOSCalendarAnalyzerPlugin());
		pls.add(new AndroidCalendarAnalyzerPlugin());
		//pls.add(new AndroidGmailAnalyzerPlugin());
		for(Plugin pl: pls){
			pl.setAvailablePlugins(pls);
		}
		
		DBController dbc = new DBQueryController();
		dbc.setDeviceID(3);
		List params = new ArrayList();
		params.add(dbc);
		//params.add(".."+File.separator+".."+File.separator+".."+File.separator+"device_ios");
		tp.setParameter(params);
		
		Node view = (Node)tp.getResult();
		StackPane sp = new  StackPane();
		sp.getChildren().add(view);
		stage.setTitle(this.getName());
	    stage.setScene(new Scene(sp)); 
	    stage.setWidth(1200);
        stage.setHeight(900);
	    stage.show();
		
	}
	public static void main(String[] args){
		launch(args); 
	}
	/***********************end of test ************************/
	
}
