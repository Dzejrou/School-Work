package dze.lesson02;

public class TimeStampPrinter extends GenericPrinter
{
	public TimeStampPrinter()
	{
		System.out.println("TimeStampPrinter");
	}
	public void print(String msg)
	{
		super.print(new java.util.Date() + " - " + msg);
	}
}
