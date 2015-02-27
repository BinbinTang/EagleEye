package analyzer;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import reader.SQLiteReaderPlugin;
import eagleeye.api.plugin.Plugin;

public class AndroidCallHistoryAnalyzerPlugin implements Plugin {
		
	private class Calls{
		
		private String callType;
		private String duration;
		private String contactName;
		private String contactNumber;
		private String timeDate;
			
		private Calls (int callType, String duration, String contactName, String contactNumber, String timeDate){
			
			switch(callType) {
				case 0: this.callType = "Outgoing Call"; break;
				case 1: this.callType = "Incoming Call"; break;
				case 2: this.callType = "Missed Outgoing Call"; break;
				case 3: this.callType = "Missed Incoming Call"; break;
				default: this.callType = "Unknown"; break;
			}
			
			this.duration = duration;
			this.contactName = contactName;
			this.contactNumber = contactNumber;
			this.timeDate = timeDate;
		}
		
		public String timestampToDate(String timestamp){
		
			System.out.println(timestamp);
			if(timestamp.equals("")) return timestamp;
			SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
			timestamp = timestamp.substring(0, 10);
			
			Long time=new Long(Integer.parseInt(timestamp));
			time = time*1000;
			System.out.println(format.format(time));
			return format.format(time);
		}
		
		
	}
	
	private String deviceRoot;
	private Plugin sqlReader;
	private String outputPath;
	private String callHistoryPath;
	private List<Calls> callHistory;
	private boolean error;
	public AndroidCallHistoryAnalyzerPlugin() {
		sqlReader = null;
		reset();
	}
	public void reset(){
		deviceRoot="";
		callHistoryPath="";
		outputPath="";
		callHistory = null;
		error = false;
	}
	
	public void getAllCalls() {
		
		List paths = new ArrayList();
		paths.add(callHistoryPath);
		sqlReader.setParameter(paths);
		Map<String, List<List<String>>> callHistoryList = (Map<String, List<List<String>>>) sqlReader.getResult();
		
		List<List<String>> callHistoryItems = callHistoryList.get("calls");
		
		List<String> colNames = callHistoryItems.get(0);
		int durationIndex = colNames.indexOf("duration");
		int callTypeIndex = colNames.indexOf("type");
		int callDateIndex = colNames.indexOf("date");
		int numberIndex = colNames.indexOf("number");	
		int nameIndex	= colNames.indexOf("name");
		
		callHistory = new ArrayList();
		
		for(List<String> i: callHistoryItems ){
			
			if(i.get(durationIndex).equals("duration")) continue;
			
			String duration = i.get(durationIndex);
			if(duration==null) duration="";
			
			String number = i.get(numberIndex);
			if(number==null) number="";
			
			String name = i.get(nameIndex);
			if(name==null) name ="";
			
			String date = i.get(callDateIndex);
			if(date==null) date = "";
			
			String type = i.get(callTypeIndex);
			if(type==null) type = "";
			
			int callType = -1;
			
			if(!type.equals(""))
				callType = Integer.parseInt(type);
			
			callHistory.add(new Calls(callType, duration, name, number, date));
		}
	}
	
	public String writeResultToFile() {
	
		String fPath = outputPath+File.separator+"android_calls_history.time";
		try{
			FileWriter fw = new FileWriter(fPath,true);
			StringBuilder sb = new StringBuilder();
			sb.append("#TIMEFLOW\tformat version\t1\n");
			sb.append("#TIMEFLOW\tsource\tEagleEye Developers\n");
			sb.append("#TIMEFLOW\tdescription\t\"This is experimental data format for Forensic Suite timeline.\n");
			sb.append("\"\n");
			sb.append("#TIMEFLOW\tfield\tCallType\tText\n");
			sb.append("#TIMEFLOW\tfield\tNumber\tText\n");
			sb.append("#TIMEFLOW\tfield\tName\tText\n");
			sb.append("#TIMEFLOW\tfield\tDuration\tText\n");
			sb.append("#TIMEFLOW\tfield\tCallDate\tDate/Time\n");
			sb.append("#TIMEFLOW\tend-metadata\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_LABEL\tTitle1\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_TRACK\tTypes\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_START\tStart\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_END\tEnd\n");
			sb.append("#TIMEFLOW\t====== End of Header. Data below is in tab-delimited format. =====\n");
			sb.append("CallType\tNumber\tName\tDuration\tCallDate\n");
			fw.append(sb.toString());
			
			for(Calls c : callHistory){
				
				String callType = c.callType;
				String number = c.contactNumber;
				String name = c.contactName;
				String duration = c.duration;
				String date = c.timestampToDate(c.timeDate);
				fw.append(callType+"\t"+number+"\t"+name+"\t"+duration+"\t"+date+"\n");
			}
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return fPath;
	}
	
	@Override
	public String getName() {
		return "Android Call History Analyzer";
	}

	@Override
	public Object getResult() {
		if(hasError()){
			return null;
		}
		String fPath = outputPath+File.separator+"android_calls_history.time";
		File f = new File(fPath); 
		
		if(f.exists()) return fPath;
		
		getAllCalls();
		
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
	public int setAvailablePlugins(List<Plugin> pls) {
		
		for(Plugin pl: pls){
			if(pl.getName().equals("SQLite Reader")){
				sqlReader= pl;
			}
		}
		return 0;
		
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
		callHistoryPath = deviceRoot+File.separator+"data"+File.separator+"com.android.providers.contacts"+File.separator+"databases"+File.separator+"contacts2.db";
		if(!(new File(outputPath)).exists()){
			error = true;
			return 2;
		}
		
		if(!(new File(callHistoryPath)).exists()){
			System.out.println("["+getName()+"] test analyze fail");
			error = true;
			return 1;
		}
		
		System.out.println("["+getName()+"] test analyze successful");
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
	/****************** start of test *******************/
	public static void main(String[] args) { 
		
		
		//Used for individual testing
		AndroidCallHistoryAnalyzerPlugin cp = new AndroidCallHistoryAnalyzerPlugin();
		List<Plugin> pls = new ArrayList<Plugin>();
		pls.add(new SQLiteReaderPlugin());
		cp.setAvailablePlugins(pls);
		List paths = new ArrayList();
		String root = ".."+File.separator+"EagleEye"+File.separator+"output"+File.separator+"CaiJun"+File.separator+"mtd8.dd"+File.separator+"mtd8.dd";
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
	/****************** end of test *******************/
}
