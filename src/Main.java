import java.awt.*;

public class Main {




    public static void main(String[] args) {



        System.out.println("Hello world!");


        EventQueue.invokeLater(()->{

            Game game  = new Game();

            game.setVisible(true);


        });
    }




}