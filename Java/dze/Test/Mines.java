import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Mines
{
    private static Boolean[] game_field;
    private static boolean lost;
    private static JButton[] butts;
    private static JFrame frame_;
    private static JPanel panel_;
    private static int bomb_count = 0;
    private static int field_count = 25;
    private static int free_count = field_count - bomb_count;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        run_game();
                    }
                });
    }

    public static void run_game()
    {
        JFrame.setDefaultLookAndFeelDecorated(true);
        game_field = generate_game_field(field_count,bomb_count);
        butts = new JButton[field_count];
        lost = false;

        frame_ = new JFrame("Minesweeper clone by Dzejrou!");
        frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel_ = new JPanel(new GridLayout(5,5));
        panel_.setBorder(BorderFactory.createEmptyBorder(25,25,5,5));

        for(int i = 0; i < game_field.length; i++)
        {
            final int index = i;
            butts[i] = new JButton("Bomb?");
            butts[i].addActionListener(
                    new ActionListener(){
                        Boolean field = game_field[index];
                        JButton butt = butts[index]; // Leave the references.
                        public void actionPerformed(ActionEvent e){
                            if(!lost && field){
                                butt.setText("[!]");
                                lost = true;
                                JOptionPane.showMessageDialog(frame_,"Lol, such a noob!");
                                System.exit(0);
                            }
                            else if(!lost){
                                free_count--;
                                butt.setText("[ ]");
                                if(free_count == 0){
                                    JOptionPane.showMessageDialog(frame_,"Congrats, you have wonnered!");
                                    System.exit(0);
                                }
                            }
                        }
                    });
            panel_.add(butts[i]);
        }

        frame_.getContentPane().add(panel_);
        frame_.pack();
        frame_.setVisible(true);
    }

    private static Boolean[] generate_game_field(int size, int bombs)
    {
        Boolean[] tmp = new Boolean[size];
        for(int i = 0; i < size; i++)
            tmp[i] = new Boolean(false);
        Random rnd = new Random();
        while(bombs > 0)
        {
            int i = rnd.nextInt(size);
            if(!tmp[i])
            {
                tmp[i] = true;
                bombs--;
            }
        }
        return tmp;
    }
}
