package eagleeye.datacarving.unpack;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import eagleeye.filesystem.format.FormatDescription;
import eagleeye.filesystem.partition.mtd.MTDCommandLineParser;
import eagleeye.filesystem.partition.mtd.MTDDefinition;

public class AndroidBootImageUnpacker implements IDiskImageUnpacker
{
	protected FormatDescription formatDescription;
	
	protected FileInputStream fileInputStream;
	protected DataInputStream inputStream;

	protected byte[] inputBytes;

	protected ByteBuffer byteBuffer;
	
	@Override
	public boolean unpack(FormatDescription formatDescription) throws Exception
	{
		if(formatDescription.getBinaryImageType() != "AndroidBoot")
		{
			return false;
		}
		
		this.formatDescription = formatDescription;
		File file = this.formatDescription.getFile();
		this.fileInputStream = new FileInputStream(file);
		this.inputStream = new DataInputStream(this.fileInputStream);
		
		byte[] inputBytes = new byte[8];
		this.inputStream.readFully(inputBytes);
		
		String magicSignature = new String(inputBytes);

		if (!magicSignature.equals("ANDROID!"))
		{
			return false;
		}
		
		System.out.printf("Unpacking Android Boot Image %s...%n", file.getName());
		System.out.println();
		
		byte[] unsignedHeaderBytes = new byte[40];
		this.inputStream.readFully(unsignedHeaderBytes);
		
		ByteBuffer bb = ByteBuffer.wrap(unsignedHeaderBytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		int kernelSize = bb.getInt();
		int kernelAddress = bb.getInt();
		int kernelBaseAddress = kernelAddress - 0x00008000;

		int ramdiskSize = bb.getInt();
		int ramdiskAddress = bb.getInt();
		int ramdiskBaseAddress = ramdiskAddress - 0x01000000;

		int secondStageSize = bb.getInt();
		int secondStageAddress = bb.getInt();
		int secondStageBaseAddress = secondStageAddress - 0x00f00000;

		int kernelTagsAddress = bb.getInt();
		int kernelTagsBaseAddress = kernelTagsAddress - 0x00000100;

		int pageSize = bb.getInt();
		
		// int unused01 = bb.getInt();
		// System.out.println("Unused 01: " + unused01);

		// int unused02 = bb.getInt();
		// System.out.println("Unused 02: " + unused02);
		
		bb.getInt();
		bb.getInt();
		
		byte[] productNameBytes = new byte[16];
		
		this.inputStream.readFully(productNameBytes);
		
		String productName = new String(productNameBytes);
		
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

		System.out.println("----------------------------------------");
		System.out.println("ANDROID IMAGE HEADER");
		System.out.println("----------------------------------------");
		
		System.out.printf("Kernel Size: %s bytes (%s mb)%n",
				numberFormat.format(kernelSize),
				numberFormat.format((double) kernelSize / 1024 / 1024));
		System.out.printf("Kernel Address: %d (0x%8s)%n", kernelAddress,
				String.format("%8s", Integer.toHexString(kernelAddress))
				.replace(' ', '0'));
		System.out.printf("Kernel Base Address: %d (0x%8s)%n",
				kernelBaseAddress,
				String.format("%8s", Integer.toHexString(kernelBaseAddress))
				.replace(' ', '0'));
		System.out.println();
		
		System.out.printf("Ramdisk Size: %s bytes (%s mb)%n",
				numberFormat.format(ramdiskSize),
				numberFormat.format((double) ramdiskSize / 1024 / 1024));
		System.out.printf("Ramdisk Address: %d (0x%8s)%n", ramdiskAddress,
				String.format("%8s", Integer.toHexString(ramdiskAddress))
				.replace(' ', '0'));
		System.out.printf("Ramdisk Base Address: %d (0x%8s)%n",
				ramdiskBaseAddress,
				String.format("%8s", Integer.toHexString(ramdiskBaseAddress))
				.replace(' ', '0'));
		System.out.println();
		
		System.out.printf("Second Stage Size: %s bytes (%s mb)%n",
				numberFormat.format(secondStageSize),
				numberFormat.format((double) secondStageSize / 1024 / 1024));
		System.out.printf("Second Stage Address: %d (0x%8s)%n",
				secondStageAddress,
				String.format("%8s", Integer.toHexString(secondStageAddress))
				.replace(' ', '0'));
		System.out.printf(
				"Second Stage Base Address: %d (0x%8s)%n",
				secondStageBaseAddress,
				String.format("%8s",
						Integer.toHexString(secondStageBaseAddress)).replace(
								' ', '0'));
		System.out.println();
		
		System.out.printf("Kernel Tags Address: %d (0x%8s)%n",
				kernelTagsAddress,
				String.format("%8s", Integer.toHexString(kernelTagsAddress))
				.replace(' ', '0'));
		System.out
		.printf("Kernel Tags Base Address: %d (0x%8s)%n",
				kernelTagsBaseAddress,
				String.format("%8s",
						Integer.toHexString(kernelTagsBaseAddress))
						.replace(' ', '0'));
		System.out.println();
		
		System.out.println("Product Name: " + productName);
		System.out.println();
		
		System.out.printf("Page Size: %s bytes (%s mb)%n",
				numberFormat.format(pageSize),
				numberFormat.format((double) pageSize / 1024 / 1024));
		System.out.println();
		
		byte[] commandLineBytes = new byte[512];
		
		this.inputStream.readFully(commandLineBytes);
		
		String commandLine = new String(commandLineBytes);
		
		System.out.println("Full Command Line: " + commandLine);
		System.out.println();
		
		byte[] unsignedIdBytes = new byte[32];
		this.inputStream.readFully(unsignedIdBytes);
		
		String[] commandLineParameters = commandLine.split(" ");
		
		// Try to parse MTD partition info
		
		boolean hasMTDDefinition = false;
		String mtdDefinitionString = "";
		
		for (String parameter : commandLineParameters)
		{
			String[] parameterKeyValue = parameter.split("=", 2);
			
			if (parameterKeyValue.length < 2)
			{
				continue;
			}

			if (parameterKeyValue[0].equals("mtdparts"))
			{
				hasMTDDefinition = true;
				mtdDefinitionString = parameter;
				break;
			}
		}
		
		if (!hasMTDDefinition)
		{
			System.out.println("Cannot find MTD info!");
			this.inputStream.close();
			return false;
		}
		
		ArrayList<MTDDefinition> mtdDefinitions = MTDCommandLineParser.parse(mtdDefinitionString);
		
		for (MTDDefinition mtdDefinition : mtdDefinitions)
		{
			mtdDefinition.print();
		}
		
		System.out.println();
		
		this.inputStream.close();
		
		return true;
	}
}