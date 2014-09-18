package eagleeye.filesystem.format;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class YAFFS2FormatIdentifier implements IFormatIdentifier
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
		
		int chunkSize = 2048 + 64;

		if (file.length() < chunkSize)
		{
			try
			{
				fileInputStream.close();
			}
			catch (IOException e)
			{
				return null;
			}
			
			return null;
		}
		
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
				
		byte[] chunkBytes = new byte[chunkSize];
		
		try
		{
			dataInputStream.readFully(chunkBytes);
			dataInputStream.close();
		}
		catch (IOException e)
		{
			return null;
		}
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(chunkBytes);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		byteBuffer.position(8);
		
		// Unused short (Used to be checksum for names)
		if (byteBuffer.getShort() != (short) 0xFFFF)
		{
			return null;
		}
		
		byteBuffer.position(266);
		
		// Unused short NOT IN STRUCT
		if (byteBuffer.getShort() != (short) 0xFFFF)
		{
			return null;
		}
		
		byteBuffer.position(512);
		
		while(byteBuffer.remaining() > 64)
		{
			if(byteBuffer.get() != (byte)0xFF)
			{
				return null;
			}
		}
				
		Format format = new Format();
		format.setFile(file);
		format.setBinaryImageType("YAFFS2");
		
		return format;
	}
	
}
