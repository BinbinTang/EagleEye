package view.locationhistory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import reader.SQLiteReaderPlugin;
import analyzer.AndroidCalendarAnalyzerPlugin;
import analyzer.AndroidGmailAnalyzerPlugin;
import analyzer.AndroidLocationAnalyzerPlugin;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import eagleeye.api.entities.EagleLocation;
import eagleeye.pluginmanager.Plugin;

public class MapPlugin extends Application implements Plugin,MapComponentInitializedListener{
	private GoogleMapView mapView;
	private GoogleMap map;
	private List<Plugin> analyzers;
	private String deviceType;
	List<List<String>> geoPoints;
	public MapPlugin(){
		
	}

	@Override
	public String getName() {
		return "Location History";
	}

	@Override
	public Object getResult() {
		geoPoints= new ArrayList<List<String>>();
		for(Plugin i: analyzers){
			if(i.getName().equalsIgnoreCase("Android Location Analyzer")){
				geoPoints  = (List<List<String>>) i.getResult();
			}
		}
		
		for(List<String> pt: geoPoints){
		    	System.out.println(Double.parseDouble(pt.get(1))+","+Double.parseDouble(pt.get(2))+","+pt.get(3));
		    	//markLocations(Double.parseDouble(geoPoints.get(0).get(1)),Double.parseDouble(geoPoints.get(0).get(2)),geoPoints.get(0).get(3));
		}
		if(geoPoints.size()==0){
			System.out.println("no data");
		}
		
		mapView = new GoogleMapView();
	    mapView.addMapInializedListener(this);
		return mapView;
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
	public void mapInitialized() {
		if(geoPoints.size()==0) return;
		
		//Set the initial properties of the map.
	    MapOptions mapOptions = new MapOptions();
	    
	    mapOptions.center(new LatLong(Double.parseDouble(geoPoints.get(0).get(1)), Double.parseDouble(geoPoints.get(0).get(2))))
	            .mapType(MapTypeIdEnum.ROADMAP)
	            .overviewMapControl(false)
	            .panControl(false)
	            .rotateControl(false)
	            .scaleControl(false)
	            .streetViewControl(false)
	            .zoomControl(false)
	            .zoom(12);

	    map = mapView.createMap(mapOptions);
	    //Add markers to the map

	    for(List<String> pt: geoPoints){
	    	System.out.println(Double.parseDouble(pt.get(1))+","+Double.parseDouble(pt.get(2))+","+pt.get(3));
	    	markLocations(Double.parseDouble(pt.get(1)),Double.parseDouble(pt.get(2)),pt.get(3));
	    }
	}
	public void markLocations(double lat, double longit, String description){
		MarkerOptions markerOptions = new MarkerOptions();
		
	    markerOptions.position( new LatLong(lat, longit))
	                .visible(Boolean.TRUE)
	                .title(description);

	    Marker marker = new Marker( markerOptions );
	    map.addMarker(marker);
	}
	@Override
	public int setParameter(List params) {
		String deviceRoot = (String)params.get(0);
		deviceType = (String) params.get(1);
		if(deviceType.equalsIgnoreCase("android")){
			for(Plugin i:analyzers){
				if(i.getName().equalsIgnoreCase("Android Location Analyzer")){
					List<String> params2 = new ArrayList<String>();
					params2.add(deviceRoot);
					i.setParameter(params);
				}
			}
		}
		
		return 0;
	}
	@Override
	public int setAvailablePlugins(List<Plugin> pls) {
		analyzers = new ArrayList<Plugin>();
		for(Plugin pl: pls){
			if(pl.getName().endsWith("Location Analyzer")){
				System.out.println(this.getName()+": "+pl.getName()+" added");
				analyzers.add(pl);
			}
		}
		return 0;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		MapPlugin tp = new MapPlugin();
		
		List<Plugin> pls = new ArrayList<Plugin>();
		pls.add(tp);
		pls.add(new AndroidLocationAnalyzerPlugin());
		pls.add(new AndroidCalendarAnalyzerPlugin());
		pls.add(new AndroidGmailAnalyzerPlugin());
		pls.add(new SQLiteReaderPlugin());
		for(Plugin pl: pls){
			pl.setAvailablePlugins(pls);
		}

	
		List params = new ArrayList();
		params.add(".."+File.separator+"EagleEye"+File.separator+"output"+File.separator+"mtd8.dd"+File.separator+"mtd8.dd");
		//params.add(".."+File.separator+".."+File.separator+".."+File.separator+"device_ios");
		params.add("android");
		tp.setParameter(params);
		
		Node view = (Node)tp.getResult();
		BorderPane bp = new BorderPane();
		bp.setCenter(view);
		stage.setTitle(this.getName());
	    stage.setScene(new Scene(bp)); 
	    stage.show();	
	}
	public static void main(String[] args){
		launch(args);
	}
}
