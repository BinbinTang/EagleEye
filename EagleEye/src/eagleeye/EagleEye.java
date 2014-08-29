package eagleeye;

import eagleeye.filesystem.AndroidImageReader;

public class EagleEye
{
	
	public static void main(String[] args)
	{
		AndroidImageReader reader = new AndroidImageReader();
		try
		{
			reader.read("C:\\Users\\Admin\\Downloads\\Case1\\mtdblock3.img");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
}
