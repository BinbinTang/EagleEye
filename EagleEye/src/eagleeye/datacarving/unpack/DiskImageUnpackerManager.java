package eagleeye.datacarving.unpack;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import eagleeye.entities.*;
import eagleeye.filesystem.format.FormatDescription;

public class DiskImageUnpackerManager extends Service<ArrayList<FileEntity>>
{
	@Override
	public boolean cancel()
	{		
		for (DiskImageUnpacker unpacker : diskImageUnpackers)
		{
			unpacker.cancel();
		}
		
		return super.cancel();
	}

	protected ArrayList<DiskImageUnpacker> diskImageUnpackers = new ArrayList<>();
	protected FormatDescription formatDescription;
	
	public void load(DiskImageUnpacker diskImageUnpacker)
	{
		diskImageUnpackers.add(diskImageUnpacker);
	}

	public void unload(DiskImageUnpacker diskImageUnpacker)
	{
		diskImageUnpackers.remove(diskImageUnpacker);
	}
	
	@Override
	protected Task<ArrayList<FileEntity>> createTask()
	{
		return new Task<ArrayList<FileEntity>>()
		{
			
			@Override
			protected ArrayList<FileEntity> call() throws Exception
			{
				ArrayList<FileEntity> unpackedFiles = null;
				
				for (DiskImageUnpacker unpacker : diskImageUnpackers)
				{
					unpacker.setFormatDescription(DiskImageUnpackerManager.this.formatDescription);
					
					Task<ArrayList<FileEntity>> task = unpacker.createTask();
					
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
						unpackedFiles = task.get();
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
					
					if(unpackedFiles != null)
					{
						return unpackedFiles;
					}
				}
				
				return null;
			}
		};
	}

	public FormatDescription getFormatDescription()
	{
		return formatDescription;
	}

	public void setFormatDescription(FormatDescription formatDescription)
	{
		this.formatDescription = formatDescription;
	}
}
