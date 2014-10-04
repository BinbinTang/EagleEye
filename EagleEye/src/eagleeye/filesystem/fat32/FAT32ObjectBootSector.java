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
	
	
	public byte[] getBS_jmpBoot() {
		return BS_jmpBoot;
	}
	public void setBS_jmpBoot(byte[] bS_jmpBoot) {
		BS_jmpBoot = bS_jmpBoot;
	}
	
	public byte[] getBS_OEMName() {
		return BS_OEMName;
	}
	public void setBS_OEMName(byte[] bS_OEMName) {
		BS_OEMName = bS_OEMName;
	}
	
	public byte[] getBPB_BytsPerSector() {
		return BPB_BytsPerSector;
	}
	public void setBPB_BytsPerSector(byte[] bPB_BytsPerSector) {
		BPB_BytsPerSector = bPB_BytsPerSector;
	}
	
	public byte[] getBPB_SectorPerClusster() {
		return BPB_SectorPerClusster;
	}
	public void setBPB_SectorPerClusster(byte[] bPB_SectorPerClusster) {
		BPB_SectorPerClusster = bPB_SectorPerClusster;
	}
	
	public byte[] getBPB_ReservedSectorCount() {
		return BPB_ReservedSectorCount;
	}
	public void setBPB_ReservedSectorCount(byte[] bPB_ReservedSectorCount) {
		BPB_ReservedSectorCount = bPB_ReservedSectorCount;
	}
	
	public byte[] getBPB_NumFATs() {
		return BPB_NumFATs;
	}
	public void setBPB_NumFATs(byte[] bPB_NumFATs) {
		BPB_NumFATs = bPB_NumFATs;
	}
	
	public byte[] getBPB_RootEntryCounts() {
		return BPB_RootEntryCounts;
	}
	public void setBPB_RootEntryCounts(byte[] bPB_RootEntryCounts) {
		BPB_RootEntryCounts = bPB_RootEntryCounts;
	}
	
	public byte[] getBPB_TotalSector16() {
		return BPB_TotalSector16;
	}
	public void setBPB_TotalSector16(byte[] bPB_TotalSector16) {
		BPB_TotalSector16 = bPB_TotalSector16;
	}
	
	public byte[] getBPB_Media() {
		return BPB_Media;
	}
	public void setBPB_Media(byte[] bPB_Media) {
		BPB_Media = bPB_Media;
	}
	
	public byte[] getBPB_FATSize16() {
		return BPB_FATSize16;
	}
	public void setBPB_FATSize16(byte[] bPB_FATSize16) {
		BPB_FATSize16 = bPB_FATSize16;
	}
	
	public byte[] getBPB_SectorPerTrack() {
		return BPB_SectorPerTrack;
	}
	public void setBPB_SectorPerTrack(byte[] bPB_SectorPerTrack) {
		BPB_SectorPerTrack = bPB_SectorPerTrack;
	}
	
	public byte[] getBPB_NumHeads() {
		return BPB_NumHeads;
	}
	public void setBPB_NumHeads(byte[] bPB_NumHeads) {
		BPB_NumHeads = bPB_NumHeads;
	}
	
	public byte[] getBPB_HiddenSector() {
		return BPB_HiddenSector;
	}
	public void setBPB_HiddenSector(byte[] bPB_HiddenSector) {
		BPB_HiddenSector = bPB_HiddenSector;
	}
	
	public byte[] getBPB_TotalSector32() {
		return BPB_TotalSector32;
	}
	public void setBPB_TotalSector32(byte[] bPB_TotalSector32) {
		BPB_TotalSector32 = bPB_TotalSector32;
	}
	
	public byte[] getBPB_FATSize32() {
		return BPB_FATSize32;
	}
	public void setBPB_FATSize32(byte[] bPB_FATSize32) {
		BPB_FATSize32 = bPB_FATSize32;
	}
	
	public byte[] getBPB_ExtentFlag() {
		return BPB_ExtentFlag;
	}
	public void setBPB_ExtentFlag(byte[] bPB_ExtentFlag) {
		BPB_ExtentFlag = bPB_ExtentFlag;
	}
	
	public byte[] getBPB_FSVer() {
		return BPB_FSVer;
	}
	public void setBPB_FSVer(byte[] bPB_FSVer) {
		BPB_FSVer = bPB_FSVer;
	}
	
	public byte[] getBPB_RootClus() {
		return BPB_RootClus;
	}
	public void setBPB_RootClus(byte[] bPB_RootClus) {
		BPB_RootClus = bPB_RootClus;
	}
	
	public byte[] getBPB_FSInfo() {
		return BPB_FSInfo;
	}
	public void setBPB_FSInfo(byte[] bPB_FSInfo) {
		BPB_FSInfo = bPB_FSInfo;
	}
	
	public byte[] getBPB_BkBootSec() {
		return BPB_BkBootSec;
	}
	public void setBPB_BkBootSec(byte[] bPB_BkBootSec) {
		BPB_BkBootSec = bPB_BkBootSec;
	}
	
	public byte[] getBPB_Reserved() {
		return BPB_Reserved;
	}
	public void setBPB_Reserved(byte[] bPB_Reserved) {
		BPB_Reserved = bPB_Reserved;
	}
	
	public byte[] getBPB_DrvNum() {
		return BPB_DrvNum;
	}
	public void setBPB_DrvNum(byte[] bPB_DrvNum) {
		BPB_DrvNum = bPB_DrvNum;
	}
	
	public byte[] getBS_Reserved1() {
		return BS_Reserved1;
	}
	
	public void setBS_Reserved1(byte[] bS_Reserved1) {
		BS_Reserved1 = bS_Reserved1;
	}
	public byte[] getBS_BootSig() {
		return BS_BootSig;
	}
	
	public void setBS_BootSig(byte[] bS_BootSig) {
		BS_BootSig = bS_BootSig;
	}
	
	public byte[] getBS_VolID() {
		return BS_VolID;
	}
	public void setBS_VolID(byte[] bS_VolID) {
		BS_VolID = bS_VolID;
	}
	
	public byte[] getBS_VolLab() {
		return BS_VolLab;
	}
	public void setBS_VolLab(byte[] bS_VolLab) {
		BS_VolLab = bS_VolLab;
	}
	
	public byte[] getBS_FileSystemType() {
		return BS_FileSystemType;
	}
	public void setBS_FileSystemType(byte[] bS_FileSystemType) {
		BS_FileSystemType = bS_FileSystemType;
	}
	
}
