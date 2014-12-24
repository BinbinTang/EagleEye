package eagleeye.utils;

import java.io.File;

public class FileFormatIdentifier {
	public enum Format{
		IMAGE,
		TEXT,
		DATABASE,
		UNIDENTIFIED
	}
	public FileFormatIdentifier(){
		
	}
	public Format checkFormat(String path){
		if(isImage(path)) return Format.IMAGE;
		if(isText(path)) return Format.TEXT;
		return Format.UNIDENTIFIED;
	}
	private boolean isImage(String path){
		String[] extList ={
				"jpg","jpeg",
				"png"
				//more to be added
		};
		
		File f = new File(path);
		String n = f.getName();
		String[] nSplit = n.split("\\.");
		String ext = nSplit[nSplit.length-1];
		System.out.println(ext);
		
		for(int i=0;i<extList.length;i++){
			if(ext.equalsIgnoreCase(extList[i]))
				return true;
		}
		return false;
		
	}
	private boolean isText(String path){
		String[] extList ={
				"txt",
				"html",
				"xml",
				"csv",
				"jsp",
				"ini",
				"h",
				"cpp",
				"c",
				"java"
				
				//more to be added
		};
		
		File f = new File(path);
		String n = f.getName();
		String[] nSplit = n.split("\\.");
		String ext = nSplit[nSplit.length-1];
		System.out.println(ext);
		
		for(int i=0;i<extList.length;i++){
			if(ext.equalsIgnoreCase(extList[i]))
				return true;
		}
		return false;
		
	}
	public static void main (String[] args){
		String testPath = "output2"+File.separator+"PNG"+File.separator+"f0048755.png";
		FileFormatIdentifier fi = new FileFormatIdentifier();
		System.out.println(fi.checkFormat(testPath));
	}
}
