package view.timeline;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import timeflow.app.ui.LinkTabPane;
import timeflow.data.db.ActDB;
import timeflow.format.file.FileExtensionCatalog;
import timeflow.format.file.Import;
import timeflow.model.TFModel;
import timeflow.views.AbstractView;
import timeflow.views.BarGraphView;
import timeflow.views.CalendarView;
import timeflow.views.DescriptionView;
import timeflow.views.SummaryView;
import timeflow.views.TableView;
import timeflow.views.TimelineView;
import eagleeye.pluginmanager.Plugin;

public class TimelinePlugin extends Application implements Plugin{
	//timeline view attributes
	public TFModel model;
	private  SwingNode timelineNode;
	
	public TimelinePlugin(){
		model = new TFModel();
		timelineNode = new SwingNode();
		initTimelineView();
	}
	
	public void initTimelineView(){
		LinkTabPane center=new LinkTabPane();
		TimelineView timeline=new TimelineView(model);
		AbstractView[] views={
				timeline,
				new CalendarView(model),
				new timeflow.views.ListView(model),
				new TableView(model),
				new BarGraphView(model),
				new DescriptionView(model),
				new SummaryView(model),
		};
		for (int i=0; i<views.length; i++)
		{
			center.addTab(views[i], views[i].getName(), i<5);
		}	
		timelineNode.setContent(center);
	}
	public void loadData(String data){
		data="testdata/data.time";
		load(data, FileExtensionCatalog.get(data), false);
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

	@Override
	public String getName() {
		return "Time Line";
	}

	@Override
	public Object getResult() {
		loadData("");
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
	public int setParameter(List arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

	@Override
	public void start(Stage stage) throws Exception {
		TimelinePlugin tp = new TimelinePlugin();
		Node view = (Node)tp.getResult();
		ScrollPane sp = new ScrollPane();
		sp.setContent(view);
		stage.setTitle(this.getName());
	    stage.setScene(new Scene(sp)); 
	    stage.setWidth(610);
        stage.setHeight(480);
	    stage.show();
		
	}
	public static void main(String[] args){
		launch(args); 
	}
}
