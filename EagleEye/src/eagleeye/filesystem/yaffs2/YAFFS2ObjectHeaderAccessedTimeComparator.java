package eagleeye.filesystem.yaffs2;

public class YAFFS2ObjectHeaderAccessedTimeComparator extends
		YAFFS2ObjectHeaderComparator
{
	@Override
	public int compare(YAFFS2ObjectHeader header1, YAFFS2ObjectHeader header2)
	{
		if (header1.getAccessedTime() > header2.getAccessedTime())
		{
			return 1;
		}

		if (header1.getAccessedTime() < header2.getAccessedTime())
		{
			return -1;
		}

		return 0;
	}
}
