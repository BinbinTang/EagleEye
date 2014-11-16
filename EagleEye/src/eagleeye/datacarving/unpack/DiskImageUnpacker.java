package eagleeye.datacarving.unpack;

import java.util.ArrayList;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import eagleeye.entities.FileEntity;
import eagleeye.filesystem.format.FormatDescription;

public abstract class DiskImageUnpacker extends Service<ArrayList<FileEntity>>
{
	protected FormatDescription formatDescription;

	public FormatDescription getFormatDescription()
	{
		return formatDescription;
	}

	public void setFormatDescription(FormatDescription formatDescription)
	{
		this.formatDescription = formatDescription;
	}

	protected abstract Task<ArrayList<FileEntity>> createTask();
}
