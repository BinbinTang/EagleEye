package eagleeye.pluginmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import eagleeye.api.entities.EagleFile;

public class PhotoRecRecover implements Plugin{
	private String PhotorecBinaryPath = "PluginBinaries\\PhotoRec\\photorec_win.exe";
	private String PhotorecSorterBinaryPath="PluginBinaries\\PhotoRec\\PhotoRec_Sorter.exe";
	private String inputPath;
	private String outputPath;
	private String preflags = "/d";//"/debug /log /d";
	private String postflags = "partition_none,options,mode_ext2,fileopt,everything,enable,search";
	private Exception err = null;		
	private static ArrayList<EagleFile> carvedFileList;
	
	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		try {
			runPhotoRec();
			createEntityList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			err=e;
		}
		return carvedFileList;
	}
	
	public void runPhotoRec() throws Exception{
		Scanner sc = new Scanner(System.in);
		
		//System.out.println("Enter the input disk image's full path, use \"/\" as folder separater:");
		//inputPath = sc.next();
		//if(inputPath.equals("@")) inputPath="";
		//if(inputPath==null || inputPath.equals("")) {
		//	/*System.out.println("ERROR: Please enter a valid pull path such as D:/Case2/mtd8.dd");
		//	System.out.println("Terminating...");
		//	return;*/
		//	inputPath="D:/MyFolder/y4/CS3283_MediaTech_Project/Case2/mtd8.dd";
		//}
		System.out.println(inputPath);
		//System.out.println("Enter full path to which the output files will be stored , use \"/\" as folder separater:");
		//outputPath = sc.next();
		//if(outputPath.equals("@")) outputPath="";
		//if(outputPath==null || outputPath.equals("")) {
		//	/*System.out.println("ERROR: Please enter a valid pull path such as D:/Case2/mtd8.dd");
		//	System.out.println("Terminating...");
		//	return;*/
		//	outputPath =  "D:/MyFolder/y4/CS3283_MediaTech_Project/CodeX/EagleEye/EagleEye/Output";
		//}
		String outputFolder=outputPath+"/recup_dir";
		if(outputPath.contains(":/")){
			String tmp[] = outputFolder.split(":/");
			outputFolder=tmp[1];
		}
		System.out.println(outputFolder);
		sc.close();
		
		/* Uncheck the following command to create a txt file containing the default execution path */	
		//String cmd = "cmd /c chdir>pwd.txt";
		
		//execute photorec to recover file
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
	public static void createFileObjs(File[] files,int parentID,int ID) {
	    for (File file : files) {
	        if (file.isDirectory()) {
	            //System.out.println("Directory: " + file.getName());
	        	carvedFileList.add(new PhotoRecFile(0,parentID,ID,false,false,true,file.getName(),file.getPath(),"ext","modext","delDate","createDate","accDate","modDate","category"));
	            createFileObjs(file.listFiles(),ID,ID+1); // Calls same method again.
	        } else {
	           // System.out.println("File: " + file.getName());
	        	String fn = file.getName();
	        	String[] nameExt = fn.split("\\.");
	        	carvedFileList.add(new PhotoRecFile(0,parentID,ID,false,false,false,nameExt[0],"",nameExt[1],"modext","","","","",nameExt[1].toUpperCase()));
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
	public int passParam(List params) {
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
	public String pluginName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}


	@Override
	public String pluginFunction() {
		// TODO Auto-generated method stub
		return "ImageUnpacker";
	}
}
