/* Pouzijte implicitni nepojmenovany balicek, tj. nepouzijte "package" */

import java.io.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.ArrayDeque;

/** DU 2. !!! Nemente jmeno tridy !!!
  * 
  * @author Jaroslav Jindrak
  */
public class CodEx {

  public static void main(String[] argv)
  {
	  Deque<Integer> stack = new ArrayDeque<Integer>();
	  try
	  {
		  // Yes, I could use a scanner to avoid writing my own parser, but I am too lazy.
		  BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		  int c;
		  int temp = -1;

		  /* TODO: Check possible (and highly probable) equal effect of these two! */
		  boolean new_line = true;
		  boolean empty = true; // Probably superior, '\n' counts as a whitespace...

		  while((c = input.read()) != -1)
		  {
			  if(!Character.isWhitespace((char)c)) // Check for line made of whitespaces!
				  empty = false;
			  if(new_line && (char)c == '\n') // Check for line containing only '\n'
				  continue; // May be obsolete due to the empty line check above.
			  else
				  new_line = false;

			  if(Character.isDigit((char)c))
			  {
				  temp = Character.digit(c,10);
				  while(is_next(input)) // Multidigit numbers!
					  temp = temp * 10 + Character.digit(input.read(),10);
			  }

			  if(!is_operand(c) && !Character.isWhitespace((char)c) && (char)c != '\n')
			  { // Possible CoDex error: whitespace before '\n' -- obsolete warning...
				  stack.push(temp);
				  temp = -1;
			  }
			  else if((char)c == '\n')
			  {
				  new_line = true;
				  if(stack.size() == 1) // Only the result stored in stack.
					  System.out.println(stack.pop());
				  else if(empty)
					  stack.clear(); // Do not leave leftovers for next expression.
				  else
					  System.out.println("ERROR");
				  empty = true;
			  }
			  else if(Character.isWhitespace((char)c))
			  {
				  // Do jack shit.
			  }
			  else if(is_operand(c))
			  {
				  if(stack.size() < 2)
				  {
					  System.out.println("ERROR");
					  while((c = input.read()) != '\n');
					  new_line = true; // For following empty line.
					  stack.clear(); // Leftovers!
					  continue;
				  }
				  int snum = stack.pop();
				  int fnum = stack.pop();
				  stack.push(calculate(fnum,snum,c));
			  }
			  else
				  System.out.println("Huh? " + (char)c); // Should not happen...?
		  }
	  }
	  catch(IOException ex)
	  {
		  System.out.println("ERROR"); // w/e
	  }
	  catch(ArithmeticException ex)
	  {
		  System.out.println("ERROR"); // Possible division by zero.
	  }
  }

  public static boolean is_operand(int code)
  { // First elegant solution didn't work properly, too lazy for a new elegant solution...
	  char temp = (char)code;
	  return temp == '+' || temp == '-' || temp == '*' || temp == '/';
  }

  public static int calculate(int fnum, int snum, int operand)
  { // Calculate the result of given binary expression (in postfix).
	  switch((char)operand)
	  {
		  case '+':
			  return fnum + snum;
		  case '-':
			  return fnum - snum;
		  case '*':
			  return fnum * snum;
		  case '/':
			  return fnum / snum;
		  default:
			  System.out.println("Chimichanga!"); // Will never happen due to the is_operand() function.
			  return 0;
	  }
  }

  public static boolean is_next(BufferedReader input) throws IOException
  { // Used for multidigit numbers, checks if the next byte read is a digit.
	  input.mark(1); // For peeking forwards in the stream.
	  int c = input.read();
	  if(c != -1 && Character.isDigit((char)c))
	  {
		  input.reset();
		  return true;
	  }
	  else
	  {
		  input.reset();
		  return false;
	  }
  }
}
