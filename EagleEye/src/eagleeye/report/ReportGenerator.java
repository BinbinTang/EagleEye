package eagleeye.report;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import eagleeye.api.entities.EagleDevice;
import eagleeye.entities.Device;
import net.sf.dynamicreports.jasper.builder.JasperConcatenatedReportBuilder;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.Exporters;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.FillerBuilder;
import net.sf.dynamicreports.report.builder.component.ImageBuilder;
import net.sf.dynamicreports.report.builder.component.SubreportBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.TimePeriod;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;



public class ReportGenerator {

	private final String FOLDER_STRUCTURE_PLUGIN = "FolderStructurePlugin";
	private final String TABLE_VIEW_PLUGIN = "TableViewPlugin";
	private final String EMPTY_STRING = "";
	private final String SPACE = " ";
	private final String DASH = "-";
	private final String COLON = ":";
	private final int TIME_DATE_INDEX = 4;
	private final int COMMENT_INDEX = 6;
	private final int KEY_INDEX = 0;
	private final int DEVICEID_INDEX = 1;
	private final int FILEID_INDEX = 2;
	private final int EVENTID_INDEX = 3;
	private final int EVENT_INDEX = 5;
	
	
	private JasperReportBuilder setUpReport(JasperReportBuilder report, String reportTitle, EagleDevice device, String projectFileName) throws FileNotFoundException {
				
		// Styles
		StyleBuilder boldText = DynamicReports.stl.style().bold();
		StyleBuilder leftAlign = DynamicReports.stl.style().setHorizontalAlignment(HorizontalAlignment.LEFT);
		StyleBuilder titleStyle = DynamicReports.stl.style(boldText).setHorizontalAlignment(HorizontalAlignment.CENTER);
				
		//change page to landscape
		report.setPageFormat(PageType.A4, PageOrientation.LANDSCAPE);
				
		// Add Title and ProjectFile Name
		//TextFieldBuilder<String> title = DynamicReports.cmp.text("\n"+reportTitle + "\n" + "Project One\n" + "21.11.2015");
		TextFieldBuilder<String> title = DynamicReports.cmp.text("\n"+reportTitle + "\n" + projectFileName +"\n" + getTodayDate());
		title.setStyle(titleStyle);
		
		// Add Device Information
		//TextFieldBuilder<String> deviceInfo = DynamicReports.cmp.text("\n" +"DeviceA\n"+"Jack\n");
		TextFieldBuilder<String> deviceInfo = DynamicReports.cmp.text("\n"+device.getDeviceName()+"\n"+device.getDeviceOwner());
		deviceInfo.setStyle(leftAlign);
		
		// Add Logo
		FileInputStream imageStream = new FileInputStream(new File("./DynamicReportLib/logo.jpg"));
		ImageBuilder logo = DynamicReports.cmp.image(imageStream).setFixedDimension(100, 100).setStyle(leftAlign);
		
		// Add Filler for better outlook
		FillerBuilder filler = DynamicReports.cmp.filler().setStyle(DynamicReports.stl.style().setTopBorder(
				DynamicReports.stl.pen2Point()
					)).setFixedHeight(2);
		
		report.title(DynamicReports.cmp.horizontalFlowList(logo.setFixedDimension(100, 70),
				title.setFixedDimension(622, 70),
				deviceInfo.setFixedDimension(100, 70)
				).newRow().add(filler));
		
		
		return report;
		
		
	}
	
