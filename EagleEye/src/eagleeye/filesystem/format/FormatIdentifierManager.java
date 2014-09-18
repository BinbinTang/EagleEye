package eagleeye.filesystem.format;

import java.io.File;
import java.util.ArrayList;

public class FormatIdentifierManager
{
	private ArrayList<IFormatIdentifier> formatIdentifiers = new ArrayList<>();

	public void load(IFormatIdentifier formatIdentifier)
	{
		formatIdentifiers.add(formatIdentifier);
	}

	public void unload(IFormatIdentifier formatIdentifier)
	{
		formatIdentifiers.remove(formatIdentifier);
	}
	
	public Format identify(File file)
	{
		Format format = null;
		
		for (IFormatIdentifier identifier : formatIdentifiers)
		{
			format = identifier.identify(file);
			
			if(format != null)
			{
				break;
			}
		}
		
		return format;
	}
}
