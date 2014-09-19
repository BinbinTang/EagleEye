package eagleeye.filesystem.yaffs2;

public class YAFFS2ObjectHeader
{
	protected int chunkId;

	protected YAFFSObjectType type;
	
	protected int parentObjectId;
	
	protected String name;
	protected int mode;
	protected int userId;
	
	protected int groupId;
	
	protected int accessedTime;
	protected int modifiedTime;
	protected int createdTime;
	protected int fileSize;
	protected int equivalentObjectId;
	
	protected String alias;
	
	protected int rdev;
	
	protected long winAccessedTime;

	protected long winModifiedTime;

	protected long winCreatedTime;
	
	protected int shadowObject;
	
	protected int isShrink;
	
	public int getAccessedTime()
	{
		return this.accessedTime;
	}
	
	public String getAlias()
	{
		return this.alias;
	}
	
	public int getChunkId()
	{
		return this.chunkId;
	}

	public int getCreatedTime()
	{
		return this.createdTime;
	}
	
	public int getEquivalentObjectId()
	{
		return this.equivalentObjectId;
	}

	public int getFileSize()
	{
		return this.fileSize;
	}
	
	public int getGroupId()
	{
		return this.groupId;
	}

	public int getIsShrink()
	{
		return this.isShrink;
	}

	public int getMode()
	{
		return this.mode;
	}

	public int getModifiedTime()
	{
		return this.modifiedTime;
	}

	public String getName()
	{
		return this.name;
	}

	public int getParentObjectId()
	{
		return this.parentObjectId;
	}

	public int getRdev()
	{
		return this.rdev;
	}

	public int getShadowObject()
	{
		return this.shadowObject;
	}

	public YAFFSObjectType getType()
	{
		return this.type;
	}

	public int getUserId()
	{
		return this.userId;
	}

	public long getWinAccessedTime()
	{
		return this.winAccessedTime;
	}

	public long getWinCreatedTime()
	{
		return this.winCreatedTime;
	}

	public long getWinModifiedTime()
	{
		return this.winModifiedTime;
	}

	public void setAccessedTime(int accessedTime)
	{
		this.accessedTime = accessedTime;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	public void setChunkId(int chunkId)
	{
		this.chunkId = chunkId;
	}

	public void setCreatedTime(int createdTime)
	{
		this.createdTime = createdTime;
	}

	public void setEquivalentObjectId(int equivalentObjectId)
	{
		this.equivalentObjectId = equivalentObjectId;
	}

	public void setFileSize(int fileSize)
	{
		this.fileSize = fileSize;
	}

	public void setFileSizeLow(int fileSizeLow)
	{
		this.fileSize = fileSizeLow;
	}

	public void setGroupId(int groupId)
	{
		this.groupId = groupId;
	}

	public void setIsShrink(int isShrink)
	{
		this.isShrink = isShrink;
	}
	
	public void setMode(int mode)
	{
		this.mode = mode;
	}

	public void setModifiedTime(int modifiedTime)
	{
		this.modifiedTime = modifiedTime;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setParentObjectId(int parentObjectId)
	{
		this.parentObjectId = parentObjectId;
	}

	public void setRdev(int rdev)
	{
		this.rdev = rdev;
	}

	public void setShadowObject(int shadowObject)
	{
		this.shadowObject = shadowObject;
	}

	public void setType(YAFFSObjectType type)
	{
		this.type = type;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public void setWinAccessedTime(long winAccessedTime)
	{
		this.winAccessedTime = winAccessedTime;
	}

	public void setWinCreatedTime(long winCreatedTime)
	{
		this.winCreatedTime = winCreatedTime;
	}

	public void setWinModifiedTime(long winModifiedTime)
	{
		this.winModifiedTime = winModifiedTime;
	}
}
