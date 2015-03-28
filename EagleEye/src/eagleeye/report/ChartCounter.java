package eagleeye.report;

public class ChartCounter {

	private String name;
	private int count;
	
	public ChartCounter(String name) {
		
		this.name = name;
		count = 1;		
	}
	
	public String getName() {
		return name;
	}
	
	public void addCount() {
		count ++;
	}
	
	public int getCount() {
		return count;
	}
	
}



