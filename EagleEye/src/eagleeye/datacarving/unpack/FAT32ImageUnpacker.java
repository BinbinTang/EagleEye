package eagleeye.datacarving.unpack;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import eagleeye.filesystem.format.FormatDescription;
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
	

	protected boolean cancel = false;
	
	public FAT32ImageUnpacker(){
		this.setPageSize(512);
	}
	
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}
	
	private FAT32ObjectBootSector readObjectBootSector(byte[] chunk)
	{	
		byte[] BPB_BytsPerSector = new byte[2];
		byte[] BPB_SectorPerClusster = new byte[1];
		byte[] BPB_ReservedSectorCount = new byte[2];
		byte[] BPB_NumFATs = new byte[1];
		byte[] BPB_RootEntryCounts = new byte[2];
		byte[] BPB_TotalSector16 = new byte[2];
		byte[] BPB_Media = new byte[1];
		byte[] BPB_FATSize16 = new byte[2];
		byte[] BPB_SectorPerTrack = new byte[2];
		byte[] BPB_NumHeads = new byte[2];
		byte[] BPB_HiddenSector = new byte[4];
		byte[] BPB_TotalSector32 = new byte[4];
		
		FAT32ObjectBootSector bootSector = new FAT32ObjectBootSector();
		
		BPB_BytsPerSector[0] = chunk[11];
		BPB_BytsPerSector[1] = chunk[12];
		bootSector.setBPB_BytsPerSector(BPB_BytsPerSector);
		
		BPB_SectorPerClusster[0] = chunk[13];
		bootSector.setBPB_SectorPerClusster(BPB_SectorPerClusster);
		
		BPB_ReservedSectorCount[0] = chunk[14];
		bootSector.setBPB_ReservedSectorCount(BPB_ReservedSectorCount);
		
		BPB_NumFATs[0] = chunk[15];
		bootSector.setBPB_NumFATs(BPB_NumFATs);
		
		BPB_RootEntryCounts[0] = chunk[2];
		bootSector.setBPB_RootEntryCounts(BPB_RootEntryCounts);
		
		BPB_TotalSector16[0] = chunk[2];
		bootSector.setBPB_TotalSector16(BPB_TotalSector16);
		
		BPB_Media[0] = chunk[1];
		bootSector.setBPB_Media(BPB_Media);
		
		BPB_FATSize16[0] = chunk[2];
		bootSector.setBPB_FATSize16(BPB_FATSize16);
		
		BPB_SectorPerTrack[0] = chunk[2];
		bootSector.setBPB_SectorPerTrack(BPB_SectorPerTrack);
		
		BPB_NumHeads[0] = chunk[2];
		bootSector.setBPB_NumHeads(BPB_NumHeads);
		
		BPB_HiddenSector[0] = chunk[4];
		bootSector.setBPB_HiddenSector(BPB_HiddenSector);
		
		BPB_TotalSector32[0] = chunk[4];
		bootSector.setBPB_TotalSector32(BPB_TotalSector32);
		
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

	@Override
	public void cancel()
	{
		this.cancel = true;
	}

}
