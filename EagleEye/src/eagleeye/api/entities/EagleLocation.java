package eagleeye.api.entities;

public interface EagleLocation {
	public void setLongitude(double x);
	public void setLatitude(double x);
	public void setDescription(String s);
	public double getLongitude();
	public double getLatitude();
	public String getDescription();
}
