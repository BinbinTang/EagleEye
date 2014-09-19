package eagleeye.filesystem.format;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AndroidBootFormatIdentifier implements IFormatIdentifier
{

	@Override
	public FormatDescription identify(File file)
	{
		FileInputStream fileInputStream;
		
		try
		{
			fileInputStream = new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
		
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		
		byte[] magicSignatureBytes = new byte[8];
		
		try
		{
			dataInputStream.readFully(magicSignatureBytes);
			dataInputStream.close();
		}
		catch(IOException e)
		{
			return null;
		}
		
		String magicSignature = new String(magicSignatureBytes);

		if (magicSignature.equals("ANDROID!"))
		{
			FormatDescription formatDescription = new FormatDescription();
			formatDescription.setFile(file);
			formatDescription.setOperatingSystem("Android");
			formatDescription.setBinaryImageType("AndroidBoot");
			
			return formatDescription;
		}

		return null;
	}
	
}
