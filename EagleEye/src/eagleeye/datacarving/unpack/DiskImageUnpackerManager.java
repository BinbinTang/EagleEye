package eagleeye.datacarving.unpack;

import java.util.ArrayList;

import eagleeye.entities.File;
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
	
	public ArrayList<File> unpack(FormatDescription formatDescription) throws Exception
	{
		ArrayList<File> unpackedFiles = null;
		
		for (IDiskImageUnpacker unpacker : diskImageUnpackers)
		{
			unpackedFiles = unpacker.unpack(formatDescription);
			
			if(unpackedFiles != null)
			{
				break;
			}
		}
		
		return unpackedFiles;
	}
}
