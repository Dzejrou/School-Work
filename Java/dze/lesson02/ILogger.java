package dze.lesson02;

public interface ILogger
{
	public void addPrinter(IPrinter p);
	public void log(int level, String msg);
	public void setLevel(int level);
}
