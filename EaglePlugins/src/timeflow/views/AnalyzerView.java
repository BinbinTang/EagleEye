package timeflow.views;

import timeflow.app.ui.EditRecordPanel;
import timeflow.data.analysis.*;
import timeflow.data.db.*;
import timeflow.data.time.*;
import timeflow.format.field.FieldFormatCatalog;
import timeflow.format.file.FileExtensionCatalog;
import timeflow.format.file.Import;
import timeflow.model.*;
import timeflow.views.*;
import timeflow.views.ListView.LinkIt;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.scene.layout.HBox;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import analyzer.IOSWhatsAppAnalyzerPlugin;
import timeflow.util.*;
import view.timeline.TimelinePlugin;
import eagleeye.api.plugin.Plugin;

public class AnalyzerView extends AbstractView {
	
	private JComponent controls;
	Image image;
	Image repeat;
	List<Plugin> analyzers;
	List<JButton> Btns;
	TFModel model;
	
	public AnalyzerView(TFModel _model, List<Plugin> _analyzers)
	{
		super(_model);
		model = _model;
		analyzers = _analyzers;
		setBackground(Color.white);
		
		//set control panel text
		StringBuilder sb = new StringBuilder();
		sb.append("Pick available analyzers <br> to start analyzing your <br> "+
				"device image. View <br> results in other tabs."+
				"<p> Available analyzers:");
		for(Plugin p: analyzers){
			sb.append("<br>"+p.getName());
		}
		controls=new HtmlControls(sb.toString());
		//makeHtml();
		
		//creat app buttons
		Btns = new ArrayList<JButton>();	
		for(Plugin p: analyzers){
			ImageIcon ic = new ImageIcon(getClass().getResource("../images/f0495561.jpg")); //TODO: use app icon
			JButton b = new JButton(p.getName(),ic);
			b.addMouseListener(new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent ev) {
					// TODO Auto-generated method stub
					System.out.println("clicked");
					Object o = p.getResult();
					if(o!=null && o.getClass().equals(String.class)){
						String outputFilePath = (String) o;
						if((new File(outputFilePath)).exists())
							loadData(outputFilePath);
						else
							System.out.println("output timeline file does not exist");
					}else{
						System.out.println("analyzer error");
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			Btns.add(b);
		}
		
	}


	public void paintComponent(Graphics g)
	{
		//set background
		g.setColor(Color.white);
		int w=getSize().width, h=getSize().height;
		g.fillRect(0,0,w,h);
		
		//set layout
		this.setLayout(new FlowLayout(FlowLayout.LEFT)); //left aligned
		/*int btnWidth = 150;
		int btnHeight = 100;
		int hGap = 50;
		int vGap = 20;
		int cols = w/(btnWidth+hGap);
		int rows = (int) Math.ceil(Btns.size()*1.0/cols);
		System.out.println(rows+" "+cols);
		this.setLayout(new GridLayout(rows, cols, hGap, vGap));*/
		
		//add buttons to view
		for(JButton b: Btns){
			b.setVerticalTextPosition(AbstractButton.BOTTOM);
			b.setHorizontalTextPosition(AbstractButton.CENTER);
			add(b);
		}
		
	}
	
	/*void makeHtml()
	{
		try
		{
			String sidebar=IO.read("settings/sidebar.html");
			controls=new HtmlControls(sidebar);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}*/
	
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
	
	@Override
	public JComponent _getControls()
	{
		return controls;
	}

	@Override
	protected void onscreen(boolean majorChange)
	{
		
	}

	protected void _note(TFEvent e) {
		// do nothing.
	}
	
	@Override
	public String getName() {
		return "Analyze";
	}

}
