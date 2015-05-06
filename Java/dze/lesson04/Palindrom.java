package dze.lesson04;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Palindrom
{
	public static void main(String[] args)
	{
		StringBuilder word = new StringBuilder("");
		if(args.length != 0)
		{
			word.append(args[0]);
			for(int i = 1; i < args.length; i++)
			{
				word.append(" ");
				word.append(args[i]);
			}
		}

		List<Character> chars = new ArrayList<Character>();

		for(int i = 0; i < word.length(); i++)
		{
			if(Character.isLetter((Character)word.charAt(i)))
				chars.add((Character)word.charAt(i));
		}

		boolean mismatch = false;
		for(int i = 0; i < chars.size(); i++)
		{
			if(chars.get(i) != chars.get(chars.size()-1-i))
			{
				mismatch = true;
				break;
			}
		}

		if(mismatch)
			System.out.println("NE");
		else
			System.out.println("ANO");
	}
}
