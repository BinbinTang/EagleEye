package eagleeye.datacarving.unpack;

import java.util.ArrayList;

import eagleeye.entities.File;
import eagleeye.filesystem.format.FormatDescription;

public class FAT32ImageUnpacker implements IDiskImageUnpacker{
	
	protected int chunkSize;
	protected int bootSectorSize;
	

	public FAT32ImageUnpacker(){
		this.setChunkSize(512);
	}
	
	public void setChunkSize(int chunkSize){
		this.chunkSize = chunkSize;
	}
	@Override
	public ArrayList<File> unpack(FormatDescription format) throws Exception {
		// TODO Auto-generated method stub
		
		return null;
	}

}
