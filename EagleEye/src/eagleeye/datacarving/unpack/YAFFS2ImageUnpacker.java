package eagleeye.datacarving.unpack;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

import eagleeye.filesystem.format.FormatDescription;
import eagleeye.filesystem.yaffs2.YAFFS2Object;
import eagleeye.filesystem.yaffs2.YAFFS2ObjectHeader;
import eagleeye.filesystem.yaffs2.YAFFS2VersionedObject;
import eagleeye.filesystem.yaffs2.YAFFS2VersionedObjectCollection;
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
	
	protected TreeMap<Integer, TreeMap<Integer, byte[]>> dataChunks = new TreeMap<>();
	
	public YAFFS2ImageUnpacker()
	{
		this.setChunkSize(2048);
	}
	
	public void setChunkSize(int chunkSize)
	{
		this.chunkSize = chunkSize;
		this.oobSize = chunkSize / 32;
	}

	@Override
	public boolean unpack(FormatDescription formatDescription) throws Exception
	{
		this.formatDescription = formatDescription;
		
		if(this.formatDescription.getBinaryImageType() != "YAFFS2")
		{
			return false;
		}
		
		File file = this.formatDescription.getFile();
		this.fileInputStream = new FileInputStream(file);
		this.inputStream = new DataInputStream(this.fileInputStream);
		
		System.out.printf("- Data carving started for %s...%n", file.getName());
		
		int totalChunkSize = this.chunkSize + this.oobSize;

		TreeMap<Integer, ArrayList<byte[]>> chunksInBlock = new TreeMap<Integer, ArrayList<byte[]>>();
		
		ArrayList<byte[]> chunks = new ArrayList<>();
		
		// Split up the data into chunks
		int totalFileBytes = (int) file.length();
		int totalBytesRead = 0;
		
		while (totalFileBytes > totalBytesRead + totalChunkSize)
		{			
			this.inputBytes = new byte[this.chunkSize + this.oobSize];
			this.inputStream.readFully(this.inputBytes);
			this.byteBuffer = ByteBuffer.wrap(this.inputBytes);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			chunks.add(this.byteBuffer.array());
			
			totalBytesRead += this.chunkSize + this.oobSize;
		}
		
		// Scan chunks for block sequence

		int chunkCount = -1;
		
		for (byte[] chunk : chunks)
		{
			chunkCount ++;
			
			this.byteBuffer = ByteBuffer.wrap(chunk);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			System.out.printf("-- Scanning chunk %s%n", chunkCount);

			this.byteBuffer.position(this.chunkSize); 

			short validMarker = this.byteBuffer.getShort();
			int blockSequence = this.byteBuffer.getInt();

			if (validMarker != (byte)0xFFFF)
			{
				System.out.println("Invalid chunk.");
				continue;
			}
			
			if(!chunksInBlock.containsKey(blockSequence))
			{
				chunksInBlock.put(blockSequence, new ArrayList<>());
			}
			
			chunksInBlock.get(blockSequence).add(chunk);
		}
		
		ArrayList<YAFFS2Object> objects = new ArrayList<YAFFS2Object>();
		YAFFS2ObjectHeader header = null;

		YAFFS2Object object = null;

		boolean hasDeletedHeader = false;
		boolean hasUnlinkedHeader = false;

		boolean isDeletedHeader = false;
		boolean isUnlinkedHeader = false;
		
		for (ArrayList<byte[]> block : chunksInBlock.values())
		{
			for (byte[] chunk : block)
			{
				this.byteBuffer = ByteBuffer.wrap(chunk);
				this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
					
				// DEBUGGING: For OOB
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
				
				System.out.printf("-- Carving chunk :");
	
				this.byteBuffer.position(this.chunkSize); 
	
				short validMarker = this.byteBuffer.getShort();
				int blockSequence = this.byteBuffer.getInt();
				
				this.inputBytes = new byte[3];
				this.byteBuffer.get(this.inputBytes);
				
			    int objectId = (this.inputBytes[2] & 0xFF) << 8 | (this.inputBytes[1] & 0xFF) << 8 | (this.inputBytes[0] & 0xFF);
			    
			    byte objectType = this.byteBuffer.get();
	
				this.inputBytes = new byte[3];
				this.byteBuffer.get(this.inputBytes);
				
				int parentOrChunkId = 0;
	
				parentOrChunkId |= this.inputBytes[2] & 0xFF;
				parentOrChunkId <<= 8;
				parentOrChunkId |= this.inputBytes[1] & 0xFF;
				parentOrChunkId <<= 8;
				parentOrChunkId |= this.inputBytes[0] & 0xFF;
			    
				byte status = this.byteBuffer.get();
				int nBytes = this.byteBuffer.getInt();
				
				System.out.printf("Block Sequence: %s Parent or Chunk ID: %s NBytes: %s ObjectID %s ", blockSequence, parentOrChunkId, nBytes, objectId);
				
				this.byteBuffer.position(0);
				
				// Try to read chunk as object header first...
				header = this.tryReadObjectHeader(chunk);
	
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
						object.setId(objectId);
						
						if(object.getParentObjectId() == objectId)
						{
							object.setParentObjectId(-1);
						}
						
						objects.add(object);
						
						hasUnlinkedHeader = false;
						hasDeletedHeader = false;
					}
					
					System.out.printf
					(
						"Found %sobject (%s) header of type %s named \"%s\" with the size of (%s bytes) and parent object id of %s (IsShrink %s)%n",
						specialHeader,
						objectId,
						header.getType(),
						header.getName(),
						header.getFileSize(),
						header.getParentObjectId(),
						header.getIsShrink()
					);
					continue;
				}
	
				// Block is data
				System.out.println("Found data chunk.");
				
				/*
				YAFFS2Object parentObject = null;
				for(YAFFS2VersionedObject currentObject : objects)
				{
					if(currentObject.getFirstVersion().getId() == objectId)
					{
						parentObject = currentObject.getFirstVersion();
						break;
					}
				}
				
				if(parentObject == null) // No object to assign to
				{
					System.out.println("Cannot determine which object this data chunk belongs to. Skipping data chunk.");
					continue;
				}
			
				//Assign data to current object
				System.out.println("Assigning data chunk to " + parentObject.getName());
				
				// Trim chunk to size
				object.addDataChunk(parentOrChunkId - 1, Arrays.copyOf(chunk, nBytes));
				*/
				if(nBytes <= 0)
				{
					continue;
				}
				
				if(!this.dataChunks.containsKey(objectId))
				{
					this.dataChunks.put(objectId, new TreeMap<Integer, byte[]>());
				}
				
				this.dataChunks.get(objectId).put(parentOrChunkId, Arrays.copyOf(chunk, nBytes));
				/*
				YAFFS2Object parentObject = null;
				for(YAFFS2Object currentObject : objects)
				{
					if(currentObject.getId() == objectId)
					{
						parentObject = currentObject;
						break;
					}
				}
				
				if(parentObject == null) // No object to assign to
				{
					System.out.println("Cannot determine which object this data chunk belongs to. Skipping data chunk.");
					continue;
				}
	
				// Assign data to current object
				System.out.println("Assigning data chunk to " + parentObject.getName());
				
				parentObject.addDataChunk(parentOrChunkId, Arrays.copyOf(chunk, nBytes));
				*/
			}
		}
		
		System.out.println("- Data carving complete.");
		
		// Map object to data
		
		for(YAFFS2Object currentObject : objects)
		{
			if(!this.dataChunks.containsKey(currentObject.getId()))
			{
				continue;
			}
			
			currentObject.addDataChunks(this.dataChunks.get(currentObject.getId()));			
		}
		
		// Map parents
		YAFFS2Object rootObject = null;
		
		for(YAFFS2Object currentObject : objects)
		{
			if(currentObject.getId() == 1)
			{
				rootObject = currentObject;
			}
			
			for(YAFFS2Object currentParentObject : objects)
			{
				if(currentObject.equals(currentParentObject) || currentParentObject.getType() != YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
				{
					continue;
				}
				
				if(currentObject.getParentObjectId() == currentParentObject.getId())
				{
					currentObject.setParent(currentParentObject);
					break;
				}
			}
		}
		
		// Iterate root to get data
		
		String filePath = "." + File.separator + "output" + File.separator + file.getName() + File.separator;
		
		dumpFiles(rootObject, filePath);
		
		return true;
	}
	
	private void dumpFiles(YAFFS2Object object, String rootFilePath) throws IOException
	{
		File file = new File(rootFilePath + object.getRelativePath());
		
		if(object.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
		{
			System.out.printf("Creating directory %s%n", file.getPath());
			
			file.mkdirs();
		}
		else if(object.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_FILE)
		{
			File parent = new File(file.getParentFile().getPath());
			file = new File(file.getParentFile().getPath() + File.separator + object.getName());

			parent.mkdirs();
			FileOutputStream fileStream = new FileOutputStream(file);
			
			System.out.printf("Writing to %s (ObjectID: %s)%n", file.getAbsolutePath(), object.getId());
			
			TreeMap<Integer, byte[]> dataChunks = object.getDataChunks();
			
			for (byte[] dataChunk : dataChunks.values())
			{
				if(dataChunk != null)
				{
					fileStream.write(dataChunk);
				}
			}
			
			fileStream.close();
		}
	
		for(YAFFS2Object child : object.getChildren())
		{
			dumpFiles(child, rootFilePath);
		}
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
