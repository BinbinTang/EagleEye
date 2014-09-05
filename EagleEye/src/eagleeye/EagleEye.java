package eagleeye;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;

import com.sun.jmx.snmp.Timestamp;

import sun.security.util.BitArray;
import eagleeye.filesystem.AndroidImageReader;
import eagleeye.filesystem.BinaryImageReader;
import eagleeye.filesystem.EMMCImageReader;

public class EagleEye
{
	public static void main(String[] args)
	{
		// Android Images
		// https://android.googlesource.com/platform/system/core/+/master/mkbootimg/bootimg.h
		
		// Case 1
		//String filePath = "C:\\Users\\Admin\\Downloads\\Case1\\mtdblock0.img";
		//String filePath = "C:\\Users\\Admin\\Downloads\\Case1\\mtdblock6.img";
		// Case 2
		//String filePath = "C:\\Users\\Admin\\Downloads\\Case2\\mtd5.dd";
		String filePath = "C:\\Users\\Admin\\Downloads\\Case2\\mtd6.dd";
		// DATA DUMP FROM DENNIS' PHONE - IGNORE FOR NOW
		// String filePath = "C:\\Users\\Admin\\Desktop\\nc111nt_rodneybeede\\mmcblk0p14.dd";
		
		// eMMC Images -- DATA DUMP FROM DENNIS' PHONE - IGNORE FOR NOW
		//String filePath = "C:\\Users\\Admin\\Desktop\\nc111nt_rodneybeede\\mmcblk0.dd";
		
		//readFS(filePath);
		
		
		try
		{
			//readYAFFSObjectHeader(filePath);
			//readRaw(filePath);
			readYAFFSExtTags(filePath);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static void readRaw(String filePath) throws IOException
	{
		File file = new File(filePath);
		
		FileInputStream fileInputStream = new FileInputStream(file);
		
		DataInputStream inputStream = new DataInputStream(fileInputStream);
		
		int totalBytes = (int)file.length();
		int totalBytesRead = 0;
		
		while(totalBytes >= totalBytesRead + 2048)
		{
			byte[] inputBytes = new byte[2048];
			inputStream.readFully(inputBytes);
			
			ByteBuffer bb = ByteBuffer.wrap(inputBytes);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			String string = new String(bb.array());
			System.out.println("--------------------------------");
			System.out.println(string);
			System.out.println("--------------------------------");
			totalBytesRead += 2048;
		}
	}

	private static void readYAFFSExtTags(String filePath) throws IOException
	{
		File file = new File(filePath);
		
		FileInputStream fileInputStream = new FileInputStream(file);
		
		DataInputStream inputStream = new DataInputStream(fileInputStream);
		
		int totalBytes = (int)file.length();
		int totalBytesRead = 0;
		int count = 0;
		
		while(totalBytes >= totalBytesRead + 2048 + 64)
		{
			inputStream.skipBytes(2048);
			
			byte[] inputBytes = new byte[64];
			inputStream.readFully(inputBytes);
			
			ByteBuffer bb = ByteBuffer.wrap(inputBytes);
			bb.order(ByteOrder.LITTLE_ENDIAN);

			int blockState = bb.getInt();
			int objectID = bb.getInt();
			int chunkID = bb.getInt();
			int nBytes = bb.getInt();
			
				System.out.println("-----------------");
				System.out.println("State: " + blockState);
				System.out.println("Object ID: " + objectID);
				System.out.println("Chunk ID: " + chunkID);
				System.out.println("nBytes: " + nBytes / 1024);
				
			
			totalBytesRead += 2048 + 64;
		}
	}
	
	private static void readYAFFSObjectHeader(String filePath) throws IOException
	{
		File dumpFile = new File("dump.txt");  
		FileOutputStream fileOutputStream = new FileOutputStream(dumpFile);  
		PrintStream printStream = new PrintStream(fileOutputStream);  
		System.setOut(printStream);
		
		File file = new File(filePath);
		
		FileInputStream fileInputStream = new FileInputStream(file);
		
		DataInputStream inputStream = new DataInputStream(fileInputStream);
		
		System.out.printf("%70s\t%20s\t%10s\t%30s\t%10s\t%10s\t%10s\t%20s\t%20s\t%20s\t%20s%n","Name", "Type", "Size", "Permissions", "User ID", "Group ID", "Parent Object ID", "Accessed", "Modified", "Created", "Is Shrink");
		
		int totalBytes = (int)file.length();
		int totalBytesRead = 0;
		
		while(totalBytes >= totalBytesRead + 2048)
		{
			byte[] inputBytes = new byte[8];
			inputStream.readFully(inputBytes);
			
			ByteBuffer bb = ByteBuffer.wrap(inputBytes);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			int objectTypeInt = bb.getInt();
			
			String objectTypeString = "Unknown Object Type";
			
			switch(objectTypeInt)
			{
				case 1:
					objectTypeString = "File";
					break;
				case 2:
					objectTypeString = "Symbolic link";
					break;
				case 3:
					objectTypeString = "Directory";
					break;
				case 4:
					objectTypeString = "Hard link";
					break;
				case 5:
					objectTypeString = "Special";
					break;
				case 0:
				default:
					objectTypeString = "Unknown Object Type";
					inputStream.skip(2040);
					totalBytesRead += 2048;
					continue;
			}
			
			int parentObjectID = bb.getInt();

			byte check01 = inputStream.readByte();
			byte check02 = inputStream.readByte();
			
			if(check01 != -1 || check02 != -1)
			{
				inputStream.skip(2038);
				totalBytesRead += 2048;
				continue;
			}
			
			inputBytes = new byte[256];
			inputStream.readFully(inputBytes);
			
			String name = new String(inputBytes);
			
			inputStream.skipBytes(2);
	
			inputBytes = new byte[4];
			inputStream.readFully(inputBytes);
			
			bb = ByteBuffer.wrap(inputBytes);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			BitSet fileModeBits = BitSet.valueOf(bb.array());
			BitSet filePermissionBits = fileModeBits.get(0, 8);
			
			char[] filePermissionChars = {'r','w','x','r','w','x','r','w','x'};
			
			for (int i = 0; i < 9; i++)
			{
				if(!filePermissionBits.get(i))
				{
					filePermissionChars[i] = '-';
				}
			}
			
			String filePermissionString = new String(filePermissionChars);
			BitSet specialBits = fileModeBits.get(9, 11);
			
			char[] specialBitChars = {'s', 's', 't'};
	
			for (int i = 0; i < 3; i++)
			{
				if(!specialBits.get(i))
				{
					specialBitChars[i] = '-';
				}
			}
			
			filePermissionString += "     " + new String(specialBitChars) + "     ";
			BitSet fileTypeBits = fileModeBits.get(11, 15);
	
			for (int i = 0; i < 4; i++)
			{
				if(fileTypeBits.get(i))
				{
					filePermissionString += "1";
				}
				else
				{
					filePermissionString += "0";
				}
			}
			
			inputBytes = new byte[28];
			inputStream.readFully(inputBytes);
			bb = ByteBuffer.wrap(inputBytes);
			bb.order(ByteOrder.LITTLE_ENDIAN);
	
			int userID = bb.getInt();
			int groupID = bb.getInt();
	
			DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
			
			int accessedTimeStamp = bb.getInt();		
			int modifiedTimeStamp = bb.getInt();
			int createdTimeStamp = bb.getInt();	
	
			Date accessedDate = new Date(accessedTimeStamp * 1000L);
			Date modifiedDate = new Date(modifiedTimeStamp * 1000L);
			Date createDate = new Date(createdTimeStamp * 1000L);
			
			int fileSize = bb.getInt();		
			int equivalentObjectID = bb.getInt();
			
			inputBytes = new byte[128];
			inputStream.readFully(inputBytes);
			String alias = new String(inputBytes);
			
			inputBytes = new byte[40];
			inputStream.readFully(inputBytes);
			bb = ByteBuffer.wrap(inputBytes);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			int rdev = bb.getInt();
			int winCreatedTimeStamp = bb.getInt();
			int winAccessedTimeStamp = bb.getInt();
			int winModifiedTimeStamp = bb.getInt();
	
			Date winAccessedDate = new Date(winAccessedTimeStamp * 1000L);
			Date winModifiedDate = new Date(winModifiedTimeStamp * 1000L);
			Date winCreateDate = new Date(winCreatedTimeStamp * 1000L);
			
			int inbandShadowedObjectID = bb.getInt();
			int inbandIsShrink = bb.getInt();
			
			int fileSizeHigh = bb.getInt();
			
			inputStream.skip(4);
			
			inputBytes = new byte[8];
			inputStream.readFully(inputBytes);
			bb = ByteBuffer.wrap(inputBytes);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			int shadowObject = bb.getInt();
			int isShrink = bb.getInt();

			System.out.format("%70s\t%20s\t%10s\t%30s\t%10s\t%10s\t%10s\t%20s\t%20s\t%20s\t%20s",
					name.trim(), objectTypeString, fileSize, filePermissionString, userID, groupID, parentObjectID,df.format(accessedDate), df.format(modifiedDate),df.format(createDate), isShrink);
			System.out.println();
			
			
			/*
			System.out.println("----------------------------------------");
			System.out.println("YASSF 2 OBJECT HEADER");
			System.out.println("----------------------------------------");
			System.out.printf("Object Type: %s (%d)%n", objectTypeString, objectTypeInt);
			System.out.println("Parent object ID: " + parentObjectID);
			System.out.println("Name: " + name.trim());
			System.out.println();
			System.out.println("Permissions: " + filePermissionString);
			System.out.println("User ID: " + userID);
			System.out.println("Group ID: " + groupID);
			System.out.println("Accessed: " + df.format(accessedDate));
			System.out.println("Modified: " + df.format(modifiedDate));
			System.out.println("Created: " + df.format(createDate));
			System.out.println();
			
			if(objectTypeInt == 1) // File
			{
				System.out.println("File size: " + fileSize);
			}
			else if(objectTypeInt == 4) // Hard link
			{
				System.out.println("Equivalent Object ID: " + equivalentObjectID);
			}
			else if(objectTypeInt == 2) // Symbolic link
			{
				System.out.println("Alias: " + alias);
			}

			System.out.println("Rdev: " + rdev);
	
			System.out.println();
			System.out.println("Win Accessed: " + df.format(winAccessedDate));
			System.out.println("Win Modified: " + df.format(winModifiedDate));
			System.out.println("Win Created: " + df.format(winCreateDate));
			System.out.println();
			System.out.println("Inband Shadowed Object ID: " + inbandShadowedObjectID);
			System.out.println("Inband Is Shrink: " + inbandIsShrink);
			System.out.println();
			System.out.println("File Size High: " + fileSizeHigh);
			System.out.println("Shadowed Object: " + shadowObject);
			System.out.println("Is Shrink: " + isShrink);
			*/
			inputStream.skip(1568);
			
			totalBytesRead += 2048;
		}
	}

	private static void readFS(String filePath)
	{
		BinaryImageReader reader = new AndroidImageReader();
		
		boolean isRead = false;
		
		try
		{
			isRead = reader.read(filePath);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		if(isRead)
		{
			return;
		}
		
		reader = new EMMCImageReader();

		try
		{
			isRead = reader.read(filePath);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		if(isRead)
		{
			return;
		}
	}
}
