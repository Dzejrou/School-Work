//package dze.lesson08;

import java.io.*;
import java.util.*;

public class Todo
{
	//private static String dir = new String("/home/dzejrou/dze/lesson08/"); 
	private static String fname = new String("todo");
	private static String tname = new String("temp_file");
	public static void main(String[] args)
	{

		if(args.length == 3 && args[0].equals("-a"))
		{
			try
			{
				int priority = Integer.parseInt(args[1]);
				String msg = args[2];
				write(priority,msg);
			}
			catch(NumberFormatException ex)
			{
				System.out.println("Wrong parameter format, exiting.");
				System.exit(1);
			}
		}
		else if(args.length == 1)
		{
			switch(args[0])
			{
				case "-l":
				case "-r":
					print(args[0].equals("-l"));
					break;
				case "-d":
					interactive();
					break;
				default:
					System.out.println("Wrong parameter, exiting.");
					System.exit(1);
			}
		}
	}

	public static void write(int priority, String msg)
	{
		String priority_str = Integer.toString(priority); // No ex-handling needed, it is an int.
		String message = priority_str + " " + msg + "\n";
		BufferedWriter writer;
		try
		{
			writer = new BufferedWriter(new FileWriter(fname,true)); // true for appending.
			writer.append(message);
			writer.close();
		}
		catch(IOException ex)
		{
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}

	public static void print(boolean top_to_bot)
	{ // Sort the file.
		final int mod;
		if(top_to_bot)
			mod  = -1;
		else
			mod = 1;

		try(BufferedReader reader = new BufferedReader(new FileReader(fname)))
		{
			List<String> contents = new ArrayList<String>();
			String line;
			while((line = reader.readLine()) != null) // Print the sorted file.
				contents.add(line);

			Collections.sort(contents, new Comparator<String>() {
				@Override
				public int compare(String first, String second)
				{
					return new Character(first.charAt(0)).compareTo(
						new Character(second.charAt(0))) * mod;
				}
			});

			for(String s : contents)
				System.out.println(s);
		}
		catch(IOException ex)
		{
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}

	public static void interactive()
	{
		try(BufferedReader reader = new BufferedReader(new FileReader(fname)))
		{
			List<String> contents = new ArrayList<String>();
			String line;
			while((line = reader.readLine()) != null) // Print the sorted file.
				contents.add(line);

			while(true)
			{
				int i = 1;
				System.out.println("Contents of the todo file:");
				for(String s : contents)
				{
					System.out.println(i + ": " + s);
					i++;
				}
				System.out.println("[num = delete line with that index][0 = save and quit]");
				
				Scanner input = new Scanner(System.in);
				int option = input.nextInt();
				if(option == 0)
				{
					BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
					for(String s : contents)
					{
						writer.write(s);
						writer.newLine();
					}
					writer.close();
					System.exit(0);
				}
				else if((option - 1) < contents.size())
					contents.remove(option-1);
			}
		}
		catch(IOException ex)
		{
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}
}
