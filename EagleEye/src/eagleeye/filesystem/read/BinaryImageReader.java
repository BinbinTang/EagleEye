package eagleeye.filesystem.read;

import java.io.File;

public class BinaryImageReader implements IBinaryImageReader
{
	protected File file;
	
	@Override
	public boolean read(String imageFilePath) throws Exception
	{
		file = new File(imageFilePath);
		
		if(!file.canRead())
		{
			throw new Exception("Cannot read image file!");
		}
		
		return false;
	}
	
	public void outputGeneralFileInformation()
	{		
		System.out.println("----------------------------------------");
		System.out.println("GENERAL FILE INFORMATION");
		System.out.println("----------------------------------------");
		System.out.println("File path: " + file.getAbsolutePath());
		System.out.println("File name: " + file.getName());
		System.out.println("File size: " + file.length() + " bytes");
		System.out.println();
	}
	
}
