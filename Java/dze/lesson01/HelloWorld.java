package dze.lesson01;

import dze.Tools.Printer;

public class HelloWorld
{
	public static void main(String[] args)
	{
		Printer tmp_printer = new Printer();
		tmp_printer.printLine("Hello world!");

		Printer.printLine("Hello again!");
	}
}
