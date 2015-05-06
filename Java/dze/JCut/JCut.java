import java.util.*;
import java.io.*;

public class JCut
{
	private static String line;
	private static List<String> cols;
	private static boolean delimited;
	private static boolean complement;
	private static char delimiter = '\t';
	private static boolean reverse = false;
	private static String col_nums_str;
	private static List<Integer> col_nums;
	private static int total_cols = 0;

	public static void main(String[] args)
	{
		if(args.length < 2)
		{
			System.out.println("Wrong amount of parameters given, aborting.");
			System.exit(1);
		}
		else if(args.length >= 2)
		{
			complement = false; // -c will turn this on.
			int i = 0;
			for(; i < args.length; i += 2)
			{
				switch(args[i])
				{
					case "-d": // args[i+1] should be of length 1 (and should exist...)!
						delimited = true;
						delimiter = args[i+1].charAt(0); // Should exist.
						break;
					case "-f":
						delimited = true;
						col_nums_str = args[i+1]; // Possible null if this has to print all of the columns?
						break;
					case "-b":
						delimited = false;
						col_nums_str = args[i+1];
						break;
					case "-c":
						delimited = false;
						complement = true;
						col_nums_str = args[i+1];
						break;
					default:
						System.out.println("Invalid parameter: " + args[i]);
						System.exit(1);
				}
			}
		}

		try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in)))
		{
			while((line = input.readLine()) != null)
			{
				if(delimited)
				{
					cols = new ArrayList<String>();
					String[] tmp_cols = line.split((Character.toString(delimiter)));
					for(int k = 0; k < tmp_cols.length; k++)
						cols.add(tmp_cols[k]);
					total_cols = cols.size();
					col_nums = get_col_nums(total_cols,col_nums_str);
					System.out.println(process(cols,col_nums,Character.toString(delimiter)));
				}
				else 
				{
					cols = new ArrayList<String>();
					for(int j = 0; j < line.length(); j++)
					{
						cols.add(Character.toString(line.charAt(j))); // Add all chars.
					}
					total_cols = cols.size();
					col_nums = get_col_nums(total_cols,col_nums_str);
					if(complement)
						System.out.println(process(cols,col_nums,"",true,true));
					else
						System.out.println(process(cols,col_nums,"",true,false));
				}
			}
		
		}
		catch(IOException ex)
		{
			System.out.println(ex.getMessage());
			System.exit(1);
		}
	}

	private static StringBuilder process(List<String> cols, List<Integer> col_nums, String delim, Boolean... b_c)
	{
		StringBuilder tmp = new StringBuilder("");
		String curr_str;

		if(b_c.length == 0)
			for(int i = 0; i < col_nums.size(); i++)
			{
				if(col_nums.get(i) >= cols.size())
					continue; // Out of bounds of columns.
				if(i >= col_nums.size())
					continue;
				curr_str = cols.get(col_nums.get(i) - 1);
				if(curr_str.equals("")) // Empty column.
					tmp.append(delim);
				else
					tmp.append(curr_str);
				tmp.append(delim);
			}
		else // It will be 0 or 2, if this would be a real-life program I'd have to check it there isn't just one bool.
		{
			// Also, if I add the b boolean, it will be true, so I will assume it here.
			if(!b_c[1])
			{
				for(int i = 0; i < col_nums.size(); i++)
				{
					if(col_nums.get(i) >= cols.size()) // Outta dem boundz!
						continue;
					curr_str = cols.get(col_nums.get(i));
					if(!curr_str.equals(""))
						tmp.append(curr_str); // No delims in -b or -c.
				}
			}
			else
			{
				for(int i = 0; i < cols.size(); i++)
				{
					if(!col_nums.contains(new Integer(i)))
						curr_str = cols.get(new Integer(i));
					else
						continue;
					tmp.append(curr_str);
				}
			}
		}
		return tmp;
	}

	private static List<Integer> get_col_nums(int total_cols, String col_nums_str)
	{
		List<Integer> tmp = new ArrayList<Integer>();
		String[] reqs = col_nums_str.split(",");
		String curr_req;
		for(int i = 0; i < reqs.length; i++)
		{
			curr_req = reqs[i]; // For simplicity.
			if(curr_req.contains("-"))
			{
				String left = curr_req.split("-")[0];
				String right;
				if(curr_req.split("-").length == 2)
				{
					right = curr_req.split("-")[1];
				}
				else
					right = "";
				Integer lb = new Integer(0); // Shut up javac!
				Integer ub = new Integer(0);

				if(left.equals(""))
					lb = 1; // TODO: Do i start from 1?
				else
				{
					try
					{
						lb = Integer.parseInt(left);
					}
					catch(NumberFormatException ex)
					{
						System.out.println("Error converting str to int: " + curr_req);
						System.exit(1);
					}
				}

				if(right.equals(""))
					ub = total_cols;
				else
				{
					try
					{
						ub = Integer.parseInt(right);
					}
					catch(NumberFormatException ex)
					{
						System.out.println("Error converting str to int: " + curr_req);
						System.exit(1);
					}
				}

				if(lb < ub)
				{
					for(Integer j = lb; j <= ub; j++)
					{
						tmp.add(new Integer(j)); // Store a copy!!!
					}
				}
				else
				{
					for(Integer j = ub; j >= lb; j--)
					{
						tmp.add(new Integer(j));
					}
				}
			}
			else // Single number.
			{
				Integer num = new Integer(0);
				try
				{
					num = Integer.parseInt(curr_req);
				}
				catch(NumberFormatException ex)
				{
					System.out.println("Error converting str to int: " + curr_req);
					System.exit(1); // For debugging purposes, wouldn't be in the final program.
				}

				tmp.add(new Integer(num));
			}
		}
		return tmp;
	}
}
