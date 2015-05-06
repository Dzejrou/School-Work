package dze.lesson02;

public class DzeVector implements ICollection
{
	private int size;
	private int last;
	private int block;
	private Object[] base;

	public DzeVector()
	{
		size = 0;
		last = 0;
		block = 10;

		base = new Object[block];
	}

	public DzeVector(int size)
	{
		this.size = size;
		block = size;
		base = new Object[size];
		last = 0;
	}

	public void add(Object o)
	{
		if(last == size)
		{
			// Reallocate.
			Object[] temp = new Object[size + block];
			for(int i = 0; i < size; i++)
				temp[i] = base[i];
			size = size + block;
			base = temp;
			temp = null;

			base[last] = o;
			last++;
		}
		else
		{
			// Push back.
			base[last] = o;
			last++;
		}
	}

	public Object get(int i)
	{
		return base[i];
	}

	public void getRidOfNulls()
	{
		int j = 0;
		Object[] temp = new Object[size];
		for(int i = 0; i < size; i++)
			if(base[i] != null)
			{
				temp[j] = base[i];
				base[i] = null;
				j++;
			}
		size = j;
		base = temp;
		temp = null;
		System.gc();
	}

	public void remove(Object o)
	{
		// Delete ALL occurences!
		for(int i = 0; i < size; i++)
			if(o.equals(base[i]))
				remove(i);
	}

	public void remove(int i)
	{
		base[i] = null;
		getRidOfNulls();
	}

	public int getSize()
	{
		return size;
	}
	
	public boolean contains(Object o)
	{
		for(Object x : base)
			if(o.equals(x))
				return true;
		return false;
	}

	public int indexOf(Object o)
	{
		for(int i = 0; i < size; i++)
			if(o.equals(base[i]))
				return i;
		return -1;
	}
}
