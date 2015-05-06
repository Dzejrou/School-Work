public class Exam
{
    public static void main(String[] args)
    {
        /* Overflow: */
        System.out.println("OVERFLOW: -----");
        int b = 2147483647;
        System.out.println(b);
        b = b + 1; // Should overflow.
        System.out.println(b);
        System.out.println();

        /* Label: */
        System.out.println("LABEL: -----");
        System.out.println("url:");
        http://google.com/
        System.out.println(":url");
        System.out.println(); // Prints url: \n :url, because http: is a label and //google.com/ a comment.

        /* Swap: */
        System.out.println("SWAP: -----");
        int x = 10;
        int y = 20;
        x ^= y ^= x ^= y;
        System.out.println(x + " " + y);

        /* Declare i so that it prints YES: */
        System.out.println("DECLARE: -----");
        int i = Integer.MIN_VALUE; // Negation is 0x7fffffff + 1 == 0x8000000 == MIN_VALUE
        if(i == -i && i != 0)
            System.out.println("YES");
        else
            System.out.println("NO");
    }
}
