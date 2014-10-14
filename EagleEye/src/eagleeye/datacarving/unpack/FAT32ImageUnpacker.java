package eagleeye.datacarving.unpack;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import eagleeye.entities.File;
import eagleeye.filesystem.format.FormatDescription;

public class FAT32ImageUnpacker implements IDiskImageUnpacker{
	
	protected FormatDescription formatDescription;
	
	protected FileInputStream fileInputStream;
	protected DataInputStream inputStream;

	protected byte[] inputBytes;

	protected ByteBuffer byteBuffer;
	protected int pageSize;
	protected int bootSectorSize;
	
	protected boolean cancel = false;
	
	public FAT32ImageUnpacker(){
		this.setPageSize(512);
	}
	
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}
	@Override
	public ArrayList<eagleeye.entities.File> unpack(FormatDescription formatDescription) throws Exception {
		this.formatDescription = formatDescription;
		
		if(this.formatDescription.getBinaryImageType() != "FAT32")
		{
			return null;
		}
		
		return null;
	}

	@Override
	public void cancel()
	{
		this.cancel = true;
	}

}
