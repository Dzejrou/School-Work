import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.math.*;

/** DU 3. !!! Nemente jmeno tridy !!!
  * @author Jaroslav Jindrak
  */
public class CodEx
{ // v2.0
	public static boolean debug = false;
	public static Map<String,BigDecimal> variables = new HashMap<String,BigDecimal>();
	public static Map<String,Function> functions = new HashMap<String,Function>();
	public static int precision = 20;

	public static void main(String[] args)
	{
		// Debug:
		if(args.length == 1 && args[0].equals("-d"))
			debug = true;

		try(BufferedReader input = new BufferedReader(new InputStreamReader(System.in)))
		{
			assign("last = 0"); // Initial value for previous expression.

			Stack<String> stack;
			String line;
			BigDecimal result;

			while((line = input.readLine()) != null)
			{
				debug("");
				debug("PROCESSING: " + line);

				if(line.trim().length() == 0) // Empty line -.-
					continue;

				try
				{
					try
					{
						if(line.contains("e"))
							line = translate_scientific(line);
					}
					catch(NumberFormatException exxx)
					{
						// Do nothing, its thrown because that e is a part of a variable name / function name.
					}

					if(line.contains("precision"))
					{
						set_precision(line);
						continue;
					}
					else if(line.contains("="))
					{
						assign(line,true);
						continue;
					}
					else if(line.contains("DEF"))
					{
						define(line);
						assign("last = 0");
						continue;
					}
					else
						stack = parse(line);

					stack = translate(stack);

					result = process(stack);
					assign("last = " + result.stripTrailingZeros().toPlainString()); // Save it!
					System.out.println(result.stripTrailingZeros().toPlainString());
				}
				catch(DzeException | NumberFormatException | EmptyStackException ex)
				{
					debug(ex.getMessage());
					System.out.println("ERROR");

					try
					{
						assign("last = " + new BigDecimal(0).setScale(precision,RoundingMode.HALF_UP).toPlainString());
					}
					catch(DzeException exx)
					{
						// Shouldn't happen.
						debug("Huh? " + exx.getMessage());
					}
				}
			}
		}
		catch(IOException | DzeException | EmptyStackException ex)
		{
			debug(ex.getMessage());
			System.out.println("ERROR");

			try
			{
				assign("last = 0");
			}
			catch(DzeException exx)
			{
				// Shouldn't happend.
			}
		}
	}

	public static void set_precision(String line)
	{
		String[] tokens = line.split("\\s+");
		try
		{
			precision = Integer.parseInt(tokens[1]);
		}
		catch(NumberFormatException ex)
		{
			debug("Invalid precision setting: " + ex.getMessage());
			System.out.println("ERROR");
		}
	}

	public static void define(String line) throws DzeException
	{
		line = line.replace("DEF","");
		StringBuilder name = new StringBuilder("");
		StringBuilder definition = new StringBuilder("");
		List<String> params = new ArrayList<String>();
		int i = 0;
		char c; // To save some processing time.

		while(i < line.length() && (c = line.charAt(i)) == ' ') // Proceed to the name.
			i++;
		
		while(i < line.length() && (c = line.charAt(i)) != '(')
		{
			if(c == ' ') // Possible problem with functions with a space in name - gotta print ERROR.
				throw new DzeException("Space in a function name: " + line);
			else
				name.append(c);
			i++;
		}

		if(name.length() == 0)
			throw new DzeException("A lambda, nice:)" + line);

		debug("DEFINE: " + name.toString());

		if(!is_word(name.toString()))
			throw new DzeException("Function name contains non-letter characters: " + name);

		// Check the variables if the name isn't taken.
		if(variables.containsKey(name.toString())) // TODO: What about redefining functions? Is it allowed?
			throw new DzeException("Trying to define function with a wrong name: " + line);

		i++; // Skip the paren.
		while(i < line.length() && (c = line.charAt(i)) != ')')
		{
			if(Character.isLetter(c))
				params.add(Character.toString(c));
			else if(c == ' ')
			{
				i++;
				continue;
			}
			else
				throw new DzeException("Invalid parameters for function definition: " + c);

			i++;
			while(i < line.length() && line.charAt(i) == ' ') i++;
			if(i < line.length() && line.charAt(i) == ',')
				i++; // Skip the ','.
		}

		for(String s : params)
			if(s.length() > 1)
				throw new DzeException("Function argument longer than one character: " + params.toString());
		if(i == line.length() || (i < line.length() && line.charAt(i) != ')'))
			throw new DzeException("Unclosed parameter list for a function.");
		i++;

		for(;i < line.length(); i++)
		{
			if((c = line.charAt(i)) != ' ')
				definition.append(c);
		}

		// Usage of normal variables is forbidden!
		Stack<String> temp_stack = parse(definition.toString());
		for(String s : temp_stack)
		{
			if((functions.containsKey(s) || variables.containsKey(s)) && !params.contains(s))
				throw new DzeException("Using outside variable or function within the function: " + line);

			if(is_word(s) && !params.contains(s))
				throw new DzeException("Using undeclared variable/function within the function: " + line);
		}

		functions.put(name.toString(), new Function(params,definition.toString()));

		debug("FUNCTIONS: " + functions.toString());
	}

