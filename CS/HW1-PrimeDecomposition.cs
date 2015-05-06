using System;

namespace primeDecomposition
{
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
	class MainClass
	{
		public static void Main (string[] args)
		{
			long input = ReadersDigest.ReadNum ();
			long upperBound = (int)(System.Math.Floor(System.Math.Sqrt(input)));
			//false = (index+1) is prime, saves time
			bool[] notPrime = new bool[upperBound];

			for (int i = 1; i < notPrime.Length; i++)
				if (notPrime[i])
					for (int j = i+1; j < notPrime.Length; j++)
						if((j+1) % (i+1) == 0)
							notPrime[j] = true;

			int k = 0;
			int[] output = new int[upperBound];
			for (int i = 1; i < notPrime.Length; i++) {
				if (!notPrime[i]) {
					while (input % (i+1) == 0) {
						output[k] = (i + 1);
						input /= (i + 1);
						k++;
					}
				}
			}
			if (input != 1) output[k] = (int)input;

			Array.Sort (output);
			for (int i = 0; i < output.Length; i++) {
				if (output [i] != 0) {
					Console.Write (output [i]);
					Console.Write (" ");
				}
			}
		}
	}
}