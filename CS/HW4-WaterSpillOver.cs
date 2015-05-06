using System;
using System.Collections.Generic;
using System.Linq;

namespace waterSpillOver
{
	class MainClass
	{
		public static List<State> Queue = new List<State>();
		public static Bottle[] Bottles = new Bottle[3];
		public static void Main (string[] args)
		{
			for(int i = 0; i < 3; i++){
				Bottles[i] = new Bottle(ReadersDigest.ReadNum());
			}
			for(int i = 0; i < 3; i++){
				Bottles[i].used = ReadersDigest.ReadNum();
			}

			Queue.Add(new State(Bottles,0));

			while(Queue.Count() > 0){
				State s = Queue.First();
				Queue.Remove(s);
				s.GetNext();
			}
			Console.Write(" ");  // Maybe?
	//		Console.WriteLine();
			StatusMonitor.Print();
		}
	}
	class Bottle{
		public int volume;
		public int used;
		public Bottle(int v){
			this.volume = v;
		}
		public int getFreeVolume(){
			return volume - used;
		}
		public void WaterIO(Bottle tarBottle){
			if(this.used <= tarBottle.getFreeVolume()){
				tarBottle.used += this.used;
				this.used = 0;
			}else{
				this.used -= tarBottle.getFreeVolume();
				tarBottle.used = tarBottle.volume; // Gets filled up.
			}

		}
	}
	class State{
		public int stepsCount;
		public Bottle[] BottleState = new Bottle[3];
		public State(Bottle[] BottleArray, int steps){
			for(int i = 0; i < 3; i++){
				BottleState[i] = new Bottle(BottleArray[i].volume);
				BottleState[i].used = BottleArray[i].used;
			}
			this.stepsCount = steps;
		}
		public string StringForm(){
			string[] temp = new string[3];
			for(int i = 0; i < 3; i++){
				temp[i] += BottleState[i].used.ToString();
			}
			return String.Join("-",temp);
		}
		public void GetNext(){
			AlreadyChecked.AddState(this);
			for(int i = 0; i < 3; i++){
				StatusMonitor.AddVolume(BottleState[i].used,stepsCount);
			}
			for(int i = 0; i < 3; i++){
				if(BottleState[i].used == 0)
					continue;
				for(int j = 0; j < 3; j++){
					if(i == j)
						continue;

					State NextState = new State(BottleState,stepsCount+1);

					if(BottleState[j].used == 0)
						continue;

					NextState.BottleState[i].WaterIO(NextState.BottleState[j]);
		//DEBUG		Console.WriteLine("Bottles: {0},{1},{2}.",NextState.BottleState[0].used,NextState.BottleState[1].used,NextState.BottleState[2].used);

					if(!MainClass.Queue.Contains(NextState) && !AlreadyChecked.Contains(NextState)){
						MainClass.Queue.Add(NextState);
					}
				}
			}
		}
	}
	class AlreadyChecked{
		// States that were already achieved.
		private static List<string> CheckList = new List<string>();
		public static void AddState(State x){
			CheckList.Add(x.StringForm());
		}
		public static bool Contains(State x){
			return CheckList.Contains(x.StringForm());
		}
	}
	class StatusMonitor{
		private static List<int> VolumeList = new List<int>();   // Corresponding members in the lists.
		private static List<int> StepList = new List<int>();     
		public static void AddVolume(int v, int s){
			if(VolumeList.Contains(v))
				if(StepList.ElementAt(VolumeList.IndexOf(v)) <= s) // In System.Linq, don't delete!
					return;

			VolumeList.Add(v);  // TODO: Check concurrent usage, possible index mismatch!
			StepList.Add(s);
		}
		public static void Sort(){
			for(int i = 0; i < VolumeList.Count() - 1; i++){
				for(int j = 0; j < VolumeList.Count() - 1; j++){
					if(VolumeList[j] > VolumeList[j+1]){
						int temp = VolumeList[j];
						VolumeList[j] = VolumeList[j+1];
						VolumeList[j+1] = temp;

						temp = StepList[j];
						StepList[j] = StepList[j+1];
						StepList[j+1] = temp;
					}
				}
			}
		}
		public static void Print(){
			Sort();
			for(int i = 0; i < VolumeList.Count(); i++){
				Console.Write("{0}:{1} ",VolumeList[i],StepList[i]);
			}
		}
		public static bool Contains(int v){
			return VolumeList.Contains(v);
		} 
	}
	class ReadersDigest
    {
		// <3 std::cin & std::cout.
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
