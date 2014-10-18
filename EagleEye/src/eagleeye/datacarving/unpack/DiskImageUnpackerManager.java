package eagleeye.datacarving.unpack;

import java.util.ArrayList;

import eagleeye.entities.*;
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
	
	public ArrayList<FileEntity> unpack(FormatDescription formatDescription) throws Exception
	{
		ArrayList<FileEntity> unpackedFiles = null;
		
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
	
	public void cancelAll()
	{
		for (IDiskImageUnpacker unpacker : diskImageUnpackers)
		{
			unpacker.cancel();
		}
	}
}
