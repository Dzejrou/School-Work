package dze.lesson03;

import java.util.Iterator;

class Node implements Iterable
{
	/* TODO: Make INode interface!? */

	public int val;
	public Node left, right;
	public Node root;

	public Node(int x)
	{
		this.val = x;
		this.left = null;
		this.right = null;
	}

	public Node(int x, boolean isRoot)
	{
		this(x);
		this.root = this;
	}

	public Node(int x, Node theRoot)
	{
		this(x);
		this.root = theRoot;
	}

	public void add(Node x)
	{
		if(this.val > x.val)
		{
			if(this.left == null)
				this.left = x;
			else
				left.add(x);
		}
		else
		{
			if(this.right == null)
				this.right = x;
			else
				right.add(x);
		}
	}

	public int getMax()
	{
		if(this.right != null)
			return this.right.getMax();
		else if (this.left != null)
			return this.left.getMax();
		else
			return this.val;
	}

	public Node getSucc(int x)
	{
		if(this.left != null && this.left.val > x)
			return this.left.getSucc(x);
		else if(this.right != null)
			return this.right.getSucc(x);
		else
			return this;
	}

	public Iterator iterator()
	{
		return new Iterator()
		{
			private Node currNode = Node.this.root;
			public boolean hasNext()
			{
				return currNode.val < currNode.root.val; 
			}

			public Node next()
			{
				return currNode.getSucc(Node.this.val);
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	public static void main(String[] args)
	{
		Node theTree;

		int temp;
		Node tempRoot = null;

		for(int i = 0; i < args.length; i++)
		{
			try
			{
				temp = Integer.parseInt(args[i]);
			}
			catch(NumberFormatException e)
			{
				if(i != 0)
				{
					System.out.println("Arg #" + i + " is not an integer, skipping.");
					continue;
				}
				else
				{
					System.out.println("Root not integer, exiting!");
					System.exit(0);
				}
			}

			if(i == 0)
			{
				theTree = new Node(temp, true);
				tempRoot = theTree;
			}
			else
				theTree.add(new Node(temp,tempRoot));
		}

		/* Print them leaves! */
		for(Node x : theTree)
			System.out.println(x.val);
	}
}
