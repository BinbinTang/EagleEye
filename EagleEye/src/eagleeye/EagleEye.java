package eagleeye;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
import com.sun.xml.internal.ws.util.StringUtils;

import eagleeye.datacarving.AndroidBinaryFileReader;
import eagleeye.datacarving.BinaryFileReader;
import eagleeye.datacarving.YAFFS2BinaryFileReader;

public class EagleEye
{
	public static void main(String[] args) throws Exception
	{
		// Android Images
		// https://android.googlesource.com/platform/system/core/+/master/mkbootimg/bootimg.h
		
		// Algorithm
		//http://sandbox.dfrws.org/2011/burenin/Android%20file%20system%20recovering.pdf

		/*
		 * STEP 01 - FORENSIC INVESTIGATOR UPLOADS RAW DATA IMAGES BY SELECTING A FOLDER
		 */
		String directoryPath = "C:\\Users\\Admin\\Downloads\\Case1";
		
		File directory = new File(directoryPath);
		
		if(!directory.isDirectory())
		{
			throw new Exception("The provided path is not a directory.");
		}
		
		// Assuming files are all in main directory
		File[] files = directory.listFiles();
		
		/*
		 * STEP 02 - FILE SYSTEM LAYER
		 * FILES ARE TAGGED TO CERTAIN TYPES TO PREPARE FOR DATA CARVING
		 */
		HashMap<File, String> identifiedFiles = new HashMap<>();
		
		for (File file : files)
		{
			identifiedFiles.put(file, identifyFileSystem(file));
		}
		
		String type;
		
		identifiedFiles.keySet().toArray(files);

		Arrays.sort(files);
		
		if(files.length > 0)
		{
			System.out.println("-----------------");
			System.out.println("File System Layer");
			System.out.println("-----------------");
			System.out.printf("%-25s\t%-10s\t%15s%n", "Name", "Type", "Size");
			
			for (File file : files)
			{
				type = identifiedFiles.get(file);
				
				if(type == null)
				{
					type = "-";
				}
				
				System.out.printf("%-25s\t%-10s\t%12s KB%n", file.getName(), type, file.length());
			}
			
			System.out.println();
		}
		
		/*
		 * STEP 03 - DATA CARVING
		 * BASED ON DATA FROM FILE SYSTEM LAYER, CARVE OUT DATA FROM FILE
		 */
		if(files.length > 0)
		{
			System.out.println("------------------");
			System.out.println("Data Carving Layer");
			System.out.println("------------------");
			
			Class baseClass = BinaryFileReader.class;
			String readerClassName;
			
			for (File file : files)
			{
				type = identifiedFiles.get(file);
				
				// Skip files that are unidentified
				if(type == null)
				{
					continue;
				}
				
				// Locate the reader
				// SIMULATION: Plugin for Android and YAFFS2
				readerClassName = baseClass.getPackage().getName() + "." + type + baseClass.getSimpleName();

				System.out.printf("Attempting to locate %s for %s ", readerClassName,  file.getName());
				
				Class readerClass;
				
				try
				{
					readerClass = Class.forName(readerClassName);
				}
				catch (ClassNotFoundException e)
				{
					System.out.println("[FAIL]");
					continue;
				}
				
				System.out.println("[OK]");
				
				BinaryFileReader reader = (BinaryFileReader) readerClass.newInstance();
				
				reader.read(file);
			}
			
			System.out.println();
		}
	}
	
	private static String identifyFileSystem(File file) throws Exception
	{
		BinaryFileReader reader;

		/*
		 * SIMULATION: Plugin for Android and YAFFS2
		 */
		
		reader = new AndroidBinaryFileReader();

		if(reader.readSignature(file))
		{
			return "Android";
		}
		
		reader = new YAFFS2BinaryFileReader();
		
		if(reader.readSignature(file))
		{
			return "YAFFS2";
		}
		
		return null;
	}
}
