package eagleeye.datacarving.unpack;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;

import eagleeye.filesystem.format.FormatDescription;
import eagleeye.filesystem.yaffs2.YAFFS2Object;
import eagleeye.filesystem.yaffs2.YAFFS2ObjectHeader;
import eagleeye.filesystem.yaffs2.YAFFSObjectType;

public class YAFFS2ImageUnpacker implements IDiskImageUnpacker
{
	protected FormatDescription formatDescription;
	
	protected FileInputStream fileInputStream;
	protected DataInputStream inputStream;

	protected byte[] inputBytes;

	protected ByteBuffer byteBuffer;

	protected int chunkSize;
	protected int oobSize;
	
	public YAFFS2ImageUnpacker()
	{
		this.chunkSize = 2048;
		this.oobSize = 64;
	}
	
	@Override
	public boolean unpack(FormatDescription formatDescription) throws Exception
	{
		this.formatDescription = formatDescription;
		File file = this.formatDescription.getFile();
		this.fileInputStream = new FileInputStream(file);
		this.inputStream = new DataInputStream(this.fileInputStream);
		
		System.out.printf("- Data carving started for %s...%n", file.getName());

		int totalBlockSize = this.chunkSize + this.oobSize;

		ArrayList<byte[]> chunks = new ArrayList<>();
		
		// Split up the data into chunks
		int totalFileBytes = (int) file.length();
		int totalBytesRead = 0;

		while (totalFileBytes > totalBytesRead + totalBlockSize)
		{
			this.inputBytes = new byte[this.chunkSize + this.oobSize];
			this.inputStream.readFully(this.inputBytes);
			this.byteBuffer = ByteBuffer.wrap(this.inputBytes);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			chunks.add(this.byteBuffer.array());
			
			totalBytesRead += this.chunkSize + this.oobSize;
		}

		// We want to read chunks from bottom up
		Collections.reverse(chunks);

		ArrayList<YAFFS2Object> objects = new ArrayList<>();
		YAFFS2ObjectHeader header = null;

		int chunkCount = -1;
		
		YAFFS2Object object = null;

		boolean hasDeletedHeader = false;
		boolean hasUnlinkedHeader = false;

		boolean isDeletedHeader = false;
		boolean isUnlinkedHeader = false;
		
		for (byte[] chunk : chunks)
		{
			chunkCount++;
			this.byteBuffer = ByteBuffer.wrap(chunk);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			// Code for debugging OOB
			/*
			 * this.byteBuffer.position(this.chunkSize); byte[] oob = new
			 * byte[64]; this.byteBuffer.get(oob); String hex =
			 * DatatypeConverter.printHexBinary(oob); StringBuilder
			 * stringBuilder = new StringBuilder(hex); for(int i = hex.length()
			 * / 2; i > 0; i--) { stringBuilder.insert(i * 2, " "); }
			 * System.out.println(stringBuilder.toString());
			 */

			// Try to read chunk as object header first...
			header = this.tryReadObjectHeader(chunk);

			System.out.printf("-- Carving chunk %d: ", chunkCount);

			if (header != null) // Chunk is an object header
			{
				if(object == null)
				{
					object = new YAFFS2Object();
					object.setHeader(header);
				}
				
				String name = header.getName();
				String specialHeader = "";
				isDeletedHeader = header.getIsShrink() > 0 && name.equals("deleted");
				isUnlinkedHeader = name.equals("unlinked");
				
				if(isDeletedHeader)
				{
					hasDeletedHeader = true;
					specialHeader += "deleted ";
				}
				else if (isUnlinkedHeader)
				{
					hasUnlinkedHeader = true;
					specialHeader += "unlinked ";
				}
				
				if(!isDeletedHeader && !isUnlinkedHeader)
				{
					// An actual object header
					object = new YAFFS2Object();
					object.setDeleted(hasUnlinkedHeader && hasDeletedHeader);
					object.setUnlinked(hasUnlinkedHeader);
					object.setHeader(header);
					objects.add(object);
					
					hasUnlinkedHeader = false;
					hasDeletedHeader = false;
				}
				
				System.out.printf
				(
					"Found %sobject header of type %s named \"%s\" with the size of (%s bytes) and parent object id of %s (IsShrink %s)%n",
					specialHeader,
					header.getType(),
					header.getName(),
					header.getFileSize(),
					header.getParentObjectId(),
					header.getIsShrink()
				);
				
				// Update the header of current object
				header.setChunkId(chunkCount);
				continue;
			}

			// Chunk is data
			System.out.print("Found data chunk. ");
			
			if(object == null) // No object to assign to
			{
				System.out.println("Cannot determine which object this data chunk belongs to. Skipping data chunk.");
				continue;
			}
			
			if(object.getDataChunksByteSize() < object.getFileSize())
			{
				// Assign data to current object
				System.out.println("Assigning data chunk to object above.");
				
				// Trim the data to exclude null bytes
				
				int i = chunk.length - 1;
			    while (i >= 0 && (chunk[i] == (byte)0xFF || chunk[i] == (byte)0x00))
			    {
			        --i;
			    }
			    
			    // Data chunks are inserted in sequence,
			    // since we read the chunks from bottom up
			    // we have to insert data chunks the other way round.		    
				object.prependDataChunk(chunk);//Arrays.copyOf(chunk, i + 1));
			    //object.addDataChunk(Arrays.copyOf(chunk, i + 1));
			}
			else
			{
				System.out.println("Excess data chunk. Skipping data chunk.");
			}
		}
		
		System.out.println("- Data carving complete.");

		ArrayList<YAFFS2Object> fileList = new ArrayList<>();
		
		// For testing purposes, filter out a file
		for (YAFFS2Object yaffs2Object : objects)
		{
			fileList.add(yaffs2Object);
		}
		
		// Look for missing data
		/*
		// Hash map of filename, parent, object
		HashMap<String, HashMap<Integer, YAFFS2Object>> previousObjects = new HashMap<String, HashMap<Integer,YAFFS2Object>>();
		
		for (int i = 0; i < fileList.size(); i++)
		{
			YAFFS2Object yaffs2Object = fileList.get(i);
			
			int sizeDifference = yaffs2Object.getFileSize() - yaffs2Object.getDataChunksByteSize();
			
			if(sizeDifference <= 0)
			{
				if(yaffs2Object.getFileSize() > 0 && yaffs2Object.getDataChunksByteSize() > 0)
				{
					HashMap<Integer, YAFFS2Object> parentObjectMap = previousObjects.getOrDefault(yaffs2Object.getName(), new HashMap<Integer, YAFFS2Object>());
					parentObjectMap.put(yaffs2Object.getParentObjectId(), yaffs2Object);
					previousObjects.put(yaffs2Object.getName(), parentObjectMap);
				}
				continue;
			}
			
			// Try to find previous object
			if(previousObjects.containsKey(yaffs2Object.getName()))
			{
				HashMap<Integer, YAFFS2Object> parentObjectMap = previousObjects.get(yaffs2Object.getName());
				
				if(parentObjectMap.containsKey(yaffs2Object.getParentObjectId()))
				{
					previousObject = parentObjectMap.get(yaffs2Object.getParentObjectId());
				}
			}
			else
			{
				previousObject = null;
			}
						
			if(previousObject != null && !previousObject.isDeleted() && previousObject.getDataChunksByteSize() > 0) // Look for previous versions
			{
				int chunkDeficit = Math.round(sizeDifference / totalBlockSize);
				
				// Get a copy of the old data
				ArrayList<byte[]> oldDataChunks = previousObject.getDataChunks();
				
				// If we have excess data chunks, trim it down
				int chunkExcess = oldDataChunks.size() - chunkDeficit;
				
				while(chunkExcess -- > 0)
				{
					oldDataChunks.remove(oldDataChunks.size() - 1);
				}
				
				ArrayList<byte[]> currentDataChunks = yaffs2Object.getDataChunks();
				
				oldDataChunks.addAll(currentDataChunks);
				yaffs2Object.setDataChunks(oldDataChunks);		
			}
			
			if(yaffs2Object.getFileSize() > 0 && yaffs2Object.getDataChunksByteSize() > 0)
			{
				HashMap<Integer, YAFFS2Object> parentObjectMap = previousObjects.getOrDefault(yaffs2Object.getName(), new HashMap<Integer, YAFFS2Object>());
				parentObjectMap.put(yaffs2Object.getParentObjectId(), yaffs2Object);
				previousObjects.put(yaffs2Object.getName(), parentObjectMap);
			}
		}
		*/
		File currentFile;
		int count = 0;
		for (YAFFS2Object yaffs2Object : fileList)
		{
			count ++;
			
			if(yaffs2Object.getDataChunksByteSize() <= 0)
			{
				continue;
			}
			
			String filePath = "." + File.separator + "output" + File.separator + file.getName() + File.separator;
			
			if(yaffs2Object.getParentObjectId() > 0)
			{
				filePath += yaffs2Object.getParentObjectId() + File.separator;
			}
			
			if(yaffs2Object.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
			{
				filePath += yaffs2Object.getName();
				currentFile = new File(filePath);
				currentFile.mkdirs();
				continue;
			}
			
			currentFile = new File(filePath);
			currentFile.mkdirs();
			filePath += count + yaffs2Object.getName().replace(":", "-");
			currentFile = new File(filePath);
			
			FileOutputStream fileStream = new FileOutputStream(currentFile);
			
			System.out.printf("Writing to %s (%s / %s bytes) IsDeleted: %s IsUnlinked: %s IsShrink: %s%n ", currentFile.getAbsolutePath(), yaffs2Object.getDataChunksByteSize(), yaffs2Object.getFileSize(), yaffs2Object.isDeleted(), yaffs2Object.isUnlinked(), yaffs2Object.getIsShrink());
			ArrayList<byte[]> dataChunks = yaffs2Object.getDataChunks();
			
			for (byte[] dataChunk : dataChunks)
			{
				fileStream.write(dataChunk);
			}
			
			fileStream.close();
		}
		
		return true;
	}

	private YAFFS2ObjectHeader tryReadObjectHeader(byte[] chunk)
	{
		if (chunk.length != this.chunkSize + this.oobSize)
		{
			return null;
		}

		this.byteBuffer = ByteBuffer.wrap(chunk);
		this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

		YAFFS2ObjectHeader objectHeader = new YAFFS2ObjectHeader();

		YAFFSObjectType type = YAFFSObjectType
				.getType(this.byteBuffer.getInt());
		
		if (type.equals(YAFFSObjectType.YAFFS_OBJECT_TYPE_UNKNOWN))
		{
			return null;
		}
		
		objectHeader.setType(type);

		int parentObjectId = this.byteBuffer.getInt();

		objectHeader.setParentObjectId(parentObjectId);

		// Unused short (Used to be checksum for names)
		if (this.byteBuffer.getShort() != (short) 0xFFFF)
		{
			return null;
		}
		
		byte[] name = new byte[256];
		
		this.byteBuffer.get(name);
		
		// Unused short NOT IN STRUCT
		if (this.byteBuffer.getShort() != (short) 0xFFFF)
		{
			return null;
		}

		int mode = this.byteBuffer.getInt();
		
		objectHeader.setMode(mode);

		int userId = this.byteBuffer.getInt();

		objectHeader.setUserId(userId);

		int groupId = this.byteBuffer.getInt();

		objectHeader.setGroupId(groupId);

		int accessedTime = this.byteBuffer.getInt();
		
		objectHeader.setAccessedTime(accessedTime);

		int modifiedTime = this.byteBuffer.getInt();

		objectHeader.setModifiedTime(modifiedTime);

		int createdTime = this.byteBuffer.getInt();

		objectHeader.setCreatedTime(createdTime);

		int fileSize = this.byteBuffer.getInt();

		objectHeader.setFileSizeLow(fileSize);

		int equivalentObjectId = this.byteBuffer.getInt();

		objectHeader.setEquivalentObjectId(equivalentObjectId);

		byte[] alias = new byte[160];
		
		this.byteBuffer.get(alias);
		
		int rdev = this.byteBuffer.getInt();

		objectHeader.setRdev(rdev);

		long winCreatedTime = this.byteBuffer.getLong();

		objectHeader.setWinCreatedTime(winCreatedTime);

		long winAccessedTime = this.byteBuffer.getLong();

		objectHeader.setWinAccessedTime(winAccessedTime);

		long winModifiedTime = this.byteBuffer.getLong();

		objectHeader.setWinModifiedTime(winModifiedTime);

		byte[] roomToGrow = new byte[16];
		this.byteBuffer.get(roomToGrow);

		int shadowObject = this.byteBuffer.getInt();

		objectHeader.setShadowObject(shadowObject);

		int isShrink = this.byteBuffer.getInt();
		
		String string = new String(name);
		
		int stringIndex = string.indexOf(0x00);
		
		if(stringIndex >= 0)
		{
			string = string.substring(0, stringIndex);
			objectHeader.setName(string);
		}
		
		string = new String(alias);
		
		stringIndex = string.indexOf(0x00);
		
		if(stringIndex >= 0)
		{
			string = string.substring(0, stringIndex);
			objectHeader.setAlias(string);
		}
		
		objectHeader.setIsShrink(isShrink);

		return objectHeader;
	}
}
