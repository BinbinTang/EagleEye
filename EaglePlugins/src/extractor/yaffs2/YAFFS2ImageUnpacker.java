package extractor.yaffs2;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import eagleeye.api.entities.*;

public class YAFFS2ImageUnpacker extends Service<ArrayList<EagleFile>>
{
	protected FileInputStream fileInputStream;
	protected DataInputStream inputStream;

	protected byte[] inputBytes;

	protected ByteBuffer byteBuffer;

	protected int chunkSize;
	protected int oobSize;
	
	protected TreeMap<Integer, TreeMap<Integer, byte[]>> dataChunks = new TreeMap<>();
	protected FormatDescription formatDescription;

	public FormatDescription getFormatDescription()
	{
		return formatDescription;
	}

	public void setFormatDescription(FormatDescription formatDescription)
	{
		this.formatDescription = formatDescription;
	}
	public YAFFS2ImageUnpacker()
	{
		this.setChunkSize(2048);
	}
	
	public void setChunkSize(int chunkSize)
	{
		this.chunkSize = chunkSize;
		this.oobSize = chunkSize / 32;
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
	
	private String writeFile(String directoryPath, String fileName, TreeMap<Integer, byte[]> dataChunks) throws IOException
	{
		File file = new File(directoryPath);
		file.mkdirs();
		
		file = new File(directoryPath + File.separator + fileName);

		FileOutputStream fileStream = new FileOutputStream(file);
		
		//System.out.println(String.format("Writing to %s", file.getCanonicalPath()));
		int MAX_PATH_LENGTH = 50;
		String name = file.getName();
		String absPath = file.getCanonicalPath();
		String parentPath = absPath.substring(0,absPath.length()-name.length());
		if(name.length()>=MAX_PATH_LENGTH) name = name.substring(0,(int)(MAX_PATH_LENGTH*0.5))+"...";
		int cutoff = 50-name.length();
		if(parentPath.length()>cutoff){
			try{
				parentPath=parentPath.substring(0,cutoff)+"..."+file.separator;
			}catch(Exception e){
				System.out.println("erroneous parent path = "+ parentPath);
				System.out.println("erroneous name = "+ name);
			}
		}
		name = parentPath+name;
		
		for (Entry<Integer, byte[]> entry : dataChunks.entrySet())
		{
			fileStream.write(entry.getValue());
		}
		
		fileStream.close();
		
		return String.format("Writing to "+name);
	}
	

	@Override
	protected Task<ArrayList<EagleFile>> createTask() 
	{
		return new Task<ArrayList<EagleFile>>()
		{
			@Override
			protected ArrayList<EagleFile> call() throws Exception
			{
				if(YAFFS2ImageUnpacker.this.formatDescription.getBinaryImageType() != "YAFFS2")
				{
					return null;
				}
				
				File file = YAFFS2ImageUnpacker.this.formatDescription.getFile();
				YAFFS2ImageUnpacker.this.fileInputStream = new FileInputStream(file);
				YAFFS2ImageUnpacker.this.inputStream = new DataInputStream(YAFFS2ImageUnpacker.this.fileInputStream);
				
//				System.out.println(String.format("Data carving started for %s...%n", file.getName()));
				
				int totalChunkSize = YAFFS2ImageUnpacker.this.chunkSize + YAFFS2ImageUnpacker.this.oobSize;
				
				ArrayList<byte[]> chunks = new ArrayList<>();
				
				// Split up the data into chunks
				int totalFileBytes = (int) file.length();
				int totalBytesRead = 0;
				
				updateMessage(file.getName()+": Splitting raw image file into chunks.");
				updateProgress(0, totalFileBytes);

				while (totalFileBytes > totalBytesRead + totalChunkSize)
				{
//					if(this.isCancelled() || Thread.currentThread().isInterrupted())
//					{
//						return null;
//					}
					
					YAFFS2ImageUnpacker.this.inputBytes = new byte[YAFFS2ImageUnpacker.this.chunkSize + YAFFS2ImageUnpacker.this.oobSize];
					YAFFS2ImageUnpacker.this.inputStream.readFully(YAFFS2ImageUnpacker.this.inputBytes);
					YAFFS2ImageUnpacker.this.byteBuffer = ByteBuffer.wrap(YAFFS2ImageUnpacker.this.inputBytes);
					YAFFS2ImageUnpacker.this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

					chunks.add(YAFFS2ImageUnpacker.this.byteBuffer.array());
					
					totalBytesRead += YAFFS2ImageUnpacker.this.chunkSize + YAFFS2ImageUnpacker.this.oobSize;
					
					updateProgress(totalBytesRead, totalFileBytes);
				}
				
				updateMessage(file.getName()+": Scanning chunks.");
				updateProgress(0, chunks.size());

				// Read from bottom up
				Collections.reverse(chunks);
				
				// Scan chunks for block sequence and object id
				TreeMap<Integer, TreeMap<Integer, ArrayList<byte[]>>> objects = new TreeMap<>();
				
				int chunkCount = -1;
				
				for (byte[] chunk : chunks)
				{
//					if(this.isCancelled() || Thread.currentThread().isInterrupted())
//					{
//						return null;
//					}
					
					chunkCount ++;
					
					YAFFS2ImageUnpacker.this.byteBuffer = ByteBuffer.wrap(chunk);
					YAFFS2ImageUnpacker.this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

//					System.out.println(String.format("Scanning chunk %s. ", chunkCount));

					YAFFS2ImageUnpacker.this.byteBuffer.position(YAFFS2ImageUnpacker.this.chunkSize); 

					short validMarker = YAFFS2ImageUnpacker.this.byteBuffer.getShort();
					int blockSequence = YAFFS2ImageUnpacker.this.byteBuffer.getInt();

					if (validMarker != (byte)0xFFFF)
					{
//						System.out.println("Invalid chunk.");
					    updateProgress(chunkCount + 1, chunks.size());
						continue;
					}
					
					if(blockSequence == 0xFFFFFFFF)
					{
//						System.out.println("Empty chunk.");
					    updateProgress(chunkCount + 1, chunks.size());
						continue;
					}

//					System.out.println(String.format("Block Sequence: %s ", blockSequence));

					YAFFS2ImageUnpacker.this.inputBytes = new byte[3];
					YAFFS2ImageUnpacker.this.byteBuffer.get(YAFFS2ImageUnpacker.this.inputBytes);
					
				    int objectId = (YAFFS2ImageUnpacker.this.inputBytes[2] & 0xFF) << 8 | (YAFFS2ImageUnpacker.this.inputBytes[1] & 0xFF) << 8 | (YAFFS2ImageUnpacker.this.inputBytes[0] & 0xFF);

//				    System.out.println(String.format("Object Id: %s %n", objectId));
				    
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
				    updateProgress((chunkCount + 1), chunks.size());
				}


			    updateMessage(file.getName()+": Carving data from chunks.");
			    
				ArrayList<YAFFS2Object> yaffs2Objects = new ArrayList<YAFFS2Object>();
				HashMap<Integer, YAFFS2ObjectHeader> yaffs2ParentObjects = new HashMap<Integer, YAFFS2ObjectHeader>();
				
				// For each object id found starting from the smallest id (naturally sorted)
				int objectsProcessed = 0;
				Set<Entry<Integer, TreeMap<Integer, ArrayList<byte[]>>>> entrySet = objects.entrySet();

			    updateProgress(0, entrySet.size());
			    
				for (Entry<Integer, TreeMap<Integer, ArrayList<byte[]>>> object : entrySet)
				{
//					if(this.isCancelled() || Thread.currentThread().isInterrupted())
//					{
//						return null;
//					}
					
					int objectId = object.getKey();
					
//					System.out.println(String.format("Carving object %s%n", objectId));
					YAFFS2Object yaffs2Object = new YAFFS2Object();
					yaffs2Object.setId(objectId);
					yaffs2Object.setChunkSize(chunkSize);
					
					TreeMap<Integer, ArrayList<byte[]>> blocks = object.getValue();
					
					// For each block starting with the largest sequence number (naturally sorted)
					for(Entry<Integer, ArrayList<byte[]>> blockChunks : blocks.descendingMap().entrySet())
					{
						int blockSequence = blockChunks.getKey();

//						System.out.println(String.format("Analyzing block. Block Sequence: %s%n", blockSequence));
						
						boolean isDeleted = false;
						boolean isUnlinked = false;
						
						for(byte[] chunk : blockChunks.getValue())
						{
							YAFFS2ImageUnpacker.this.byteBuffer = ByteBuffer.wrap(chunk);
							YAFFS2ImageUnpacker.this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

//							System.out.println("Carving chunk");
							
							YAFFS2ImageUnpacker.this.byteBuffer.position(YAFFS2ImageUnpacker.this.chunkSize); 
				
							short validMarker = YAFFS2ImageUnpacker.this.byteBuffer.getShort();

							if (validMarker != (byte)0xFFFF)
							{
//								System.out.println("Invalid chunk.");
								continue;
							}
							
							YAFFS2ImageUnpacker.this.byteBuffer.getInt(); // Block Sequence
							
							YAFFS2ImageUnpacker.this.inputBytes = new byte[3];
							YAFFS2ImageUnpacker.this.byteBuffer.get(YAFFS2ImageUnpacker.this.inputBytes); // Object Id
							
						    YAFFS2ImageUnpacker.this.byteBuffer.get(); // Object type
						    
							YAFFS2ImageUnpacker.this.inputBytes = new byte[3];
							YAFFS2ImageUnpacker.this.byteBuffer.get(YAFFS2ImageUnpacker.this.inputBytes);
							
							int parentIdOrChunkId = 0;
				
							parentIdOrChunkId |= YAFFS2ImageUnpacker.this.inputBytes[2] & 0xFF;
							parentIdOrChunkId <<= 8;
							parentIdOrChunkId |= YAFFS2ImageUnpacker.this.inputBytes[1] & 0xFF;
							parentIdOrChunkId <<= 8;
							parentIdOrChunkId |= YAFFS2ImageUnpacker.this.inputBytes[0] & 0xFF;
						    
							YAFFS2ImageUnpacker.this.byteBuffer.get(); // Status
							int nBytes = YAFFS2ImageUnpacker.this.byteBuffer.getInt();				
							
//							System.out.println(String.format("Parent Id or Chunk Id: %s, Bytes in block: %s. ", parentIdOrChunkId, nBytes));
							
							YAFFS2ImageUnpacker.this.byteBuffer.position(0);
							
							// Try to read chunk as object header first...
							YAFFS2ObjectHeader header = YAFFS2ImageUnpacker.this.tryReadObjectHeader(chunk);
							
							if (header != null) // Chunk is an object header
							{
								// Discard ghost headers
								if(header.getName().length() == 0 && objectId > 1)
								{
									continue;
								}
								
								if(header.getName().equals("deleted") && header.getParentObjectId() == 4)
								{
									isDeleted = true;
//									System.out.println("Found delete header.\n");
									continue;
								}
								
								if(header.getName().equals("unlinked") && header.getParentObjectId() == 3)
								{
									isUnlinked = true;
//									System.out.println("Found unlink header.\n");
									continue;
								}
								
								header.setDeleted(isDeleted);
								header.setUnlinked(isUnlinked);
								
								yaffs2Object.addHeader(header);
								
								if(header.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
								{
									if(objectId <= 1)
									{
										header.setName(file.getName());
										header.setParentObjectId(0);
									}
									
									yaffs2ParentObjects.put(objectId, header);
								}
				
								if(!yaffs2Objects.contains(yaffs2Object))
								{
									yaffs2Objects.add(yaffs2Object);
								}
								
//								System.out.println("Found object header.\n");
								continue;
							}
							
							// Block is data
//							System.out.println("Found data chunk.\n");
							yaffs2Object.addDataChunk(parentIdOrChunkId, Arrays.copyOf(chunk, nBytes));
						}
					}

					objectsProcessed++;
					updateProgress(objectsProcessed, entrySet.size());
				}

				ArrayList<EagleFile> files = new ArrayList<EagleFile>();
				String deviceName = YAFFS2ImageUnpacker.this.formatDescription.getDeviceName();
				
				String rootFilePath = "." + File.separator + "output" + File.separator + deviceName + File.separator + file.getName();
				HashMap<Integer, String> parentPaths = new HashMap<Integer, String>();
				
				for (Entry<Integer, YAFFS2ObjectHeader> entry : yaffs2ParentObjects.entrySet())
				{
//					if(this.isCancelled() || Thread.currentThread().isInterrupted())
//					{
//						return null;
//					}
					
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
//					if(this.isCancelled() || Thread.currentThread().isInterrupted())
//					{
//						return null;
//					}
					
					File directory = new File(path);
					directory.mkdirs();
				}
				
				updateMessage(file.getName()+": Writing files to local file system.");
				updateProgress(0, yaffs2Objects.size());
				
				objectsProcessed = 0;
				for (YAFFS2Object yaffs2Object : yaffs2Objects)
				{
//					if(this.isCancelled() || Thread.currentThread().isInterrupted())
//					{
//						return null;
//					}
					
					//TreeMap<Integer, SimpleEntry<YAFFS2ObjectHeader, TreeMap<Integer, byte[]>>> versions = yaffs2Object.getVersions();

					//for (Entry<Integer, SimpleEntry<YAFFS2ObjectHeader, TreeMap<Integer, byte[]>>> entry : versions.entrySet())
					//{
						//int version = entry.getKey();
						Entry<Integer, SimpleEntry<YAFFS2ObjectHeader, TreeMap<Integer, byte[]>>> entry = yaffs2Object.getVersions().firstEntry(); //lastEntry();
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
						
						FileEntity genericFile = new FileEntity();
						
						genericFile.modifyIsRecovered(header.isDeleted());
						
						if(header.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_FILE)
						{
							String writeMessage = YAFFS2ImageUnpacker.this.writeFile(filePath, header.getName(), dataChunks);
							updateMessage(file.getName()+": "+writeMessage);
							
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
							
							genericFile.modifyContentType(metaData.get(Metadata.CONTENT_TYPE));
						}
						
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");

						genericFile.modifyDateAccessed(dateFormat.format(new Date(header.getAccessedTime() * 1000L)));
						genericFile.modifyDateCreated(dateFormat.format(new Date(header.getCreatedTime() * 1000L)));
						genericFile.modifyDateModified(dateFormat.format(new Date(header.getModifiedTime() * 1000L)));
						genericFile.modifyDirectoryID(header.getParentObjectId());
						genericFile.modifyFileID(yaffs2Object.getId());

						if(header.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
						{
							genericFile.modifyIsDirectory(true);
							genericFile.modifyFileName(header.getName());
						}
						else
						{
							if(header.getName().indexOf('.') > -1)
							{
								if(header.getName().lastIndexOf('.') + 1 < header.getName().length())
								{
									genericFile.modifyFileExt(header.getName().substring(header.getName().lastIndexOf('.') + 1));
								}
								
								genericFile.modifyFileName(header.getName().substring(0, header.getName().lastIndexOf('.')));
							}
							else
							{
								genericFile.modifyFileName(header.getName());
							}
						}
						
						genericFile.modifyFilePath(filePath);
						
						files.add(genericFile);
					//}
					objectsProcessed ++;
					updateProgress(objectsProcessed, yaffs2Objects.size());
				}
				
				updateMessage("Finished.");
				return files;
			}
		};
	}
	
}
