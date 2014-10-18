package eagleeye.datacarving.unpack;

import java.util.ArrayList;

import eagleeye.entities.FileEntity;
import eagleeye.filesystem.format.FormatDescription;

public interface IDiskImageUnpacker
{
	public ArrayList<FileEntity> unpack(FormatDescription format) throws Exception;
	public void cancel();
}
