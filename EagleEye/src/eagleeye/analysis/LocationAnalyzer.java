package eagleeye.analysis;


import java.util.ArrayList;
import java.util.List;

import eagleeye.api.analyzer.EagleLocationAnalyzer;
import eagleeye.api.entities.EagleLocation;
import eagleeye.entities.LocationEntity;
public class LocationAnalyzer implements EagleLocationAnalyzer{
	List<EagleLocation> locations;
	public LocationAnalyzer(){
		locations=new ArrayList<EagleLocation>();
	}
	
	public void populateLocationList(int n){
		tmpLocationGenerator lg = new tmpLocationGenerator();
		locations = lg.generateLcoation(n);
	}

	@Override
	public List<EagleLocation> getLocations() {
		// TODO replace with db queries
		populateLocationList(30);
		
		return locations;
	}
	
	public void main (String[] args){
		LocationAnalyzer la = new LocationAnalyzer();
		List<EagleLocation> lst = la.getLocations();
		for(int i=0; i<lst.size(); i++){
			System.out.println(lst.get(i).getLatitude()+","+lst.get(i).getLongitude());
		}
	}
}
