package extractor.yaffs2;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class YAFFS2FormatIdentifier implements IFormatIdentifier
{
	protected int blockSize;
	protected int oobSize;
	private boolean cancel = false;
	
	public YAFFS2FormatIdentifier()
	{
		this.setBlockSize(2048);
	}
	
	public void setBlockSize(int blockSize)
	{
		this.blockSize = blockSize;
		this.oobSize = blockSize / 32;
	}

	@Override
	public FormatDescription identify(File file) throws Exception
	{
		int totalBlockSize = this.blockSize + this.oobSize;

		FileInputStream fileInputStream = new FileInputStream(file);
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		
		ArrayList<byte[]> blocks = new ArrayList<>();
		
		// Split up the data into blocks
		int totalFileBytes = (int) file.length();
		int totalBytesRead = 0;
		
		if(file.length() % totalBlockSize != 0)
		{
			return null;
		}

		byte[] inputBytes = new byte[this.blockSize + this.oobSize];
		
		ByteBuffer byteBuffer;
		
		while (totalFileBytes > totalBytesRead + totalBlockSize)
		{
			if(cancel)
			{
				dataInputStream.close();
				return null;
			}
			
			inputBytes = new byte[this.blockSize + this.oobSize];
			dataInputStream.readFully(inputBytes);
			byteBuffer = ByteBuffer.wrap(inputBytes);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			blocks.add(byteBuffer.array());
			
			totalBytesRead += this.blockSize + this.oobSize;
		}
		
		for (byte[] block : blocks)
		{
			if(cancel)
			{
				dataInputStream.close();
				return null;
			}
			
			byteBuffer = ByteBuffer.wrap(block);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			
			byteBuffer.position(8);
			
			// Unused short (Used to be checksum for names)
			if (byteBuffer.getShort() != (short) 0xFFFF)
			{
				continue;
			}
			
			byteBuffer.position(266);
			
			// Unused short NOT IN STRUCT
			if (byteBuffer.getShort() != (short) 0xFFFF)
			{
				continue;
			}
			
			byteBuffer.position(512);
			
			while(byteBuffer.remaining() > 64)
			{
				if(cancel)
				{
					dataInputStream.close();
					return null;
				}
				
				if(byteBuffer.get() != (byte)0xFF)
				{
					continue;
				}
			}
					
			FormatDescription formatDescription = new FormatDescription();
			formatDescription.setFile(file);
			formatDescription.setBinaryImageType("YAFFS2");
			
			dataInputStream.close();
			return formatDescription;
		}

		dataInputStream.close();
		return null;
	}

	@Override
	public void cancel()
	{
		this.cancel = true;
	}
	
}
