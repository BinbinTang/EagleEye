package eagleeye.filesystem.format;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FAT32FormatIdentifier implements IFormatIdentifier {

	protected int pageSize;
	private boolean cancel = false;
	
	public FAT32FormatIdentifier(){
		this.setPageSize(512);
	}
	
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}
		
	@Override
	public FormatDescription identify(File file) throws Exception {
		
		FileInputStream fileInputStream = new FileInputStream(file);
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		
		byte[] inputBytes = new byte[this.pageSize];
		
		ByteBuffer byteBuffer;

		
		// Split up the data into blocks
		long totalFileBytes = (long) file.length();
		long totalBytesRead = 0;

		while (totalFileBytes > totalBytesRead + this.pageSize)
		{			
			inputBytes = new byte[this.pageSize];
			dataInputStream.readFully(inputBytes);
			byteBuffer = ByteBuffer.wrap(inputBytes);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			//System.out.println (byteBuffer);
			totalBytesRead += this.pageSize;
			
			// check the bootSector
			// Case 1: jmpBoot[0] = 0xE9 jmpBoot[1] = 0x?? jmpBoot[2] = 0x??;
			if (byteBuffer.get() == (byte) 0xE9)
			{
				FormatDescription formatDescription = new FormatDescription();
				formatDescription.setFile(file);
				formatDescription.setBinaryImageType("FAT32");
				
				dataInputStream.close(); 
				return formatDescription;
			}
			// Case 2:jmpBoot[0] = 0xEB jmpBoot[1] = 0x?? jmpBoot[2] = 0x90;
			if (byteBuffer.get()!= (byte) 0xEB)
				continue;
			
			byteBuffer.position(2);
		
			if (byteBuffer.get()!= (byte) 0x90)
				continue;
			
			FormatDescription formatDescription = new FormatDescription();
			formatDescription.setFile(file);
			formatDescription.setBinaryImageType("FAT32");
			
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
