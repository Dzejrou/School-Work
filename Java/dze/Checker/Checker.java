import java.util.*;
import java.io.*;

public class Checker
{
	private static String fname; // File name.
	private static String line; // Input line.
	private static String left, right; // Sides of the input.
	private static int var_count = 0; // Number of variables.

	private static Stack<String> oper_l,oper_r;
	private static Stack<String> vars_l,vars_r;
	private static HashMap<String,Boolean> vars;
	private static boolean[][] perms;
	private static int curr_perm = 0;

	public static void main(String[] args)
	{
		/* Get the filename: */
		if(args.length > 0)
			fname = args[0];
		else
		{
			System.out.println("Invalid amount of parameters, aborting.");
			System.exit(0);
		}

		/* Get the content: */
		try(BufferedReader input = new BufferedReader(new FileReader(fname)))
		{
			line = input.readLine(); // Assuming validity of input, check for null otherwise.
		}
		catch(IOException ex)
		{
			System.out.println(ex.getMessage());
		}

		vars = new HashMap<>();
		/* Split the input: */
		left = line.split("=>")[0];
		right = line.split("=>")[1];

		parse(left,true);
		parse(right,false);

		perms = generate_perms();
        for(int i = 0; i < perms.length; i++)
        {
            Set<String> keys = vars.keySet();
            String[] k_arr = keys.toArray(new String[0]);
            for(int j = 0; j < k_arr.length; j++)
            {
                replace(vars,k_arr[j],perms[i][j]);
            }
            boolean b_left = process(oper_l,vars_l);
            boolean b_right = process(oper_r,vars_r);
            boolean result = b_left && !b_right;
            if(result)
            {
                System.out.println("Invalid " + vars.toString());
                System.exit(0);
            }
		}
		System.out.println("Valid");
	}

	private static boolean[][] generate_perms()
	{
        int count = vars.size();
        int rows = (int)Math.pow(2, count);
        boolean[][] bools = new boolean[rows][count];
        for(int i = 0; i < bools.length; i++)
        {
            for(int j = 0; j < bools[i].length; j++)
            {
                int val = bools.length * j + i;
                int ret = (1 & (val >>> j));
                bools[i][j] = ret != 0;
                System.out.print(bools[i][j] + "\t");
            }
            System.out.println();
        }
		return bools;
	}

	private static ArrayList<Boolean> clone(ArrayList<Boolean> b)
	{
		ArrayList<Boolean> tmp = new ArrayList<>();
		for(Boolean bool : b)
			tmp.add(new Boolean(bool));
		return tmp;
	}

	private static boolean subs(String s)
	{ // For true and false constants.
		if(s.equals("True") || s.equals("False"))
			return Boolean.parseBoolean(s);
		else
			//return Boolean.booleanValue(vars.get(s));
			return vars.get(s).booleanValue();
	}

	private static Stack<String> clone_stack(Stack<String> tar)
	{
		Stack<String> tmp = new Stack<String>();
		for(String s : tar)
			tmp.add(new String(s));
		return tmp;
	}

	private static boolean process(Stack<String> oo, Stack<String> vv)
	{
		Stack<String> o = clone_stack(oo);
		Stack<String> v = clone_stack(vv);
		String curr_oper;
		String farg;
		String sarg;
		boolean fbool;
		boolean sbool;
		while(o.size() > 0) // Again, from input correctness I expect that no problems related to empty stack exceptions.
		{
			curr_oper = o.pop();
			switch(curr_oper)
			{
				case "AND":
					farg = v.pop();
					sarg = v.pop();

					fbool = subs(farg);
					sbool = subs(sarg);

					v.push(fbool && sbool ? "True" : "False");
					break;
				case "OR":
					farg = v.pop();
					sarg = v.pop();

					fbool = subs(farg);
					sbool = subs(sarg);

					v.push(fbool || sbool ? "True" : "False");
					break;
				case "NEG":
					farg = v.pop();
					fbool = subs(farg);
					v.push(fbool ? "False" : "True");
					break;
				default:
					// Should not happend.
					break;
			}
		}

		// Now that there are no operators, there should be only one value whitch evaluates the expression.
		return subs(v.pop()); // TODO: this is temp!
	}

	private static void parse(String line,boolean is_left)
	{ /* Parse the line to create stacks. */
		List<String> tokens = new ArrayList<String>();
		String[] tmp = line.split("[(),]");
		for(int i = 0; i < tmp.length; i++)
		{
			String s = tmp[i];
			if(s != null && !s.equals("") && !s.equals(" "))
				tokens.add(s.trim()); // Trailing spaces.
		}
		Stack<String> oper_stack = new Stack<String>();
		Stack<String> var_stack = new Stack<String>();

		String curr;
		for(int i = 0; i < tokens.size(); i++)
		{
			curr = tokens.get(i);
			switch(curr)
			{
				case "AND":
				case "OR":
				case "NEG":
					oper_stack.push(curr);
					break;
				default:
					if(!vars.containsKey(curr))
						vars.put(curr,new Boolean(false)); // False is the initial value, will be set via permutation.
					var_stack.push(curr);
					break;
			}
		}

		if(is_left) // Modify the responding stacks.
		{
			oper_l = oper_stack;
			vars_l = var_stack;
		}
		else
		{
			oper_r = oper_stack;
			vars_r = var_stack;
		}
	}

	private static HashMap<String,Boolean> replace(HashMap<String,Boolean> map, String key, Boolean new_val)
	{
		Boolean tmp = map.remove(key);
		map.put(key,new_val);
		return map;
	}
}
