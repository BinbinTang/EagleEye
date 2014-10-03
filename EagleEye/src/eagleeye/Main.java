package eagleeye;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import eagleeye.datacarving.unpack.AndroidBootImageUnpacker;
import eagleeye.datacarving.unpack.DiskImageUnpackerManager;
import eagleeye.datacarving.unpack.YAFFS2ImageUnpacker;
import eagleeye.filesystem.format.AndroidBootFormatIdentifier;
import eagleeye.filesystem.format.FormatDescription;
import eagleeye.filesystem.format.FormatIdentifierManager;
import eagleeye.filesystem.format.YAFFS2FormatIdentifier;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application
{
	@Override
    public void start(Stage primaryStage) throws Exception
	{
		/*
		 * STEP 01 - FORENSIC INVESTIGATOR UPLOADS RAW DATA IMAGES BY SELECTING
		 * A FOLDER
		 */
		
		//String directoryPath = "/Users/BinbinTang/Downloads/Case2";
		String directoryPath = "C:\\Users\\Admin\\Downloads\\Case2";

		File directory = new File(directoryPath);

		if (!directory.isDirectory())
		{
			throw new Exception("The provided path is not a directory.");
		}

		// Assuming files are all in main directory
		File[] files = directory.listFiles();

		/*
		 * STEP 02 - FILE SYSTEM LAYER FILES ARE TAGGED TO CERTAIN TYPES TO
		 * PREPARE FOR DATA CARVING
		 */
		FormatIdentifierManager formatIdentifierManager = new FormatIdentifierManager();
		
		// Simulate plug ins
		formatIdentifierManager.load(new AndroidBootFormatIdentifier());
		formatIdentifierManager.load(new YAFFS2FormatIdentifier());
		
		ArrayList<FormatDescription> formatDescriptions = new ArrayList<FormatDescription>();

		Arrays.sort(files);

		if (files.length > 0)
		{
			System.out.println("-----------------");
			System.out.println("File System Layer");
			System.out.println("-----------------");
			System.out.printf("%-25s\t%-20s\t%15s%n", "Name", "Binary Image Type", "Size");
			
			for (File file : files)
			{
				FormatDescription formatDescription = formatIdentifierManager.identify(file);
				String binaryImageType = "-";
				
				if(formatDescription != null)
				{
					formatDescriptions.add(formatDescription);
					binaryImageType = formatDescription.getBinaryImageType();
				}
				
				System.out.printf("%-25s\t%-20s\t%12s KB%n", file.getName(), binaryImageType, file.length());
			}			
		}

		System.out.println();

		/*
		 * STEP 03 - DATA CARVING BASED ON DATA FROM FILE SYSTEM LAYER, CARVE
		 * OUT DATA FROM FILE
		 */

		DiskImageUnpackerManager diskImageUnpackerManager = new DiskImageUnpackerManager();
		
		// Simulate plug ins
		diskImageUnpackerManager.load(new AndroidBootImageUnpacker());
		diskImageUnpackerManager.load(new YAFFS2ImageUnpacker());
		
		if (formatDescriptions.size() > 0)
		{
			System.out.println("------------------");
			System.out.println("Data Carving Layer");
			System.out.println("------------------");
			System.out.println();
			
			// Always unpack OS images first

			for (FormatDescription formatDescription : formatDescriptions)
			{
				if(formatDescription.getOperatingSystem() == null)
				{
					continue;
				}
				
				if(diskImageUnpackerManager.unpack(formatDescription) == null)
				{
					break;
				}
			}

			for (FormatDescription formatDescription : formatDescriptions)
			{
				if(formatDescription.getOperatingSystem() != null)
				{
					continue;
				}
				
				diskImageUnpackerManager.unpack(formatDescription);
			}

			System.out.println();
		}

		/*
		 * User Interface
		 */
		try
		{
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(
					getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
