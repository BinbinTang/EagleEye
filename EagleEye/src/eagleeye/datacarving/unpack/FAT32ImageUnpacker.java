package eagleeye.datacarving.unpack;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import eagleeye.filesystem.format.FormatDescription;
import eagleeye.filesystem.yaffs2.YAFFS2ObjectHeader;
import eagleeye.filesystem.fat32.FAT32ObjectBootSector;

public class FAT32ImageUnpacker implements IDiskImageUnpacker{
	
	protected FormatDescription formatDescription;
	
	protected FileInputStream fileInputStream;
	protected DataInputStream inputStream;

	protected byte[] inputBytes;

	protected ByteBuffer byteBuffer;
	
	protected int chunkSize; // temporary page size used to find boot sector
	protected int bootSectorSize;
	
	protected int pageSize;
	
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}
	
	private FAT32ObjectBootSector readObjectBootSector(byte[] chunk)
	{
		FAT32ObjectBootSector bootSector = new FAT32ObjectBootSector();
		//bootSector.setBPB_BytsPerSector();
		return bootSector;
	}
	
	@Override
	public ArrayList<eagleeye.entities.File> unpack(FormatDescription formatDescription) throws Exception {
		this.formatDescription = formatDescription;
		
		if(this.formatDescription.getBinaryImageType() != "FAT32")
		{
			return null;
		}
		
		java.io.File file = this.formatDescription.getFile();
		this.fileInputStream = new FileInputStream(file);
		this.inputStream = new DataInputStream(this.fileInputStream);
		
		return null;
	}

}
