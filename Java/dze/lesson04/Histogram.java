package dze.lesson04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Histogram
{

	public static void main(String[] args)
	{
		/* Counters: */
		int cChar = 0;
		int cLine = 0;
		int cWhite = 0;
		int cWord = 0;

		try
		{
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		int c = 0;
		char temp;
			while((c = input.read()) != -1)
			{
				temp = (char)c;
				if(Character.isLetterOrDigit(temp))
					cChar++;
				else if(Character.isWhitespace(temp))
				{
					cWhite++;
					if(temp == '\n')
					{
						cWord++;
						cLine++;
					}
					else if (temp == ' ')
						cWord++;
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("IO Except.");
		}
		System.out.println("Char count: " + cChar);
		System.out.println("Word count: " + cWord);
		System.out.println("Line count: " + cLine);
		System.out.println("Whitespace count: " + cWhite);
		System.out.println("Note: Multiple space = multiple words...");
	}
}
