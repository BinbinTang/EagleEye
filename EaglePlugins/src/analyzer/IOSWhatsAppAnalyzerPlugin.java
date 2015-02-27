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

public class IOSWhatsAppAnalyzerPlugin implements Plugin{
	private class WAMessage{
		private boolean isFrom;
		private String timestamp;
		private String message;
		private String contactName;
		private String contactPhone;
		private WAMessage(boolean _isFrom, String _timestamp, String _message, String _contactName, String _contactPhone){
			isFrom = _isFrom;
			timestamp = _timestamp;
			message = _message;
			contactName = _contactName;
			contactPhone= _contactPhone;
		}
		public String timestampToDate(){
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
			return "["+timestampToDate()+"]"+((isFrom)?"From":"To")+":"+contactName+" ("+contactPhone+")\t"+message+"\n";
		}
	}
	private class Chat{
		private String contactName;
		private String contactPhone;
		private List<WAMessage> chat;
		private Chat(String _contactName, String _contactPhone){
			contactName = _contactName;
			contactPhone = _contactPhone;
			chat = new ArrayList<WAMessage>();
		}
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("Chat with: "+contactName+" ("+contactPhone+")\n");
			for(WAMessage c: chat){
				sb.append(c.toString());
			}
			return sb.toString();
		}
	}
	private String deviceRoot;
	private String WAChatHistoryPath;
	private String outputPath;
	private Plugin sqlreader;
	private List<Chat> chats;
	private boolean error;
	public IOSWhatsAppAnalyzerPlugin(){
		sqlreader=null;
		reset();
	}
	public void reset(){
		deviceRoot="";
		WAChatHistoryPath="";
		outputPath="";
		chats = null;
		error = false;
	}
	
	public void getAllChats(){
		//read DB & get tables
		List paths = new ArrayList();
		paths.add(WAChatHistoryPath);
		sqlreader.setParameter(paths);
		Map<String, List<List<String>>> chatHistory = (Map<String, List<List<String>>>) sqlreader.getResult();
		
		
		//get media
		List<List<String>> zwamediaitem = chatHistory.get("zwamediaitem".toUpperCase());
		System.out.println(zwamediaitem.size());
		List<String> colNames = zwamediaitem.get(0);
		int indexMediaPath = colNames.indexOf("ZMEDIALOCALPATH");
		int indexThumbPath = colNames.indexOf("ZXMPPTHUMBPATH");
		int indexMediaKey = colNames.indexOf("Z_PK");
		Map<String,String> mediaItems = new HashMap<String,String>();
		for(int i = 1;i<zwamediaitem.size();i++){
			String media = zwamediaitem.get(i).get(indexMediaPath);
			if (media==null) media = zwamediaitem.get(i).get(indexThumbPath);
			if (media==null) media = "<other non-pic non-movie media>";
			mediaItems.put(zwamediaitem.get(i).get(indexMediaKey),media);
		}
		
		//get chat messages
		List<List<String>> zwamessage = chatHistory.get("zwamessage".toUpperCase());
		System.out.println(zwamessage.size());
		colNames = zwamessage.get(0);
		int indexIsFromMe = colNames.indexOf("ZISFROMME");
		int indexTimestamp = colNames.indexOf("ZMESSAGEDATE");
		int indexFrom = colNames.indexOf("ZFROMJID");
		int indexTo	= colNames.indexOf("ZTOJID");
		int indexName = colNames.indexOf("ZPUSHNAME");
		int indexMessage = colNames.indexOf("ZTEXT");
		int indexMedia = colNames.indexOf("ZMEDIAITEM");
		int indexGroupEventType = colNames.indexOf("ZGROUPEVENTTYPE");
		
		chats = new ArrayList<Chat>();
		for(int i = 1;i<zwamessage.size();i++){
			boolean isFrom = true;
			String groupEventType = "";
			String contactName="";
			String contactPhone="";
			String timestamp="";
			String message=""; //TODO: String does not interpretate emoticon properly
			String mediaKey="";
			if(zwamessage.get(i).get(indexIsFromMe).equals("1")) isFrom = false;
			if(isFrom){
				contactPhone = zwamessage.get(i).get(indexFrom);
				contactName = zwamessage.get(i).get(indexName);
				if (contactName == null) contactName="";
			}else{
				contactPhone = zwamessage.get(i).get(indexTo);	
			}
			String[] tmp = contactPhone.split("\\@");
			contactPhone = tmp[0];
			timestamp = zwamessage.get(i).get(indexTimestamp);
			
			message = zwamessage.get(i).get(indexMessage);
			if(message==null) message="";
			mediaKey = zwamessage.get(i).get(indexMedia);
			if(mediaKey!=null) message = mediaItems.get(mediaKey);
			groupEventType = zwamessage.get(i).get(indexGroupEventType);
			if(groupEventType.equals("1")) contactName = message;

			int index = -1;
			for(int j=0; j<chats.size(); j++){
				if(chats.get(j).contactPhone.equals(contactPhone)){
					index = j;		
					break;
				}
			}
			if (index == -1){
				Chat c = new Chat(contactName,contactPhone);
				chats.add(c);
				if(groupEventType.equals("1")) continue;
				if(message.equals("")) continue;
				c.chat.add(new WAMessage(isFrom, timestamp, message, contactName, contactPhone));
			}else{
				if(groupEventType.equals("1")){
					String n = chats.get(index).contactName;
					if(chats.get(index).contactName.equals("")) chats.get(index).contactName=contactName;
					else chats.get(index).contactName+=" | "+contactName;
					continue;
				}
				if(chats.get(index).contactName.equals(""))
					chats.get(index).contactName=contactName;
				if(message.equals("")) continue;
				chats.get(index).chat.add(new WAMessage(isFrom, timestamp, message, contactName, contactPhone));
			}
		}
		/*for(Chat c : chats){
			System.out.println(c);
		}*/
		
	}
	public void analyzeChatsByContact(){
		if (chats==null){
			System.out.println("Error: cannot get chat data");
			return;
		}
		System.out.println("===============================");
		System.out.println("   Chat Frequency By Contacts  ");
		System.out.println("===============================");
		for(Chat c : chats){
			System.out.println(c.contactName+" ("+c.contactPhone+"):\t"+c.chat.size());
		}
	}
	public String writeResultToFile(){
		String fPath = outputPath+File.separator+"whatsapp.time";
		try{
			FileWriter fw = new FileWriter(fPath,true);
			StringBuilder sb = new StringBuilder();
			sb.append("#TIMEFLOW\tformat version\t1\n");
			sb.append("#TIMEFLOW\tsource\tEagleEye Developers\n");
			sb.append("#TIMEFLOW\tdescription\t\"This is experimental data format for Forensic Suite timeline.\n");
			sb.append("\"\n");
			sb.append("#TIMEFLOW\tfield\tTitle\tText\n");
			sb.append("#TIMEFLOW\tfield\tTypes\tList\n");
			sb.append("#TIMEFLOW\tfield\tStart\tDate/Time\n");
			sb.append("#TIMEFLOW\tfield\tEnd\tDate/Time\n");
			sb.append("#TIMEFLOW\tfield\tDescription\tText\n");
			sb.append("#TIMEFLOW\tend-metadata\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_LABEL\tTitle1\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_TRACK\tTypes\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_START\tStart\n");
			sb.append("#TIMEFLOW\talias\tTIMEFLOW_END\tEnd\n");
			sb.append("#TIMEFLOW\t====== End of Header. Data below is in tab-delimited format. =====\n");
			sb.append("Title\tTypes\tStart\tEnd\tDescription\n");
			fw.append(sb.toString());
			for(Chat c : chats){
				String type = c.contactName+" ("+c.contactPhone+")";
				List<WAMessage> messages = c.chat;
				for (WAMessage m: messages){
					String title = (m.isFrom)?("From:"+m.contactName):"To";
					String start = m.timestampToDate();
					String description = m.message.replaceAll("\"", "''");	//" is special char used in timeline app
					description = "\""+description+"\"";
					fw.append(title+"\t"+type+"\t"+start+"\t"+"\t"+description+"\n");
				}
			}
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return fPath;
	}
	@Override
	public String getName() {
		return "IOS WhatsApp Analyzer";
	}

	@Override
	public Object getResult() {
		if(hasError()){
			return null;
		}
		String fPath = outputPath+File.separator+"whatsapp.time";
		File f = new File(fPath); 
		if(f.exists()){
			System.out.println("["+getName()+"] uses previous analysis result at: "+outputPath);
			return fPath;
		}
		
		getAllChats();
		//analyzeChatsByContact();
		
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
		WAChatHistoryPath = deviceRoot+File.separator+"private"+File.separator+"var"+File.separator+"mobile"+File.separator+"Applications"+File.separator+"00CAE5F5-CA3E-45D2-91F2-33E3F2FB12E1"+File.separator+"Documents"+File.separator+"ChatStorage.sqlite";
		if(!(new File(outputPath)).exists()){
			error = true;
			return 2;
		}
		
		if(!(new File(WAChatHistoryPath)).exists()){
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
	
	public static void main(String[] args) { 
		
		IOSWhatsAppAnalyzerPlugin sp = new IOSWhatsAppAnalyzerPlugin();
		List<Plugin> pls = new ArrayList<Plugin>();
		pls.add(new SQLiteReaderPlugin());
		sp.setAvailablePlugins(pls);
		
		List paths = new ArrayList();
		String root = ".."+File.separator+".."+File.separator+".."+File.separator+"device_ios";
		paths.add(root);
		String out = "analysis";
		paths.add(out);
		sp.setParameter(paths);
		sp.getResult();
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
	@Override
	public Object getMarkedItems() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setMarkedItems(Object arg0) {
		// TODO Auto-generated method stub
		
	} 
	

}
