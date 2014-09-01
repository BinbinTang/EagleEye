package eagleeye;

import eagleeye.filesystem.AndroidImageReader;
import eagleeye.filesystem.BinaryImageReader;
import eagleeye.filesystem.EMMCImageReader;

public class EagleEye
{
	
	public static void main(String[] args)
	{
		// Android Images
		// Case 1
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
