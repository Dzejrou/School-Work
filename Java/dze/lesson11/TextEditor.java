import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;

public class TextEditor
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run()
                    {
                        start_gui();
                    }
                }
                );
    }

    private static void start_gui()
    {
        JFrame frame_ = new JFrame("Swing Program 02.");
        frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar bar_ = new JMenuBar();

        /* Create menu items: */
        JMenu menu_ = new JMenu("File");
        JMenuItem item_save = new JMenuItem("Save.");
        item_save.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        save();
                    }
                });

        JMenuItem item_close = new JMenuItem("Close.");
        item_close.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        System.exit(0);
                    }
                });

        /* Add items: */
        menu_.add(item_save);
        menu_.add(item_close);

        /* Add menu and set the bar: */
        bar_.add(menu_);
        frame_.setJMenuBar(bar_);

        /* Create text area and scroll pane: */
        JTextArea area_ = new JTextArea("");
        area_.setFont(new Font("Inconsolata",14));
        area_.setLineWrap(true);

        JScrollPane scroll_ = new JScrollPane(area_);
        scroll_.setPreferredSize(new Dimension(640,480));

        /* Pack it all up and show the frame: */
        frame_.pack();
        frame_.setVisible(true);
    }

    public static void save()
    {
        // TODO:
    }
}
