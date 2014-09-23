package eagleeye.filesystem.yaffs2;

import java.lang.reflect.Field;
import java.util.HashMap;

public class YAFFS2Object extends YAFFS2ObjectHeader
{
	protected int id = -1;

	protected HashMap<Integer, byte[]> dataBlocks = new HashMap<>();
	
	protected boolean isUnlinked = false;
	
	protected boolean isDeleted = false;
	
	public void addDataBlock(int sequenceId, byte[] dataChunk)
	{
		this.dataBlocks.put(sequenceId, dataChunk);
	}
	
	public HashMap<Integer, byte[]> getDataChunks()
	{
		return this.dataBlocks;
	}

	public int getDataChunksByteSize()
	{
		int size = 0;

		for (byte[] bytes : this.dataBlocks.values())
		{
			size += bytes.length;
		}

		return size;
	}

	public YAFFS2ObjectHeader getHeader()
	{
		return this;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public boolean isDeleted()
	{
		return this.isDeleted;
	}
		
	public boolean isUnlinked()
	{
		return this.isUnlinked;
	}
	
	public void setDeleted(boolean isDeleted)
	{
		this.isDeleted = isDeleted;
	}
	
	public void setHeader(YAFFS2ObjectHeader header)
	{
		Field[] fields = header.getClass().getDeclaredFields();

		for (Field field : fields)
		{
			try
			{
				header.getClass().getDeclaredField(field.getName()).set(this, field.get(header));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setUnlinked(boolean isUnlinked)
	{
		this.isUnlinked = isUnlinked;
	}
}