package extractor.yaffs2;

import java.io.File;

public abstract interface IFormatIdentifier
{
	public FormatDescription identify(File file) throws Exception;

	public void cancel();
}
