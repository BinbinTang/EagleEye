package eagleeye.filesystem.yaffs2;

public class YAFFS2ObjectHeader
{
	protected int chunkID;
	
	public int getChunkID()
	{
		return chunkID;
	}

	public void setChunkID(int chunkID)
	{
		this.chunkID = chunkID;
	}

	protected YAFFSObjectType type;
	protected int parentObjectID;
	protected String name;

	protected int mode;

	protected int userID;
	protected int groupID;
	protected int accessedTime;
	protected int modifiedTime;
	protected int createdTime;

	protected int fileSize;

	protected int equivalentObjectID;

	protected String alias;
	
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
	
	public int getCreatedTime()
	{
		return this.createdTime;
	}
	
	public int getEquivalentObjectID()
	{
		return this.equivalentObjectID;
	}
	
	public int getFileSize()
	{
		return this.fileSize;
	}
	
	public int getGroupID()
	{
		return this.groupID;
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
	
	public int getParentObjectID()
	{
		return this.parentObjectID;
	}
	
	public int getShadowObject()
	{
		return this.shadowObject;
	}
	
	public YAFFSObjectType getType()
	{
		return this.type;
	}
	
	public int getUserID()
	{
		return this.userID;
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
	
	public void setCreatedTime(int createdTime)
	{
		this.createdTime = createdTime;
	}
	
	public void setEquivalentObjectID(int equivalentObjectID)
	{
		this.equivalentObjectID = equivalentObjectID;
	}
	
	public void setFileSizeLow(int fileSizeLow)
	{
		this.fileSize = fileSizeLow;
	}
	
	public void setGroupID(int groupID)
	{
		this.groupID = groupID;
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
	
	public void setParentObjectID(int parentObjectID)
	{
		this.parentObjectID = parentObjectID;
	}
	
	public void setShadowObject(int shadowObject)
	{
		this.shadowObject = shadowObject;
	}
	
	public void setType(YAFFSObjectType type)
	{
		this.type = type;
	}
	
	public void setUserID(int userID)
	{
		this.userID = userID;
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
