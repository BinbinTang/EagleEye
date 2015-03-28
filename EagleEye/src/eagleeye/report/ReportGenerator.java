package eagleeye.report;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;



public class ReportGenerator {

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
		File f = new File("./TableReport.pdf");
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
	
	private JRDataSource createBarChartCounterDataSource(List<List<String>> reportData, String groupBy) {
		
		int groupByIndex = -1;
		boolean existGroupName = false;
		List<ChartCounter> BarChartCounter = new ArrayList<ChartCounter>();
		
		for(int i = 0; i<reportData.get(0).size();i++) {
			
			String groupByName = reportData.get(0).get(i);
			if(groupByName.equals(groupBy)) {
				groupByIndex = i;
				break;
			}
		}
		
		if(groupByIndex == -1)
			return null;
		
		for(int i = 0; i<reportData.size(); i++) {
			
			String groupName = reportData.get(i).get(groupByIndex);
			
			for(ChartCounter counter : BarChartCounter) {
				
				if(groupName.equals(counter.getName())) {
					counter.addCount();
					existGroupName = true;
					break;
				}
			}
			
			if(!existGroupName) {
				ChartCounter newCounter = new ChartCounter(groupName);
				BarChartCounter.add(newCounter);
			}
			
			existGroupName = false;
		}
		
		DRDataSource dataSource = new DRDataSource(groupBy, "Quantity");
		
		for(ChartCounter counter : BarChartCounter) {
			
			System.out.println(counter.getName() + " " + counter.getCount());
			dataSource.add(counter.getName(),counter.getCount());
		}
		
		return dataSource;
		
	}
	
	public boolean generateBarChartReport (List<List<String>> reportData, EagleDevice device, String projectFileName, String groupBy) throws Exception {
		
		if(reportData==null || device==null || projectFileName==null ){
			//TODO: is projectfile necessary?
			return false;
		}
		
		JasperConcatenatedReportBuilder mainReport = DynamicReports.concatenatedReport();
		JasperReportBuilder barChartReport = DynamicReports.report();
		JasperReportBuilder dataDetailReport = DynamicReports.report();
		
		String reportTitle = "Marked Files Report in 3D Bar Chart group by " + groupBy;
		
		barChartReport = setUpReport(barChartReport,reportTitle,device,projectFileName);
		dataDetailReport.setPageFormat(PageType.A4, PageOrientation.LANDSCAPE);
		TextColumnBuilder<String> groupByColumn = null;
		
		// Styles
		StyleBuilder boldStyle = DynamicReports.stl.style().bold();
		StyleBuilder boldStyleCenter = DynamicReports.stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.CENTER);
		StyleBuilder centerText = DynamicReports.stl.style().setHorizontalAlignment(HorizontalAlignment.CENTER);
		StyleBuilder columnHeaderStyle = DynamicReports.stl.style(boldStyleCenter).setBorder(
					DynamicReports.stl.pen1Point()
					).setBackgroundColor(Color.LIGHT_GRAY);
		
		//initialize column for bar Chart Data
		TextColumnBuilder<String> groupColumn = Columns.column(groupBy, groupBy,DynamicReports.type.stringType());
		TextColumnBuilder<Integer> quantityColumn = Columns.column("Quantity", "Quantity",DynamicReports.type.integerType());
		barChartReport.addColumn(groupColumn);
		barChartReport.addColumn(quantityColumn);
		
		// set column header Style
		barChartReport.setColumnTitleStyle(columnHeaderStyle);
		
		// Highlight rows for better outlook
		barChartReport.highlightDetailEvenRows();
				
		// add data source
		JRDataSource barChartData = createBarChartCounterDataSource(reportData, groupBy);
		barChartReport.setDataSource(barChartData).setColumnStyle(centerText);
						
		//add Charts
		Bar3DChartBuilder chart = DynamicReports.cht.bar3DChart().setTitle("Bar Chart Report").setCategory(groupColumn).addSerie(DynamicReports.cht.serie(quantityColumn));
		barChartReport.summary(chart);
		
		//DETAIL DATA SECTION
		
		// add columns
		List<String> columnNames = getColumnNames(reportData);
		for(String columnName : columnNames){
			
			TextColumnBuilder<String> column = Columns.column(columnName, columnName,DynamicReports.type.stringType());
			dataDetailReport.addColumn(column);
			if(columnName.equals(groupBy))
				groupByColumn = column;
		}
		
		// set column header Style
		dataDetailReport.setColumnTitleStyle(columnHeaderStyle);
		
		// add data source
		JRDataSource data = createDataSource(reportData);
		dataDetailReport.setDataSource(data).setColumnStyle(centerText);
		
		// Highlight rows for better outlook
		dataDetailReport.highlightDetailEvenRows();
		dataDetailReport.groupBy(groupByColumn).setTextStyle(boldStyle);
		
		mainReport.concatenate(barChartReport,dataDetailReport);
		mainReport.toPdf(Exporters.pdfExporter("./barChartReport.pdf"));
			
		return true;
	}
	
	
	//The main method is only used for testing
	//run generateTableStyleReport.
	public static void main(String[] args) throws Exception {
		
		ReportGenerator RG = new ReportGenerator();
		
		List<List<String>> reportData = new ArrayList<List<String>>();
		List<String> entry = new ArrayList<String>();
		
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
		
		Device device = new Device();
		device.modifyDeviceName("Jack Android Phone");
		device.modifyDeviceOwner("Jack");
		
		//RG.generateTableStyleReport(reportData,device,"Jack Project File 1");
		RG.generateBarChartReport(reportData, device, "Jack Project File 1 ", "Plugin Name");
	}

}