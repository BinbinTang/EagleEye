package eagleeye.filesystem;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EMMCImageReader extends BinaryImageReader
{
	
	@Override
	public boolean read(String imageFilePath) throws Exception
	{
		super.read(imageFilePath);
		
		FileInputStream fileInputStream = new FileInputStream(file);
		
		DataInputStream inputStream = new DataInputStream(fileInputStream);

		byte[] lba0 = new byte[512];
		
		inputStream.readFully(lba0);
		/*
		byte[] headerBytes = new byte[92];
		inputStream.readFully(headerBytes);
		headerBytes[16] = 0;
		headerBytes[17] = 0;
		headerBytes[18] = 0;
		headerBytes[19] = 0;
		CRC32 crc32 = new CRC32();
		crc32.update(headerBytes);
		System.out.println(crc32.getValue());
		return false;
		*/
		// LBA 1

		byte[] magicSignatureBytes = new byte[8];
		
		inputStream.readFully(magicSignatureBytes);
		
		String magicSignature = new String(magicSignatureBytes);

		if(!magicSignature.equals("EFI PART"))
		{
			inputStream.close();
			return false;
		}
		
		outputGeneralFileInformation();

		System.out.println("----------------------------------------");
		System.out.println("GPT SIGNATURE CHECK");
		System.out.println("----------------------------------------");
	
		System.out.printf("Image %s contains the GPT magic signature!%n", file.getName());
		System.out.println();
		System.out.println("Magic Signature: " + magicSignature);
		System.out.println();
		
		int revision1 = inputStream.readByte();
		int revision2 = inputStream.readByte();
		int revision3 = inputStream.readByte();
		int revision4 = inputStream.readByte();
		
		System.out.printf("Revision: %d.%d.%d.%d%n", revision1, revision2, revision3, revision4);

		byte[] bytes = new byte[4];
		inputStream.readFully(bytes);
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		int headerSize = bb.getInt();
		
		System.out.printf("Header size: %d bytes%n", headerSize);
		/*
		inputStream.readFully(bytes);
		bb = ByteBuffer.wrap(bytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		*/
		int crc32OfHeader = bb.getInt();
		
		System.out.println(crc32OfHeader);
		
		System.out.println(inputStream.readInt());
		
		inputStream.close();
		return false;
	}
	
}
