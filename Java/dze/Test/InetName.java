import java.net.*;

public class InetName
{
    public static void main(String[] args) throws Exception
    {
        InetAddress addr = InetAddress.getByName("google.com");
        System.out.println(addr);
    }
}
