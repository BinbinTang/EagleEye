package eagleeye.filesystem.format;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AndroidBootFormatIdentifier implements IFormatIdentifier
{

	@Override
	public Format identify(File file)
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
			Format format = new Format();
			format.setFile(file);
			format.setOperatingSystem("Android");
			format.setBinaryImageType("AndroidBoot");
			
			return format;
		}

		return null;
	}
	
}
