package view.image;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import eagleeye.pluginmanager.Plugin;



public class ImageViewPlugin extends Application implements Plugin{
	private class CustomImageView{
		//private File img;
		private Image im;
		private ImageView iv;
		
		/*window params*/
		private double ww;
		private double wh;
		private double centerLeftOffset;	//offset between image center & origin at start
		private double centerTopOffset; 	//offset between image center & origin at start
		
		/*scroll params*/
		private double scrollSteps; 	//track accumulated wheel scrolls
		private double scalingFactor;	//multiplier to scrollSteps to obtain image scaling
		private double minScale;		//1.0 if image smaller than window, else <1.0 and fit to window
		private double imgOW;			//record original image width
		private double imgOH;			//record original image height
		
		/*pan params*/
		private boolean isPanning;
		private double imgPosX;
		private double imgPosY;
		private double startPosX;
		private double startPosY;
		
		private CustomImageView(String imgPath, double ww, double wh){
			scrollSteps = 0;	//init to no scroll
			scalingFactor = 0.2;//can be customized
			isPanning = false;
			iv = new ImageView();
			this.ww = ww;
			this.wh = wh;
			centerLeftOffset = ww/2.0;	
			centerTopOffset = wh/2.0; 	
			File img = new File (imgPath);
			try {
				im = new Image(img.toURI().toURL().toString(),false);	//false: not load in background so that imgOW/OH is defined
				imgOW = im.getWidth();
				imgOH = im.getHeight();
				System.out.println(imgOW+" "+imgOH);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		private void update(){
			
			double imgW = imgOW*(minScale+scalingFactor*scrollSteps);
			double imgH = imgOH*(minScale+scalingFactor*scrollSteps);
			if(imgW>ww || imgH>wh){
				isPanning =true;
				if(imgW>ww){
					double leftBleed = iv.getX()+centerLeftOffset-imgW/2.0;
					if (leftBleed>0) iv.setX(imgW/2.0-centerLeftOffset);
					double rightBleed = ww-(iv.getX()+centerLeftOffset+imgW/2.0);
					if (rightBleed>0) iv.setX(ww-imgW/2.0-centerLeftOffset);
				}else{
					iv.setX(0.0);
				}
				if(imgH>wh){
					double topBleed = iv.getY()+centerTopOffset-imgH/2.0;
					if (topBleed>0) iv.setY(imgH/2.0-centerTopOffset);
					double bottomBleed = wh - (iv.getY()+centerTopOffset+imgH/2.0);
					if (bottomBleed>0) iv.setY(wh-imgH/2.0-centerTopOffset);
				}else{
					iv.setY(0.0);
				}
			}else{
				isPanning = false;
				iv.setX(0.0);
				iv.setY(0.0);
			}

			iv.setTranslateX(iv.getX());
			iv.setTranslateY(iv.getY());
		}
		private Node initView(){
			//set event handlers
			iv.setOnScroll(new EventHandler<ScrollEvent>(){
				@Override
				public void handle(ScrollEvent ev) {
					scrollSteps += ev.getDeltaY()/ev.getMultiplierY();
					if (scrollSteps<0.0) scrollSteps=0.0; //IMPORTANT: impose minimum scale
					iv.setScaleX(minScale+scalingFactor*scrollSteps);
					iv.setScaleY(minScale+scalingFactor*scrollSteps);
					update();
				}			
			});
			iv.setOnMousePressed(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent ev) {
					//get start params
					imgPosX = iv.getX();
					imgPosY = iv.getY();
					startPosX = ev.getSceneX();
					startPosY = ev.getSceneY();
				}
			});
			iv.setOnMouseDragged(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent ev) {
					if(isPanning){
						double transX = ev.getSceneX() - startPosX;
						double transY = ev.getSceneY() - startPosY;
						iv.setX(imgPosX+transX); //IMPORTANT: update image origin X
						iv.setY(imgPosY+transY); //IMPORTANT: update image origin Y
						update();
					}
				}
			});

			//set image
			iv.setImage(im);
			System.out.println(ww+" "+wh);
			double ratioW = ww/imgOW;
			double ratioH = wh/imgOH;
			if(ratioW <1 || ratioH <1){
				if(ratioW<ratioH){
					iv.setScaleX(ratioW);
					iv.setScaleY(ratioW);
					minScale=ratioW;

				}else{
					iv.setScaleX(ratioH);
					iv.setScaleY(ratioH);
					minScale=ratioH;
				}
			}else{
				minScale = 1.0;
			}
			update();

			return iv;
		}
	}
	private CustomImageView civ;
	
	public ImageViewPlugin(){
		
	}

	@Override
	public Object getResult() {
		return civ.initView();
	}
	
	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int setParameter(List argList) {
		
		String imgPath = (String)argList.get(0);
		double windowWidth = (double)argList.get(1);
		double windowHeight = (double)argList.get(2);
		civ = new CustomImageView(imgPath,windowWidth,windowHeight);
		return 0;
	}
	
	@Override
	public Type getType() {
		return Type.GUI_POPUP;
	}
	
	@Override
	public String getName() {
		return "Image View";
	}
	
	/******************For Test**********************/
	@Override 
	public void start(Stage stage) {	
		//params
		//String inputImgPath = "C:/Users/Public/Pictures/Sample Pictures/Chrysanthemum.jpg";
		//String inputImgPath = ".."+File.separator+"EagleEye"+File.separator+"output2"+File.separator+"JPG"+File.separator+"f0495561.jpg";
		String inputImgPath = ".."+File.separator+"EagleEye"+File.separator+"output2"+File.separator+"PNG"+File.separator+"f0048755.png";
		double WindowWidth = 600;
		double WindowHeight = 400;
		double hBorder = 600-584.0;
		double vBorder = 400-362.0;	//TODO: automate to be platform-independent
		
		//set plugin input params
		List params = new ArrayList();
		params.add(inputImgPath);
		params.add(WindowWidth-hBorder);
		params.add(WindowHeight-vBorder);
		setParameter (params);
		
		//get result
		Node pc = (Node)getResult();
		
		//use result
		StackPane sp = new StackPane();
		sp.getChildren().add(pc);
		
		stage.setScene(new Scene(sp));
		stage.setTitle(new File(inputImgPath).getName());
		stage.setWidth(WindowWidth);
		stage.setHeight(WindowHeight);
		stage.show();
		
		System.out.println(sp.getWidth()+" "+ sp.getHeight());
	}
	public static void main(String[] args) { 
		launch(args); 
	}
	/*************************************************/

	@Override
	public int setAvailablePlugins(List<Plugin> arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}