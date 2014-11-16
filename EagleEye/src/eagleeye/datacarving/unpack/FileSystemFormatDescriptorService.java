package eagleeye.datacarving.unpack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import eagleeye.filesystem.format.AndroidBootFormatIdentifier;
import eagleeye.filesystem.format.FAT32FormatIdentifier;
import eagleeye.filesystem.format.FormatDescription;
import eagleeye.filesystem.format.FormatIdentifierManager;
import eagleeye.filesystem.format.YAFFS2FormatIdentifier;

public class FileSystemFormatDescriptorService extends Service<ArrayList<FormatDescription>>
{
	private File directory;
	private FormatIdentifierManager formatIdentifierManager = new FormatIdentifierManager();
	
	public FileSystemFormatDescriptorService(File directory)
	{
		setDirectory(directory);
	}
	
	public File getDirectory()
	{
		return directory;
	}
	
	public void setDirectory(File directory)
	{
		this.directory = directory;
	}
	
	@Override
	protected Task<ArrayList<FormatDescription>> createTask()
	{
		return new Task<ArrayList<FormatDescription>>()
		{
			@Override
			protected ArrayList<FormatDescription> call() throws Exception
			{
				try
				{
					return describeFileSystemFormat();
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
		
		if(formatIdentifierManager != null)
		{
			formatIdentifierManager.cancelAll();
		}
    }
	
	private ArrayList<FormatDescription> describeFileSystemFormat() throws Exception
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
		//formatIdentifierManager.load(new FAT32FormatIdentifier());
		
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
		
		return formatDescriptions;
	}
}