package eagleeye.datacarving;

import java.io.File;


public interface IBinaryFileReader 
{
	public boolean read(File file) throws Exception;
	public boolean readSignature(File file) throws Exception;
}
