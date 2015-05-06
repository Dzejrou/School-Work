package dze.lesson05;

import java.util.Iterator;

public interface IDzeString implements Iterable
{
	public void append(String str);
	public void append(char ch);
	public void insert(int pos, String str);
	public void insert(int pos, char ch);
	public void delete(int pos, int length);
	public default void append(Object str)
	{
		append(str.toString());
	}

	public default void append(Object... vargs)
	{
		for(Object arg : vargs)
			append(arg.toString())
	}

	public Iterator iterator();
}
