package eagleeye.datacarving;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import eagleeye.filesystem.yaffs2.YAFFS2ObjectHeader;
import eagleeye.filesystem.yaffs2.YAFFS2ObjectHeaderAccessedTimeComparator;
import eagleeye.filesystem.yaffs2.YAFFS2ObjectHeaderComparator;
import eagleeye.filesystem.yaffs2.YAFFSObjectType;

public class YAFFS2BinaryFileReader extends BinaryFileReader
{
	protected final int chunkSize = 2048;
	protected final int oobSize = 0; // Assume no OOB for now
	
	@Override
	public boolean read(File file) throws Exception
	{
		super.read(file);
		System.out.printf("- Data carving started for %s...%n", file.getName());
		
		int totalBlockSize = this.chunkSize + this.oobSize;
		
		ArrayList<byte[]> chunks = new ArrayList<>();
		
		// Split up the data into chunks
		int totalFileBytes = (int) file.length();
		int totalBytesRead = 0;
		
		while(totalFileBytes > totalBytesRead + totalBlockSize)
		{
			this.inputBytes = new byte[this.chunkSize + this.oobSize];
			this.inputStream.readFully(this.inputBytes);
			this.byteBuffer = ByteBuffer.wrap(this.inputBytes);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			
			chunks.add(this.byteBuffer.array());

			totalBytesRead += this.chunkSize + this.oobSize;
		}
		
		ArrayList<YAFFS2ObjectHeader> objectHeaders = new ArrayList<>();
		YAFFS2ObjectHeader header, currentHeader;
		
		int chunkCount = -1;
		
		int dataChunkCount = 0;
		ByteBuffer dataBuffer = ByteBuffer.allocate(36 * totalBlockSize);

		ArrayList<byte[]> bytes = new ArrayList<>();
		
		for (byte[] chunk : chunks)
		{
			chunkCount ++;
			this.byteBuffer = ByteBuffer.wrap(chunk);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			
			// Code for debugging OOB
			/*
			this.byteBuffer.position(this.chunkSize);
			byte[] oob = new byte[64];
			this.byteBuffer.get(oob);
			String hex = DatatypeConverter.printHexBinary(oob);
			StringBuilder stringBuilder = new StringBuilder(hex);
			for(int i = hex.length() / 2; i > 0; i--)
			{
				stringBuilder.insert(i * 2, " ");
			}
			System.out.println(stringBuilder.toString());
			*/
			
			// Try to read chunk as object header first...
			header = tryReadObjectHeader(chunk);
			
			if(header != null) // Chunk is an object header
			{
				System.out.printf("-- %d Carved object header [%s] %s (%s bytes) of type %s from Chunk %d (IsShrink %s)%n", chunkCount, header.getName(), header.getParentObjectID(), header.getFileSize(), header.getType(), header.getChunkID(), header.getIsShrink());
				header.setChunkID(chunkCount);
				objectHeaders.add(header);
				currentHeader = header;
				continue;
			}
			
			// Chunk is data
			System.out.println("-- Carved data chunk");
			
			if(chunkCount > 7745 && chunkCount < 7781)
			{
				bytes.add(chunk);
				dataChunkCount ++;
			}
		}
		
		for (byte[] bs : bytes)
		{
			dataBuffer.put(bs);
		}
		
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream("C:\\Users\\Admin\\git\\EagleEye\\EagleEye\\testFile.txt"));
		stream.write(dataBuffer.array());
		stream.close();
		
		Collections.reverse(objectHeaders);
		
		HashMap<String, YAFFS2ObjectHeader> latestObjectHeadersHashMap = new HashMap<>();
		
		for (YAFFS2ObjectHeader objectHeader : objectHeaders)
		{
			if(latestObjectHeadersHashMap.containsKey(objectHeader.getName()))
			{
				continue;
			}
			
			latestObjectHeadersHashMap.put(objectHeader.getName(), objectHeader);
		}
		
		ArrayList<YAFFS2ObjectHeader> latestObjectHeaders = objectHeaders;//new ArrayList<YAFFS2ObjectHeader>(latestObjectHeadersHashMap.values());
		
