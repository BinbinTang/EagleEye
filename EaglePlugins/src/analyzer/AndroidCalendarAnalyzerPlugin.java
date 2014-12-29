package analyzer;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reader.SQLiteReaderPlugin;
import eagleeye.pluginmanager.Plugin;

public class AndroidCalendarAnalyzerPlugin implements Plugin{
	private class Event{
		private String summary;
		private String location;
		private String description;
		private String timezone;
		private String startDate;
		private String endDate;
		private Event(String _summary, String _location, String _description, String _timezone, String _startDate, String _endDate){
			summary =_summary;
			location=_location;
			description=_description;
			timezone = _timezone;
			startDate=_startDate;
			endDate=_endDate;
		}
		public String timestampToDate(String timestamp){
			if(timestamp.equals("")) return timestamp;
			SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
			timestamp = timestamp.substring(0, 10);
			
			Long time=new Long(Integer.parseInt(timestamp));
			//time += new Long(978307200);
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
	public AndroidCalendarAnalyzerPlugin(){
		
	}
	public void getAllEvents(){
		//read DB & get tables
		List paths = new ArrayList();
		paths.add(calendarPath);
		sqlreader.setParameter(paths);
		Map<String, List<List<String>>> calendar = (Map<String, List<List<String>>>) sqlreader.getResult();
		
		/*
		//get location
		List<List<String>> locations = calendar.get("Location");
		System.out.println(locations.size());
		List<String> colNames = locations.get(0);
		int indexRowID = colNames.indexOf("ROWID");
		int indexLocation = colNames.indexOf("title");
		Map<String,String> idxToLocation = new HashMap<String,String>();
		for(List<String> i: locations){
			idxToLocation.put(i.get(indexRowID),i.get(indexLocation));
		}*/
		
		//get events
		List<List<String>> calendaritem = calendar.get("Events");
		System.out.println(calendaritem.size());
		List<String> colNames = calendaritem.get(0);
		int indexTitle = colNames.indexOf("title");
		int indexEventLocation = colNames.indexOf("eventLocation");
		int indexDescription = colNames.indexOf("description");
		int indexEventTimeZone = colNames.indexOf("eventTimezone");	//TODO: for future
		int indexStartTimestamp	= colNames.indexOf("dtstart");
		int indexEndTimestamp	= colNames.indexOf("dtend");		
		events = new ArrayList<Event>();
		for(List<String> i: calendaritem ){
			if(i.get(indexTitle).equals("title")) continue;
			String summary=i.get(indexTitle);
			if(summary==null) summary="";
			String location = i.get(indexEventLocation);
			if(location==null) location="";
			String description = i.get(indexDescription);
			if(description==null) description="";
			String timezone= i.get(indexEventTimeZone);
			String startTimestamp=i.get(indexStartTimestamp);
			if(startTimestamp==null) startTimestamp="";
			String endTimestamp=i.get(indexEndTimestamp);
			if(endTimestamp==null) endTimestamp="";
			
			events.add(new Event(summary,location,description,timezone,startTimestamp,endTimestamp));

		}

		/*for(Event i : events){
			System.out.print(i);
		}*/
		
	}
	public String writeResultToFile(){
		String fPath = outputPath+File.separator+"android_calendar.time";
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
		return "Android Calendar Analyzer";
	}

	@Override
	public Object getResult() {
		String fPath = outputPath+File.separator+"android_calendar.time";
		File f = new File(fPath); 
		if(f.exists()) return fPath;
		
		getAllEvents();
		
		return writeResultToFile();
	}

	@Override
	public Type getType() {
		return Type.ANALYZER;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int setParameter(List argList) {
		deviceRoot = (String) argList.get(0);
		calendarPath = deviceRoot+File.separator+"data"+File.separator+"com.android.providers.calendar"+File.separator+"databases"+File.separator+"calendar.db";
		outputPath = (String) argList.get(1);
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
	
	public static void main(String[] args) { 
		
		AndroidCalendarAnalyzerPlugin cp = new AndroidCalendarAnalyzerPlugin();
		List<Plugin> pls = new ArrayList<Plugin>();
		pls.add(new SQLiteReaderPlugin());
		cp.setAvailablePlugins(pls);
		List paths = new ArrayList();
		String root = ".."+File.separator+"EagleEye"+File.separator+"output"+File.separator+"mtd8.dd"+File.separator+"mtd8.dd";
		paths.add(root);
		String out = "analysis";
		paths.add(out);
		cp.setParameter(paths);
		cp.getResult();
		/*
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		String timestamp = "1304596800";
		String date = timestamp;

		Long time=new Long(Integer.parseInt(timestamp));
		time = time*1000;
		System.out.println(""+time);
		date = format.format(time);
		System.out.println(date);

		long times = 0;
        try {  
            times = (int) ((Timestamp.valueOf("2011-05-05 20:00:00").getTime())/1000);  
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
