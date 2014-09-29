package eagleeye.filesystem.fat32;

public class FAT32ObjectBootSector {
	
	protected byte[] BS_jmpBoot = new byte[3];
	protected byte[] BS_OEMName = new byte[8];
	
	protected byte[] BPB_BytsPerSector = new byte[2];
	protected byte[] BPB_SectorPerClusster = new byte[1];
	protected byte[] BPB_ReservedSectorCount = new byte[2];
	protected byte[] BPB_NumFATs = new byte[1];
	protected byte[] BPB_RootEntryCounts = new byte[2];
	protected byte[] BPB_TotalSector16 = new byte[2];
	protected byte[] BPB_Media = new byte[1];
	protected byte[] BPB_FATSize16 = new byte[2];
	protected byte[] BPB_SectorPerTrack = new byte[2];
	protected byte[] BPB_NumHeads = new byte[2];
	protected byte[] BPB_HiddenSector = new byte[4];
	protected byte[] BPB_TotalSector32 = new byte[4];
	
	// Especially for FAT32
	
	
	
	
	
	
}
