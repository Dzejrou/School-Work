import java.util.Random;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Histogram
{
	public static void main(String[] args)
	{
		Scanner vstup = new Scanner(System.in);
		Random nahodnaCisla = new Random(10);

		System.out.println("Zadejte pocet dimenzi:");
		int dimenze = vstup.nextInt();

		System.out.println("Zadejte velikost poli:");
		int velikost = vstup.nextInt();

		// Vytvoreni tri matic ruznych dimenzi:
		int[]     matice1 = new int[velikost];
		int[][]   matice2 = new int[velikost][velikost];
		int[][][] matice3 = new int[velikost][velikost][velikost];

		// Pole pro pocitani cetnosti jednotlivych cisel.
		int[] cetnost = new int[10]; // 0..9 reprezentuje 1..10, proto pouzivam matice1[i] - 1.

		switch(dimenze)
		{
			case 1:
				for(int i = 0; i < velikost; i++)
				{
					matice1[i] = nahodnaCisla.nextInt(10) + 1; // 0..9 + 1 = 1..10
					cetnost[matice1[i] - 1]++; // Zvys cetnost daneho cisla.
				}
				break;
			case 2:
				for(int i = 0; i < velikost; i++)
					for(int j = 0; j < velikost; j++)
					{
						matice2[i][j] = nahodnaCisla.nextInt(10) + 1;
						cetnost[matice2[i][j] - 1]++;
					}
				break;
			case 3:
				for(int i = 0; i < velikost; i++)
					for(int j = 0; j < velikost; j++)
						for(int k = 0; k < velikost; k++)
						{
							matice3[i][j][k] = nahodnaCisla.nextInt(10) + 1;
							cetnost[matice3[i][j][k] - 1]++;
						}
				break;
			default:
				System.out.println("Neplatny pocet dimenzi.");
				System.exit(0);
				break;
		}

		// Znormovani cetnosti:
		for(int i = 0; i < 10; i++)
			cetnost[i] /= velikost;

		vytiskniHistogram(cetnost);
		ulozHistogram(cetnost);
	}

	// Vytiskne formatovany histogram.
	public static void vytiskniHistogram(int[] cetnost)
	{
		for(int i = 1; i <= 10; i++)
		{
			System.out.print(i + ": ");
			for(int j = 0; j < cetnost[i - 1]; j++)
				System.out.print("*"); // Vytiskni (pocet vyskytu i) jednicek.
			System.out.println(); // Odradkuj.
		}
	}

	// Ulozi formatovany histogram do souboru "Histogram.txt".
	public static void ulozHistogram(int[] cetnost)
	{
		BufferedWriter vystup;
		try
		{
			vystup = new BufferedWriter(new FileWriter("Histogram.txt"));
			char[] hvezdicky; // Pro zapisovani hvezdicek.
			String zacatek; // Pro ocislovani.
			for(int i = 1; i <= 10; i++)
			{
				zacatek = new String(i + ": ");
				hvezdicky = new char[cetnost[i - 1]];
				for(int j = 0; j < cetnost[i - 1]; j++)
					hvezdicky[j] = '*';

				vystup.write(zacatek);
				vystup.write(hvezdicky);
				vystup.newLine(); // Odradkovani.
			}
			vystup.close();
		}
		catch(IOException chyba)
		{
			System.out.println("Nastala chyba pri zapisu do souboru.");
		}
	}
}
