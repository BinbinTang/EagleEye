package eagleeye.geomapview;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class GeomapView extends Application implements MapComponentInitializedListener {

GoogleMapView mapView;
GoogleMap map;

@Override
public void start(Stage stage) throws Exception {

    //Create the JavaFX component and set this as a listener so we know when 
    //the map has been initialized, at which point we can then begin manipulating it.
    mapView = new GoogleMapView();
    mapView.addMapInializedListener(this);

    //Scene scene = new Scene(mapView);

    //stage.setTitle("JavaFX and Google Maps");
    //stage.setScene(scene);
    //stage.show();
}


@Override
public void mapInitialized() {
    //Set the initial properties of the map.
    MapOptions mapOptions = new MapOptions();

    mapOptions.center(new LatLong(1.297221, 103.776379))
            .mapType(MapTypeIdEnum.ROADMAP)
            .overviewMapControl(false)
            .panControl(false)
            .rotateControl(false)
            .scaleControl(false)
            .streetViewControl(false)
            .zoomControl(false)
            .zoom(12);

    map = mapView.createMap(mapOptions);

   /* //Add markers to the map
    double[][] locs = {{1.297221,103.776379},{1.297521,103.778379}};
    for(int i=0; i<locs.length;i++){
    	markLocations(locs[i][0],locs[i][1]);
    }
*/
}


public void markLocations(double lat, double longit){
	MarkerOptions markerOptions = new MarkerOptions();
	
    markerOptions.position( new LatLong(lat, longit) )
                .visible(Boolean.TRUE)
                .title("Suspect location");

    Marker marker = new Marker( markerOptions );

    map.addMarker(marker);
}
/*
public static void main(String[] args) {
    launch(args);
}*/
}