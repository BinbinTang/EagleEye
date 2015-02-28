package view.locationhistory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import netscape.javascript.JSObject;
import reader.SQLiteReaderPlugin;
import tempDBcontroller.DBQueryController;
import analyzer.AndroidCalendarAnalyzerPlugin;
import analyzer.AndroidGmailAnalyzerPlugin;
import analyzer.AndroidLocationAnalyzerPlugin;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
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
import eagleeye.api.plugin.Plugin;
import eagleeye.api.dbcontroller.*;

//import db.DBQueryController;

public class LocationHistoryPlugin extends Application implements Plugin,MapComponentInitializedListener{
	public class GeoPoint{
		public double lat;
		public double longit;
		public String time;
		public String comment;
		public GeoPoint(){
			lat=0.0;
			longit=0.0;
			time="";
			comment="";
		}
	}
	private GoogleMapView mapView;
	private GoogleMap map;
	private List<Plugin> analyzers;
	private int chosenAnalyzerIdx;
	List<GeoPoint> geoPoints;
	List<Integer> markedPtIdx;
	public LocationHistoryPlugin(){
		
	}

	@Override
	public String getName() {
		return "Location History";
	}

	@Override
	public Object getResult() {
		if(chosenAnalyzerIdx ==-1){
			geoPoints = new ArrayList<GeoPoint>();
		}else{
			markedPtIdx = new ArrayList<Integer>();
			geoPoints = new ArrayList<GeoPoint>();
			
			List<List<String>> result  = (List<List<String>>) analyzers.get(chosenAnalyzerIdx).getResult();
			for(List<String> pt: result){
				GeoPoint gp = new GeoPoint();
				gp.lat = Double.parseDouble(pt.get(1));
				gp.longit = Double.parseDouble(pt.get(2));
				gp.time = pt.get(3);
				geoPoints.add(gp);
		    }
			
			
		}
		//for(List<String> pt: geoPoints){
		//    	System.out.println(Double.parseDouble(pt.get(1))+","+Double.parseDouble(pt.get(2))+","+pt.get(3));
		//}
		
		if(geoPoints.size()==0){
			System.out.println("no data");
			return null;
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
	    
	    mapOptions.center(new LatLong(geoPoints.get(0).lat, geoPoints.get(0).longit))
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

	    for(int i=0; i<geoPoints.size(); i++){
	    	System.out.println(geoPoints.get(i).lat+","+geoPoints.get(i).longit+","+geoPoints.get(i).time);
	    	markLocations(i, geoPoints.get(i).lat,geoPoints.get(i).longit,geoPoints.get(i).time);
	    }
	}
	public void markLocations(int ptID, double lat, double longit, String description){
		
		
		MarkerOptions markerOptions = new MarkerOptions();
		
	    markerOptions.position( new LatLong(lat, longit))
	                .visible(Boolean.TRUE)
	                .title(description);

	    Marker marker = new Marker( markerOptions );
	    marker.setID(ptID);
	    
	    map.addMarker(marker);
	    
	    map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
	    	String [] iconURLs ={
					"http://mt.googleapis.com/vt/icon/name=icons/spotlight/spotlight-poi.png",
					"http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png"
			};
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
            String prevIcon = marker.getIcon();
            int idx = marker.getID();
            
            if(prevIcon ==null || prevIcon.equals(iconURLs[0])){
            	marker.setIcon(iconURLs[1]);
            	markedPtIdx.add(idx);
            	System.out.println(markedPtIdx.size());
            	System.out.println("Marked: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());
            	System.out.println("Marked: lat: " + geoPoints.get(idx).lat + " lng: " + geoPoints.get(idx).longit);

            }else{
            	marker.setIcon(iconURLs[0]);
            	if(markedPtIdx.remove(new Integer(idx))){
            		System.out.println(markedPtIdx.size());
            		System.out.println("UnMarked: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());
                	System.out.println("UnMarked: lat: " + geoPoints.get(idx).lat + " lng: " + geoPoints.get(idx).longit);
            	}
            }
        });
	    
//	    google.maps.event.addListener(map, 'event_type', function(event) {
//	        document.jsHandlers.handleUIEvent('key', event.latLng);
//	   });
	}
	@Override
	public int setParameter(List params) {
		chosenAnalyzerIdx = -1;
		Object p = params.get(0);
		Class[] interfaces = p.getClass().getInterfaces();
		if(interfaces[0].equals(DBController.class)){
			DBController dbc = (DBController) p;
			
			List params2 = new ArrayList();
			params2.add(dbc.getDeviceRootPath()+File.separator+"mtd8.dd"+File.separator+"mtd8.dd");
			for(int i=0; i<analyzers.size(); i++){
				if(analyzers.get(i).setParameter(params2)==0){
					chosenAnalyzerIdx=i;
					return 0;	//assume only 1 analyzer meet the requirement
				}
			}
			System.out.println("ERROR: ["+getName()+"] no available data or suitable analyzer");
		}else{
			System.out.println("ERROR: ["+getName()+"] cannot recognize input parameters");
		}
		return 1;
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
	public Object getMarkedItems() {

		List<List<String>> markedItems = new ArrayList<List<String>>();
		if(markedPtIdx == null || markedPtIdx.size()==0) return markedItems;
		
		List<String> headers = new ArrayList<String>();
		headers.add("Timestamp");
		headers.add("Latitude");
		headers.add("Longitude");
		headers.add("Comments");
		markedItems.add(headers);
		
		for(int idx : markedPtIdx){
			GeoPoint gp = geoPoints.get(idx);
			List<String> item = new ArrayList<String>();
			item.add(gp.time);
			item.add(String.valueOf(gp.lat));
			item.add(String.valueOf(gp.longit));
			item.add(gp.comment);
			markedItems.add(item);
		}
		
		return markedItems;
	}

	@Override
	public void setMarkedItems(Object o) {
		List<List<String>> markedItems = (List<List<String>>) o;
		//TODO: find index of matching geoPoints, recreate markedPtIdx
		//TODO: may need db to store previous analysis results tied to device
		
		
	}
	
	/**************************for test*******************************/
	@Override
	public void start(Stage stage) throws Exception {
		LocationHistoryPlugin tp = new LocationHistoryPlugin();
		
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
		
		//uncomment for success case
		DBController dc =  new DBQueryController();
		dc.setDeviceID(3);
		params.add(dc);
		/*
		//failure case
		params.add("this/is/a/bad/path");*/
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
	/**************************end test*******************************/
}
