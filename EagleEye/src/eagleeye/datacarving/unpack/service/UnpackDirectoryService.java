package eagleeye.datacarving.unpack.service;

import java.util.ArrayList;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import eagleeye.datacarving.unpack.AndroidBootImageUnpacker;
import eagleeye.datacarving.unpack.DiskImageUnpackerManager;
import eagleeye.datacarving.unpack.FAT32ImageUnpacker;
import eagleeye.datacarving.unpack.YAFFS2ImageUnpacker;
import eagleeye.entities.FileEntity;
import eagleeye.filesystem.format.FormatDescription;

public class UnpackDirectoryService extends Service<ArrayList<FileEntity>>
{
	private ArrayList<FormatDescription> formatDescriptions;

	private DiskImageUnpackerManager diskImageUnpackerManager;
	
	public ArrayList<FormatDescription> getFormatDescriptions()
	{
		return formatDescriptions;
	}

	public void setFormatDescriptions(ArrayList<FormatDescription> formatDescriptions2)
	{
		this.formatDescriptions = formatDescriptions2;
	}

	public UnpackDirectoryService(ArrayList<FormatDescription> formatDescriptions)
	{
		setFormatDescriptions(formatDescriptions);
	}
	
	@Override
	protected Task<ArrayList<FileEntity>> createTask()
	{
		return new Task<ArrayList<FileEntity>>()
		{
			@Override
			protected ArrayList<FileEntity> call() throws Exception
			{
				try
				{
					return unpackDirectory();
					//loadDirectoryFromPlugin(directory);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				
				return null;
			}
		};
	}
	
	@Override
	protected void cancelled()
	{
		super.cancelled();
		
		if(diskImageUnpackerManager != null)
		{
			diskImageUnpackerManager.cancelAll();
		}
    }
	
	private ArrayList<FileEntity> unpackDirectory() throws Exception
	{
		/*
		 * STEP 03 - DATA CARVING BASED ON DATA FROM FILE SYSTEM LAYER, CARVE OUT DATA FROM FILE
		 */
		
		diskImageUnpackerManager = new DiskImageUnpackerManager();
		
		// Simulate plug ins
		diskImageUnpackerManager.load(new AndroidBootImageUnpacker());
		diskImageUnpackerManager.load(new YAFFS2ImageUnpacker());
		diskImageUnpackerManager.load(new FAT32ImageUnpacker());

		ArrayList<FileEntity> fileList = new ArrayList<FileEntity>();
		
		if (formatDescriptions.size() > 0)
		{
			System.out.println("------------------");
			System.out.println("Data Carving Layer");
			System.out.println("------------------");
			System.out.println();
			
			// Always unpack OS images first
			
			for (FormatDescription formatDescription : formatDescriptions)
			{
				if (formatDescription.getOperatingSystem() == null)
				{
					continue;
				}
				
				if (diskImageUnpackerManager.unpack(formatDescription) == null)
				{
					continue;
				}
			}
			
			for (FormatDescription formatDescription : formatDescriptions)
			{
				if (formatDescription.getOperatingSystem() != null)
				{
					continue;
				}
				
				ArrayList<FileEntity> newFileList = diskImageUnpackerManager.unpack(formatDescription);
				
				if(newFileList != null)
				{
					fileList.addAll(newFileList);
				}
			}
			/*
			if(fileList.size() > 0)
			{
				DBInsertTransaction transaction = new DBInsertTransaction();
				transaction.insertNewDeviceData(new Device("Test Device 02", "100GB", "Dennis"), fileList);
				
				return transaction.getDeviceID();
			}*/
		}
		
		return fileList;
	}
}
