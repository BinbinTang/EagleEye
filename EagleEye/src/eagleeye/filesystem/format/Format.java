package eagleeye.filesystem.format;

import java.io.File;

public class Format
{
	protected File file;
	protected String operatingSystem;
	protected String binaryImageType;
	
	public String getBinaryImageType()
	{
		return binaryImageType;
	}
	
	public File getFile()
	{
		return file;
	}
	
	public String getOperatingSystem()
	{
		return operatingSystem;
	}
	
	public void setBinaryImageType(String binaryImageType)
	{
		this.binaryImageType = binaryImageType;
	}
	
	public void setFile(File file)
	{
		this.file = file;
	}
	
	public void setOperatingSystem(String operatingSystem)
	{
		this.operatingSystem = operatingSystem;
	}
}
