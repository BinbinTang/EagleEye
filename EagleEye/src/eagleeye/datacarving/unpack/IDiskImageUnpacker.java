package eagleeye.datacarving.unpack;

import java.util.ArrayList;

import eagleeye.entities.File;
import eagleeye.filesystem.format.FormatDescription;

public interface IDiskImageUnpacker
{
	public ArrayList<File> unpack(FormatDescription format) throws Exception;
}
