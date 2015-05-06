package dze.lesson02;

public class Core
{
	public static void main(String[] args)
	{

	/*	Printer test.
	GenericPrinter genPrint = new GenericPrinter();
	TimeStampPrinter tsPrint = new TimeStampPrinter();
	PrefixPrinter pfxPrint = new PrefixPrinter("Dze");

	genPrint.print("Test1");
	tsPrint.print("Test2");
	pfxPrint.print("Test3");
	*/

	/* Logger test.
	SinglePrinterLogger spLogger = new SinglePrinterLogger(new PrefixPrinter("Dze"));

	spLogger.setLevel(3);

	spLogger.log(1,"[System] CPU on fire!");
	spLogger.log(5,"[TV Reminder] News start in 5 minutes.");
	spLogger.log(2,"[TV Reminder] Dr Who starts in 5 minutes.");
	spLogger.log(10,"[System] User idle for 1 minute already.");
	spLogger.log(3,"[Mail] Inbox full.");

	System.out.println();

	spLogger.addPrinter(new TimeStampPrinter());

	spLogger.setLevel(1);
	spLogger.log(2000,"[System] Hello!");
	spLogger.log(1,"[System] Compilation complete.");
	spLogger.log(2,"[Whatever] Nobody will read this anyways.");

	System.out.println();

	spLogger.setLevel(3);
	spLogger.log(2,"Trolololo.");
	spLogger.log(4,"Trolololo?");
	*/

	DzeVector myVector = new DzeVector(3);

	myVector.add(new String("Ahoj"));
	myVector.add(new Double(2.5));
	myVector.add(new Integer(1));

	System.out.println(myVector.contains(2.5));
	System.out.println(myVector.contains(2));
	System.out.println(myVector.indexOf(1));

	// Resize.
	myVector.add(new String("Trololo."));
	
	myVector.remove(1);
	myVector.remove(new Integer(1));

	for(int i = 0; i < myVector.getSize(); i++)
		System.out.println(myVector.get(i));

	}
}
