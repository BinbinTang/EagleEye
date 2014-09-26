package eagleeye.filesystem.yaffs2;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class YAFFS2Object extends YAFFS2ObjectHeader
{
	protected int id = -1;

	protected TreeMap<Integer, byte[]> dataChunks = new TreeMap<Integer, byte[]>();
	
	protected boolean isUnlinked = false;

	protected boolean isDeleted = false;
	
	protected ArrayList<YAFFS2Object> children = new ArrayList<>();

	private YAFFS2Object parent;
	
	public void addDataChunk(int sequenceId, byte[] dataChunk)
	{
		this.dataChunks.put(sequenceId, dataChunk);
	}
	
	public TreeMap<Integer, byte[]> getDataChunks()
	{
		return this.dataChunks;
	}

	public int getDataChunksByteSize()
	{
		int size = 0;

		for (byte[] bytes : this.dataChunks.values())
		{
			size += bytes.length;
		}

		return size;
	}

	public ArrayList<YAFFS2Object> getChildren()
	{
		return children;
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

	public void addChild(YAFFS2Object currentObject)
	{
		this.children.add(currentObject);
	}

	public YAFFS2Object getParent()
	{
		return parent;
	}

	public void setParent(YAFFS2Object parent)
	{
		this.parent = parent;
		
		parent.addChild(this);
	}

	public String getRelativePath()
	{		
		if(this.parent == null)
		{
			return this.getName();
		}
		
		return this.parent.getRelativePath() + File.separator + this.getName();
	}

	public void addDataChunks(TreeMap<Integer, byte[]> map)
	{
		this.dataChunks.putAll(map);
	}
}