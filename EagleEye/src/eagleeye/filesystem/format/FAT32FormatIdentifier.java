package eagleeye.filesystem.format;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class FAT32FormatIdentifier implements IFormatIdentifier {

	protected int pageSize;
	
	public FAT32FormatIdentifier(){
		this.setPageSize(2048);
	}
	
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}
	
	@Override
	public FormatDescription identify(File file) throws Exception {
		
		FileInputStream fileInputStream = new FileInputStream(file);
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		
		ArrayList<byte[]> blocks = new ArrayList<>();
		
		byte[] inputBytes = new byte[this.pageSize];
		
		ByteBuffer byteBuffer;

		
		// Split up the data into blocks
		int totalFileBytes = (int) file.length();
		int totalBytesRead = 0;

		
		while (totalFileBytes > totalBytesRead + this.pageSize)
		{			
			inputBytes = new byte[this.pageSize];
			dataInputStream.readFully(inputBytes);
			byteBuffer = ByteBuffer.wrap(inputBytes);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			blocks.add(byteBuffer.array());
			
			totalBytesRead += this.pageSize;
		}
		
		
		return null;
	}

}
