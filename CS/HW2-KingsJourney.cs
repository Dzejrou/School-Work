using System;

namespace kingsJourney
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
	class Queue{ // Thanks, Microsoft, but I'm gonna make my own class.
		private int sQue,eQue;
		private int[] queue = new int[128];

		public Queue(){
			sQue = 0;
			eQue = 0;
			for (int i = 0; i <= 127; i++)
				queue[i] = -1;
		}
		public void enQueue (int fnum,int snum)
		{
			if (Chess.isInBoard (fnum, snum) & ((Chess.Board[fnum, snum] == -3) || (Chess.Board[fnum,snum] == 0))) {
				Chess.Board[fnum,snum] = Chess.getStep();
				queue [eQue] = fnum;
				eQue++;
				queue [eQue] = snum;
				eQue++;
			}
		}
		public void enQueue(int fnum,int snum,bool isStartCoord){ // Start field enQueue.
			if (isStartCoord){
				queue[eQue] = fnum;
				eQue++;
				queue[eQue] = snum;
				eQue++;
			}
		}
		public int deQueue(){
			int temp = queue[sQue];
			sQue++;
			return temp;
		}
	}
	class Chess{
		public static int[,] Board = new int[10,10];  // 8x8 + safety rows & cols
		private static int step;
		public static bool isInBoard(int x, int y){
			return ((x >= 1 && x <= 8) && (y >= 1 && y <= 8));
		}
		public static int getStep(){
			return step;
		}
		public static void setStep(int number){
			step = number;
		}
		public static void initSafetyProtocol(){  // Safety rows & cols.
			for(int i = 0; i <= 9; i++){
				Chess.Board[0,i] = -4;
				Chess.Board[i,0] = -4;
				Chess.Board[9,i] = -4;
				Chess.Board[i,9] = -4;
			}
		}
	}
	class Debug{
		public static void setBoard(){
			Chess.Board[1,1] = -2;
			Chess.Board[1,7] = -3;

			Chess.Board[1,2] = -1;
			Chess.Board[1,6] = -1;
			Chess.Board[1,8] = -1;
			Chess.Board[2,2] = -1;
			Chess.Board[2,4] = -1;
			Chess.Board[2,6] = -1;
			Chess.Board[3,2] = -1;
			Chess.Board[3,4] = -1;
			Chess.Board[3,6] = -1;
			Chess.Board[3,7] = -1;
			Chess.Board[4,2] = -1;
			Chess.Board[4,4] = -1;
			Chess.Board[4,6] = -1;
			Chess.Board[5,2] = -1;
			Chess.Board[5,4] = -1;
			Chess.Board[5,6] = -1;
			Chess.Board[5,8] = -1;
			Chess.Board[6,2] = -1;
			Chess.Board[6,4] = -1;
			Chess.Board[6,6] = -1;
			Chess.Board[7,2] = -1;
			Chess.Board[7,4] = -1;
			Chess.Board[7,6] = -1;
			Chess.Board[7,7] = -1;
			Chess.Board[8,4] = -1;
		}
		public static void printBoard(){
			for (int i = 1; i <= 8; i++){
				for (int j = 1; j <= 8; j++)
					Console.Write(Chess.Board[i,j]);
				Console.WriteLine();
			}
		}
	}
	class MainClass
	{
		public static void Main (string[] args)
		{
			Queue MyQue = new Queue ();
			Chess.initSafetyProtocol();

			int obsCount = ReadersDigest.ReadNum ();
			for (int i = 1; i <= obsCount; i++) {  // Getting them obstacles.
				int obsX = ReadersDigest.ReadNum ();
				int obsY = ReadersDigest.ReadNum ();
				if (Chess.isInBoard (obsX, obsY))
					Chess.Board [obsX, obsY] = -1;
			}

			int startX = ReadersDigest.ReadNum ();  // Starting field.
			int startY = ReadersDigest.ReadNum ();
			if (Chess.isInBoard (startX, startY))
				Chess.Board [startX, startY] = -2;

			int finX = ReadersDigest.ReadNum ();  // Target field.
			int finY = ReadersDigest.ReadNum ();
			if (Chess.isInBoard (finX, finY))
				Chess.Board [finX, finY] = -3;

			/*
			Debug.setBoard();
			int startX = 1;
			int startY = 1;
			int finX = 1;
			int finY = 7;
			int obsCount = 25;
			*/

			int currX,currY = 0;
			MyQue.enQueue(startX,startY,true);  // MyQue.enQueue overload for first two coords.
			Chess.setStep(1);

			while (Chess.Board[finX,finY] == -3) {
				currX = MyQue.deQueue();
				currY = MyQue.deQueue();
				if (currX == -1 || currY == -1){  // In case nothing gets enqueued.
					Console.WriteLine(-1);
					System.Environment.Exit(0); 
				}
				if ((Chess.isInBoard (currX, currY)) & (Chess.Board[currX,currY] != -2))
					Chess.setStep(Chess.Board[currX,currY] + 1);

				// Setting the queue.
				MyQue.enQueue (currX, currY + 1);  // down
				MyQue.enQueue (currX + 1, currY + 1);  // down-right
				MyQue.enQueue (currX + 1, currY);  // right
				MyQue.enQueue (currX + 1, currY - 1);  // up-right
				MyQue.enQueue (currX, currY - 1);  // up
				MyQue.enQueue (currX - 1, currY - 1);  // up-left
				MyQue.enQueue (currX - 1, currY);  // left
				MyQue.enQueue (currX - 1, currY + 1); // down-left

				if (Chess.getStep() >= (64 - obsCount)){  // Takes suspiciously long.
					Console.WriteLine(-1);
					System.Environment.Exit(0);
				}
			}
			// Outputting result if target reached.
			Console.WriteLine(Chess.getStep());

			// Debug.printBoard(); Printing the board for checking.
	}
}
}