package eagleeye.filesystem.yaffs2;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.TreeMap;

public class YAFFS2Object
{
	protected int id = -1;
	protected int chunkSize = 2048;
	
	public int getChunkSize()
	{
		return chunkSize;
	}

	public void setChunkSize(int chunkSize)
	{
		this.chunkSize = chunkSize;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	protected ArrayList<YAFFS2ObjectHeader> headers = new ArrayList<YAFFS2ObjectHeader>();
	protected TreeMap<Integer, SimpleEntry<Integer, ArrayList<byte[]>>> dataChunks = new TreeMap<Integer, SimpleEntry<Integer, ArrayList<byte[]>>>();
	
	public void addHeader(YAFFS2ObjectHeader header)
	{
		this.headers.add(header);
	}
	
	public void addDataChunk(int chunkId, byte[] chunk)
	{
		if(!this.dataChunks.containsKey(chunkId))
		{
			this.dataChunks.put(chunkId, new SimpleEntry<Integer, ArrayList<byte[]>>(this.getVersionCount(), new ArrayList<byte[]>()));
		}
		
		this.dataChunks.get(chunkId).getValue().add(chunk);
	}
	
	public int getVersionCount()
	{
		return this.headers.size();
	}
	
	public YAFFS2ObjectHeader getVersionHeader(int version)
	{
		if(this.getVersionCount() < version)
		{
			return null;
		}
		
		return this.headers.get(version);
	}

	public TreeMap<Integer, SimpleEntry<YAFFS2ObjectHeader, TreeMap<Integer, byte[]>>> getVersions()
	{
		int versionCount = this.getVersionCount();
		
		TreeMap<Integer, SimpleEntry<YAFFS2ObjectHeader, TreeMap<Integer, byte[]>>> versions = new TreeMap<Integer, SimpleEntry<YAFFS2ObjectHeader,TreeMap<Integer,byte[]>>>();
		while(versionCount -- > 0)
		{
			versions.put(versionCount, new SimpleEntry<>(this.getVersionHeader(versionCount), this.getVersionDataChunks(versionCount)));
		}
		
		return versions;
	}
	
	public TreeMap<Integer, byte[]> getVersionDataChunks(int version)
	{
		if(this.getVersionCount() < version)
		{
			return null;
		}
		
		YAFFS2ObjectHeader header = this.headers.get(version);
		
		int lastDeletedHeader = version;
		
		while(version -- >= 0)
		{
			if(header.getName().equals("deleted") && header.getParentObjectId() == 4)
			{
				break;
			}
		}
		
		TreeMap<Integer, byte[]> versionDataChunks = new TreeMap<>();
		
		int numberOfChunks = (int)Math.ceil((double)header.getFileSize() / chunkSize);
		
		while(numberOfChunks > 0)
		{
			if(this.dataChunks.containsKey(numberOfChunks))
			{
				SimpleEntry<Integer, ArrayList<byte[]>> entry = this.dataChunks.get(numberOfChunks);
				ArrayList<byte[]> chunkVersions = entry.getValue();
				int versionAdded = entry.getKey();
				
				if(versionAdded >= lastDeletedHeader && chunkVersions.size() > 0)
				{
					versionDataChunks.put(numberOfChunks, chunkVersions.get(0));
				}
			}
			
			numberOfChunks --;
		}
		
		return versionDataChunks;
	}
}