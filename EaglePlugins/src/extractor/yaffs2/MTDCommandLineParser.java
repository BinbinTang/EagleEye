package extractor.yaffs2;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to help parse the mtdparts section of a command line.
 *
 * The command line format is defined as follows:
 *
 * mtdparts=<mtd-definition>[;<mtd-definition>]
 *
 * <mtd-definition> := <mtd-id>:<partition-definition>[,<partition-definition>]
 * <mtd-id> := Unique name used in mapping driver/device from the "cat /proc/mtd" command (mtd->name)
 * <partition-definition> := <size>[@<offset>][(<name>)]
 */
public class MTDCommandLineParser
{
	public static ArrayList<MTDDefinition> parse(String commandLine) throws Exception
	{
		String[] commandLineParts = commandLine.split("=", 2);
		
		if (commandLineParts.length < 2 || !commandLineParts[0].equals("mtdparts"))
		{
			throw new Exception("Could not find mtdparts parameter in command line: " + commandLine);
		}
		
		String[] mtdDefinitionStrings = commandLineParts[1].split(";");
		
		ArrayList<MTDDefinition> mtdDefinitions = new ArrayList<MTDDefinition>();
		
		for (String mtdDefinitionString : mtdDefinitionStrings)
		{
			String[] mtdDefinitionParts = mtdDefinitionString.split(":", 2);
			
			if (mtdDefinitionParts.length < 2)
			{
				throw new Exception("Could not find MTD ID in MTD Definition " + mtdDefinitionString);
			}
			
			String mtdID = mtdDefinitionParts[0];
			String[] partitionDefinitionStrings = mtdDefinitionParts[1].split(",");
			
			ArrayList<MTDPartitionDefinition> partitionDefinitions = new ArrayList<MTDPartitionDefinition>();
			
			for (String partitionDefinitionString : partitionDefinitionStrings)
			{
				Pattern partitionDefinitionPattern = Pattern.compile("(?<size>\\d+)(?<sizeModifier>k|m)(@(?<offset>\\d+)(?<offsetModifier>k|m))?\\((?<name>[^)]+)\\)");
				Matcher matcher = partitionDefinitionPattern.matcher(partitionDefinitionString);
				
				if (matcher.find())
				{
					String name = matcher.group("name");
					int size = Integer.parseInt(matcher.group("size"));
					char sizeModifier = matcher.group("sizeModifier").charAt(0);
					int sizeInBytes = getSizeInBytes(size, sizeModifier);
					
					int offsetInBytes = 0;
					
					if (matcher.group("offset") != null)
					{
						int offset = Integer.parseInt(matcher.group("offset"));
						char offsetModifier = matcher.group("offsetModifier").charAt(0);
						offsetInBytes = getSizeInBytes(offset, offsetModifier);
					}
					
					partitionDefinitions.add(new MTDPartitionDefinition(name, sizeInBytes, offsetInBytes));
				}
			}
			
			MTDDefinition mtdDefinition = new MTDDefinition(mtdID, partitionDefinitions);
			mtdDefinitions.add(mtdDefinition);
		}
		
		return mtdDefinitions;
	}
	
	protected static int getSizeInBytes(int size, char modifier) throws Exception
	{
		ArrayList<Character> units = new ArrayList<Character>();
		units.add('b');
		units.add('k');
		units.add('m');
		units.add('g');
		units.add('t');
		
		if (!units.contains(modifier))
		{
			throw new Exception("Invalid size modifier " + modifier);
		}
		
		double multiplier = Math.pow(1024, units.indexOf(modifier));
		
		size *= multiplier;
		
		return size;
	}
}
