/* Pouzijte implicitni nepojmenovany balicek, tj. nepouzijte "package" */

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

/** DU 1. !!! Nemente jmeno tridy !!!
  * 
  * @author Jaroslav Jindrak
  */
public class CodEx {

	

  public static void main(String[] argv)
  {
    
    /* VZOR: kod, ktery opisuje data ze standardniho vstupu na standardni vystup
     * a konci pokud narazi na konec vstupu.
     *
     *
     *  try {
     *    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
     *    int c;
     *    while ((c = input.read()) != -1) {
     *      System.out.print((char) c);
     *    }
     *  } catch (IOException ex) {
     *    System.out.println("Nastala IOException");
     *  }
     *
     */

	  try
	  {
		  BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		  ArrayList<String> line = new ArrayList<String>();
		  String s;

		  int line_length = 0;
		  try
		  {
			  line_length = Integer.parseInt(input.readLine()); // Get the line number.
		  }
		  catch(NumberFormatException e)
		  {
			  System.out.println("Error");
			  System.exit(0);
		  }

		  while((s = input.readLine()) != null)
		  {
			  line.addAll(Arrays.asList(s.split("\\s+")));

			  input.mark(1000); // Set a mark for empty-line check!
			  String temp = input.readLine();
			  boolean next_empty = true;
			  if(temp != null) // Next line exists.
				  next_empty = is_empty_line(temp);
			  input.reset();

			  if(!next_empty)
				  line = print(line,line_length); // Print and return leftovers!
			  else if(len(line) <= line_length)
				  print_last(line);
			  else
			  {
				  while(len(line) > line_length)
					  line = print(line,line_length);
				  print_last(line);
				  line.clear();
			  }

		  }

		  if(line.size() != 0)
		  {
			  while(len(line) > line_length) // Leftovers after last line.
				  line = print(line,line_length);
			  print_last(line);
			  line = null;
		  }
	  }
	  catch(IOException ex)
	  {
		  System.out.println(ex.getMessage());
	  }

  }

  public static int len(ArrayList<String> line)
  {
	  int length = 0;
	  for(String s : line)
		  length += s.length() + 1;
	  return length - 1; // Ommitt last space.
  }

  public static ArrayList<String> print(ArrayList<String> line, int line_size)
  {
	  if(line.size() == 1 && line.get(0).length() > line_size)
	  { // Single word longer than the line size.
		  System.out.println(line.get(0).length());
		  return new ArrayList<String>();
	  }


	  int temp_size = 0;
	  int word_count = 0;
	  int space_count = 0;
	  int bonus_spaces = 0;

	  if(line.get(0).length() == line_size) // Hmm?
	  {
		  System.out.print(line.get(0)); // New line is lower in the code.
		  line.set(0,null);
	  }
	  else
	  {
		  for(int i = 0; i < line.size(); i++)
		  {
			  if(temp_size + line.get(i).length() <= line_size) // +1 for the last space.
			  {
			 // System.out.println("Adding word: " + line.get(i) + " - temp = " + temp_size + " - size = " + line.get(i).length());
				  temp_size += line.get(i).length() + 1; // Number of characters w 1 space.
				  word_count++;
			  }
			  else
				  break; // Would allow to skip words!
		  }
		  temp_size--; // Last space won't be included.
	  }

	  if(word_count > 1)
	  {
		  space_count = (line_size - temp_size) / (word_count - 1); // Balanced number of spaces.
		  bonus_spaces = (line_size - temp_size) % (word_count - 1);
	  }
	  else
	  {
		  space_count = 0;
		  bonus_spaces = 0;
	  }
/*
	  System.out.println();
	  System.out.println("--- NEW LINE ---");
	  System.out.println("COUNT = " + (temp_size + space_count * word_count + bonus_spaces));
	  System.out.println("temp_size: " + temp_size);
	  System.out.println("space_count: " + space_count + " * " + word_count);
	  System.out.println("bonus_spaces: " + bonus_spaces);
*/
	  for(int i = 0; i < word_count; i++) // word_count should be within bounds!
	  {
		  if(i != word_count - 1) // Don't print last space!
			  System.out.print(line.get(i) + " ");
		  else
		  { // Ugly hack, i know... but it doesn't print trailing spaces!
			  System.out.println(line.get(i));
			  line.set(i,null);

			  ArrayList<String> temp_list = new ArrayList<String>();
			  for(int j = 0; j < line.size(); j++) // Get rid of nulls.
				  if(line.get(j) != null)
					  temp_list.add(line.get(j));

			  return temp_list;
		  }
		  line.set(i,null); // Won't need it.

		  if(bonus_spaces > 0)
		  {
			  bonus_spaces--; // Fill the extra spaces from the left side.
			  System.out.print(" ");
		  }

		  for(int j = 0; j < space_count; j++)
			  System.out.print(" ");
	  }
	  System.out.println(); // The '\n' gets ommitted in the main loop.

	  ArrayList<String> temp_list = new ArrayList<String>();
	  for(int i = 0; i < line.size(); i++) // Get rid of nulls.
		  if(line.get(i) != null)
			  temp_list.add(line.get(i));

	  return temp_list;
  }

  public static boolean is_empty_line(String line)
  {
	  if(line.trim().length() > 0) // Contains non-whitespace characters!
		  return false;
	  return true;
  }

  public static void print_last(ArrayList<String> line)
  {
	  for(int i = 0; i < line.size() - 1; i++)
		  System.out.print(line.get(i) + " ");
	  System.out.println(line.get(line.size()-1)); // TODO: What about eof?
  }
}

