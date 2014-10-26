package eagleeye.datacarving.unpack.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import eagleeye.datacarving.unpack.AndroidBootImageUnpacker;
import eagleeye.datacarving.unpack.DiskImageUnpackerManager;
import eagleeye.datacarving.unpack.FAT32ImageUnpacker;
import eagleeye.datacarving.unpack.YAFFS2ImageUnpacker;
import eagleeye.dbcontroller.DBInsertTransaction;
import eagleeye.entities.Device;
import eagleeye.filesystem.format.AndroidBootFormatIdentifier;
import eagleeye.filesystem.format.FAT32FormatIdentifier;
import eagleeye.filesystem.format.FormatDescription;
import eagleeye.filesystem.format.FormatIdentifierManager;
import eagleeye.filesystem.format.YAFFS2FormatIdentifier;
import eagleeye.pluginmanager.PluginManager;

public class UnpackDirectoryService extends Service<Void>
{
	private File directory;
	private DiskImageUnpackerManager diskImageUnpackerManager;
	
	public File getDirectory()
	{
		return directory;
	}
	
	public void setDirectory(File directory)
	{
		this.directory = directory;
	}
	
	@Override
	protected Task<Void> createTask()
	{
		File directory = this.getDirectory();
		return new Task<Void>()
		{
			@Override
			protected Void call() throws Exception
			{
				try
				{
					loadDirectory(directory);
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
	
	private void loadDirectory(File directory) throws Exception
	{
		// Assuming files are all in main directory
		File[] files = directory.listFiles();
		
		/*
		 * STEP 02 - FILE SYSTEM LAYER FILES ARE TAGGED TO CERTAIN TYPES TO PREPARE FOR DATA CARVING
		 */
		FormatIdentifierManager formatIdentifierManager = new FormatIdentifierManager();
		
		// Simulate plug ins
		formatIdentifierManager.load(new AndroidBootFormatIdentifier());
		formatIdentifierManager.load(new YAFFS2FormatIdentifier());
		formatIdentifierManager.load(new FAT32FormatIdentifier());
		
		ArrayList<FormatDescription> formatDescriptions = new ArrayList<FormatDescription>();
		
		Arrays.sort(files);
		
		if (files.length > 0)
		{
			System.out.println("-----------------");
			System.out.println("File System Layer");
			System.out.println("-----------------");
			System.out.printf("%-25s\t%-20s\t%15s%n", "Name", "Binary Image Type", "Size");
			
			for (File file : files)
			{
				FormatDescription formatDescription = formatIdentifierManager.identify(file);
				String binaryImageType = "-";
				
				if (formatDescription != null)
				{
					formatDescriptions.add(formatDescription);
					binaryImageType = formatDescription.getBinaryImageType();
				}
				
				System.out.printf("%-25s\t%-20s\t%12s KB%n", file.getName(), binaryImageType, file.length());
			}
		}
		
		System.out.println();
		
		/*
		 * STEP 03 - DATA CARVING BASED ON DATA FROM FILE SYSTEM LAYER, CARVE OUT DATA FROM FILE
		 */
		
		diskImageUnpackerManager = new DiskImageUnpackerManager();
		
		// Simulate plug ins
		diskImageUnpackerManager.load(new AndroidBootImageUnpacker());
		diskImageUnpackerManager.load(new YAFFS2ImageUnpacker());
		diskImageUnpackerManager.load(new FAT32ImageUnpacker());
		
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
			
			ArrayList<eagleeye.entities.FileEntity> fileList = new ArrayList<eagleeye.entities.FileEntity>();
			
			for (FormatDescription formatDescription : formatDescriptions)
			{
				if (formatDescription.getOperatingSystem() != null)
				{
					continue;
				}
				
				ArrayList<eagleeye.entities.FileEntity> newFileList = diskImageUnpackerManager.unpack(formatDescription);
				
				if(newFileList != null)
				{
					fileList.addAll(newFileList);
				}
			}
			
			if(fileList.size() > 0)
			{
				DBInsertTransaction transaction = new DBInsertTransaction();
				transaction.insertNewDeviceData(new Device("Test Device", "100GB", "Dennis"), fileList);
			}
		}
	}
	
	//ALTERNATIVE LOAD METHOD
	private void loadDirectoryFromPlugin(File directory) throws Exception{
		String diskimg = "mtd8.dd";
		System.out.println("disk image to unpack is: "+diskimg);
		
		String diskimgPath = directory.getPath().replace("\\", "/");
		diskimgPath +="/"+diskimg;
		System.out.println("located at: "+diskimgPath);
		
		String outputPath=System.getProperty("user.dir").replace("\\", "/");
		outputPath += "/EagleEye/output";
		System.out.println("will be unpacked to: "+outputPath);
		
		String pluginFolder="PluginBinaries";
		PluginManager demo = new PluginManager(pluginFolder);
		demo.getPlugins();
		demo.extractFiles(diskimgPath,outputPath);
	}
}
