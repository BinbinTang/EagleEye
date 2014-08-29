package eagleeye.filesystem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import sun.io.Converters;

public class AndroidImageReader implements IBinaryImageReader
{

	@Override
	public void read(String imageFilePath) throws Exception
	{
		File file = new File(imageFilePath);
		
		if(!file.canRead())
		{
			throw new Exception("Cannot read image file!");
		}
		
		System.out.println("File path: " + file.getAbsolutePath());
		System.out.println("File name: " + file.getName());
		System.out.println("File size: " + file.length());
		
		int totalBytes = (int) file.length();
		byte[] bytes = new byte[totalBytes];		
		int totalBytesRead = 0;
		int bytesRead;
		int bytesRemaining;
		
		FileInputStream fileInputStream = new FileInputStream(file);
		
		bytesRemaining = totalBytes - totalBytesRead;
		bytesRead = fileInputStream.read(bytes, totalBytesRead, bytesRemaining);
		
		System.out.println("Bytes read: " + bytesRead);
		
		System.out.println();
		
		for (byte b : bytes)
		{
			if(b > -1)
			{
				System.out.print((char)b);
			}
		}
		
		System.out.println();
		
		String androidTestString = new String(bytes, 0, 7);
		System.out.println("Android Test String: " + androidTestString);
	}	
}
