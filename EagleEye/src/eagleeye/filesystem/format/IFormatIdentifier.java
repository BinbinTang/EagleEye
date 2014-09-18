package eagleeye.filesystem.format;

import java.io.File;

public abstract interface IFormatIdentifier
{
	public Format identify(File file);
}
