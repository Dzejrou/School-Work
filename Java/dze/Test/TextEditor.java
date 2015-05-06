import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class TextEditor
{
    private static JFrame frame_;
    private static JScrollPane pane_;
    private static JTextArea area_;
    private static JMenuBar menu_;
    private static File file_;
    private static boolean changed_ = false;

    public static void main(String[] args)
    {
        file_ = new File("/tmp/dze_text");
        try
        {
            if(!file_.exists())
                file_.createNewFile();
        }
        catch(IOException ex)
        {
            System.out.println("Ay caramba! Couldn't create temp file.");
        }

        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        run_gui();
                    }
                });
    }

    private static void run_gui()
    {
        frame_ = new JFrame("Text editor by Dzejrou.");
        frame_.setPreferredSize(new Dimension(640,480));
        frame_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        area_ = new JTextArea();
        area_.setLineWrap(true);
        area_.getDocument().addDocumentListener(
                new DocumentListener(){
                    public void insertUpdate(DocumentEvent e){
                        if(!changed_)
                            frame_.setTitle(frame_.getTitle() + "*");
                        changed_ = true;
                    }
                    public void removeUpdate(DocumentEvent e){
                        if(!changed_)
                            frame_.setTitle(frame_.getTitle() + "*");
                        changed_ = true;
                    }
                    public void changedUpdate(DocumentEvent e){
                        if(!changed_)
                            frame_.setTitle(frame_.getTitle() + "*");
                        changed_ = true;
                    }
                });
        area_.setText("Write you text here."); // Initial text.
        save_as(file_);

        pane_ = new JScrollPane(area_);
        pane_.setVerticalScrollBar(new JScrollBar());
        frame_.add(pane_);
        menu_ = create_menu();
        frame_.setJMenuBar(menu_);

        frame_.pack();
        frame_.setVisible(true);
    }

    public static JMenuBar create_menu()
    {
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("File");

        /* OPEN menu option: */
        JMenuItem item = new JMenuItem("Open");
        item.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        if(changed_){
                            int choice = JOptionPane.showConfirmDialog(frame_,"Save before closing?",
                                "Alert!",JOptionPane.YES_NO_CANCEL_OPTION);
                            if(choice == JOptionPane.YES_OPTION && file_ != null){
                                save_as(file_);
                                open();
                            } else if(choice == JOptionPane.YES_OPTION){
                                save_as(get_file("save"));
                                open();
                            } else if(choice == JOptionPane.NO_OPTION){
                                open();
                            }
                        }
                        else{
                            open();
                        }
                    }
                });
        menu.add(item);

        /* SAVE menu option: */
        item = new JMenuItem("Save");
        item.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        if(changed_ && file_ != null){
                            save_as(file_);
                        } else if(changed_){
                            save_as(get_file("save"));
                        }
                    }
                });
        menu.add(item);

        /*SAVE AS menu option: */
        item = new JMenuItem("Save as");
        item.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        save_as(get_file("save"));
                    }
                });
        menu.add(item);
        menu.add(new JSeparator());

        /* QUIT menu option: */
        item = new JMenuItem("Quit");
        item.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        if(changed_){
                            int choice = JOptionPane.showConfirmDialog(frame_,"Save before closing?",
                                "Alert!",JOptionPane.YES_NO_CANCEL_OPTION);
                            if(choice == JOptionPane.YES_OPTION && file_ != null){
                                save_as(file_);
                            } else if(choice == JOptionPane.YES_OPTION){
                                save_as(get_file("save"));
                            }
                        }
                        System.exit(0);
                    }
                });
        menu.add(item);
        bar.add(menu);

        /* Create the ABOUT section: */
        menu = new JMenu("Help");
        item = new JMenuItem("About");
        item.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        JOptionPane.showMessageDialog(frame_,
                            "Text editor written in Java.\nCreated by Dzejrou 2015.");
                    }
                });
        menu.add(item);
        bar.add(menu);
        return bar;
    }

    private static void open()
    {
        file_ = get_file("open");
        try
        {
            String input = new Scanner(file_).useDelimiter("\\Z").next();
            area_.setText(input);
        }
        catch(FileNotFoundException ex)
        {
            JOptionPane.showMessageDialog(frame_,ex.getMessage());
        }
        changed_ = false;
        frame_.setTitle(frame_.getTitle().replace("*",""));
    }

    private static void save_as(File file)
    {
        try(BufferedWriter output = new BufferedWriter(new FileWriter(file.getAbsoluteFile())))
        {
            if(!file.exists())
                file.createNewFile();
            output.write(area_.getText());
        }
        catch(IOException ex)
        {
            JOptionPane.showMessageDialog(frame_,ex.getMessage());
        }
        changed_ = false;
        frame_.setTitle(frame_.getTitle().replace("*",""));
    }

    private static File get_file(String type)
    {
        JFileChooser chooser = new JFileChooser();
        int choice;
        if(type.equals("open"))
        {
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            choice = chooser.showOpenDialog(null);
        }
        else
        {
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            choice = chooser.showSaveDialog(null);
        }

        if(choice == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile();
        else
            return file_;
    }
}
