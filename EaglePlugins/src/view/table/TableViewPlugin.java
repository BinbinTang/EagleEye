package view.table;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

import reader.SQLiteReaderPlugin;  //comment out after test
import view.folderstructure.FolderStructureTreePlugin.MyTreeItem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import eagleeye.api.dbcontroller.DBController;
import eagleeye.api.entities.EagleFile;
import eagleeye.api.plugin.*;


public class TableViewPlugin extends Application implements Plugin {
	private DBController dbController;
	private Plugin sqlReader;
	private HBox view;
    private Map<String, TimeEvent> markedItems;
    
	public class TimeEvent {
		private int deviceID;
		private int fileID;
		private int itemID;
		private String time;
        private String event;
        private String comment;
        private boolean marked;
        private String displayMark;
        
        
        public TimeEvent(int deviceID, int fileID, int itemID, String time, String event, String comment, boolean marked) {
        	setDeviceID(deviceID);
        	setFileID(fileID);
        	setItemID(itemID);
        	setTime(time);
        	setEvent(event);
        	setMarked(marked);
        	setComment(comment);
        }
       
		@Override
		public String toString() {
			return "TimeEvent [deviceID=" + deviceID + ", fileID=" + fileID
					+ ", itemID=" + itemID + ", time=" + time + ", event=..."+", marked="+marked+"]";
		}
		
		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getDisplayMark() {
			return displayMark;
		}

		public void setDisplayMark(String displayMark) {
			this.displayMark = displayMark;
		}

		public boolean isMarked() {
			return marked;
		}

		public void setMarked(boolean marked) {
			this.marked = marked;
			if(this.marked==true){
				setDisplayMark("MARK");
			}else{
				setDisplayMark("");
			}
		}

		public int getDeviceID() {
			return deviceID;
		}

		public void setDeviceID(int deviceID) {
			this.deviceID = deviceID;
		}

		public int getFileID() {
			return fileID;
		}

		public void setFileID(int fileID) {
			this.fileID = fileID;
		}

		public int getItemID() {
			return itemID;
		}

		public void setItemID(int itemID) {
			this.itemID = itemID;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getEvent() {
			return event;
		}

		public void setEvent(String event) {
			this.event = event;
		}

    }
	
	
	public class FileItem {
		 
        private String name;
        private String path;
        int deviceID;
        int fileID;
        public FileItem(int deviceID, int fileID, String name, String path) {
        	setDeviceID(deviceID);
        	setFileID(fileID);
        	setName(name);
        	setPath(path);
        }
        
		public int getDeviceID() {
			return deviceID;
		}

		public void setDeviceID(int deviceID) {
			this.deviceID = deviceID;
		}

		public int getFileID() {
			return fileID;
		}

		public void setFileID(int fileID) {
			this.fileID = fileID;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
		@Override
		public String toString(){
			return name;
		}

    }
	
	/*
	public class myTableCell extends TableCell<TimeEvent,String>{

		@Override
		protected void updateItem(String item, boolean empty){
			super.updateItem(item, empty);
			setText(item);
			if(!empty){
				TableRow row = getTableRow();
				if (row!=null){
					System.out.println("update row ID = "+ getTableRow().getIndex());
					TimeEvent evt = (TimeEvent) getTableRow().getItem();
					if(evt==null){
						System.out.println("update row, item null");
					}
					else if(evt.isMarked()){
						System.out.println(evt.getItemID()+" "+evt.isMarked());
						getTableRow().setStyle("-fx-background-color: yellow");
					}
				}
			}
		}
	}
	*/
	public TableViewPlugin(){
	}
	public void printMarkedItems(){
		System.out.println("["+getName()+"] MarkedItems:");
		for(Map.Entry<String, TimeEvent> entry : markedItems.entrySet()){
			System.out.println(entry.getValue());
		}
	}
	
