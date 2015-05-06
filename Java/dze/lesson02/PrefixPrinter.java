package dze.lesson02;

public class PrefixPrinter extends GenericPrinter
{
	private String prefix;

	public PrefixPrinter(String prefix)
	{
		this.prefix = prefix;
		System.out.println("PrefixPrinter");
	}

	public void print(String msg)
	{
		super.print(prefix + " - " + msg);
	}
}
