package extractor.photorec;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import eagleeye.api.entities.EagleDirectory;
import eagleeye.api.entities.EagleFile;
import eagleeye.pluginmanager.Plugin;

public class PhotoRecRecover implements Plugin{
	private String PhotorecBinaryPath = "PluginBinaries\\PhotoRec\\photorec_win.exe";
	private String PhotorecSorterBinaryPath="PluginBinaries\\PhotoRec\\PhotoRec_Sorter.exe";
	private String TestDiskBinaryPath = "PluginBinaries\\PhotoRec\\testdisk_win.exe";
	private String inputPath;
	private String outputPath;
	private String preflags = "/d";//"/debug /log /d";
	private String postflags = "partition_none,options,mode_ext2,fileopt,everything,enable,search";
	private String testDiskPostflags = "partition_none,advanced,type,b,boot,rebuildbs,list,recursive";
	private Exception err = null;		
	private ArrayList<EagleFile> carvedFileList;
	private ArrayList<EagleDirectory> carvedDirectoryList;
	private ArrayList result;
	
	//constructor
	public PhotoRecRecover (){
		carvedFileList = new ArrayList<EagleFile>();
		carvedDirectoryList = new ArrayList<EagleDirectory>();
		result = new ArrayList();
	}
	
	@Override
	public Object getResult() {
		System.out.println("getResult invoked");
		try {
			runPhotoRec();
			createEntityList();
			result.add(carvedDirectoryList);
			result.add(carvedFileList);
		} catch (Exception e) {
			err=e;
		}
		return result;
	}
	public void runTestDisk() throws Exception{
		
	}
	public void runPhotoRec() throws Exception{

		System.out.println(inputPath);

		String outputFolder=outputPath+"/recup_dir";
		if(outputPath.contains(":/")){
			String tmp[] = outputFolder.split(":/");
			outputFolder=tmp[1];
		}
		System.out.println(outputFolder);
		
		String cmd = "cmd /c "+PhotorecBinaryPath+" "+preflags+" /"+outputFolder+" /cmd "+inputPath+" "+postflags;
        Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		
		//copy photorec sorter executable to output folder
		File source = new File(PhotorecSorterBinaryPath);
		File dest = new File(outputPath+"\\PhotoRec_Sorter.exe");
		long start = System.nanoTime();
		Files.copy(source.toPath(), dest.toPath());
		System.out.println("Time taken by Java8 Files Copy = "+(System.nanoTime()-start));
		
		//execute photorec sorter to put recovered files to folders according to extensions
		PhotorecSorterBinaryPath = outputPath+"\\PhotoRec_Sorter.exe";
		cmd = "cmd /c "+PhotorecSorterBinaryPath;
		p = Runtime.getRuntime().exec(cmd);
		InputStream is = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
		  System.out.println(line);
		}
		
		//delete photorec sorter from output folder
		Files.delete(dest.toPath());	
		
		System.out.println("done");

	}
	public void createFileObjs(File[] files,int parentID,int ID) {
	    int  deviceID =2;
		for (File file : files) {
	        if (file.isDirectory()) {
	            //System.out.println("Directory: " + file.getName());
	        	carvedDirectoryList.add(new PhotoRecDirectory(deviceID,ID,file.getName(),parentID,"createDate","accDate","modDate","delDate", false));
	            createFileObjs(file.listFiles(),ID,ID+1); // Calls same method again.
	        } else {
	           // System.out.println("File: " + file.getName());
	        	String fn = file.getName();
	        	String[] nameExt = fn.split("\\.");
	        	carvedFileList.add(new PhotoRecFile(deviceID,parentID,ID,false,false,false,nameExt[0],"",nameExt[1],"modext","","","","",nameExt[1].toUpperCase()));
	        }
	    }
	}
	public void createEntityList (){
		File root = new File(outputPath);
		System.out.println("=========new version===========");
		if(root.isDirectory()){
			carvedFileList = new ArrayList<EagleFile>();
			createFileObjs(root.listFiles(),0,1);
		}	
		System.out.println(carvedFileList.size());	
	}
	
	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		if(err==null)
			return false;
		else
			err.printStackTrace();
		return true;
	}
	@Override
	public int setParameter(List params) {
		//sanity check
		
		if (params.size()!=2){
			System.out.println("PhotoRecRecover receives incorrect number of inputs");
			return 1;
		}
		
		for(int i=0;i<params.size();i++){
			Object o = params.get(i);
			if(o.getClass()!=String.class){
				System.out.println("incorrect input format. Expect String, get "+o.getClass().getName());
				return 2;
			}
			if(i==0){
				inputPath=(String)o;
			}
			if(i==1){
				outputPath=(String)o;
			}
		}
		return 0;	
	}

	@Override
	public String getName() {
		return "Photorec Extractor";
	}

	@Override
	public Type getType() {
		return Type.EXTRACTOR;
	}
}
