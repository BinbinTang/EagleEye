package eagleeye.datacarving.unpack;

import eagleeye.filesystem.format.FormatDescription;

public interface IDiskImageUnpacker
{
	public boolean unpack(FormatDescription format) throws Exception;
}