	public void addToMarkedList(TimeEvent evt){
		String key = evt.getDeviceID()+","+evt.getFileID()+","+evt.getItemID();
		markedItems.put(key,evt);
	}
	public void removeFromMarkedList(TimeEvent evt){
		String key = evt.getDeviceID()+","+evt.getFileID()+","+evt.getItemID();
		markedItems.remove(key);
	}
	public void updateMarkedList(TimeEvent evt){
		if(evt.isMarked()){
			addToMarkedList(evt);
		}else{
			removeFromMarkedList(evt);
		}
	}
	public String timestampToDate(String timestamp){
		if(timestamp==null || timestamp.equals("") || timestamp.length()<10) return timestamp;
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		timestamp = timestamp.substring(0, 10);
		try{
			Long time=new Long(Integer.parseInt(timestamp));
			//time += new Long(978307200);
			time = time*1000;
			return format.format(time);
		}catch(Exception e){
			return timestamp;
		}
	}
	public Node getLeftPane() {
		if(view==null || view.getChildren().size()<1) return null;
		return view.getChildren().get(0);
	}
	public void setLeftPane(Node leftPane) {
		if(view==null) return;
		if(view.getChildren().size()<1){
			view.getChildren().add(leftPane);
		}else{
			view.getChildren().set(0, leftPane);
		}
	}
	public Node getRightPane() {
		if(view==null || view.getChildren().size()<2) return null;
		return view.getChildren().get(1);
	}
	public void setRightPane(Node rightPane) {
		if(view==null) return;
		if(view.getChildren().size()<2){
			view.getChildren().add(rightPane);
		}else{
			view.getChildren().set(1, rightPane);
		}
		HBox.setHgrow(rightPane, Priority.ALWAYS);
	}
	public Text generateTextPane(String status){
		return new Text(status);
	}
	
	public TableView generateTablePane(int deviceID, int fileID, List<List<String>> events){
		TableView<TimeEvent> tablePane = new TableView<TimeEvent>();
		tablePane.setEditable(false);
		TableColumn<TimeEvent, String> markCol = new TableColumn<TimeEvent, String>("Marked");
		markCol.setCellValueFactory(new PropertyValueFactory<TimeEvent, String>("displayMark"));
		TableColumn<TimeEvent, Integer> IDCol = new TableColumn<TimeEvent, Integer>("ID");
		IDCol.setCellValueFactory(new PropertyValueFactory<TimeEvent, Integer>("itemID"));
        TableColumn<TimeEvent, String> timeCol = new TableColumn<TimeEvent, String>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<TimeEvent, String>("time"));
        TableColumn<TimeEvent, String> eventCol = new TableColumn<TimeEvent, String>("Event");
        eventCol.setCellValueFactory(new PropertyValueFactory<TimeEvent, String>("event"));
        TableColumn<TimeEvent, String> commentCol = new TableColumn<TimeEvent, String>("Comment");
        commentCol.setCellValueFactory(new PropertyValueFactory<TimeEvent, String>("comment"));
		ObservableList<TimeEvent> eventList = FXCollections.observableArrayList(); 
		// Install custom cell renderer
		//eventCol.setCellFactory(column->{
		//	return new myTableCell();
		//});
        
