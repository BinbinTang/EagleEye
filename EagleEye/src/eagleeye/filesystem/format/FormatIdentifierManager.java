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
	
	public FormatDescription identify(File file)
	{
		FormatDescription formatDescription = null;
		
		for (IFormatIdentifier identifier : formatIdentifiers)
		{
			try
			{
				formatDescription = identifier.identify(file);
			}
			catch (Exception e)
			{
				continue;
			}
			
			if(formatDescription != null)
			{
				break;
			}
		}
		
		return formatDescription;
	}
}
