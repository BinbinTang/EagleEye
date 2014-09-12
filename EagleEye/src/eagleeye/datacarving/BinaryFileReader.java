package eagleeye.datacarving;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

abstract public class BinaryFileReader implements IBinaryFileReader
{
	protected File file;

	protected FileInputStream fileInputStream;
	protected DataInputStream inputStream;
	
	protected byte[] inputBytes;
	
	protected ByteBuffer byteBuffer;
		
	@Override
	public boolean read(File file) throws Exception
	{
		this.file = file;
		
		checkFileCanRead();
		
		fileInputStream = new FileInputStream(file);
		inputStream = new DataInputStream(fileInputStream);
		
		return false;
	}
	
	public void outputGeneralFileInformation()
	{		
		System.out.println("----------------------------------------");
		System.out.println("GENERAL FILE INFORMATION");
		System.out.println("----------------------------------------");
		System.out.println("File path: " + file.getAbsolutePath());
		System.out.println("File name: " + file.getName());
		System.out.println("File size: " + file.length() + " bytes");
		System.out.println();
	}

	@Override
	public boolean readSignature(File file) throws Exception
	{
		this.file = file;
		
		checkFileCanRead();

		fileInputStream = new FileInputStream(file);
		inputStream = new DataInputStream(fileInputStream);
		
		return true;
	}
	
	private void checkFileCanRead() throws Exception
	{
		if(!file.canRead())
		{
			throw new Exception("Cannot read image file!");
		}
	}
}
