package eagleeye.pluginmanager;

import java.util.Scanner;

public class PhotoRecRecover implements Plugin{
	private String binaryPath = "PluginBinaries\\PhotoRec\\photorec_win.exe";
	private String inputPath = "";//"D:/MyFolder/y4/CS3283/Case2/mtd8.dd";
	private String outputPath = "";//"MyFolder/y4/CS3283/CodeX/EagleEye/EagleEye/PluginBinaries/PhotoRec";
	private String preflags = "/d";//"/debug /log /d";
	private String postflags = "partition_none,options,mode_ext2,fileopt,everything,enable,search";
	private Exception err = null;		
	
	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		try {
			runPhotoRec();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			err=e;
		}
		return 0;
	}
	public void runPhotoRec() throws Exception{
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter the input disk image's full path, use \"/\" as folder separater:");
		inputPath = sc.next();
		if(inputPath==null || inputPath.equals("")) {
			System.out.println("ERROR: Please enter a valid pull path such as D:/Case2/mtd8.dd");
			System.out.println("Terminating...");
			return;
		}
		System.out.println("Enter full path to which the output files will be stored , use \"/\" as folder separater:");
		outputPath = sc.next();
		if(inputPath==null || inputPath.equals("")) {
			System.out.println("ERROR: Please enter a valid pull path such as D:/Case2/mtd8.dd");
			System.out.println("Terminating...");
			return;
		}
		if(outputPath.contains(":/")){
			String tmp[] = outputPath.split(":/");
			outputPath=tmp[1];
		}
			
		/* Uncheck the following command to create a txt file containing the default execution path */	
		//String cmd = "cmd /c chdir>pwd.txt";
		String cmd = "cmd /c "+binaryPath+" "+preflags+" /"+outputPath+" /cmd "+inputPath+" "+postflags;
        Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		sc.close();
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
	public void passParam(int arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String pluginName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}
}
