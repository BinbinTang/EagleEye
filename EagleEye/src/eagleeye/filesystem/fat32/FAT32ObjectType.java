package eagleeye.filesystem.fat32;

public enum FAT32ObjectType {
	FAT32_OBJECT_TYPE_UNKNOWN, FAT32_OBJECT_TYPE_FILE, FAT32_OBJECT_TYPE_SYMLINK, FAT32_OBJECT_TYPE_DIRECTORY, FAT32_OBJECT_TYPE_HARDLINK, FAT32_OBJECT_TYPE_SPECIAL;
	
	public static FAT32ObjectType getType(int intValue)
	{
		switch (intValue)
		{
			case 1:
				return FAT32ObjectType.FAT32_OBJECT_TYPE_FILE;
			case 2:
				return FAT32ObjectType.FAT32_OBJECT_TYPE_SYMLINK;
			case 3:
				return FAT32ObjectType.FAT32_OBJECT_TYPE_DIRECTORY;
			case 4:
				return FAT32ObjectType.FAT32_OBJECT_TYPE_HARDLINK;
			case 5:
				return FAT32ObjectType.FAT32_OBJECT_TYPE_SPECIAL;
			case 0:
			default:
				return FAT32ObjectType.FAT32_OBJECT_TYPE_UNKNOWN;
		}
	}

}
