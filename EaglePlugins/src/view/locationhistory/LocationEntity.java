package view.locationhistory;

import eagleeye.api.entities.*;

public class LocationEntity implements EagleLocation{
	double longitude;
	double latitude;
	String description;
	
	public LocationEntity(){
		setLongitude(0.0);
		setLatitude(0.0);
		setDescription("default location");
			
	}
	public LocationEntity(double x, double y, String s){
		setLatitude(x);
		setLongitude(y);
		setDescription(s);
	}
	@Override
	public void setLongitude(double x) {
		longitude=x;
	}

	@Override
	public void setLatitude(double x) {
		latitude=x;
		
	}

	@Override
	public void setDescription(String s) {
		description=s;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
}
