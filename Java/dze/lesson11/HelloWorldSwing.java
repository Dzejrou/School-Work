import javax.swing.*;

public class HelloWorldSwing
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run()
                    {
                        gui_start();
                    }
                });
    }

    private static void gui_start()
    {
        JFrame frame_ = new JFrame("Swing Program 01");
        frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label_ = new JLabel("Which button do you choose?");
        frame_.getContentPane().add(label_);
        //frame_.setSize(640,480);
        
        JButton butt_1 = new JButton(
                new Action()
                {
                    System.exit(0);
                });

        JButton butt_2 = new JButton(
                new Action()
                {
                    label_.setText("Not that one.");
                });

        butt_1.setText("This one.");
        butt_2.setText("This one.");

        frame_.getContentPane().add(butt_1);
        frame_.getContentPane().add(butt_2);
        frame_.pack();
        frame_.setVisible(true);
    }
}
