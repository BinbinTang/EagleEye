package view.locationhistory;

import java.util.List;

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
	
	public MapPlugin(){
		mapView = new GoogleMapView();
	    mapView.addMapInializedListener(this);
	}

	@Override
	public String getName() {
		return "Location History";
	}

	@Override
	public Object getResult() {
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
	public int setParameter(List arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void mapInitialized() {
		//Set the initial properties of the map.
	    MapOptions mapOptions = new MapOptions();

	    mapOptions.center(new LatLong(1.352083, 103.819836))
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
	    LocationAnalyzer gla = new LocationAnalyzer();
	    List<EagleLocation> locs = gla.getLocations();
	    for(int i=0; i<locs.size();i++){
	    	//System.out.println(locs.get(i).getLatitude()+","+locs.get(i).getLongitude());
	    	markLocations(locs.get(i).getLatitude(),locs.get(i).getLongitude(),locs.get(i).getDescription());
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
	public void start(Stage stage) throws Exception {
		Node view = (Node)getResult();
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
