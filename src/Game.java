import javax.swing.*;

public class Game extends JFrame {


    public Game (){


        setSize(1000,1000);
        setVisible(true);
        setTitle("Space Dodge");
        Board spaceGame = new Board(getContentPane().getSize().width,getContentPane().getSize().height);
        add(spaceGame);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);



    }



}
