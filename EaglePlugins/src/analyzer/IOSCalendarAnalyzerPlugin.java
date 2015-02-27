package analyzer;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reader.SQLiteReaderPlugin;
import eagleeye.api.plugin.Plugin;

public class IOSCalendarAnalyzerPlugin implements Plugin{
	private class Event{
		private String summary;
		private String location;
		private String description;
		//private boolean isAllDay;
		private String startDate;
		private String endDate;
		private Event(String _summary, String _location, String _description, String _startDate, String _endDate){
			summary =_summary;
			location=_location;
			description=_description;
			//isAllDay=_isAllDay;
			startDate=_startDate;
			endDate=_endDate;
		}
		public String timestampToDate(String timestamp){
			if(timestamp.equals("")) return timestamp;
			SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
			String[] tmp =timestamp.split("\\.");
			timestamp = tmp[0];
			
			Long time=new Long(Integer.parseInt(timestamp));
			time += new Long(978307200);
			time = time*1000;
			return format.format(time);
		}
		@Override
		public String toString(){
			return "["+timestampToDate(startDate)+"~"+timestampToDate(endDate)+"]"+summary+":"+location+":"+description+"\n";
		}
	}
	
	private String deviceRoot;
	private String calendarPath;
	private String outputPath;
	private Plugin sqlreader;
	private List<Event> events;
	private boolean error;
	public IOSCalendarAnalyzerPlugin(){
		sqlreader=null;
		reset();
	}
	public void reset(){
		deviceRoot="";
		calendarPath="";
		outputPath="";
		events = null;
		error = false;
	}
	public void getAllEvents(){
		//read DB & get tables
		List paths = new ArrayList();
		paths.add(calendarPath);
		sqlreader.setParameter(paths);
		Map<String, List<List<String>>> calendar = (Map<String, List<List<String>>>) sqlreader.getResult();
		
		
		//get location
		List<List<String>> locations = calendar.get("Location");
		System.out.println(locations.size());
		List<String> colNames = locations.get(0);
		int indexRowID = colNames.indexOf("ROWID");
		int indexLocation = colNames.indexOf("title");
		Map<String,String> idxToLocation = new HashMap<String,String>();
		for(List<String> i: locations){
			idxToLocation.put(i.get(indexRowID),i.get(indexLocation));
		}
		
		//get events
		List<List<String>> calendaritem = calendar.get("CalendarItem");
		System.out.println(calendaritem.size());
		colNames = calendaritem.get(0);
		int indexSummary = colNames.indexOf("summary");
		int indexLocationId = colNames.indexOf("location_id");
		int indexDescription = colNames.indexOf("description");
		//int indexIsAllDay = colNames.indexOf("all_day");
		int indexStartTimestamp	= colNames.indexOf("start_date");
		int indexEndTimestamp	= colNames.indexOf("end_date");		
		events = new ArrayList<Event>();
		for(List<String> i: calendaritem ){
			if(i.get(indexSummary).equals("summary")) continue;
			String summary=i.get(indexSummary);
			if(summary==null) summary="";
			String location = idxToLocation.get(i.get(indexLocationId));
			if(location==null) location="";
			String description = i.get(indexDescription);
			if(description==null) description="";
			//boolean isAllDay=(i.get(indexIsAllDay).equals("1"))?true:false;
			String startTimestamp=i.get(indexStartTimestamp);
			if(startTimestamp==null) startTimestamp="";
			String endTimestamp=i.get(indexEndTimestamp);
			if(endTimestamp==null) endTimestamp="";
			
			events.add(new Event(summary,location,description,startTimestamp,endTimestamp));

		}

		/*for(Event i : events){
			System.out.print(i);
		}
		*/
	}
	public String writeResultToFile(){
		String fPath = outputPath+File.separator+"calendar.time";
		try{
			FileWriter fw = new FileWriter(fPath,true);
			StringBuilder sb = new StringBuilder();
			sb.append("#TIMEFLOW\tformat version\t1\n");
			sb.append("#TIMEFLOW\tsource\tEagleEye Developers\n");
			sb.append("#TIMEFLOW\tdescription\t\"This is experimental data format for Forensic Suite timeline.\n");
			sb.append("\"\n");
			sb.append("#TIMEFLOW\tfield\tTitle\tText\n");
			sb.append("#TIMEFLOW\tfield\tLocation\tText\n");
			sb.append("#TIMEFLOW\tfield\tStart\tDate/Time\n");
			sb.append("#TIMEFLOW\tfield\tEnd\tDate/Time\n");
			sb.append("#TIMEFLOW\tfield\tDescription\tText\n");
			sb.append("#TIMEFLOW\tend-metadata\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_LABEL\tTitle1\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_TRACK\tTypes\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_START\tStart\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_END\tEnd\n");
			sb.append("#TIMEFLOW\t====== End of Header. Data below is in tab-delimited format. =====\n");
			sb.append("Title\tLocation\tStart\tEnd\tDescription\n");
			fw.append(sb.toString());
			for(Event e : events){
				String title = e.summary;
				String location = "\""+e.location+"\"";
				String start = e.timestampToDate(e.startDate);
				String end = e.timestampToDate(e.endDate);
				String description = e.description.replaceAll("\"", "''");	//" is special char used in timeline app
				description = "\""+description+"\"";
				fw.append(title+"\t"+location+"\t"+start+"\t"+end+"\t"+description+"\n");
			}
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return fPath;
	}
	@Override
	public String getName() {
		return "IOS Calendar Analyzer";
	}

	@Override
	public Object getResult() {
		if(hasError()){
			return null;
		}
		String fPath = outputPath+File.separator+"calendar.time";
		File f = new File(fPath); 
		if(f.exists()){
			System.out.println("["+getName()+"] uses previous analysis result at: "+outputPath);
			return fPath;
		}
		
		getAllEvents();
		
		return writeResultToFile();
	}

	@Override
	public Type getType() {
		return Type.ANALYZER;
	}

	@Override
	public boolean hasError() {
		return error;
	}

	@Override
	public int setParameter(List argList) {
		reset();
		if(argList.size()!=2){
			error = true;
			return 2;
		}
		
		Object o1 = argList.get(0);
		Object o2 = argList.get(1);
		if(!(o1.getClass().equals(String.class) && o2.getClass().equals(String.class))){
			error = true;
			return 2;
		}
		
		deviceRoot = (String) o1;
		outputPath = (String) o2;
		calendarPath = deviceRoot+File.separator+"data"+File.separator+"com.android.providers.calendar"+File.separator+"databases"+File.separator+"calendar.db";
		if(!(new File(outputPath)).exists()){
			error = true;
			return 2;
		}
		
		if(!(new File(calendarPath)).exists()){
			System.out.println("["+getName()+"] test analyze fail");
			error = true;
			return 1;
		}
		
		System.out.println("["+getName()+"] test analyze successful");
		return 0;
	}
	
	@Override
	public int setAvailablePlugins(List<Plugin> pls) {
		// TODO Auto-generated method stub
		for(Plugin pl: pls){
			if(pl.getName().equals("SQLite Reader")){
				sqlreader= pl;
			}
		}
		return 0;
	} 
	@Override
	public Object getMarkedItems() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setMarkedItems(Object arg0) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) { 
		
		IOSCalendarAnalyzerPlugin cp = new IOSCalendarAnalyzerPlugin();
		List<Plugin> pls = new ArrayList<Plugin>();
		pls.add(new SQLiteReaderPlugin());
		cp.setAvailablePlugins(pls);
		List paths = new ArrayList();
		String root = ".."+File.separator+".."+File.separator+".."+File.separator+"device_ios";
		paths.add(root);
		String out = "analysis";
		paths.add(out);
		cp.setParameter(paths);
		cp.getResult();
		/*SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		String timestamp = "978307200";
		String date = timestamp;

		Long time=new Long(Integer.parseInt(timestamp));
		time = time*1000;
		System.out.println(""+time);
		date = format.format(time);
		System.out.println(date);

		long times = 0;
        try {  
            times = (int) ((Timestamp.valueOf("2014-12-07 11:40:23").getTime())/1000);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        if(times==0){
            System.out.println("String转10位时间戳失败");
        }else{
        	System.out.println(times - time/1000);
        }*/
	}
	
	

}
