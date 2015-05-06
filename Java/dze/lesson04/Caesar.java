package dze.lesson04;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Caesar
{
	public static void main(String[] args)
	{
		if(args.length != 2)
		{
			System.out.println("Wrong amount of parameters, aborting.");
			System.exit(0);
		}

		int n = Integer.parseInt(args[0]);
		String file = args[1];

		List<StringBuilder> lines = new ArrayList<StringBuilder>();
		StringBuilder temp = new StringBuilder();
		
		FileReader input;

		try
		{
			input = new FileReader(file);
			int c;
			int temp2;
			while((c = input.read()) != -1)
			{
				temp2 = c + n;
				if(Character.isLetter((char)c))
					temp.append((char)temp2);
				else if(((char)c) == ' ')
					temp.append((char)c);
				else if(((char)c) == '\n')
				{
					//temp.append('\n');  --  obsolete!
					lines.add(new StringBuilder(temp));
					temp = new StringBuilder();
				}
			}

			input.close();
		}
		catch(IOException e1)
		{
			System.out.println("IO Error.");
			System.out.println(e1.getMessage());
		}

		for(StringBuilder line : lines)
			System.out.println(line);
	}
}
