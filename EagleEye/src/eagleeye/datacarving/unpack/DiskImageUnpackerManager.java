package eagleeye.datacarving.unpack;

import java.util.ArrayList;

import eagleeye.filesystem.format.FormatDescription;

public class DiskImageUnpackerManager
{
	private ArrayList<IDiskImageUnpacker> diskImageUnpackers = new ArrayList<>();

	public void load(IDiskImageUnpacker diskImageUnpacker)
	{
		diskImageUnpackers.add(diskImageUnpacker);
	}

	public void unload(IDiskImageUnpacker diskImageUnpacker)
	{
		diskImageUnpackers.remove(diskImageUnpacker);
	}
	
	public boolean unpack(FormatDescription formatDescription) throws Exception
	{
		boolean unpackedFile = false;
		
		for (IDiskImageUnpacker unpacker : diskImageUnpackers)
		{
			unpackedFile = unpacker.unpack(formatDescription);
			
			if(unpackedFile != false)
			{
				break;
			}
		}
		
		return unpackedFile;
	}
}
