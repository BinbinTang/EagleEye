package eagleeye.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eagleeye.api.entities.*;
import eagleeye.entities.*;

public class tmpLocationGenerator {

	public tmpLocationGenerator(){

	}
	
	public List<EagleLocation> generateLcoation(int n){
		double r = 1.43445609-1.352083;
		double centerlat = 1.352083;
		double centerlong = 103.819836;
		
		Random rn = new Random();
		
		ArrayList<EagleLocation> locations = new ArrayList<EagleLocation>();
		for(int i=0;i<n;i++){
			int degree = rn.nextInt(360+1);
			double length = r*rn.nextDouble();
			double lat = Math.cos(degree)*length+centerlat;
			double longit=Math.sin(degree)*length+centerlong;
			locations.add(new LocationEntity(lat,longit,"location"));
		}
		return locations;
	}
}
