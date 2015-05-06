package dze.lesson09;

import java.util.List;
import java.util.ArrayList;
import java.net.*;

class Server
{
	public static fname;
	public static bname;
	public static int count;
	public static List<String> msgs;
	public static List<InputConnection> in_connections;
	public static void main(String[] args)
	{
		if(args.length == 0)
			fname = "todo";
		else
			fname = args[0];
		banem = fname + "_temp";

		msgs = read_msgs(fname);
		in_connections = new ArrayList<InputConnection>();

		try(ServerSocket server = new SocketServer(3000))
		{
			while(true)
			{
				try(Socket sock = server.accept())
				{
					in_connections.add(new InputConnection(sock));
					in_connections.get(in_connections.size() - 1).start();
				}
				catch(Exception exx)
				{
					System.out.println("[INNER]Java ma problemy: " + exx.getMessage());
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("[OUTER]Java ma problemy: " + ex.getMessage());
		}
	}

	public static void exit()
	{
		write();
		broadcast("System is going down.");
		System.exit(0);
	}

	public static List<String> read_msgs(String name)
	{
		List<String> msgs = new Collections.synchronizedList<String>(new ArrayList<String>());
		try(BufferedReader input = new BufferedReader(new FileReader(name)))
		{
			String s;
			while((s = input.readLine()) != null)
				msgs.add(s);
		}
		catch(IOException ex)
		{
			System.out.println("[READ]Java ma problemy: " + ex.getMessage());
		}
		return msgs;
	}

	public static void broadcast(String msg)
	{
		for(InputConnection i : in_connections)
			i.send(msg);
	}

	static class InputConnection implements Runnable
	{
		private Socket sock_;
		private String name_;
		public InputConnection(Socket sock)
		{
			sock_ = sock;
			name_ = new String("GUEST");
		}
		public void run()
		{
			try
			{
				Reader in = new InputStreamReader(sock_.getInputStream());
				PrinterWriter out = new PrinterWriter(new OutputStreamWriter(sock_.getOutputStream()),true);
				while(true)
				{
					String msg = in.readLine();
					String answer = process(msg);
					send(answer);
				}
			}
			catch(Exception ex)
			{
				System.out.println("Java ma problemy: " + ex.getMessage());
			}
		}

		public String process(String msg)
		{
			if(msg.startsWith("init"))
			{
				String[] temp = msg.split(" ");
				name_ = temp[1];
				return new String("Hello, " + name_);
			}

			switch(msg.charAt(0))
			{
				case 'a':
					String prio = msg.split(" ")[1];
					String msg = msg.split(" ")[2];
					add(prio,msg);
					return new String("Msg successfully added.");
				case 'g':
					String index = msg.split(" ")[1];
					return new String(get(index));
				case 'd':
					String index = msg.split(" ")[1];
					return new String("Successfully deleted.");
				case 'e':
					Server.exit();
					break;
			}
		}

		public void add(String prio, String msg)
		{
			Server.msgs.add(prio + " " + msg);
		}

		public String get(String index)
		{
			try
			{
				int i = Integer.parseInteger(index);
				return Server.msgs.get(i);
			}
			catch(NumberFormatException ex)
			{
				System.out.println("Java ma problemy: " + ex.getMessage());
			}
		}

		public void del(String index)
		{
			Server.broadcast("User " + name + " deleted message at index " + index + "!");
			try
			{
				int i = Integer.parseInteger(index);
				Server.msgs.remove(i);
			}
			catch(NumberFormatException ex)
			{
				System.out.println("Java ma problemy: " + ex.getMessage());
			}
		}

		public void send(String msg)
		{
			PrinterWriter out = new PrinterWriter(new OutputStreamWriter(sock_.getOutputStream()),true);
			out.write(msg);
		}
	}
}
