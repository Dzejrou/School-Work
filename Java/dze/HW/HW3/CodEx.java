import java.io.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;

/** DU 3. !!! Nemente jmeno tridy !!!
  * @author Jaroslav Jindrak
  */
public class CodEx
{
	public static void main(String[] argv)
	{
		try
		{
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			Deque<String> queue;
			String line;
			while((line = input.readLine()) != null)
			{
				if(line.trim().length() != 0) // Non-empty lines only!
				{
					queue = new ArrayDeque<String>(Arrays.asList(line.split("\\s+"))); // Split by w/s.
					if(queue.size() == 1 && queue.peek() != null)
					{ // Single number on line.
						System.out.printf("%.5f", Double.parseDouble(queue.poll()));
						System.out.println();
						continue;
					}
					else if (!queue.isEmpty())
					{
						line = null; // Won't need it for nao.

						try
						{
							Double result = process(queue);
							System.out.printf("%.5f", result);
							System.out.println();
						}
						catch(ArithmeticException ex)
						{
							System.out.println("ERROR");
						}
						catch(NumberFormatException ex)
						{
							System.out.println("ERROR");
						}
						catch(NullPointerException ex)
						{
							System.out.println("ERROR");
						}
					}
				}
			}
		}
		catch(IOException ex)
		{
			System.out.println("ERROR");
		}
		catch(ArithmeticException ex)
		{
			System.out.println("ERROR");
		}
		catch(NumberFormatException ex)
		{
			System.out.println("ERROR");
		}
	}

	public static Double process(Deque<String> queue) throws ArithmeticException
	{
		System.out.println("Processing: " + queue.toString());
		String operators = "+-*/";
		Deque<Double> nums = new ArrayDeque<Double>(); // Stack.
		Deque<Character> opers = new ArrayDeque<Character>(); // Stack.

		while(!queue.isEmpty())
		{
			if(queue.peek().equals("(")) // Inline parenthesis calculation.
			{
				queue.poll(); // Get rid of it.

				if(queue.isEmpty()) // "(" was last on the line.
					throw new ArithmeticException();

				if(queue.peek().equals(")")) // Empty parens.
					throw new ArithmeticException();

				Deque<String> temp_que = new ArrayDeque<String>();
				int paren_pairs = 1; // Initial pair.
				boolean temp = true;

				while(!queue.isEmpty() && temp)
				{
					if(queue.peek().equals(")") && (paren_pairs == 1))
						temp = false; // Not adding the last paren!
					else if(queue.peek().equals(")"))
					{
						temp_que.add(queue.poll());
						paren_pairs--;
					}
					else if(queue.peek().equals("(")) // Nested parens!
					{
						temp_que.add(queue.poll());
						paren_pairs++;
					}
					else
						temp_que.add(queue.poll());
				}

				if(temp_que.isEmpty())
					throw new ArithmeticException(); // Unclosed parenthesies.
				else
					nums.push(process(temp_que)); // Let's do it recursive!

				queue.poll(); // Thanks to the conditional I know, that there is a ")".
			}
			else if(is_num(queue.peek()))
				nums.push(Double.parseDouble(queue.poll()));
			else if(operators.contains(queue.peek())) // Operator.
				opers.push(new Character(queue.poll().charAt(0)));
			else
				throw new ArithmeticException(); // Huh? Don't want that in me queues!
		}

		if(opers.isEmpty() || nums.isEmpty())
			throw new ArithmeticException();

		while(opers.size() >= 2)
		{
			Character foper = opers.pop();
			Character soper = opers.pop();

			System.out.println("Opers: " + foper + " " + soper);
			if(prec(foper) > prec(soper))
			{
				opers.push(foper);
				opers.push(soper);
			}
			else
			{
				System.out.println("Nums: " + nums.toString());
				if(nums.size() < 2)
					throw new ArithmeticException(); // Not enough operands.
				Double fnum = nums.pop();
				Double snum = nums.pop();
				System.out.println("After pop: " + fnum + " " + snum);
				nums.push(calculate(fnum,snum,soper));
				opers.push(foper); // Return unused operator.
			}
		}

		if(nums.size() != 2 || opers.size() != 1) // One operator remaining, but it needs 2 operands!
			throw new ArithmeticException();
		else
		{
			Double fnum = nums.pop();
			Double snum = nums.pop();
			Character oper = opers.pop();
			return calculate(fnum,snum,oper);
		}
	}

	public static int prec(Character oper)
	{
		switch(oper)
		{
			case '+':
			case '-':
				return 1;
			case '*':
			case '/':
				return 2;
			default:
				return Integer.MIN_VALUE; // Won't happen... hopenully.
		}
	}

	public static boolean is_num(String expr)
	{
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

	public static Double calculate(Double fnum, Double snum, Character oper)
	{
		System.out.println("Calculating: " + fnum + oper + snum);
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
				throw new ArithmeticException();
		}
	}

	/*
	public static Double calculate(Deque<String> queue)
	{
		Double result = new Double(0);
		Double first = new Double(0);
		Double second = new Double(0);
		Deque<Object> without_brackets = new ArrayDeque<Object>();
		String oper = new String("");
		while(!queue.isEmpty() && queue.peek() != null) // Isn't it the same?
		{
			String temp_str = queue.poll();
			if(temp_str != null && temp_str.equals("(")) // == doesn't work?
			{
				Deque<String> temp_que = new ArrayDeque<String>();
				while (queue.peek() != null && !queue.peek().equals(")"))
					temp_que.add(queue.poll()); // Add contents of the brackets.
				if(queue.peek() != null && queue.peek().equals(")"))
					queue.poll(); // Get rid of it.
				else // Unclosed parenthesies.
					return null;
				Double temp0 = calculate(temp_que);
				if(temp0 == null)
					return null;
				without_brackets.add(temp0); // Calculate contents of the queue.
			}
			else // temp_str isn't null
				without_brackets.add(temp_str); // No brackets, move along:)
		}

		while(!without_brackets.isEmpty())
		{
			try
			{
				if(without_brackets.peek() instanceof String && without_brackets.peek() != null)
					first = Double.parseDouble((String)without_brackets.poll());
				else if (without_brackets.peek() != null)
					first = (Double)without_brackets.poll();

				if(without_brackets.peek() != null)
					oper = (String)without_brackets.poll();

				if(without_brackets.peek() instanceof String && without_brackets.peek() != null)
					second = Double.parseDouble((String)without_brackets.poll());
				else if(!without_brackets.isEmpty() && without_brackets.peek() != null)
					second = (Double)without_brackets.poll();

				switch(oper)
				{
					case "*":
						result += first * second;
						break;
					case "/":
						result += first / second;
						break;
					case "+":
						result += first + second;
						break;
					case "-":
						result += first - second;
						break;
					default:
						return null; // Wrong input.
				}
			}
			catch(NumberFormatException ex)
			{ // Wrong input.
				return null;
			}
		}
		return result;
	}
	*/
}

