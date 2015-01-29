package extractor.yaffs2;

import java.io.File;

public class FormatDescription
{
	protected File file;
	protected String operatingSystem;
	protected String binaryImageType;
	protected String deviceName;
	
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
	
	public void setDeviceName(String deviceName)
	{
		this.deviceName = deviceName;
	}
	public String getDeviceName()
	{
		return deviceName;
	}
}
