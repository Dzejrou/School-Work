package dze.lesson02;

public class GenericPrinter implements IPrinter
{
	public GenericPrinter()
	{
		System.out.println("GenericPrinter");
	}
	public void print(String msg)
	{
		System.out.println(msg);
	}
}
