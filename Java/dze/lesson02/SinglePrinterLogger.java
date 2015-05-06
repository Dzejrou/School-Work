package dze.lesson02;

public class SinglePrinterLogger implements ILogger
{
	private IPrinter printer;
	private int level;
	private int ignoredMessages;

	public SinglePrinterLogger()
	{
		level = 0;
		ignoredMessages = 0;
	}

	public SinglePrinterLogger(IPrinter p)
	{
		this();
		printer = p;
	}

	public void addPrinter(IPrinter p)
	{
		printer = p;
	}

	public void log(int level, String msg)
	{
		// TODO:
		if(level <= this.level)
			printer.print(msg);
		else
			++ignoredMessages;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}
}
