package eagleeye.datacarving.unpack;

import java.util.ArrayList;

import eagleeye.entities.File;
import eagleeye.filesystem.format.FormatDescription;

public class FAT32ImageUnpacker implements IDiskImageUnpacker{
	
	protected int pageSize;
	protected int bootSectorSize;
	

	public FAT32ImageUnpacker(){
		this.setPageSize(512);
	}
	
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}
	@Override
	public ArrayList<File> unpack(FormatDescription format) throws Exception {
		
		return null;
	}

}
