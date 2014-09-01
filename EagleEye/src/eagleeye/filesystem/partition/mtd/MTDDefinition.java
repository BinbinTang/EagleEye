package eagleeye.filesystem.partition.mtd;

import java.util.ArrayList;

public class MTDDefinition
{
	protected String id;
	protected ArrayList<MTDPartitionDefinition> partitionDefinitions;
	
	public MTDDefinition(String id, ArrayList<MTDPartitionDefinition> partitionDefinitions)
	{
		this.id = id;
		this.partitionDefinitions = partitionDefinitions;
	}
	
	public ArrayList<MTDPartitionDefinition> getParitionDefinitions()
	{
		return this.partitionDefinitions;
	}
	
	public String getID()
	{
		return this.id;
	}

	public void print()
	{
		System.out.println("----------------------------------------");
		System.out.printf("MTD DEFINITION (%s)%n", this.id);
		System.out.println("----------------------------------------");
		System.out.println();
		
		for(MTDPartitionDefinition partitionDefinition : this.partitionDefinitions)
		{
			partitionDefinition.print();
		}
	}
}
