package eagleeye.filesystem.format;

import java.io.File;

public abstract interface IFormatIdentifier
{
	public FormatDescription identify(File file) throws Exception;
}