	public static Stack<String> translate_function(String name, List<String> params) throws DzeException
	{
		// Check the parameters:
		for(String p : params)
		{
			if(p.contains("+") || p.contains("-") || p.contains("*") || p.contains("/"))
				throw new DzeException("Parameter to a function is an expression: " + p);
			if(functions.containsKey(p))
				throw new DzeException("Parameter to a function is a function: " + p);
		}

		Stack<String> stack = new Stack<String>();
		List<String> par = functions.get(name).par;
		String def = functions.get(name).def;

		if(params.size() != par.size())
			throw new DzeException("Invalid number of parameters for: " + name);

		for(int i = 0; i < par.size(); i++)
			def = def.replace(par.get(i),params.get(i)); // Replace variables with their numbers.
		
		return parse(def);
	}
	
	public static Stack<String> translate(Stack<String> stack) throws DzeException
	{
		String temp;
		for(int i = 0; i < stack.size(); i++)
		{
			temp = stack.elementAt(i);
			String temp2 = new String("");
			if(temp.charAt(0) == '-')
				temp2 = temp.substring(1);

			if(variables.containsKey(temp))
				stack.setElementAt(variables.get(temp).stripTrailingZeros().toPlainString(),i);
			else if(variables.containsKey(temp2)) // Negative version of the variable.
				stack.setElementAt("-" + variables.get(temp2).stripTrailingZeros().toPlainString(),i);
			//else if(!is_num(temp) && (temp.length() == 1 && "*+-/".indexOf(temp) < 0))
			else if(is_word(temp))
			{
				assign(temp + " = 0");
				stack.setElementAt(new BigDecimal("0").stripTrailingZeros().toPlainString(),i);
			}
		}
		return stack;
	}

	public static boolean is_word(String s)
	{
		for(int i = 0; i < s.length(); i++)
			if(!Character.isLetter(s.charAt(i)))
				return false;
		return true;
	}

	public static Stack<String> parse(String line) throws DzeException
	{
		//Stack<String> stack = new Stack<String>();
		//Pattern pattern = Pattern.compile("((\\d*\\.\\d+)|(\\d+)|([\\+\\-\\*/\\(\\)]))");
		//Matcher matcher = pattern.matcher(line);
		//while(matcher.find())
		//		stack.push(matcher.group());

		Stack<String> stack = new Stack<String>();
		StringBuilder temp = new StringBuilder("");
		int i = 0;
		while(i < line.length())
		{
			char c = line.charAt(i);

			if(c == '(' && is_word(temp.toString()) && !functions.containsKey(temp.toString()))
				throw new DzeException("Undeclared function used: " + line);
			if(c == '(' && functions.containsKey(temp.toString()))
			{
				ArrayList<String> params = new ArrayList<String>();
				StringBuilder temp2 = new StringBuilder("");

				if(i == line.length() - 1)
					throw new DzeException("Line ending with function name without params.");
				i++; // Skip the paren.

				while((c = line.charAt(i)) != ')' && i < line.length())
				{
					if(c == ',')
					{
						params.add(temp2.toString());
						temp2 = new StringBuilder("");
					}
					else if(c != ' ')
						temp2.append(Character.toString(c));
					i++;
				}
				i++; // Skip the paren.
				if(temp2.length() > 0)
					params.add(temp2.toString()); // Flush the rest.

				Stack<String> to_be_translated = new Stack<String>(); // Name is ugly on purpose!
				for(String s : params)
					to_be_translated.push(s);
				to_be_translated = translate(to_be_translated);
				params.clear();
				for(String s : to_be_translated)
					params.add(s);

				stack.push(process(translate_function(temp.toString(),params)).toString());

			}
			else if(Character.isDigit(c) || Character.isLetter(c) || c == '.')
			{
				temp.append(Character.toString(c));
			}
			else if(c == '-' && (i == 0 || (stack.size() > 0 && is_oper(stack.peek().charAt(0)))))
			{
				// Append and continue, this is a negative number.
				temp.append(Character.toString(c));
			}
			else if(is_oper(c) || c == '(' || c == ')')
			{
				if(temp.length() > 0 && !temp.equals(" "))
					stack.push(temp.toString());
				stack.push(Character.toString(c));
				temp = new StringBuilder("");
			}
			else if(Character.isWhitespace(c) && temp.length() != 0 && !temp.equals("") && temp.toString().compareTo("-") != 0)
			{
				stack.push(temp.toString());
				temp = new StringBuilder("");
			}

			if(i == line.length() - 1 && !temp.equals(""))
				stack.push(temp.toString());
			i++;
		}

		/* Obsolete.
		stack.addAll(Arrays.asList(line.split("\\s+")));
		clear(stack);
		*/

		return stack;
	}

