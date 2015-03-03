package eagleeye.test;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.stage.Stage;
import eagleeye.entities.Device;
import eagleeye.entities.FileEntity;
import org.junit.Assert;
import eagleeye.dbcontroller.DBInsertTransaction;
import eagleeye.dbcontroller.DBQueryController;
import eagleeye.entities.Directory;
import eagleeye.entities.Filter;
import eagleeye.filesystem.format.FormatDescription;
import eagleeye.filesystem.format.FormatIdentifierManager;
import eagleeye.filesystem.partition.mtd.MTDDefinition;
import eagleeye.filesystem.partition.mtd.MTDPartitionDefinition;
//import eagleeye.utils.FileFormatIdentifier;
import eagleeye.view.NewDeviceDialogController;

public class test {
	

public void testDevice(){
Device myD=new Device();
File myFile=new File("ss");
myD.modifiyDeviceImageFolder(myFile);
myD.modifyDeviceID(1);
myD.modifyDeviceName("testDevice");
myD.modifyContentSize("size");
myD.modifyLastViewedOn("20130102");
myD.modifyDateCreated("20120201");
myD.modifyDeviceOwner("ee");
myD.modifyDeviceFolderPath("/Users/MACHERIE/Documents/");

Assert.assertSame("/Users/MACHERIE/Documents/",myD.getDeviceFolderPath() );
//	public File getDeviceImageFolder() 

Assert.assertSame(1,myD.getDeviceID());
Assert.assertSame ("testDevice",myD.getDeviceName());
Assert.assertSame("size",myD.getContentSize());
Assert.assertSame ("20130102",myD.getLastViewedOn());
Assert.assertSame ("20120201",myD.getDateCreated());
Assert.assertSame ("ee",myD.getDeviceOwner());

}
public void testDirectory(){
Directory myDir=new Directory();
myDir.modifyDeviceID(1);
myDir. modifyDirectoryID(2);
myDir. modifyDirectoryName("test");
myDir. modifyParentDirectory(3);
myDir.modifyDateCreated("20110201");
myDir.modifyDateAccessed("20120201");
myDir. modifyDateModified("20121010");
myDir.modifyIsRecovered(true);

Assert.assertSame(1,myDir.getDeviceID());
Assert.assertSame (2,myDir.getDirectoryID());
Assert.assertSame ("test",myDir.getDirectoryName());
Assert.assertSame (3,myDir.getParentDirectory());
Assert.assertSame  ("20110201",myDir.getDateCreated());
Assert.assertSame ("20120201",myDir.getDateAccessed());
Assert.assertSame ("20121010",myDir.getDateModified());
Assert.assertTrue (myDir.getIsRecovered());


}
public void testFileEntity(){
FileEntity myF=new FileEntity();

myF.modifyIsDirectory(true);
myF. modifyIsRecovered(false);
myF. modifyIsModified(true);

myF.modifyFileName("test");
myF.modifyFilePath("/Users/MACHERIE/Documents/");
myF. modifyFileExt("test");
myF. modifyFileExtID(4);
myF.modifyContentType("txt");
myF. modifyCategory("text");
myF.modifyDeviceID(1);
myF. modifyDirectoryID(2);
myF.modifyFileID(3);
myF. modifyDirectoryName("test");
myF.modifyDateCreated("20110201");
myF.modifyDateAccessed("20120201");
myF. modifyDateModified("20121010");

Assert.assertTrue(myF.getIsDirectory());
Assert.assertFalse(myF.getIsRecovered());
Assert.assertTrue(myF.getIsModified());

Assert.assertSame("test",myF.getFileName());
Assert.assertSame("/Users/MACHERIE/Documents/",myF.getFilePath());
Assert.assertSame("test",myF.getFileExt());
Assert.assertSame(4,myF.getFileExtID());
Assert.assertSame("txt",myF.getContentType());
Assert.assertSame("text",myF.getCategory());
Assert.assertSame(1,myF.getDeviceID());
Assert.assertSame(2,myF.getDirectoryID());
Assert.assertSame(3,myF.getFileID());
Assert.assertSame("test",myF.getDirectoryName());
Assert.assertSame("20110201",myF.getDateCreated());
Assert.assertSame("20120201",myF.getDateAccessed());
Assert.assertSame("20121010",myF.getDateModified());

}
public void testFilter(){
Filter myFt=new Filter();
LocalDate today = LocalDate.now();

myFt.modifyStartTimeDaily("a"); 
myFt.modifyEndTimeDaily("b") ;
myFt.modifyKeyword("key") ;
myFt.modifyStartDate(today);
myFt.modifyEndDate(today) ;
myFt.modifyStartTime("c") ;
myFt.modifyEndTime("d");

myFt.modifyIsModified(false); 
myFt.modifyIsRecovered(false);
myFt.modifiyIsOriginal(true) ;
boolean res=false;
boolean res2=false;
boolean res3=false;
boolean res4=false;
boolean res5=false;

System.out.println(myFt.getStartTimeDaily());

if("a:00".equals(myFt.getStartTimeDaily())){
res=true;
}
if("b:00".equals(myFt.getEndTimeDaily())){
res2=true;
}
if("c:00".equals(myFt.getStartTime())){
res3=true;
}
if("d:00".equals(myFt.getEndTime())){
res4=true;
}
if("%key%".equals(myFt.getKeyword())){
res5=true;
}

Assert.assertTrue(res);
Assert.assertTrue(res2 );
Assert.assertTrue(res5);
Assert.assertSame(today,myFt.getStartDate());
Assert.assertSame(today,myFt.getEndDate() );
Assert.assertTrue(res3);
Assert.assertTrue(res4);
Assert.assertSame(0,myFt.getIsModified());
Assert.assertSame(0,myFt.getIsRecovered());
Assert.assertSame(1,myFt.getIsOriginal());


//	public void setCategoryFilter(ArrayList<String> categoryFilter)
}

/*
public void testFileFormatIdentifier(){

FileFormatIdentifier myFFI=new FileFormatIdentifier();
String ImagePath="/Users/MACHERIE/Documents/test.png";
String TextPath="/Users/MACHERIE/Documents/try.txt";
String UnidentifiedPath="/Users/MACHERIE/Documents/music.mp3";

myFFI.checkFormat(ImagePath);
Assert.assertTrue(myFFI.isPhoto);
myFFI.checkFormat(TextPath);
Assert.assertTrue(myFFI.isText);
myFFI.checkFormat(UnidentifiedPath);
Assert.assertTrue(myFFI.isUnidentified);
}*/


public void testFormatDescription(){

FormatDescription myFD=new FormatDescription();
File file= new File("xx");
myFD.setBinaryImageType("a");
myFD.setFile(file);
myFD.setOperatingSystem("b");
myFD.setDeviceName("c");

Assert.assertSame("a",myFD.getBinaryImageType());
Assert.assertSame(file,myFD.getFile());
Assert.assertSame("b",myFD.getOperatingSystem());
Assert.assertSame("c",myFD.getDeviceName());
}
public void testFormatIdentifierManager(){
FormatIdentifierManager myFIFM=new FormatIdentifierManager();
FormatDescription myFD=new FormatDescription();

File file =new File("xxx");

myFD.setFile(file);
myFD.setDeviceName("a");
myFD.setBinaryImageType("b");
myFD.setOperatingSystem("os");

//myFIFM.load(IFormatIdentifier formatIdentifier);
//myFIFM.unload(IFormatIdentifier formatIdentifier);

Assert.assertSame(myFD,myFIFM.identify(file));
}

public void testMTDDefinition(){

MTDPartitionDefinition myMTDP= new MTDPartitionDefinition("a",1,2);
ArrayList<MTDPartitionDefinition> list=new ArrayList<MTDPartitionDefinition>();
list.add(myMTDP);

MTDDefinition myMTDD=new MTDDefinition("id",list);

Assert.assertSame(list,myMTDD.getParitionDefinitions());
Assert.assertSame("id",myMTDD.getID());
}
public void testMTDPartitionDefinition(){
MTDPartitionDefinition myMTDPD= new MTDPartitionDefinition("name",1,2);
Assert.assertSame("name",myMTDPD.getName());
Assert.assertSame(1,myMTDPD.getSizeInBytes());
Assert.assertSame(2,myMTDPD.getOffsetInBytes());

}
//---------------------------------------------identifier-----------------------------
public void testYAFFS2FormatIdentifier(){


}

public void testAndroidBootFormatIdentifier(){


}
public void testFAT32FormatIdentifier(){


}


public void testNewDeviceDialogController(){
Stage dialog = new Stage();
NewDeviceDialogController myNDD= new NewDeviceDialogController();
myNDD.setDialogStage(dialog);
//  myNDD.initiailize();
}

//---------------------------------------------db-----------------------------
public void testDBInsertController(){

/*	DBInsertController myDBIC=new DBInsertController();
        Device device=new Device();
   
        Connection con = DriverManager.getConnection  ("jdbc:derby://localhost:1527/testDb","name","pass");
        PreparedStatement stmt = con.prepareStatement("aaa");
      //  PreparedStatement stmt=;
       *  
        FileEntity fe=new FileEntity();
        ArrayList<FileEntity> list=new ArrayList<FileEntity>();
        list.add(fe);
        myDBIC.insertNewDevice(device, stmt, qstmt);
myDBIC. insertNewRootDirectory(stmt);
myDBIC.insertNewDirectory(list, stmt);
        myDBIC.insertNewFileExt(list, stmt) ;
myDBIC.insertNewFile(list, stmt) ;
        Assert.assertSame( myDBIC.getDirectoryAutoIncrementMarker(stmt) );
        Assert.assertSame(myDBIC.getAllFileExt(list));

myDBIC.updateDirectoryRoute( stmt, 1, 0);
myDBIC.updateFileExtID( stmt);
myDBIC.updateFileDirectoryID(stmt, 1, 0);
myDBIC.getDirectoryAutoIncrementMarker( stmt) ;
myDBIC.getAllFileExt(list);

*/
}
public void testDBInsertTransaction (){

DBInsertTransaction myDBIT=new DBInsertTransaction();
FileEntity fe=new FileEntity();
ArrayList<FileEntity> list = new ArrayList<FileEntity>();
list.add(fe);
ArrayList<ArrayList<FileEntity>> listOfList=new ArrayList<ArrayList<FileEntity>>();
Device newDevice=new Device();
//Assert.assertTrue(myDBIT.insertNewDeviceData(newDevice, listOfList) );
//separateFilesAndDirectory(ArrayList<FileEntity> FilesList) 

}
public void testDBQueryController(){
DBQueryController myDBQC=new DBQueryController();
myDBQC.setDeviceID(1);
Assert.assertSame(1,myDBQC.getDeviceID());
/*	ArrayList<EagleDevice> myDBQC.getAllDevices(); 
ArrayList<String> myDBQC.getAllDeviceNames() ;

ArrayList<EagleDirectory> myDBQC.getAllDirectoriesAndFiles() ;

ArrayList<EagleFile> myDBQC.getAllFiles() ;

ArrayList<EagleFile> myDBQC.getFilteredFiles(EagleFilter filter); 

String myDBQC.getDeviceRootPath() ;

ArrayList<EagleDirectory> myDBQC.organizeFilesAndDirectory (ArrayList<EagleDirectory> listOfDirectory, ArrayList<EagleFile> listOfFiles);

PreparedStatement myDBQC.setFieldsForFilter(PreparedStatement stmt,boolean isKeywordPresent, EagleFilter filter) ;

*/
}


}
