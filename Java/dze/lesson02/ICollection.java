package dze.lesson02;

public interface ICollection
{
	public void add(Object o);
	public Object get(int i);
	public void remove(Object o);
	public void remove(int i);
	public int getSize();
	public boolean contains(Object o);
	public int indexOf(Object o);
}
