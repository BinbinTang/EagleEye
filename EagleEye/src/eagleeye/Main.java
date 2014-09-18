package eagleeye;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import eagleeye.filesystem.format.AndroidBootFormatIdentifier;
import eagleeye.filesystem.format.Format;
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
		String directoryPath = "C:\\Users\\Admin\\Downloads\\Case1";

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
		
		// Simulate plug in
		formatIdentifierManager.load(new AndroidBootFormatIdentifier());
		formatIdentifierManager.load(new YAFFS2FormatIdentifier());
		
		ArrayList<Format> formats = new ArrayList<Format>();

		Arrays.sort(files);

		if (files.length > 0)
		{
			System.out.println("-----------------");
			System.out.println("File System Layer");
			System.out.println("-----------------");
			System.out.printf("%-25s\t%-20s\t%15s%n", "Name", "Binary Image Type", "Size");
			
			for (File file : files)
			{
				Format format = formatIdentifierManager.identify(file);
				String binaryImageType = "-";
				
				if(format != null)
				{
					formats.add(format);
					binaryImageType = format.getBinaryImageType();
				}
				
				System.out.printf("%-25s\t%-20s\t%12s KB%n", file.getName(), binaryImageType, file.length());
			}			
		}
		
		// UI
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