	public static void assign(String line, boolean... print) throws DzeException
	{
		String[] temp = line.split("=");
		temp[0] = temp[0].replaceAll(" ",""); // Get rid of spaces around the variable name.
		temp[0] = temp[0].replaceAll("\\s+",""); // Just to be sure.

		debug("ASSIGN: " + temp[0] + " = " + temp[1]);

		temp[1] = temp[1].replace(" ","");
		// Take care of scientific notation.
		temp[1] = translate_scientific(temp[1]);

		if(!is_word(temp[0])) // Assignment used in an expression or name contains numbers.
			throw new DzeException("Invalid variable name: " + temp[0]);

		Stack<String> stack = parse(temp[1]); // Parse the expression.
		stack = translate(stack); 
		BigDecimal result = process(stack);

		// Check the functions if the name isn't already used.
		if(functions.containsKey(temp[0])) // Last is there form the start so a function will not be named last.
			throw new DzeException("Trying to assing to a variable whose name is used as a function: " + line);

		if(!variables.containsKey(temp[0]))
			variables.put(temp[0],result); // Save the variable.
		else
			variables.replace(temp[0],result);

		if(!temp[0].equals("last")) // Print it.
			variables.replace("last",result);

		if(print.length > 0 && print[0])
		{
			System.out.printf("%.5f",result);
			System.out.println();
		}
	}

	public static Stack<String> clear(Stack<String> target)
	{
		Stack<String> stack = target;
		Iterator<String> it = stack.iterator();
		while(it.hasNext())
		{
			if(it.next().equals(""))
				it.remove();
		}
		return stack;
	}

	public static String translate_scientific(String line) throws DzeException
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

			// In case there are space:
			fnum = trim(fnum);
			snum = trim(snum);
			debug("REPLACE: " + fnum + "e" + snum + " = " + new BigDecimal(fnum + "e" + snum).stripTrailingZeros().toPlainString());

			// Replace it with the number.
			temp.replace(start,end + 1,new BigDecimal(new String(fnum + "e" + snum)).stripTrailingZeros().toPlainString());
		}
		String result = new String(temp);
		return result;
	}

	public static BigDecimal process(Stack<String> stack) throws DzeException
	{
		debug("PROCESS: " + stack.toString());

		String operators = new String("*/+-");
		Stack<BigDecimal> nums = new Stack<BigDecimal>();
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
					nums.push(new BigDecimal(stack.pop()).setScale(precision,RoundingMode.HALF_UP));
				}
				catch(NumberFormatException ex)
				{ // Rethrow it.
					throw new DzeException(ex.getMessage());
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
				BigDecimal fnum = nums.pop();
				BigDecimal snum = nums.pop();
				nums.push(calculate(fnum,snum,foper));
				opers.push(soper);
			}
			else
			{
				if(nums.size() < 2)
					throw new DzeException("Not enought operands, nums: " + nums.toString());
				BigDecimal fnum = nums.pop();
				BigDecimal snum = nums.pop();
				BigDecimal tnum = nums.pop();

				nums.push(calculate(snum,tnum,soper)); // Calculate the expression.
				nums.push(fnum); // Return unused operand.
				opers.push(foper); // Return unused operator.
			}
		}

		// Now opers.size() is 1, nums should be two.
		if(nums.size() != 2)
			throw new DzeException("Not enough operands for the last expression, nums: " + nums.toString());

		BigDecimal fnum = nums.pop();
		BigDecimal snum = nums.pop();
		Character oper = opers.pop();
		// Size of nums and opers is now 0.
		return calculate(fnum,snum,oper);

	}

	public static BigDecimal calculate(BigDecimal fnum, BigDecimal snum, Character oper) throws DzeException
	{
		switch(oper)
		{
			case '+':
				return fnum.add(snum);
			case '-':
				return fnum.subtract(snum);
			case '*':
				return fnum.multiply(snum);
			case '/':
				return fnum.divide(snum,precision,RoundingMode.HALF_UP);
			default: // Shouldn't happen.
				throw new DzeException("Invalid operator in calculate(): " + oper);
		}
	}

	public static boolean is_num(String expr)
	{ // Checks if the string is a number.
		try
		{ // TODO: Redo this with bigdecimal?
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

	static class Function // Name is the key of the map this will be stored in, no need to have it twice.
	{
		public List<String> par;
		public String def;

		public Function(List<String> p, String d)
		{
			par = p;
			def = d;
		}

		public String toString() // For debug purposes.
		{
			return new String(par.toString() + " " + def);
		}
	}

	public static void debug(String msg)
	{
		if(debug)
			System.out.println(msg);
	}

	public static boolean is_oper(char c)
	{
		return c == '+' || c == '-' || c == '*' || c == '/';
	}

	public static StringBuilder trim(StringBuilder s)
	{ // No trim() method for stringbuilder:(
		int j = 0;
		for(int i = 0; i < s.length(); i++)
		{
			if(!Character.isWhitespace(s.charAt(i)))
			{
				s.setCharAt(j++, s.charAt(i));
			}
		}
		s.delete(j, s.length());
		return s;
	}
}
