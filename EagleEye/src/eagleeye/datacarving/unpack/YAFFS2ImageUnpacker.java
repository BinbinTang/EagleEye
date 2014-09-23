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
import java.util.Enumeration;
import java.util.HashMap;

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

	protected int blockSize;
	protected int oobSize;
	
	public YAFFS2ImageUnpacker()
	{
		this.blockSize = 2048;
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

		int totalBlockSize = this.blockSize + this.oobSize;

		ArrayList<byte[]> blocks = new ArrayList<>();
		
		// Split up the data into blocks
		int totalFileBytes = (int) file.length();
		int totalBytesRead = 0;
		
		while (totalFileBytes > totalBytesRead + totalBlockSize)
		{			
			this.inputBytes = new byte[this.blockSize + this.oobSize];
			this.inputStream.readFully(this.inputBytes);
			this.byteBuffer = ByteBuffer.wrap(this.inputBytes);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			blocks.add(this.byteBuffer.array());
			
			totalBytesRead += this.blockSize + this.oobSize;
		}
		
		YAFFS2VersionedObjectCollection objects = new YAFFS2VersionedObjectCollection();
		YAFFS2ObjectHeader header = null;

		int blockCount = -1;
		
		YAFFS2Object object = null;

		boolean hasDeletedHeader = false;
		boolean hasUnlinkedHeader = false;

		boolean isDeletedHeader = false;
		boolean isUnlinkedHeader = false;
		
		for (byte[] block : blocks)
		{
			blockCount ++;
			this.byteBuffer = ByteBuffer.wrap(block);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			// DEBUGGING: For OOB
			
			/*
			this.byteBuffer.position(this.blockSize); 
			byte[] oob = new byte[64];
			this.byteBuffer.get(oob); 
			String hex = DatatypeConverter
					.printHexBinary(oob); 
			StringBuilder stringBuilder = new StringBuilder(hex); 
			for(int i = hex.length() / 2; i > 0; i--)
			{
				stringBuilder.insert(i * 2, " ");
			}
			System.out.println(stringBuilder.toString());
			*/
			
			System.out.printf("-- Carving block %d: ", blockCount);

			this.byteBuffer.position(this.blockSize); 
			
			short validMarker = this.byteBuffer.getShort();		// Confirmed
			short blockUsed = this.byteBuffer.getShort();		// Unconfirmed
			short chunkId = this.byteBuffer.getShort();			// Unconfirmed
			short objectId = this.byteBuffer.getShort();		// Confirmed
			this.byteBuffer.getShort();							// Unknown
			short blockSequence = this.byteBuffer.getShort();	// Confirmed
			this.byteBuffer.getShort();							// Unknown
			int nBytes = this.byteBuffer.getShort();			// Confirmed - but with exception
			
			if (validMarker != (byte)0xFFFF)
			{
				System.out.println("Invalid block.");
				continue;
			}

			if (blockUsed == (byte)0xFFFF)
			{
				System.out.println("Unused block.");
				continue;
			}

			System.out.printf("Chunk Id: %s NBytes: %s State: %s ObjectID %s ", chunkId, nBytes, blockUsed, objectId);
			
			this.byteBuffer.position(0);

			// Try to read block as object header first...
			header = this.tryReadObjectHeader(block);

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
			System.out.printf("Found data block. Sequence: %s ", blockSequence);
			
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
				System.out.println("Cannot determine which object this data block belongs to. Skipping data block.");
				continue;
			}
		
			// Assign data to current object
			System.out.println("Assigning data block to " + parentObject.getName());
			
			// Trim block to size
			object.addDataBlock(blockSequence - 1, Arrays.copyOf(block, nBytes));
		}
		
		System.out.println("- Data carving complete.");
		
		// Map parents
		YAFFS2VersionedObject rootObject = null;
		
		for(YAFFS2VersionedObject currentObject : objects)
		{
			if(currentObject.getFirstVersion().getId() == 1)
			{
				rootObject = currentObject;
			}
			
			for(YAFFS2VersionedObject currentParentObject : objects)
			{
				if(currentObject.equals(currentParentObject))
				{
					continue;
				}
				
				if(currentObject.getFirstVersion().getParentObjectId() == currentParentObject.getFirstVersion().getId())
				{
					currentParentObject.addChild(currentObject);
					break;
				}
			}
		}
		
		// Iterate root to get data
		
		String filePath = "." + File.separator + "output" + File.separator + file.getName() + File.separator;
		
		dumpFiles(rootObject, filePath);
		
		return true;
	}
	
	private void dumpFiles(YAFFS2VersionedObject versionedObject, String rootFilePath) throws IOException
	{
		if(versionedObject == null)
		{
			return;
		}
		
		int numberOfVersions = versionedObject.getVersionsCount();
		
		while(-- numberOfVersions >= 0)
		{
			YAFFS2Object object = versionedObject.getVersion(numberOfVersions);
			
			File file = new File(rootFilePath + versionedObject.getRelativePath());
			
			if(object.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
			{
				System.out.printf("Creating directory %s%n", file.getPath());
				
				file.mkdirs();
			}
			else if(object.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_FILE)
			{
				File parent = new File(file.getParentFile().getAbsolutePath());
				file = new File(file.getParentFile().getAbsolutePath() + File.separator + "V" + numberOfVersions + "_" + object.getName());

				parent.mkdirs();
				FileOutputStream fileStream = new FileOutputStream(file);
				
				System.out.printf("Writing to %s (ObjectID: %s)%n", file.getAbsolutePath(), object.getId());
				
				HashMap<Integer, byte[]> dataChunks = object.getDataChunks();
				
				for (byte[] dataChunk : dataChunks.values())
				{
					if(dataChunk != null)
					{
						fileStream.write(dataChunk);
					}
				}
				
				fileStream.close();
			}
		}
		Enumeration<YAFFS2VersionedObject> enumeration = versionedObject.children();
		
		while(enumeration.hasMoreElements())
		{
			YAFFS2VersionedObject child = enumeration.nextElement();
			dumpFiles(child, rootFilePath);
		}
		
	}

	private YAFFS2ObjectHeader tryReadObjectHeader(byte[] block)
	{
		if (block.length != this.blockSize + this.oobSize)
		{
			return null;
		}

		this.byteBuffer = ByteBuffer.wrap(block);
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
