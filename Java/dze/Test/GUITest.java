import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUITest
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        gui();
                    }
                });
    }

    public static void gui()
    {
        JFrame frame_ = new JFrame("GUITest");
        frame_.setSize(new Dimension(100,100));
        frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel_ = new JPanel();
        JLabel label_ = new JLabel("Label");
        panel_.add(label_);
        JButton butt_ = new JButton("Butt");
        butt_.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        label_.setText("HEY!");
                    }
                });
        panel_.add(butt_);
        frame_.add(panel_);
        frame_.setVisible(true);
    }
}