	public boolean generateTableStyleReport(List<List<String>> reportData, EagleDevice device, String projectFileName) throws Exception {
		
		if(reportData==null || device==null || projectFileName==null ){
			//TODO: is projectfile necessary?
			return false;
		}
		
		JasperReportBuilder tableReport = DynamicReports.report(); 
		tableReport = setUpReport(tableReport,"Marked Files Report in Table",device,projectFileName);
		
		// Styles
		StyleBuilder boldStyle = DynamicReports.stl.style().bold();
		StyleBuilder boldStyleCenter = DynamicReports.stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.CENTER);
		StyleBuilder centerText = DynamicReports.stl.style().setHorizontalAlignment(HorizontalAlignment.CENTER);
		StyleBuilder columnHeaderStyle = DynamicReports.stl.style(boldStyleCenter).setBorder(
				DynamicReports.stl.pen1Point()
				).setBackgroundColor(Color.LIGHT_GRAY);
		
		
		// add columns
		List<String> columnNames = getColumnNames(reportData);
				
		// add number columns
		TextColumnBuilder<Integer> rowNumColumn = Columns.reportRowNumberColumn("No.").setStyle(centerText);
		rowNumColumn.setFixedWidth(20);
		tableReport.addColumn(rowNumColumn);
		
		for(String columnName : columnNames){
			
			TextColumnBuilder<String> column = Columns.column(columnName, columnName,DynamicReports.type.stringType());
			tableReport.addColumn(column);
		}
		
		// set column header Style
		tableReport.setColumnTitleStyle(columnHeaderStyle);
		
		// add data source
		JRDataSource data = createDataSource(reportData);
		tableReport.setDataSource(data).setColumnStyle(centerText);
		
		// Highlight rows for better outlook
		tableReport.highlightDetailEvenRows();
		
		// Add Footer
		tableReport.pageFooter(DynamicReports.cmp.pageXofY());
		
		//tableReport.show();
		
		//write to PDF
		String fileName = "./" + projectFileName + "_" + "TableReport.pdf";
		File f = new File(fileName);
		FileOutputStream fileOutput = new FileOutputStream(f);
		tableReport.toPdf(fileOutput);
		fileOutput.flush();
		fileOutput.close();
		
