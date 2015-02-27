package analyzer;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import reader.SQLiteReaderPlugin;
import eagleeye.api.plugin.Plugin;

public class AndroidSMSHistoryAnalyzerPlugin implements Plugin {
	
	private class SMSs{
		
		private String SMSType;
		private String contactAddress;
		private String timeDate;
		private String body;
		
		private SMSs (int SMSType, String contactAddress, String timeDate, String body){
			
			switch(SMSType) {
				case 0: this.SMSType = "All SMSs"; break;
				case 1: this.SMSType = "Inbox"; break;
				case 2: this.SMSType = "Sent"; break;
				case 3: this.SMSType = "Draft"; break;
				case 4: this.SMSType = "Outbox"; break;
				case 5: this.SMSType = "Failed"; break;
				case 6: this.SMSType = "Queued (Send later)"; break;
	
				default: this.SMSType = "Unknown"; break;
			}
		
			this.contactAddress = contactAddress;
			this.body = body;
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
	private String SMSHistoryPath;
	private List<SMSs> SMSHistory;
	private boolean error;
	
	public AndroidSMSHistoryAnalyzerPlugin(){
		sqlReader = null;
		reset();
	}
	public void reset(){
		deviceRoot="";
		SMSHistoryPath="";
		outputPath="";
		SMSHistory = null;
		error = false;
	}
	public void getAllSMSs(){
		
		return;
	}
	
	public String writeResultToFile() {
		return null;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Android SMS History Analyzer";
	}

	@Override
	public Object getResult() {
		if(hasError()){
			return null;
		}
		String fPath = outputPath+File.separator+"android_smss_history.time";
		File f = new File(fPath); 
		
		if(f.exists()) return fPath;
		
		getAllSMSs();
		
		return writeResultToFile();

	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.ANALYZER;
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return error;
	}

	@Override
	public int setAvailablePlugins(List<Plugin> pls) {
		// TODO Auto-generated method stub
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
		SMSHistoryPath = deviceRoot+File.separator+"data"+File.separator+"com.android.providers.telephony"+File.separator+"databases"+File.separator+"mmssms.db";
		if(!(new File(outputPath)).exists()){
			error = true;
			return 2;
		}
		
		if(!(new File(SMSHistoryPath)).exists()){
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
	public static void main(String[] args)
	{
		// For individual testing
		AndroidSMSHistoryAnalyzerPlugin cp = new AndroidSMSHistoryAnalyzerPlugin();
		List<Plugin> pls = new ArrayList<Plugin>();
		pls.add(new SQLiteReaderPlugin());
		cp.setAvailablePlugins(pls);
		List paths = new ArrayList();
		String root = ".."+File.separator+"EagleEye"+File.separator+"output"+File.separator+"Case 2"+File.separator+"mtd8.dd"+File.separator+"mtd8.dd";
		paths.add(root);
		String out = "analysis";
		paths.add(out);
		cp.setParameter(paths);
		cp.getResult();
	}
	/****************** end of test *******************/
}
