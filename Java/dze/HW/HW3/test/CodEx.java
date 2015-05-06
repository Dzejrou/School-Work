import java.io.*;
import java.util.Arrays;
import java.util.Stack;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/** DU 3. !!! Nemente jmeno tridy !!!
  * @author Jaroslav Jindrak
  */
public class CodEx
{ // v2.0
	public static boolean debug = false;
	public static void main(String[] args)
	{
		// Debug:
		if(args.length == 1 && args[0].equals("-d"))
			debug = true;

		try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in)))
		{
			Stack<String> stack;
			String line;

			while((line = input.readLine()) != null)
			{
				stack = new Stack<String>();
				//stack.addAll(Arrays.asList(line.split("\\s+")));
				//stack.addAll(Arrays.asList(line.split("(?=[*+-/()])|(?<=[*+-/()])")));

				try
				{
					if(Arrays.asList(line.split("\\s+")).size() > 1 && line.indexOf('e') >= 0)
						line = translate(line);
				}
				catch(DzeException tr_ex)
				{
					throw new IOException(tr_ex.getMessage()); // Simple hack.
				}
				catch(NumberFormatException tr_ex)
				{
					throw new IOException(tr_ex.getMessage());
				}

				if(Arrays.asList(line.split("\\s+")).size() > 1)
				{
					Pattern pattern = Pattern.compile("((\\d*\\.\\d+)|(\\d+)|([\\+\\-\\*/\\(\\)]))");
					Matcher matcher = pattern.matcher(line);
					while(matcher.find())
							stack.push(matcher.group());

					// Paren on start or end of the line would add an empty string.
					Iterator<String> ite = stack.iterator();
					while(ite.hasNext())
					{
						String tstr = ite.next();
						if(tstr.equals("") || tstr.equals(" "))
							ite.remove();
					}
				}
				else
					stack.push(line);

				if(line.trim().length() == 0) // Empty lines.
					continue;
				else if(stack.size() == 1 && stack.peek() != null)
				{ // Single number on line.
					Double temp;
					try
					{
						temp = Double.parseDouble(stack.peek());
						System.out.printf("%.5f", temp);
						System.out.println();
						continue;
					}
					catch(NumberFormatException ex)
					{ // Expression without spaces.
						stack.addAll(Arrays.asList(stack.pop().replaceAll("(?=[*+-/()])|(?<=[*+-/()])", " ").split("\\s+")));

						// Paren on start or end of the line would add an empty string.
						Iterator<String> it = stack.iterator();
						while(it.hasNext())
						{
							if(it.next().equals(""))
								it.remove();
						}

						// Unary -
						Stack<String> temp_stack = new Stack<String>();
						boolean last_was_oper = true; // For - at the start of the line.
						for(int i = 0; i < stack.size(); i++)
						{
							if(stack.elementAt(i).equals("-") && last_was_oper)
							{
								String temp_token = stack.elementAt(i) + stack.elementAt(i+1);
								stack.removeElementAt(i+1);
								temp_stack.push(temp_token);
								last_was_oper = false;
							}
							else if("+-/*".contains(stack.elementAt(i)))
							{
								temp_stack.push(stack.elementAt(i));
								last_was_oper = true;
							}
							else
							{
								temp_stack.push(stack.elementAt(i));
								last_was_oper = false;
							}
						}
						stack = temp_stack;
						
						if(stack.size() > 1) // It's okay now.
						{
							try // proccess() can throw DzeException
							{
								System.out.printf("%.5f", process(stack));
								System.out.println();
								continue;
							}
							catch(DzeException ex_again)
							{
								if(debug)
									System.out.println(ex_again.getMessage());
								else
									System.out.println("ERROR");
							}
						}
						if(debug)
							System.out.println(ex.getMessage());
						else
							System.out.println("ERROR");
					}
				}
				else if(!stack.isEmpty())
				{
					line = null; // Won't need it for now.

					try
					{
						Double result = process(stack);
						System.out.printf("%.5f", result);
						System.out.println();
					}
					catch(DzeException ex)
					{
						if(debug)
							System.out.println(ex.getMessage());
						else
							System.out.println("ERROR");
					}
					catch(NumberFormatException ex)
					{
						if(debug)
							System.out.println(ex.getMessage());
						else
							System.out.println("ERROR");
					}
					catch(ArithmeticException ex)
					{
						if(debug)
							System.out.println(ex.getMessage());
						else
							System.out.println("ERROR");
					}
					catch(EmptyStackException ex)
					{
						if(debug)
							System.out.println(ex.getMessage());
						else
							System.out.println("ERROR");
					}
				}
			}
		}
		catch(IOException ex)
		{
			if(debug)
				System.out.println(ex.getMessage());
			else
				System.out.println("ERROR");
		}
	}

	public static String translate(String line) throws DzeException
	{
		StringBuilder temp = new StringBuilder(line);
		while(temp.indexOf("e") > 0)
		{
			int index = temp.indexOf("e");

			if(index - 1 < 0 || index + 1 >= temp.length())
				throw new DzeException("[fnum e snum] with only one or none nums.");

			int i = index - 1;
			int start, end;
			StringBuilder fnum = new StringBuilder("");
			StringBuilder snum = new StringBuilder("");

			// Go back.
			while(true)
			{
				if(i < 0)
				{
					start = i + 1;
					break;
				}

				if("+-*/()".indexOf(temp.charAt(i)) < 0)
				{
					fnum.append(temp.charAt(i));
					i--;
				}
				else
				{
					start = i + 1; // Where the number starts.
					break;
				}
			}

			i = index + 1;
			// Go forward.
			while(true)
			{
				if(i >= temp.length())
				{
					end = i - 1;
					break;
				}

				if("+*/()".indexOf(temp.charAt(i)) < 0)
				{
					snum.append(temp.charAt(i));
					i++;
				}
				else if(temp.charAt(i) == '-' && temp.charAt(i) == 'e') // Unary - is allowed behind e.
				{
					snum.append(temp.charAt(i));
					i++;
				}
				else
				{
					end = i - 1;
					break;
				}
			}

			// Replace it with the number.
			System.out.print("REPLACING: " + fnum + "e" + snum + " with "
					+ Double.parseDouble(new String(fnum + "e" + snum)) + " ");
			System.out.printf("%.5f",Double.parseDouble(fnum + "e" + snum));
			System.out.println();
			temp.replace(start,end,"" + Double.parseDouble(new String(fnum + "e" + snum)));
		}
		String result = new String(temp);
		return result;
	}

	public static Double process(Stack<String> stack) throws DzeException
	{
		String operators = "*/+-";
		Stack<Double> nums = new Stack<Double>();
		Stack<Character> opers = new Stack<Character>();

		// Sorting to nums and opers:
		while(!stack.empty())
		{
			if(stack.peek().equals( ")"))
			{
				Stack<String> temp = new Stack<String>();
				stack.pop(); // Get rid of the paren.

				if(stack.peek().equals("(")) // Empty:(
					throw new DzeException("Empty parenthesies.");

				int paren_count = 1;
				// If the stack gets empty before "(", EmptyStackException is rightfully thrown.
				while(true) // Will be terminated from the inside.
				{
					if(stack.peek().equals(")"))
						paren_count++;
					else if(stack.peek().equals("(") && paren_count == 1)
						break;
					else if(stack.peek().equals("("))
						paren_count--;
					temp.push(stack.pop());
				}

				stack.pop(); // Get rid of the paren.

				//Reverse the temp stack:
				Stack<String> reversed = new Stack<String>();
				while(!temp.empty())
					reversed.push(temp.pop());

				nums.push(process(reversed)); // Recursively calculate the content of the parens.
			}
			else if(is_num(stack.peek()))
			{
				try
				{
					nums.push(Double.parseDouble(stack.pop()));
				}
				catch(NumberFormatException ex)
				{ // Rethrow it.
					throw ex;
				}
			
			}
			else if(operators.contains(stack.peek()))
				opers.push(stack.pop().charAt(0));
			else
				throw new DzeException("Invalid token: " + stack.pop());
		}

		if(nums.size() == 1)
			return nums.pop();
		else if(nums.empty() || opers.empty()) // Self explanatory.
			throw new DzeException("Empty stack\nNums: " + nums.toString() + "\n" + "Opers: " + opers.toString());

		// Calculation:
		while(opers.size() >= 2)
		{
			Character foper = opers.pop();
			Character soper = opers.pop();

			if(prec(foper) <= prec(soper))
			{
				Double fnum = nums.pop();
				Double snum = nums.pop();
				nums.push(calculate(fnum,snum,foper));
				opers.push(soper);
			}
			else
			{
				if(nums.size() < 2)
					throw new DzeException("Not enought operands, nums: " + nums.toString());
				Double fnum = nums.pop();
				Double snum = nums.pop();
				Double tnum = nums.pop();

				nums.push(calculate(snum,tnum,soper)); // Calculate the expression.
				nums.push(fnum); // Return unused operand.
				opers.push(foper); // Return unused operator.
			}
		}

		// Now opers.size() is 1, nums should be two.
		if(nums.size() != 2)
			throw new DzeException("Not enough operands for the last expression, nums: " + nums.toString());

		Double fnum = nums.pop();
		Double snum = nums.pop();
		Character oper = opers.pop();
		// Size of nums and opers is now 0.
		return calculate(fnum,snum,oper);

	}

	public static Double calculate(Double fnum, Double snum, Character oper) throws DzeException
	{
		switch(oper)
		{
			case '+':
				return fnum + snum;
			case '-':
				return fnum - snum;
			case '*':
				return fnum * snum;
			case '/':
				return fnum / snum;
			default: // Shouldn't happen.
				throw new DzeException("Invalid operator in calculate(): " + oper);
		}
	}

	public static boolean is_num(String expr)
	{ // Checks if the string is a number.
		try
		{
			Double temp = Double.parseDouble(expr);
		}
		catch(NumberFormatException ex)
		{
			return false;
		}
		return true;
	}

	public static int prec(Character oper)
	{ // Get operator priority.
		switch(oper)
		{
			case '*':
			case '/':
				return 1;
			case '+':
			case '-':
				return 2;
			default:
				return Integer.MIN_VALUE; // Shouldn't happen.
		}
	}

	static class DzeException extends Exception
	{ // Gotta make my own exception instead of using the arithmetic one.
		DzeException(String msg)
		{
			super(msg);
		}
	}
}