		return true;
	}
	
	private JRDataSource createDataSource(List<List<String>> reportData) {
		
		DRDataSource dataSource = new DRDataSource(reportData.get(0).toArray(new String[reportData.get(0).size()]));
		
		for(int i = 1; i<reportData.size();i++) {
			
			dataSource.add(reportData.get(i).toArray(new Object[reportData.get(i).size()]));
		}
		
		return dataSource;
	}
	
	
	private List<String> getColumnNames(List<List<String>> reportData) {
		
		List<String> columnNames = new ArrayList<String>();
		
		for(String columnName : reportData.get(0)) {
			
			columnNames.add(columnName);
		}
		
		return columnNames;
	}
	
	private String getTodayDate() {
		
		Date date = new Date();
		return date.toString();
		
	}
		
	public boolean generateDateTimeReport(List<List<String>> reportData, EagleDevice device, String projectFileName) throws Exception {
		
		String reportTitle = "Events TimeLine Report";
		
		JasperConcatenatedReportBuilder mainReport = DynamicReports.concatenatedReport();
		
		ArrayList<TimeLineEventNode> eventList = createTimeLineEventNodes(reportData);
		ArrayList<ArrayList<TimeLineEventNode>> eventListByYear =  classifyEventByYear(eventList); 
		int count = 0;
		
		for(ArrayList<TimeLineEventNode> eventListYearly : eventListByYear)
		{
			int year = eventListYearly.get(0).getYear();
		
			JasperReportBuilder timeEventReport = DynamicReports.report();
			if(count == 0)
				timeEventReport = setUpReport(timeEventReport,reportTitle,device,projectFileName);
			timeEventReport.setPageFormat(PageType.A4, PageOrientation.LANDSCAPE);
		
		
			// Styles
			FontBuilder boldFont = DynamicReports.stl.fontArialBold().setFontSize(12);
			StyleBuilder boldStyle = DynamicReports.stl.style().bold();
			StyleBuilder boldStyleCenter = DynamicReports.stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.CENTER);
			StyleBuilder centerText = DynamicReports.stl.style().setHorizontalAlignment(HorizontalAlignment.CENTER);
			StyleBuilder columnHeaderStyle = DynamicReports.stl.style(boldStyleCenter).setBorder(
					DynamicReports.stl.pen1Point()
					).setBackgroundColor(Color.LIGHT_GRAY);
				
			//initialize column for timeLine Data
			TextColumnBuilder<Integer> keyColumn = Columns.column("Key","Key", DynamicReports.type.integerType());
			TextColumnBuilder<Integer> dateMonthColumn = Columns.column("Event Node","Date Month", DynamicReports.type.integerType());
			TextColumnBuilder<String> eventColumn = Columns.column("Event","Event", DynamicReports.type.stringType());
			TextColumnBuilder<String> commentColumn = Columns.column("Comment","Comment", DynamicReports.type.stringType());
			TextColumnBuilder<String> dateColumn = Columns.column("Date","Date", DynamicReports.type.stringType());
			TextColumnBuilder<String> timeColumn = Columns.column("Time","Time", DynamicReports.type.stringType());
				
				
			timeEventReport.addColumn(keyColumn);
			timeEventReport.addColumn(eventColumn);
			timeEventReport.addColumn(commentColumn);
			timeEventReport.addColumn(dateColumn);
			timeEventReport.addColumn(timeColumn);
		
			// set column header Style
			timeEventReport.setColumnTitleStyle(columnHeaderStyle);
				
			// Highlight rows for better outlook
			timeEventReport.highlightDetailEvenRows();
						
			JRDataSource timeEventReportData = createTimeLineEventDataSource(eventListYearly);
		
		
			//add Charts
			timeEventReport.summary(
				
					DynamicReports.cht.scatterChart()
					.setTitle("Time Line of Marked Events")
					.setTitleFont(boldFont)
					.setShowLines(false)
					.setXValue(keyColumn)
					.series(
							DynamicReports.cht.xySerie(dateMonthColumn))
					.setXAxisFormat(
							DynamicReports.cht.axisFormat().setLabel("Event Key ID"))
					.setYAxisFormat(
							DynamicReports.cht.axisFormat().setLabel("Month of Year " + year)
	        		.setRangeMinValueExpression(1)));
	        		
			//set data source
			timeEventReport.setDataSource(timeEventReportData).setColumnStyle(centerText);
		
			mainReport.concatenate(timeEventReport);
			count++;
		}
		
		//write to PDF
		String fileName = "./" + projectFileName + "_" + "EventTimeLineReport.pdf";
		File f = new File(fileName);
		FileOutputStream fileOutput = new FileOutputStream(f);
		mainReport.toPdf(fileOutput);
		fileOutput.flush();
		fileOutput.close();
				
		return true;		
	}
	
	private JRDataSource createTimeLineEventDataSource(ArrayList<TimeLineEventNode> eventList) {
		
		  DRDataSource dataSource = new DRDataSource("Key","Date Month","Event","Comment","Event Hour","Date", "Time");
		  
		  for(TimeLineEventNode event : eventList) {
			  
			  dataSource.add(event.getKey(),event.getDateMonth(),event.getEvent(),event.getComment(),event.getHour(),event.getDate(),event.getTimeValue());
			  
			  
		  }
		  
		  return dataSource;
		
	}
	
	public ArrayList<ArrayList<TimeLineEventNode>> classifyEventByYear(List<TimeLineEventNode> eventList) {
		
		Collections.sort(eventList); 
		ArrayList<ArrayList<TimeLineEventNode>> eventListByYear = new ArrayList<ArrayList<TimeLineEventNode>>();
		int prevYear = eventList.get(0).getYear();
		eventListByYear.add(new ArrayList<TimeLineEventNode>());
		int count = 0;
		
		for(TimeLineEventNode event: eventList)
		{
			int year = event.getYear();
			if(prevYear == year) {
				eventListByYear.get(count).add(event);
			} else {
				eventListByYear.add(new ArrayList<TimeLineEventNode>());
				count ++;
				eventListByYear.get(count).add(event);
				prevYear = year;
			}
		}
		
		return eventListByYear;
	}
	
	
	public ArrayList<TimeLineEventNode> createTimeLineEventNodes(List<List<String>> reportData) {
		
		ArrayList<TimeLineEventNode> eventList = new ArrayList<TimeLineEventNode>();
		
		
		for(int i = 1; i < reportData.size(); i++) {
			
			List<String> data = reportData.get(i);
			String timeDateVal = data.get(TIME_DATE_INDEX);
			if(timeDateVal.equals(EMPTY_STRING))
				continue;
			else {
							
				try {
					
					String [] timeDateSplit = timeDateVal.split(SPACE);
					if(timeDateSplit.length != 2)
						continue;
					String dateValString = timeDateSplit[0];
					String timeValString = timeDateSplit[1];
					
					int [] dateVal = processDateData(dateValString);
					int [] timeVal = processTimeData(timeValString);
					
					if(dateVal == null || timeVal == null)
						continue;
					else {
						TimeLineEventNode newEvent = new TimeLineEventNode();
						newEvent.setComment(data.get(COMMENT_INDEX));
						newEvent.setDate(dateValString);
						newEvent.setDateMonth(dateVal[1]);
						newEvent.setEvent(data.get(EVENT_INDEX));
						newEvent.setKey(Integer.parseInt(data.get(KEY_INDEX)));
						newEvent.setTimeValue(timeValString);
						newEvent.setHour(timeVal[0]);
						newEvent.setYear(dateVal[0]);
						
						eventList.add(newEvent);
						
					} 
				} catch(Exception e){
					continue;
				}
			}
	
			
		}
		
		return eventList;
	
	}

	private int[] processDateData(String dateVal) throws Exception {
		
		int [] dateValue = new int [3];
		String [] dateValSplit = dateVal.split(DASH);
		
		if(dateValSplit.length!=3)
			return null;
		
		dateValue[0] = Integer.parseInt(dateValSplit[0]);
		dateValue[1] = Integer.parseInt(dateValSplit[1]);
		dateValue[2] = Integer.parseInt(dateValSplit[2]);
		
		return dateValue;
	}
	
	private int[] processTimeData(String timeVal) throws Exception {
		
		int [] timeValue = new int [3];
		String [] timeValSplit = timeVal.split(COLON);
		
		if(timeValSplit.length!=3)
			return null;
		
		timeValue[0] = Integer.parseInt(timeValSplit[0]);
		timeValue[1] = Integer.parseInt(timeValSplit[1]);
		timeValue[2] = Integer.parseInt(timeValSplit[2]);
		
		return timeValue;
		
	}
	
	public boolean generateReport(Map<String, List<List<String>>> reportData, EagleDevice device, String projectFileName) throws Exception{
		if(reportData==null || device==null || projectFileName==null ){
			return false;
		}
		
		//iterate through reportData map, get the marked items for each plugins.
		for (Map.Entry<String, List<List<String>>> entry : reportData.entrySet()) {
			
			String pluginName = entry.getKey();
			List<List<String>> markedItems = entry.getValue();
			
			if(pluginName.equals(FOLDER_STRUCTURE_PLUGIN))
				generateTableStyleReport(markedItems,device,projectFileName);
			else if (pluginName.equals(TABLE_VIEW_PLUGIN))
				generateDateTimeReport(markedItems, device, projectFileName);
			else
				return false;
			
			//TODO: check for pluginName=FolderStructurePlugin -> generate talbe
			//		check for pluginName=TableViewPlugin	-> generate timeline
			
			
			
			//content in TableViewPlugin's List<List<String>>:
			//"Key","DeviceID","FileID","EventID","Time","event","Comment"
			//
			//To plot graph, use "Time" field which is in the format: "2011-05-10 15:19:15"
			//There will be case that the Time field is empty, null or other values, exclude these from the plot but include in the table 
		}
		
		return true;
	}
	
	//The main method is only used for testing
	//run generateTableStyleReport.
	public static void main(String[] args) throws Exception {
		
		ReportGenerator RG = new ReportGenerator();
		
		List<List<String>> reportData = new ArrayList<List<String>>();
		List<String> entry = new ArrayList<String>();
		
		entry = Arrays.asList("Key","DeviceID","FileID", "EventID","Time","Event","Comment");
		reportData.add(entry);
		
		entry = Arrays.asList("1","10","12", "11","2015-12-13 23:56:00","Nothing","HAHA");
		reportData.add(entry);
		entry = Arrays.asList("2","10","13", "13","","yoyo","You see this u are wrong");
		reportData.add(entry);
		entry = Arrays.asList("3","10","14", "12","2015-11-16 23:00:00","HOHO","Suspect");
		reportData.add(entry);
		entry = Arrays.asList("4","10","15", "45","2016-12-17 07:56:00","HIHI","Wrong phone");
		reportData.add(entry);
		entry = Arrays.asList("5","10","16", "98","aaaaaaaa","NONO","Wierd location");
		reportData.add(entry);
		entry = Arrays.asList("6","10","16", "3","2015-10-16 12:56:00","NINI","Just to add");
		reportData.add(entry);
		entry = Arrays.asList("7","10","16", "4","2015-09-03 03:56:00","NMNMNM","I am a king");
		reportData.add(entry);
		entry = Arrays.asList("8","10","12", "11","2016-01-13 23:56:00","Nothing","HAHA");
		reportData.add(entry);
		entry = Arrays.asList("9","10","13", "13","","yoyo","You see this u are wrong");
		reportData.add(entry);
		entry = Arrays.asList("10","10","14", "12","2015-02-16 23:00:00","HOHO","Suspect");
		reportData.add(entry);
		entry = Arrays.asList("11","10","15", "45","2015-02-17 07:56:00","HIHI","Wrong phone");
		reportData.add(entry);
		entry = Arrays.asList("12","10","16", "98","aaaaaaaa","NONO","Wierd location");
		reportData.add(entry);
		entry = Arrays.asList("13","10","16", "3","2016-05-16 12:56:00","NINI","Just to add");
		reportData.add(entry);
		entry = Arrays.asList("14","10","16", "4","2015-07-03 03:56:00","NMNMNM","I am a king");
		reportData.add(entry);
		entry = Arrays.asList("15","10","16", "3","2015-08-16 12:56:00","NINI","Just to add");
		reportData.add(entry);
		entry = Arrays.asList("16","10","16", "4","2015-09-03 03:56:00","NMNMNM","I am a king");
		reportData.add(entry);
		entry = Arrays.asList("17","10","16", "3","2015-04-16 12:56:00","NINI","Just to add");
		reportData.add(entry);
		entry = Arrays.asList("18","10","16", "4","2015-06-03 03:56:00","NMNMNM","I am a king");
		reportData.add(entry);
		
		
		/*
		entry = Arrays.asList("Plugin Name","Item Name","Details", "Comments");
		reportData.add(entry);
		
		entry = Arrays.asList("Folder Structure Plugin","LOL.png","recovered file", "Suspect picture");
		reportData.add(entry);
		
		entry = Arrays.asList("Call History","Call 1203","Jack to Peter", "Suspect calls");
		reportData.add(entry);
		
		entry = Arrays.asList("Call History","Call 2103","May to Peter", "Suspect calls");
		reportData.add(entry);
		
		entry = Arrays.asList("SMS Plugin","SMS 213","Peter to Jack: Nothing","Suspicious sms");
		reportData.add(entry);
		*/
		Device device = new Device();
		device.modifyDeviceName("Jack Android Phone");
		device.modifyDeviceOwner("Jack");
		
		RG.generateDateTimeReport(reportData, device, "HAHA");
		//RG.generateTableStyleReport(reportData,device,"Jack Project File 1");
		
	}
	
}