		YAFFS2ObjectHeaderComparator comparator = new YAFFS2ObjectHeaderAccessedTimeComparator();
		
		latestObjectHeaders.sort(comparator.reversed());
		
		System.out.printf("- Data carving ended for %s...%n", file.getName());
		return true;
	}

	private YAFFS2ObjectHeader tryReadObjectHeader(byte[] chunk)
	{
		if(chunk.length != this.chunkSize + this.oobSize)
		{
			return null;
		}
		
		this.byteBuffer = ByteBuffer.wrap(chunk);
		this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		YAFFS2ObjectHeader objectHeader = new YAFFS2ObjectHeader();
		
		YAFFSObjectType type = YAFFSObjectType.getType(this.byteBuffer.getInt());
				
		if(type.equals(YAFFSObjectType.YAFFS_OBJECT_TYPE_UNKNOWN))
		{
			return null;
		}

		objectHeader.setType(type);
		
		int parentObjectId = this.byteBuffer.getInt();
		
		objectHeader.setParentObjectID(parentObjectId);
		
		// Unused short (Used to be checksum for names)
		if(this.byteBuffer.getShort() != (short)0xFFFF)
		{
			return null;
		}
			
		byte[] name = new byte[256];

		this.byteBuffer.get(name);

		// Unused short NOT IN STRUCT
		if(this.byteBuffer.getShort() != (short)0xFFFF)
		{
			return null;
		}
		
		int mode = this.byteBuffer.getInt();

		objectHeader.setMode(mode);
		
		int userId = this.byteBuffer.getInt();
		
		objectHeader.setUserID(userId);
		
		int groupId = this.byteBuffer.getInt();
		
		objectHeader.setGroupID(groupId);
		
		int accessedTime = this.byteBuffer.getInt();

		objectHeader.setAccessedTime(accessedTime);
		
		int modifiedTime = this.byteBuffer.getInt();
		
		objectHeader.setModifiedTime(modifiedTime);
		
		int createdTime = this.byteBuffer.getInt();
		int fileSize = this.byteBuffer.getInt();
		
		objectHeader.setFileSizeLow(fileSize);
		
		int equivalentObjectId = this.byteBuffer.getInt();
		
		byte[] alias = new byte[160];

		this.byteBuffer.get(alias);
		
		int rdev = this.byteBuffer.getInt();
		long winCreatedTime = this.byteBuffer.getLong();
		
		objectHeader.setWinCreatedTime(winCreatedTime);
		
		long winAccessedTime = this.byteBuffer.getLong();
		
		objectHeader.setWinAccessedTime(winAccessedTime);
		
		long winModifiedTime = this.byteBuffer.getLong();
		
		objectHeader.setWinModifiedTime(winModifiedTime);
		
		byte[] roomToGrow = new byte[16];
		this.byteBuffer.get(roomToGrow);
		
		int shadowObject = this.byteBuffer.getInt();
		int isShrink = this.byteBuffer.getInt();

		String string = new String(name);

		string = string.substring(0, string.indexOf(0x00));
		
		objectHeader.setName(string);
		
		objectHeader.setIsShrink(isShrink);
		
		return objectHeader;
	}

	@Override
	public boolean readSignature(File file) throws Exception
	{
		super.readSignature(file);
		int totalBlockSize = this.chunkSize + this.oobSize;
		
		if(this.file.length() < totalBlockSize)
		{
			return false;
		}
		
		// Exclude this check for corrupted files
		/*if(this.file.length() % totalBlockSize != 0)
		{
			return false;
		}*/
		
		this.inputBytes = new byte[totalBlockSize];
		this.inputStream.readFully(this.inputBytes);
		this.byteBuffer = ByteBuffer.wrap(this.inputBytes);
		this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		// Attempting to get the first header is the most stable method
		YAFFS2ObjectHeader header = tryReadObjectHeader(this.inputBytes);
		
		if(header != null)
		{
			return true;
		}
		
		return false;
	}
}