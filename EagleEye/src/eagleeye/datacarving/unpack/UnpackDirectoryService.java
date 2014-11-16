package eagleeye.datacarving.unpack;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import eagleeye.entities.FileEntity;
import eagleeye.filesystem.format.FormatDescription;

public class UnpackDirectoryService extends Service<ArrayList<ArrayList<FileEntity>>>
{
	protected ArrayList<FormatDescription> formatDescriptions;
	protected DiskImageUnpackerManager diskImageUnpackerManager;
	
	public ArrayList<FormatDescription> getFormatDescriptions()
	{
		return formatDescriptions;
	}

	@Override
	public boolean cancel()
	{
		if(diskImageUnpackerManager != null)
		{
			diskImageUnpackerManager.cancel();
		}
		
		return super.cancel();
	}

	public void setFormatDescriptions(ArrayList<FormatDescription> formatDescriptions)
	{
		this.formatDescriptions = formatDescriptions;
	}

	public UnpackDirectoryService(ArrayList<FormatDescription> formatDescriptions)
	{
		setFormatDescriptions(formatDescriptions);
	}
	
	@Override
	protected Task<ArrayList<ArrayList<FileEntity>>> createTask()
	{
		return new Task<ArrayList<ArrayList<FileEntity>>>()
		{
			@Override
			protected ArrayList<ArrayList<FileEntity>> call()
			{
				/*
				 * STEP 03 - DATA CARVING BASED ON DATA FROM FILE SYSTEM LAYER, CARVE OUT DATA FROM FILE
				 */
				
				diskImageUnpackerManager = new DiskImageUnpackerManager();
				
				// Simulate plug ins
				diskImageUnpackerManager.load(new AndroidBootImageUnpacker());
				diskImageUnpackerManager.load(new YAFFS2ImageUnpacker());
				diskImageUnpackerManager.load(new FAT32ImageUnpacker());

				ArrayList<ArrayList<FileEntity>> fileList = new ArrayList<ArrayList<FileEntity>>();
				
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
						
						diskImageUnpackerManager.setFormatDescription(formatDescription);
						Task<ArrayList<FileEntity>> task = diskImageUnpackerManager.createTask();

						task.progressProperty().addListener
						(
							new ChangeListener<Number>()
							{
				
								@Override
								public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
								{
									updateProgress(newValue.doubleValue(), 1.0);
								}
							}
						);
						
						task.messageProperty().addListener
						(
							new ChangeListener<String>()
							{
				
								@Override
								public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue)
								{
									updateMessage(newValue);
								}
							}
						);
						
						task.run();
						
						try
						{
							if(task.get() != null)
							{
								break;
							}
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						catch (ExecutionException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					for (FormatDescription formatDescription : formatDescriptions)
					{
						if (formatDescription.getOperatingSystem() != null)
						{
							continue;
						}

						diskImageUnpackerManager.setFormatDescription(formatDescription);
						Task<ArrayList<FileEntity>> task = diskImageUnpackerManager.createTask();

						task.progressProperty().addListener
						(
							new ChangeListener<Number>()
							{
				
								@Override
								public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue)
								{
									updateProgress(newValue.doubleValue(), 1.0);
								}
							}
						);
						
						task.messageProperty().addListener
						(
							new ChangeListener<String>()
							{
				
								@Override
								public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue)
								{
									updateMessage(newValue);
								}
							}
						);
						
						task.run();
						
						try
						{
							ArrayList<FileEntity> newFileList = task.get();
							if(newFileList != null)
							{
								fileList.add(newFileList);
							}
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						catch (ExecutionException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				return fileList;
			}
		};
	}
}
