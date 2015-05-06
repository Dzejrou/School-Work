import java.io.*;
import java.util.*;

/** DU 6. !!! Nemente jmeno tridy !!!
  * @author Jaroslav Jindrak
  */
public class CodEx
{
	public static char[] cells;
	public static String program;
	public static void main(String[] argv)
	{
		try // Handle parameters and init.
		{
			if(argv.length >= 2)
			{
				cells = new char[Integer.parseInt(argv[1])];
				program = argv[0];
			}
			else if(argv.length == 1)
			{
				cells = new char[30000];
				String pname = argv[0];
				
				try(BufferedReader input = new BufferedReader(new FileReader(pname)))
				{
					String s;
					StringBuilder temp = new StringBuilder("");
					while((s = input.readLine()) != null)
						temp.append(s + '\n'); // For line counting in check.

					program = temp.toString();
				}
				catch(IOException exx)
				{
					System.out.println(exx.getMessage());
				}
			}
			else
			{
				System.out.println("Wrong amount of parameters, aborting.");
				System.exit(1);
			}
		}
		catch(NumberFormatException ex)
		{ // Wrong number of cells.
			System.out.println(ex.getMessage());
			System.exit(1);
		}

		check_program(program);

		// Execute the program.
		boolean loop = true;
		int curr = 0; // Curr cell.
		int index = 0;
		char c;
		while(loop)
		{
			c = comm(index);
			switch(c)
			{
				case '>':
					curr++;
					assert_cells(curr);
					break;
				case '<':
					curr--;
					assert_cells(curr);
					break;
				case '+':
					cells[curr]++;
					break;
				case '-':
					cells[curr]--;
					break;
				case '.':
					System.out.print(cells[curr]);
					break;
				case ',':
					try
					{
						cells[curr] = (char)System.in.read();
					}
					catch(IOException ex)
					{
						System.out.println(ex.getMessage());
					}
					break;
				case '[':
					if(cells[curr] == 0)
						while(comm(index) != ']')
						{
							index++;
							loop = assert_instr(index);
						}
					break;
				case ']':
					if(cells[curr] != 0)
						while(comm(index) != '[')
						{
							index--;
							loop = assert_instr(index);
						}
					break;
				default:
					break;
			}
			index++;
			loop = assert_instr(index);
		}
	}

	public static void assert_cells(int i)
	{ // Check cell pointer position.
		if(i < 0)
		{
			System.out.println("Memory underrun");
			System.exit(0);
		}
		if(i >= cells.length)
		{
			System.out.println("Memory overrun");
			System.exit(0);
		}
	}

	public static boolean assert_instr(int i)
	{ // Check instruction pointer position.
		if(i < 0 || i >= program.length())
			return false;
		return true;
	}

	public static char comm(int i)
	{ // For simplicity.
		return program.charAt(i);
	}

	public static void check_program(String program)
	{
		char c;
		int line_num = 1;
		int char_num = 1;
		Stack<Position> positions = new Stack<Position>();

		for(int i = 0; i < program.length(); i++)
		{
			c = program.charAt(i);
			if(c == '[')
			{
				positions.push(new Unclosed(line_num,char_num)); // Note the starting paren.
				char_num++;
			}
			else if(c == ']')
			{
				if(positions.size() != 0)
					positions.pop(); // Most inner pair closed, remove last added paren positions.
				else
					positions.push(new Unopened(line_num,char_num)); // Unopened paren.

				char_num++;
			}
			else if(c == '\n')
			{
				char_num = 1;
				line_num++;
			}
			else
				char_num++; // Just pass it.
		}
		if(positions.size() != 0)
		{
			// Print errors if any:
			for(Position p : positions)
				System.out.println(p);

			System.exit(0); // Do I really have to exit here? Guess so :/
		}
	}
}

abstract class Position
{
	int line, pos;
	public Position(int x, int y)
	{
		line = x;
		pos = y;
	}
	
	public abstract String toString();
}

class Unclosed extends Position
{
	public Unclosed(int x, int y)
	{
		super(x,y);
	}

	public String toString()
	{
		return new String("Unclosed cycle - line " + this.line + " character " + this.pos);
	}
}

class Unopened extends Position
{
	public Unopened(int x, int y)
	{
		super(x,y);
	}

	public String toString()
	{
		return new String("Unopened cycle - line " + this.line + " character " + this.pos);
	}
}
