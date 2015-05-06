package dze.lesson05;

import java.util.Iterator;

public class DzeString implements Iterable
{
	String base;
	String temp;

	public DzeString()
	{
		base = new String();
		temp = null;
	}

	public DzeString(String str)
	{
		base = str;
		temp = null;
	}

	public void append(String str)
	{
		temp = base + str;
		base = temp;
		temp = null;
	}

	public void append(char ch)
	{
		append(Character.toString(ch));
	}

	public void insert(int pos, String str)
	{
		temp = base.substring(0,pos-1) + str + base.substring(pos,base.length());
		base = temp;
		temp = null;
	}

	public void insert(int pos, char ch)
	{
		insert(pos, Character.toString(ch));
	}

	public void delete(int pos, int length)
	{
		temp = base.substring(0,pos-1) + base.substring(pos+length,base.length());
		base = temp;
		temp = null;
	}

	public String toString()
	{
		return base;
	}

	public Iterator iterator()
	{
		return new Iterator()
		{
			int index = 0;

			public boolean hasNext()
			{
				return index < (base.length() - 1);
			}

			public Character next()
			{
				return new Character(base.charAt(index++));
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	public static void main(String[] args)
	{
		// CONSTRUCTION:
		StringBuilder testing = new StringBuilder(new String("LALA"));
		DzeString tested = new DzeString(new String("LALA"));
		assert tested.toString() != testing.toString();

		// APPEND:
		testing.append(new String("Ahoj."));
		tested.append(new String("Ahoj."));
		assert tested.toString() != testing.toString();

		// INSERT:
		testing.insert(3,new String("---"));
		tested.insert(3,new String("---"));
		assert tested.toString() != testing.toString();

		// DELETE:
		testing.delete(2,3);
		tested.delete(2,3);
		assert tested.toString() != testing.toString();

		// ITERATE:
		for(Object c : tested)
			System.out.print(c);
		System.out.println();
	}
}
