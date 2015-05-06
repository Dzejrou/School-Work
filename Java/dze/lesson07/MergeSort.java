package dze.lesson07;

import java.lang.Thread;
import java.util.List;
import java.util.ArrayList;

public class MergeSort extends Thread
{
	static Integer count = 0;
	static int max_count;
	int id;

	public static List<MergeSort> threads = new ArrayList<MergeSort>();

	// Sort variables:
	int[] array_;
	int[] help_;
	int left_;
	int right_;

	public MergeSort(int[] array, int[] help, int left, int right)
	{
		inc_count();

		array_ = array;
		help_ = help;
		left_ = left;
		right_ = right;
		start();
	}
	
	public void run()
	{
		System.out.println("Starting thread #" + get_count());
		merge_sort(array_,help_,left_,right_);
	}

	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			System.out.println("Chimichanga!");
			System.exit(0);
		}

		max_count = Integer.parseInt(args[0]);

		int[] smth = {3,0,1,6,4,5,2,7,9,8};
		int[] help = new int[10]; // Help array.
		merge_sort(smth,help,0,9);

		for(int i = 9; i >= 0; i--)
			System.out.print(smth[i] + " ");
		System.out.println();

	}

	public static void merge_sort(int[] array, int[] help, int left, int right)
	{
		if(left == right)
			return;
		int mid = (left + right) / 2;

		if(count < max_count)
			add(new MergeSort(array,help,left,mid));
		else
			merge_sort(array,help,left,mid);

		if(count < max_count)
			add(new MergeSort(array,help,mid+1,right));
		else
			merge_sort(array,help,mid+1,right);

		merge(array,help,left,right);

		for(int i = left; i <= right; i++)
			array[i] = help[i];

		wait_for_threads();
	}

	public static void merge(int[] array, int[] help, int left, int right)
	{
		int mid = (left + right) / 2;
		int left_index = left;
		int right_index = mid + 1;
		int help_index = left;

		while(left_index <= mid && right_index <= right)
		{
			if(array[left_index] >= array[right_index])
				help[help_index++] = array[left_index++];
			else
				help[help_index++] = array[right_index++];
		}

		while(left_index <= mid)
			help[help_index++] = array[left_index++];

		while(right_index <= right)
			help[help_index++] = array[right_index++];
	}

	public synchronized Integer get_count()
	{
		return count;
	}

	public synchronized void inc_count()
	{
		count++;
	}

	public static synchronized void add(MergeSort thread)
	{
		threads.add(thread);
	}

	public static synchronized void wait_for_threads()
	{
		try
		{
			for(MergeSort thread : threads)
				thread.join();
		}
		catch(InterruptedException ex)
		{
			System.out.println("Java sucks.");
		}
	}
}