		int id=0;
		for (List<String> event: events){
			++id;
			boolean marked;
			String comment="";
			String key = deviceID+","+fileID+","+id;
			if( markedItems.containsKey(key)){
				marked=true;
				comment = markedItems.get(key).getComment();
			}else{
				marked=false;
			}
			eventList.add(new TimeEvent(deviceID, fileID, id, timestampToDate(event.get(0)), event.get(1),comment ,marked));
        }
		tablePane.setItems(eventList);
		tablePane.getColumns().addAll(markCol,IDCol,timeCol, eventCol,commentCol);
		tablePane.setOnMouseClicked(new EventHandler<MouseEvent>() {

		    @Override
		    public void handle(MouseEvent click) {

		        if (click.getClickCount() == 2) {
		           
		        	//remove previous item 
		           TimeEvent currentItemSelected = tablePane.getSelectionModel().getSelectedItem();
		           int idx = tablePane.getItems().indexOf(currentItemSelected);
		           currentItemSelected = tablePane.getItems().remove(idx);
		           //create updated item
		           TimeEvent currentItemSelectedCopy = new TimeEvent(currentItemSelected.getDeviceID(),
		        	   												currentItemSelected.getFileID(),
		        		   											currentItemSelected.getItemID(),
		        		   											currentItemSelected.getTime(),
		        		   											currentItemSelected.getEvent(),
		        		   											currentItemSelected.getComment(),
		        		   											!currentItemSelected.isMarked());
		           if (!currentItemSelectedCopy.isMarked()){
		        	   currentItemSelectedCopy.setComment("");
		           }
		           tablePane.getItems().add(idx, currentItemSelectedCopy);
		           System.out.println(currentItemSelectedCopy);
		           //update selection
		           if(idx==0){
		        	   tablePane.getSelectionModel().selectFirst();
		           }else{
		        	   tablePane.getSelectionModel().selectIndices(idx);
		           }
		           //update markedItems
		           updateMarkedList(tablePane.getItems().get(idx));
		           //print all marked Items
		           printMarkedItems();
		        }
		    }
		});
		return tablePane;
	}
	public ListView generateListPane(){
		//get all sql files
		List<EagleFile> fList = dbController.getAllFiles();
		List<EagleFile> sqlList = new ArrayList<EagleFile>();
		String[] keys = {"db","sqlite"};
		for(EagleFile f:fList){
			String ext = f.getFileExt();
			for(String dbExt: keys){
				if(ext.equalsIgnoreCase(dbExt)){
					sqlList.add(f);
					break;
				}
			}
		}
		
		List<FileItem> itemList = new ArrayList<FileItem>();
		for(EagleFile f: sqlList){
			String fname = f.getFileName()+"."+f.getFileExt();
			String fpath = f.getFilePath()+File.separator+fname;
			int deviceID = f.getDeviceID();
			int fileID	 = f.getFileID();
			itemList.add(new FileItem(deviceID, fileID, fname,fpath));
		}
		
		ListView<FileItem> fileListPane = new ListView<FileItem>();
		ObservableList<FileItem> fileList = FXCollections.observableArrayList(itemList); 
		fileListPane.setItems(fileList);

		fileListPane.setOnMouseClicked(new EventHandler<MouseEvent>() {

		    @Override
		    public void handle(MouseEvent click) {

		        if (click.getClickCount() == 2) {        
		           //analyze file
		           analyzeFile(fileListPane.getSelectionModel().getSelectedItem());
		           
		        }
		    }
		});
		return fileListPane;
	}
	public void analyzeFile(FileItem f){
		String filePath = f.getPath();
        int deviceID = f.getDeviceID();
        int fileID = f.getFileID();
		System.out.println(filePath);

		//use sqlreader
		List<List<String>> eventList = readSQLFile(filePath);
		setRightPane(generateTablePane(deviceID, fileID, eventList));


	}
	public List<List<String>> readSQLFile(String filePath){
		List<List<String>> evenList = new ArrayList<List<String>>();
		//String root = ".."+File.separator+"EagleEye"+File.separator+"output"+File.separator+"PLUG"+File.separator+"mtd8.dd"+File.separator+"mtd8.dd";
		//filePath = root+File.separator+"data"+File.separator+"com.google.android.providers.gmail"+File.separator+"databases"+File.separator+"mailstore.yobtaog@gmail.com.db";
		
		//invoke sqlreader plugin
		List paths = new ArrayList();
		paths.add(filePath);
		sqlReader.setParameter(paths);
		Map<String, List<List<String>>> tables = (Map<String, List<List<String>>>) sqlReader.getResult();
		for (Map.Entry<String, List<List<String>>> entry : tables.entrySet()) { 
			List<List<String>> parsedResult =  parseTimeEvent(entry.getValue());
			if(parsedResult!=null){
				for(List<String> timeEvent: parsedResult){
					evenList.add(timeEvent);
				}
			}
		}
		if(tables==null) return null;
			
		return evenList;
	}
	public List<List<String>> parseTimeEvent(List<List<String>> table){
		
		String[] keywords = {"time","date","day","start","end","created","modified"};
		List<String> header = table.get(0);
		int timeColIdx=-1;
		for(int i=0; i<header.size(); i++){
			String name = header.get(i);
			for(String timeLikeString: keywords){
				if(name.toLowerCase().contains(timeLikeString)){
					timeColIdx = i;
					break;
				}
			}
			if(timeColIdx!=-1){
				break;
			}else{
				continue;
			}
		}
		if(timeColIdx==-1){
			return null;
		}else{
			List<List<String>> parsedList = new ArrayList<List<String>>();
			for(int i=1; i<table.size(); i++){
				List<String> entry = table.get(i);
				StringBuilder sb = new StringBuilder();
				String time="";
				for(int j=0; j<entry.size(); j++){
					String cell = entry.get(j);
					if(j==timeColIdx){
						time = cell;
					}else{
						sb.append("["+header.get(j)+"] "+cell+"\n");
					}
				}
				List<String> timeEvent = new ArrayList<String>();
				timeEvent.add(time);
				timeEvent.add(sb.toString());
				parsedList.add(timeEvent);
			}
			return parsedList;
		}		
	}
	@Override
	public String getName() {
		return "Table View";
	}

