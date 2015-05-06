using System;

namespace myLittleMonster
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			int width = ReadersDigest.ReadNum();
			int height = ReadersDigest.ReadNum();
			string str;
			Game.setBoard(width+1,height+1);
			for (int i = 1; i <= height; i++){
				str = Console.ReadLine();
				for (int j = 1; j <= width; j++){
					switch (str[j-1]){
					case '^': 
						Monster.setDir(1);
						Game.Board[i,j] = str[j-1];
						Monster.setX(i);
						Monster.setY(j);
						break;
					case '>':
						Monster.setDir(2);
						Game.Board[i,j] = str[j-1];
						Monster.setX(i);
						Monster.setY(j);
						break;
					case 'v':
						Monster.setDir(3);
						Game.Board[i,j] = str[j-1];
						Monster.setX(i);
						Monster.setY(j);
						break;
					case '<':
						Monster.setDir(4);
						Game.Board[i,j] = str[j-1];
						Monster.setX(i);
						Monster.setY(j);
						break;
					default:
						Game.Board[i,j] = str[j-1];
						break;
					}
				}
		}

			for (int i = 1; i <= 20; i++){
				Monster.Move();
				Game.printBoard();
			}
        }			
	}
	class Monster
	{
		private static int x = 0;
		private static int y = 0;
		private static int dir = 0; // 1 = ^, 2 = >, 3 = v, 4 = <
		private static bool turned;
		public static void setX(int value){
			x = value;
		} 
		public static void setY(int value){
			y = value;
		} 
		public static int getX(){
			return x;
		} // Not really needed.
		public static int getY(){
			return y;
		} // Not really needed.
		public static void setDir(int value){
				dir = value;
			}
		public static char getDir(){
			switch (dir) {
			case 1:
				return '^';
			case 2:
				return '>';
			case 3:
				return 'v';
			case 4:
				return '<';
			default:
				return 'M';
			}
		} 
		public static bool isWallFront ()
		{
			switch (dir) {
			case 1:
				return Game.isWall (x-1, y);
				break;
			case 2:
				return Game.isWall (x,y+1);
				break;
			case 3:
				return Game.isWall (x+1,y);
				break;
			case 4:
				return Game.isWall (x,y-1);
				break;
			default:
				return false;
				break;	
			}
		}
		public static bool isWallRight(){
			switch (dir) {
			case 1:
				return Game.isWall (x,y+1);
			case 2:
				return Game.isWall (x+1,y);
			case 3:
				return Game.isWall (x,y-1);
			case 4:
				return Game.isWall (x-1,y);
			default:
				return false;
			}
		}
		public static void turnLeft(){
			switch(getDir()){
			case '^':
				setDir(4); // <
				Game.Board[x,y] = getDir();
				break;
			case '>':
				setDir(1); // ^
				Game.Board[x,y] = getDir();
				break;
			case 'v':
				setDir(2); // >
				Game.Board[x,y] = getDir();
				break;
			case '<':
				setDir(3); // v
				Game.Board[x,y] = getDir();
				break;
			}
		}
		public static void turnRight(){
			switch(getDir()){
			case '^':
				setDir(2); // >
				Game.Board[x,y] = getDir();
				break;
			case '>':
				setDir(3); // v
				Game.Board[x,y] = getDir();
				break;
			case 'v':
				setDir(4); // <
				Game.Board[x,y] = getDir();
				break;
			case '<':
				setDir(1); // ^
				Game.Board[x,y] = getDir();
				break;
			}
		} 
		public static void moveForward(){
			Game.Board[x,y] = '.';
			switch (dir) {
			case 1:
				x--;
				break;
			case 2:
				y++;
				break;
			case 3:
				x++;
				break;
			case 4:
				y--;
				break;
			}
			Game.Board[x,y] = getDir();
		}
		public static void Move(){
			if (isWallRight()){
				if (isWallFront()){
					turnLeft();
					turned = true;
				}else{
					moveForward();
					turned = false;
				}
			}else
				if(turned){
					moveForward();
					turned = false;
				}else{
					turnRight();
					turned = true;
				}
		}

	}
	class Game
	{
		public static char[,] Board;
		public static int h, w;
		public static void setBoard(int fnum, int snum){ // Create & set the board.
			h = fnum;
			w = snum;
			Board = new char[w+1,h+1]; // Don't want to start from zero.
		}
		public static bool isWall(int x, int y){
			return Game.Board[x,y] == 'X';
		}
		public static void printBoard(){
			for (int i = 1; i <= w-1; i++) {
				for (int j = 1; j <= h-1; j++)
					Console.Write (Game.Board [i, j]);
				Console.WriteLine();
			}
			Console.WriteLine();
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