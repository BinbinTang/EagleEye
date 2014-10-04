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
	protected byte[] BPB_FATSize32 = new byte[4];
	protected byte[] BPB_ExtentFlag = new byte[2];
	protected byte[] BPB_FSVer = new byte[2];
	protected byte[] BPB_RootClus = new byte[4];
	protected byte[] BPB_FSInfo = new byte[2];
	protected byte[] BPB_BkBootSec = new byte[2];
	protected byte[] BPB_Reserved = new byte[12];
	protected byte[] BPB_DrvNum = new byte[1];
	protected byte[] BS_Reserved1 = new byte[1];
	protected byte[] BS_BootSig = new byte[1];
	protected byte[] BS_VolID = new byte[4];
	protected byte[] BS_VolLab = new byte[11];
	protected byte[] BS_FileSystemType = new byte[8];
	
	
	
	
	
	
}