	@Override
	public Type getType() {
		return Type.GUI_VIEW;
	}

	@Override
	public int setAvailablePlugins(List<Plugin> plugins) {
		if(plugins == null) return 0;
		sqlReader = plugins.get(0);
		return 0;
	}

	@Override
	public int setParameter(List params) {
		if(params.size()!=1)
			return 1;
		Object o = params.get(0);
		Class[] interfaces = o.getClass().getInterfaces();
		if(interfaces[0].equals(DBController.class)){
			dbController = (DBController) o;
			/*
			if(markedItemsHash == null){
				initItemList();
			}*/
			return 0;
		}
		return 1;
	}

	@Override
	public Object getResult() {
		view = new HBox();
		
		//left pane
		setLeftPane(generateListPane());
		//right pane
		setRightPane(generateTextPane("No file selected."));
		
		return view;
	}

	@Override
	public Object getMarkedItems() {
		//markedItems
		List<List<String>> markedItemsResult = new ArrayList<List<String>>();
		List<String> headers = new ArrayList<String>();
		headers.add("Key");
		headers.add("DeviceID");
		headers.add("FileID");
		headers.add("EventID");
		headers.add("Time");
		headers.add("event");
		headers.add("Comment");
		markedItemsResult.add(headers);
		for(Map.Entry<String, TimeEvent> entry : markedItems.entrySet()){
			List<String> item = new ArrayList<String>();
			item.add(entry.getKey());
			item.add(entry.getValue().getDeviceID()+"");
			item.add(entry.getValue().getFileID()+"");
			item.add(entry.getValue().getItemID()+"");
			item.add(entry.getValue().getTime());
			item.add(entry.getValue().getEvent());
			item.add(entry.getValue().getComment());
			markedItemsResult.add(item);
		}
		return markedItemsResult;
	}

	@Override
	public void setMarkedItems(Object items) {
		markedItems = new HashMap<String, TimeEvent>();
		List<List<String>> castedItems = new ArrayList<List<String>>();
		if(items!=null && items.getClass().equals(castedItems.getClass())){
			castedItems = (List<List<String>>) items;
			for(int i=1; i<castedItems.size(); i++){
				String key = castedItems.get(i).get(0);
				int deviceID	= Integer.parseInt(castedItems.get(i).get(1));
				int fileID		= Integer.parseInt(castedItems.get(i).get(2));
				int itemID		= Integer.parseInt(castedItems.get(i).get(3));
				String time		= castedItems.get(i).get(4);
		        String event	= castedItems.get(i).get(5);
				String comment	= castedItems.get(i).get(6);
				markedItems.put(key, new TimeEvent(deviceID,fileID,itemID,time,event,comment,true));
			}
		}
	}

	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/****************** start of test ****************/

	@Override
	public void start(Stage primaryStage) throws Exception {
		TableViewPlugin tvp = new TableViewPlugin();
		List<Plugin> pls= new ArrayList<Plugin>();
		pls.add(new SQLiteReaderPlugin());
		tvp.setAvailablePlugins(pls);
		
		Node view = (Node)tvp.getResult();
		BorderPane viewContainer = new BorderPane();
		viewContainer.setCenter(view);
		primaryStage.setTitle(this.getName());
	    primaryStage.setScene(new Scene(viewContainer)); 

        //primaryStage.setScene(scene);
        primaryStage.show();

	    // Set On Close Window
	    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
            	System.out.println("exit");
                Platform.exit();
                System.exit(0);
            }
        });	
		
	}
	public static void main(String[] args){
		launch(args);
	}
	
	/****************** end of test *****************/

}
