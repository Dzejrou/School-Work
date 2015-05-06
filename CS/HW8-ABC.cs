using System;
using System.Collections.Generic;

namespace abcdefgh
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			int width = ReadersDigest.ReadNum();
			int height = ReadersDigest.ReadNum();
			string KBTable = Console.ReadLine();
			string pattern = Console.ReadLine();

			KBoard kb = new KBoard(width, height, KBTable);

			Console.WriteLine(kb.KeyStrokesFinder(pattern));
		}
	}
	struct Coords{
		// Simple [x,y] structure.
		public int x,y;
		public Coords(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	class KBoard{
		public Dictionary<char, List<Coords>> table = new Dictionary<char, List<Coords>>();
		public Coords size;
		public KBoard(int fnum, int snum, string input){
			size = new Coords(fnum, snum);
			for (int i = 0; i < input.Length; i++) {
				this.Add((char)input[i], i % size.x, i % size.y);
			}
		}
		public void Add(char input, int x, int y){
			List<Coords> temp;
			try{
				// Key already in table.
				table.TryGetValue(input, out temp);
				temp.Add(new Coords(x,y));
			}catch(NullReferenceException){
				// #ifndef
				// #define
				temp = new List<Coords>();
				temp.Add(new Coords(x,y));
				table.Add(input, temp);
			}
		}
		private int distance(Coords fCoords, Coords sCoords){
			return Math.Abs(fCoords.x - sCoords.x) + Math.Abs(fCoords.y - sCoords.y);
		} 
		public int KeyStrokesFinder(string input){
			

			// !!! TODO: returns (the correct number + 1), fix it

			input = this.FixInput(input); // Possible characters not on keyboard.
			if(input.Length == 0) // Empty input maybe?
				throw new ArgumentException(); 

			List<Coords> previous;
			List<Coords> current;
			List<int> prevStrokes = new List<int>();
			List<int> currStrokes = new List<int>();

			table.TryGetValue(input[0], out previous); // Get position of first key 
													   // (or positions - if multiple occurences are present).
			for(int i = 0; i < previous.Count; i++){
				Console.WriteLine("Getting first keys: Distance of [{0},{1}] and [0,0] = {2}.",previous[i].x,previous[i].y,distance(previous[i],new Coords(0,0)));
				prevStrokes.Add(distance(previous[i],new Coords(0,0))+1);
				// Initial way. (increment is for enter)
			}

			for(int i = 1; i < input.Length; i++) {
				// TODO: check loop variable start
				table.TryGetValue(input[i], out current);
				for(int j = 0; j < current.Count; j++){
					currStrokes.Add(int.MaxValue); // For fresh list.
					for(int k = 0; k < previous.Count; k++){
						int temp = distance(previous[k],current[j]) + prevStrokes[k];
						Console.WriteLine("Getting distance of [{0},{1}] and [{2},{3}] = {4}",previous[k].x,previous[k].y,current[j].x,current[j].y,temp-prevStrokes[k]);
						if(temp < currStrokes[j])
							currStrokes[j] = temp+1;
					}
				}
				previous = current;
				prevStrokes = currStrokes;
				currStrokes = new List<int>();
			}
			return getMinimum(prevStrokes);
		}
		private int getMinimum(List<int> list){
			// TODO: Check MSDN for already implemented method.
			int temp = int.MaxValue;
			foreach(int number in list){
				if(number < temp)
					temp = number;
				//Console.Write (number);
			}
			//Console.WriteLine ();
			return temp;
 		}
		private string FixInput(string input){
			string temp = "";
			for(int i = 0; i < input.Length; i++)
				if(table.ContainsKey(input[i]))
					temp += input[i];
			return temp;
		}
	}
    class ReadersDigest
    {
        static bool isNum(int num)
        {
            return (( num >= '0' ) && ( num <= '9' ));
        }

        public static int ReadNum()
        {
            int predInput = 0;
            bool isNeg = false;
            int input = Console.Read();
            while (!isNum(input))
            {
                predInput = input;
                input = Console.Read();
            }

            if (predInput == '-') isNeg = true; 

            int temp = 0;
            while (isNum(input)) {
                temp = 10 * temp + ( input - '0' );
                input = Console.Read();
            }

            if (isNeg) return -temp;
            else return temp;
        }
    }
}
