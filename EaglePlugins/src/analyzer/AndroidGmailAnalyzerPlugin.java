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

public class AndroidGmailAnalyzerPlugin implements Plugin{
	private class Email{
		private String account;
		private String subject;
		private String from;
		private String to;
		private String body;	
		private String sentDate;
		private String receivedDate;
		private Email(String _account,String _subject, String _from, String _to, String _body, String _sentDate, String _receivedDate){
			account=_account;
			subject =_subject;
			from=_from;
			to = _to;
			body=_body;
			sentDate=_sentDate;
			receivedDate=_receivedDate;
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
			return account+"["+timestampToDate(sentDate)+"~"+timestampToDate(receivedDate)+"]"+subject+":"+from+":"+to+":"+"\n"+body+"\n";
		}
	}
	
	private String deviceRoot;
	private String dbFolderPath;
	private String outputPath;
	private Plugin sqlreader;
	private List<Email> mails;
	public AndroidGmailAnalyzerPlugin(){
		
	}
	public List<String> getDBFiles(){
		File dbFolder = new File(dbFolderPath);
		if (dbFolder.exists() && dbFolder.isDirectory()) {
			String[] files = dbFolder.list();
			List<String> dbFiles = new ArrayList<String>();
			for(int i=0; i< files.length; i++){
				if(files[i].endsWith("gmail.com.db")){
					dbFiles.add(files[i]);
				}
			}
			return dbFiles;
		}
		return null;
	}
	public void getAllEvents(){
		List<String> dbFiles = getDBFiles();
		mails = new ArrayList<Email>();
		if(dbFiles==null) return;
		for(String f: dbFiles){
			System.out.println(f);
			//read DB & get tables
			List paths = new ArrayList();
			paths.add(dbFolderPath+File.separator+f);
			sqlreader.setParameter(paths);
			Map<String, List<List<String>>> mailbox = (Map<String, List<List<String>>>) sqlreader.getResult();
			
			//get mail messages
			List<List<String>> messages = mailbox.get("messages");
			System.out.println(messages.size());
			List<String> colNames = messages.get(0);
			int indexSubject = colNames.indexOf("subject");
			int indexFrom = colNames.indexOf("fromAddress");
			int indexTo = colNames.indexOf("toAddresses");
			int indexCc = colNames.indexOf("ccAddresses");
			int indexBcc = colNames.indexOf("bccAddresses");
			int indexBody = colNames.indexOf("body");
			int indexSentTimestamp	= colNames.indexOf("dateSentMs");
			int indexReceivedTimestamp	= colNames.indexOf("dateReceivedMs");		

			for(List<String> i: messages ){
				if(i.get(indexSubject).equals("subject")) continue;	//skip header
				String account = f;
				account = account.substring(10,f.length()-3);
				String subject=i.get(indexSubject);
				if(subject==null) subject="";
				String from = i.get(indexFrom);
				if(from==null) from="";
				String to = i.get(indexTo);
				String cc = i.get(indexCc);
				String bcc = i.get(indexBcc);
				if(to==null) to="";
				if(cc==null) cc="";
				if(bcc==null) bcc="";
				to = to+cc+bcc;
				String body = i.get(indexBody);
				if(body==null) body="";
				String sentTimestamp=i.get(indexSentTimestamp);
				if(sentTimestamp==null) sentTimestamp="";
				String receivedTimestamp=i.get(indexReceivedTimestamp);
				if(receivedTimestamp==null) receivedTimestamp="";
				
				mails.add(new Email(account, subject,from,to,body,sentTimestamp,receivedTimestamp));
			}
			for(Email i : mails){
				System.out.print(i);
			}			
		}
		
	}
	public String writeResultToFile(){
		String fPath = outputPath+File.separator+"android_gmail.time";
		try{
			FileWriter fw = new FileWriter(fPath,true);
			StringBuilder sb = new StringBuilder();
			sb.append("#TIMEFLOW\tformat version\t1\n");
			sb.append("#TIMEFLOW\tsource\tEagleEye Developers\n");
			sb.append("#TIMEFLOW\tdescription\t\"This is experimental data format for Forensic Suite timeline.\n");
			sb.append("\"\n");
			sb.append("#TIMEFLOW\tfield\tSubject\tText\n");
			sb.append("#TIMEFLOW\tfield\tAccount\tList\n");
			sb.append("#TIMEFLOW\tfield\tFrom\tText\n");
			sb.append("#TIMEFLOW\tfield\tTo\tText\n");
			sb.append("#TIMEFLOW\tfield\tTime Sent\tDate/Time\n");
			sb.append("#TIMEFLOW\tfield\tTime Received\tDate/Time\n");
			sb.append("#TIMEFLOW\tfield\tBody\tText\n");
			sb.append("#TIMEFLOW\tend-metadata\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_LABEL\tSubject\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_TRACK\tAccount\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_START\tTime Sent\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_END\tTime Received\n");
			sb.append("#TIMEFLOW\t====== End of Header. Data below is in tab-delimited format. =====\n");
			sb.append("Subject\tAccount\tFrom\tTo\tTime Sent\tTime Received\tBody\n");
			fw.append(sb.toString());
			for(Email e : mails){
				String subject = e.subject;
				String account = e.account;
				String from = e.from.replaceAll("\"", "");
				String to = "\""+e.to.replaceAll("\"", "")+"\"";
				String timeSent = e.timestampToDate(e.sentDate);
				String timeReceived = e.timestampToDate(e.receivedDate);
				String body = e.body.replaceAll("\"", "''");	//" is special char used in timeline app
				body = "\""+body+"\"";
				fw.append(subject+"\t"+account+"\t"+from+"\t"+to+"\t"+timeSent+"\t"+timeReceived+"\t"+body+"\n");
			}
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return fPath;
	}
	@Override
	public String getName() {
		return "Android Gmail Analyzer";
	}

	@Override
	public Object getResult() {
		String fPath = outputPath+File.separator+"android_gmail.time";
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
		//System.out.println(deviceRoot);
		dbFolderPath = deviceRoot+File.separator+"data"+File.separator+"com.google.android.providers.gmail"+File.separator+"databases";
		File f = new File(dbFolderPath);
		if(!f.exists()){
			System.out.println("["+getName()+"] test analyze fail");
			return 1;
		}
		System.out.println("["+getName()+"] test analyze successful");
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
		
		AndroidGmailAnalyzerPlugin cp = new AndroidGmailAnalyzerPlugin();
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
