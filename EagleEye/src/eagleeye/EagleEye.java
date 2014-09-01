package eagleeye;

import eagleeye.filesystem.read.AndroidImageReader;
import eagleeye.filesystem.read.BinaryImageReader;
import eagleeye.filesystem.read.EMMCImageReader;

public class EagleEye
{
	
	public static void main(String[] args)
	{
		// Android Images
		// https://android.googlesource.com/platform/system/core/+/master/mkbootimg/bootimg.h
		// Algorithm
		//http://sandbox.dfrws.org/2011/burenin/Android%20file%20system%20recovering.pdf

		// Case 1
		
		String directory = "test";
		String filePath = "C:\\Users\\Admin\\Downloads\\Case1\\mtdblock3.img";
		
		// Case 2
		//String filePath = "C:\\Users\\Admin\\Desktop\\nc111nt_rodneybeede\\mmcblk0p14.dd";
		
		// eMMC Images -- DATA DUMP FROM DENNIS' PHONE - IGNORE FOR NOW
		//String filePath = "C:\\Users\\Admin\\Desktop\\nc111nt_rodneybeede\\mmcblk0.dd";
		
		BinaryImageReader reader = new AndroidImageReader();
		
		boolean isRead = false;
		
		try
		{
			isRead = reader.read(filePath);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		if(isRead)
		{
			return;
		}
		
		reader = new EMMCImageReader();

		try
		{
			isRead = reader.read(filePath);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		if(isRead)
		{
			return;
		}
	}
}
