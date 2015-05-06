using System;
using System.Collections.Generic;

namespace sokoban
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			Warehouse.setWH(10,10);
			Warehouse.getWH();

			// BREAK!
			// Warehouse.Print();
			// int temp = ReadersDigest.ReadNum();
			// CONT!

			Console.WriteLine(Warehouse.MoveStuffAround());
		}
	
	}
	class MyObject{
		// A class used to create object (box, little dude) instances. (Instances needed for search.)
		public int x,y;
		public MyObject(int x, int y){
			// Constructor.
			if(x >= 1 && x <= 10 && y >= 1 && y <= 10){
				this.x = x;
				this.y = y;
			}else{
				this.x = 1;
				this.y = 1;
			}
		}
		public void Move(int x, int y){
			// Just moving the object.
			int tempX = this.x + x;
			int tempY = this.y + y;
			if(tempX >= 1 && tempX <= 10 && tempY >= 1 && tempY <= 10){
				this.x += x;
				this.y += y;
			}
		}
		public MyObject newObject(){
			// Creates a copy of itself at it's coords.
			return new MyObject(x,y);
		}

	}
	class Warehouse{
		// Static x instances?
		private static char[,] WHField; // The x,y coord field for the warehouse.
		private static int sizeX,sizeY;
		private static MyObject littleGuy;
		private static MyObject box;
		private static int[,] whereTo = new int[,] {{0,1},{1,0},{0,-1},{-1,0}}; // right,up,left,down

		public static void setWH(int x, int y){
			sizeX = x;
			sizeY = y;
			WHField = new char[sizeX + 2,sizeY + 2]; // Including safety borders.
		}

		public static void getWH(){
			string tempString;
			for(int i = 0; i < sizeX; i++){
				tempString = Console.ReadLine();
				char tempChar;
				for(int j = 0; j < tempString.Length; j++){
					tempChar = tempString[j];
					switch(tempChar){
						// Special characters.
						case 'S': littleGuy = new MyObject(i+1,j+1);
							break;
						case 'B': box = new MyObject(i+1,j+1);
							break;
						// No default, just continue.
					}
					WHField[i+1,j+1] = tempChar;
				}
			}
			for (int i = 0; i < 12; i++) {
				WHField[i,0] = 'X';
				WHField[i,sizeY+1] = 'X';
				WHField[0,i] = 'X';
				WHField[sizeX+1,i] = 'X';
			}

			/**
			// Setting the warehouse walls. Possible time cut by using one for cycle with 12 iterations. (Fixed size.)
			for(int i = 0; i <= sizeX; i++){
				WHField[i,0] = 'X';
				WHField[i,sizeY+1] = 'X';
			}
			for(int i = 0; i <= sizeY; i++){
				WHField[0,i] = 'X';
				WHField[sizeX+1,i] = 'X';
			}
			**/
		}

		public static void Print(){
			// Debug printing method. If i and j would start from 0, safety borders would be printed.
			for (int i = 1; i < sizeX+1; i++) {
				for (int j = 1; j < sizeY+1; j++) {
					Console.Write(WHField[i,j]);
				}
				Console.WriteLine(); // LF
			}
		}

		public static int MoveStuffAround(){
			int[,,,] progressCheck = new int[sizeX+1,sizeY+1,sizeX+1,sizeY+1]; // First two for little guy, second two for box.
			Queue<MyObject> qLittleGuy = new Queue<MyObject>();
			Queue<MyObject> qBox = new Queue<MyObject>();

			// First enqueue.
			qLittleGuy.Enqueue(littleGuy);
			qBox.Enqueue(box);

			while(qLittleGuy.Count != 0){
				MyObject tempLG = qLittleGuy.Dequeue();
				MyObject tempB = qBox.Dequeue();
				int prev = progressCheck[tempLG.x,tempLG.y,tempB.x,tempB.y];

				for(int i = 0; i < (whereTo.Length / 2); i++){
					littleGuy = tempLG.newObject();
					box = tempB.newObject();

					littleGuy.Move(whereTo[i,0],whereTo[i,1]); // whereTo[,] defined at Warehouse::49.
					if(littleGuy.x == box.x && littleGuy.y == box.y)
						box.Move(whereTo[i,0],whereTo[i,1]);

					if(WHField[littleGuy.x,littleGuy.y] == 'X' || WHField[box.x,box.y] == 'X'){
						continue; // Obstacle.
					}

					if(WHField[box.x,box.y] == 'C')
						return prev+1;

					if(progressCheck[littleGuy.x,littleGuy.y,box.x,box.y] != 0){
						continue;
					}
					progressCheck[littleGuy.x,littleGuy.y,box.x,box.y] = prev + 1;
					qLittleGuy.Enqueue(littleGuy.newObject());
					qBox.Enqueue(box.newObject());
				}
			}
			return -1;
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
