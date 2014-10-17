package eagleeye.datacarving.unpack;

import java.io.Console;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

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
	
	protected boolean cancel = false;
	
	protected TreeMap<Integer, TreeMap<Integer, byte[]>> dataChunks = new TreeMap<>();
	
	public YAFFS2ImageUnpacker()
	{
		this.setChunkSize(2048); // Assume chunk size of 2048 for now
	}
	
	public void setChunkSize(int chunkSize)
	{
		this.chunkSize = chunkSize;
		this.oobSize = chunkSize / 32;
	}

	@Override
	public ArrayList<eagleeye.entities.File> unpack(FormatDescription formatDescription) throws Exception
	{
		this.formatDescription = formatDescription;
		
		if(this.formatDescription.getBinaryImageType() != "YAFFS2")
		{
			return null;
		}
		
		java.io.File file = this.formatDescription.getFile();
		this.fileInputStream = new FileInputStream(file);
		this.inputStream = new DataInputStream(this.fileInputStream);
		
		System.out.printf("- Data carving started for %s...%n", file.getName());
		
		int totalChunkSize = this.chunkSize + this.oobSize;
		
		ArrayList<byte[]> chunks = new ArrayList<>();
		
		// Split up the data into chunks
		int totalFileBytes = (int) file.length();
		int totalBytesRead = 0;
		
		while (totalFileBytes > totalBytesRead + totalChunkSize)
		{
			if(cancel)
			{
				cancel = false;
				return null;
			}
			this.inputBytes = new byte[this.chunkSize + this.oobSize];
			this.inputStream.readFully(this.inputBytes);
			this.byteBuffer = ByteBuffer.wrap(this.inputBytes);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			chunks.add(this.byteBuffer.array());
			
			totalBytesRead += this.chunkSize + this.oobSize;
		}
		
		// Read from bottom up
		Collections.reverse(chunks);
		
		// Scan chunks for block sequence and object id
		TreeMap<Integer, TreeMap<Integer, ArrayList<byte[]>>> objects = new TreeMap<>();
		
		int chunkCount = -1;
		
		for (byte[] chunk : chunks)
		{
			if(cancel)
			{
				cancel = false;
				return null;
			}
			
			chunkCount ++;
			
			this.byteBuffer = ByteBuffer.wrap(chunk);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			System.out.printf("-- Scanning chunk %s. ", chunkCount);

			this.byteBuffer.position(this.chunkSize); 

			short validMarker = this.byteBuffer.getShort();
			int blockSequence = this.byteBuffer.getInt();

			if (validMarker != (byte)0xFFFF)
			{
				System.out.println("Invalid chunk.");
				continue;
			}
			
			if(blockSequence == 0xFFFFFFFF)
			{
				System.out.println("Empty chunk.");
				continue;
			}

			System.out.printf("Block Sequence: %s ", blockSequence);

			this.inputBytes = new byte[3];
			this.byteBuffer.get(this.inputBytes);
			
		    int objectId = (this.inputBytes[2] & 0xFF) << 8 | (this.inputBytes[1] & 0xFF) << 8 | (this.inputBytes[0] & 0xFF);

		    System.out.printf("Object Id: %s %n", objectId);
		    
		    if(!objects.containsKey(objectId))
		    {
		    	objects.put(objectId, new TreeMap<>());
		    }
		    
		    TreeMap<Integer, ArrayList<byte[]>> currentObject = objects.get(objectId);
		    
		    if(!currentObject.containsKey(blockSequence))
		    {
		    	currentObject.put(blockSequence, new ArrayList<>());
		    }
		    
		    ArrayList<byte[]> blockSequenceChunks = currentObject.get(blockSequence);
		    
		    blockSequenceChunks.add(chunk);
		}

		ArrayList<YAFFS2Object> yaffs2Objects = new ArrayList<YAFFS2Object>();
		HashMap<Integer, YAFFS2ObjectHeader> yaffs2ParentObjects = new HashMap<Integer, YAFFS2ObjectHeader>();
		
		// For each object id found starting from the smallest id (naturally sorted)
		for (Entry<Integer, TreeMap<Integer, ArrayList<byte[]>>> object : objects.entrySet())
		{
			if(cancel)
			{
				cancel = false;
				return null;
			}
			
			int objectId = object.getKey();
			
			System.out.printf("- Carving object %s%n", objectId);
			YAFFS2Object yaffs2Object = new YAFFS2Object();
			yaffs2Object.setId(objectId);
			yaffs2Object.setChunkSize(chunkSize);
			
			TreeMap<Integer, ArrayList<byte[]>> blocks = object.getValue();
			
			// For each block starting with the largest sequence number (naturally sorted)
			for(Entry<Integer, ArrayList<byte[]>> blockChunks : blocks.descendingMap().entrySet())
			{
				int blockSequence = blockChunks.getKey();

				System.out.printf("-- Analyzing block. Block Sequence: %s%n", blockSequence);
				
				boolean isDeleted = false;
				boolean isUnlinked = false;
				
				for(byte[] chunk : blockChunks.getValue())
				{
					this.byteBuffer = ByteBuffer.wrap(chunk);
					this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

					System.out.printf("--- Carving chunk ");
					
					this.byteBuffer.position(this.chunkSize); 
		
					short validMarker = this.byteBuffer.getShort();

					if (validMarker != (byte)0xFFFF)
					{
						System.out.println("Invalid chunk.");
						continue;
					}
					
					this.byteBuffer.getInt(); // Block Sequence
					
					this.inputBytes = new byte[3];
					this.byteBuffer.get(this.inputBytes); // Object Id
					
				    this.byteBuffer.get(); // Object type
				    
					this.inputBytes = new byte[3];
					this.byteBuffer.get(this.inputBytes);
					
					int parentIdOrChunkId = 0;
		
					parentIdOrChunkId |= this.inputBytes[2] & 0xFF;
					parentIdOrChunkId <<= 8;
					parentIdOrChunkId |= this.inputBytes[1] & 0xFF;
					parentIdOrChunkId <<= 8;
					parentIdOrChunkId |= this.inputBytes[0] & 0xFF;
				    
					this.byteBuffer.get(); // Status
					int nBytes = this.byteBuffer.getInt();				
					
					System.out.printf("Parent Id or Chunk Id: %s, Bytes in block: %s. ", parentIdOrChunkId, nBytes);
					
					this.byteBuffer.position(0);
					
					// Try to read chunk as object header first...
					YAFFS2ObjectHeader header = this.tryReadObjectHeader(chunk);
					
					if (header != null) // Chunk is an object header
					{
						if(header.getName().equals("deleted") && header.getParentObjectId() == 4)
						{
							isDeleted = true;
							System.out.println("Found delete header.");
							continue;
						}
						
						if(header.getName().equals("unlinked") && header.getParentObjectId() == 3)
						{
							isUnlinked = true;
							System.out.println("Found unlink header.");
							continue;
						}
						
						header.setDeleted(isDeleted);
						header.setUnlinked(isUnlinked);
						
						yaffs2Object.addHeader(header);
						
						if(header.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
						{
							yaffs2ParentObjects.put(objectId, header);
						}
						
						if(!yaffs2Objects.contains(yaffs2Object))
						{
							yaffs2Objects.add(yaffs2Object);
						}
						
						System.out.println("Found object header.");
						continue;
					}
					
					// Block is data
					System.out.println("Found data chunk.");
					yaffs2Object.addDataChunk(parentIdOrChunkId, Arrays.copyOf(chunk, nBytes));
				}
			}
		}

		ArrayList<eagleeye.entities.File> files = new ArrayList<eagleeye.entities.File>();
		
		String rootFilePath = "." + File.separator + "output" + File.separator + file.getName();
		HashMap<Integer, String> parentPaths = new HashMap<Integer, String>();
		
		for (Entry<Integer, YAFFS2ObjectHeader> entry : yaffs2ParentObjects.entrySet())
		{
			if(cancel)
			{
				cancel = false;
				return null;
			}
			
			int parentId = entry.getKey();
			ArrayList<String> filePathPieces = new ArrayList<String>();
			String filePath = rootFilePath;
			
			while(parentId != -1)
			{
				if(yaffs2ParentObjects.containsKey(parentId))
				{
					filePathPieces.add(yaffs2ParentObjects.get(parentId).getName());
					int newParentId = yaffs2ParentObjects.get(parentId).getParentObjectId();
					
					if(newParentId != parentId)
					{
						parentId = newParentId;
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			
			Collections.reverse(filePathPieces);
			
			for (String filePathPiece : filePathPieces)
			{
				filePath += File.separator + filePathPiece;
			}
			
			parentPaths.put(entry.getKey(), filePath);
			
			YAFFS2Object yaffs2Object = new YAFFS2Object();
			yaffs2Object.addHeader(entry.getValue());
		}
		
		for (String path : parentPaths.values())
		{
			if(cancel)
			{
				cancel = false;
				return null;
			}
			
			File directory = new File(path);
			directory.mkdirs();
		}
		
		
		for (YAFFS2Object yaffs2Object : yaffs2Objects)
		{
			if(cancel)
			{
				cancel = false;
				return null;
			}
			
			//TreeMap<Integer, SimpleEntry<YAFFS2ObjectHeader, TreeMap<Integer, byte[]>>> versions = yaffs2Object.getVersions();

			//for (Entry<Integer, SimpleEntry<YAFFS2ObjectHeader, TreeMap<Integer, byte[]>>> entry : versions.entrySet())
			//{
				//int version = entry.getKey();
				Entry<Integer, SimpleEntry<YAFFS2ObjectHeader, TreeMap<Integer, byte[]>>> entry = yaffs2Object.getVersions().lastEntry();
				YAFFS2ObjectHeader header = entry.getValue().getKey();
				TreeMap<Integer, byte[]> dataChunks = entry.getValue().getValue();
				
				int parentId = header.getParentObjectId();
				String filePath = rootFilePath;
				
				if(header.getName().equals("deleted") && parentId == 4)
				{
					continue;
				}
				
				if(header.getName().equals("unlinked") && parentId == 3)
				{
					continue;
				}
				
				if(parentPaths.containsKey(parentId))
				{
					filePath = parentPaths.get(parentId);
				}
				
				eagleeye.entities.File genericFile = new eagleeye.entities.File();
				
				genericFile.modifyIsRecovered(header.isDeleted());
				
				if(header.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_FILE)
				{
					this.writeFile(filePath, header.getName(), dataChunks);

					AutoDetectParser parser = new AutoDetectParser();
					BodyContentHandler contentHandler = new BodyContentHandler(10 * 1024 * 1024);
					Metadata metaData = new Metadata();
					FileInputStream stream = new FileInputStream(new File(filePath + File.separator + header.getName()));
					
					try
					{
						parser.parse(stream, contentHandler, metaData);
					}
					catch(TikaException e)
					{
						// Unable to parse
						e.printStackTrace();
					}
					
					stream.close();
					
					String extension = "";
					if(header.getName().lastIndexOf('.') > -1)
					{
						extension = header.getName().substring(header.getName().lastIndexOf('.'));
					}
					
					System.out.printf("Ext %s Type %s", extension, metaData.get(Metadata.CONTENT_TYPE));
					System.out.println();					
				}
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");

				genericFile.modifyDateAccessed(dateFormat.format(new Date(header.getAccessedTime() * 1000L)));
				genericFile.modifyDateCreated(dateFormat.format(new Date(header.getCreatedTime() * 1000L)));
				genericFile.modifyDateModified(dateFormat.format(new Date(header.getModifiedTime() * 1000L)));
				genericFile.modifyDirectoryID(header.getParentObjectId());
				genericFile.modifyFileID(yaffs2Object.getId());
				
				if(header.getName().indexOf('.') > -1)
				{
					genericFile.modifyFileExt(header.getName().substring(header.getName().lastIndexOf('.')));
					genericFile.modifyFileName(header.getName().substring(0, header.getName().lastIndexOf('.')));
				}
				else
				{
					genericFile.modifyFileName(header.getName());
				}
				
				genericFile.modifyFilePath(filePath);
				
				if(header.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
				{
					genericFile.modifyIsDirectory(true);
				}
		
				files.add(genericFile);
			//}
		}
		
		return files;
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
	
	private void writeFile(String directoryPath, String fileName, TreeMap<Integer, byte[]> dataChunks) throws IOException
	{
		File file = new File(directoryPath);
		file.mkdirs();
		
		file = new File(directoryPath + File.separator + fileName);

		FileOutputStream fileStream = new FileOutputStream(file);
		
		System.out.printf("Writing to %s%n", file.getCanonicalPath());
		
		for (Entry<Integer, byte[]> entry : dataChunks.entrySet())
		{
			fileStream.write(entry.getValue());
		}
		
		fileStream.close();
	}

	@Override
	public void cancel()
	{
		this.cancel = true;
	}
}
