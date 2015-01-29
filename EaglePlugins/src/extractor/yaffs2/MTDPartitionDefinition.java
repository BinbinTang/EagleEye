package extractor.yaffs2;

public class MTDPartitionDefinition
{
	protected String name;
	protected int sizeInBytes;
	protected int offsetInBytes;

	public MTDPartitionDefinition(String name, int sizeInBytes,	int offsetInBytes)
	{
		this.name = name;
		this.sizeInBytes = sizeInBytes;
		this.offsetInBytes = offsetInBytes;
	}

	public String getName()
	{
		return this.name;
	}

	public int getSizeInBytes()
	{
		return this.sizeInBytes;
	}

	public int getOffsetInBytes()
	{
		return this.offsetInBytes;
	}
	
	public void print()
	{
		System.out.printf("%s (%d bytes, Offset %d bytes)%n", this.name, this.sizeInBytes, this.offsetInBytes);
	}
}
