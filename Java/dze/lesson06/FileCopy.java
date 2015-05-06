package dze.lesson06;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;

class FileCopy
{
	public static void main(String[] args)
	{
		try
		{
			if(args.length < 2)
			{
				System.out.println("Not enough parameters - need atleast [old_name] [new_name].");
				System.exit(1);
			}

			String old_name = args[0];
			String new_name = args[1];

			/*Create the file if necessary: */
			File target = new File(old_name);
			if(!target.exists())
			{
				target.createNewFile();
				target.deleteOnExit(); // Delete original.
			}

			String method;
			if(args.length == 3)
				method = args[2];
			else
				method = new String("Chimichanga!"); // Help instead of a default value.

			switch(method)
			{
				case "IO":
					use_IO(old_name,new_name);
					break;
				case "NIOCB":
					use_NIO_CB(old_name,new_name);
					break;
				case "NIOFC":
					use_NIO_FC(old_name,new_name);
					break;
				case "NIOMM":
					use_NIO_MM(old_name,new_name);
					break;
				default:
					System.out.println("IO - Streams");
					System.out.println("NIOCB - Channels & Buffers");
					System.out.println("NIOFC - FileChannel.transfer ...");
					System.out.println("NIOMM - Memory mapped files");
					System.out.println("NIO2 - Files.copy()");
					break;
			}
		}
		catch(IOException ex)
		{
			System.out.println("Huh? " + ex.getMessage());
		}
	}

	public static void use_IO(String old_name, String new_name) throws IOException
	{

		FileReader input = new FileReader(old_name);
		FileWriter output = new FileWriter(new_name);

		output.write(""); // To overwrite the file if it exists.

		int c;
		while((c = input.read()) != -1)
			output.append((char)c);

		// Apply changes:
		input.close();
		output.close();
	}

	public static void use_NIO_CB(String old_name, String new_name) throws IOException
	{
		FileChannel input = new FileInputStream(old_name).getChannel();
		FileChannel output = new FileOutputStream(new_name).getChannel();
		ByteBuffer buffik = ByteBuffer.allocate(1024);
		
		int bytes_read = input.read(buffik);
		while(bytes_read != -1)
		{
			buffik.flip();
			while(buffik.hasRemaining())
			{
				output.write(buffik);
			}
			buffik.clear();
			bytes_read = input.read(buffik);
		}

		// Apply changes:
		input.close();
		output.close();
	}

	public static void use_NIO_FC(String old_name, String new_name) throws IOException
	{
		FileChannel input = new FileInputStream(old_name).getChannel();
		FileChannel output = new FileOutputStream(new_name).getChannel();

		input.transferTo(0,input.size(),output);

		// Apply changes:
		input.close();
		output.close();
	}

	public static void use_NIO_MM(String old_name, String new_name) throws IOException
	{
		int length = 0x8FFFFFF;
		MappedByteBuffer input = new RandomAccessFile(old_name,"rw").getChannel()
			.map(FileChannel.MapMode.READ_WRITE,0,length);
		MappedByteBuffer output = new RandomAccessFile(old_name,"rw").getChannel()
			.map(FileChannel.MapMode.READ_WRITE,0,length);

		for(int i = 0; i < length; i++)
			output.put(input.get(i));
	}

	public static void use_NIO_2(String old_name, String new_name) throws IOException
	{
		Path old_p = Paths.get(old_name);
		Path new_p = Paths.get(new_name);
		Files.copy(old_p,new_p,REPLACE_EXISTING);
	}
}
