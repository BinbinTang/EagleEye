package eagleeye.filesystem.yaffs2;

public enum YAFFSObjectType
{
	YAFFS_OBJECT_TYPE_UNKNOWN, YAFFS_OBJECT_TYPE_FILE, YAFFS_OBJECT_TYPE_SYMLINK, YAFFS_OBJECT_TYPE_DIRECTORY, YAFFS_OBJECT_TYPE_HARDLINK, YAFFS_OBJECT_TYPE_SPECIAL;
	
	public static YAFFSObjectType getType(int intValue)
	{
		switch (intValue)
		{
			case 1:
				return YAFFSObjectType.YAFFS_OBJECT_TYPE_FILE;
			case 2:
				return YAFFSObjectType.YAFFS_OBJECT_TYPE_SYMLINK;
			case 3:
				return YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY;
			case 4:
				return YAFFSObjectType.YAFFS_OBJECT_TYPE_HARDLINK;
			case 5:
				return YAFFSObjectType.YAFFS_OBJECT_TYPE_SPECIAL;
			case 0:
			default:
				return YAFFSObjectType.YAFFS_OBJECT_TYPE_UNKNOWN;
		}
	}
}
