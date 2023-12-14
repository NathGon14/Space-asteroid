import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Board extends JPanel implements  ActionListener {


    final int SHIP_SCALE  =5;
    int ship_width =16 * SHIP_SCALE;
    int ship_height =16 * SHIP_SCALE;

    int ship_x;
    int ship_y;
    private Timer timer;
    private  int score = 1;

    private final int DELAY = 5;
    private BufferedImage ship_image;
    private BufferedImage rock_image;
    private int SCREEN_HEIGHT;
    private  int SCREEN_WIDTH;
    private  int highScore = 0;
    private long startTime  =0;
    private long accumulatedTime = 0;
    private final int INITIAL_ROCKS_NUMBER = 5;
    private final int ROCK_LIMIT_NUMBER = 15;
    private int current_rocks = INITIAL_ROCKS_NUMBER;
    private int SPEED_INCREASER = 500;

    private int gameStatus = 0;
    //0 means pause
    //1 playing;

    private ArrayList<Rock> rocks = new ArrayList<>();
    public  Board(int width, int height){
        SCREEN_HEIGHT = height;
        SCREEN_WIDTH= width;
        init();


    }
    public void init(){
        setVisible(true);
        setFocusable(true);
        addKeyListener(new keyboardHandler());
        addMouseMotionListener(new mouseHandler());
        loadImages();
        timer = new Timer(5,this);
    }


    public void gameStart(){
        try {
            // Get the center coordinates of the JFrame
            Point center = getLocationOnScreen();
            center.x += getWidth() / 2;
            center.y += getHeight() / 2;

            // Use Robot to move the mouse to the center
            Robot robot = new Robot();
            robot.mouseMove(center.x, center.y);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }

        score =1;
        startTime = System.currentTimeMillis();
        accumulatedTime = 0 ;
        rocks.clear();
        timer.start();

    }
    public  void loadImages(){
        try {
            ship_image = ImageIO.read(new File("img/Alien1.png"));

            rock_image  = ImageIO.read(new File("img/rock.png"));

        ship_image = scaleImage(ship_image,SHIP_SCALE);



        }catch (Exception e){
            System.out.println(e);
        }

    }
    public BufferedImage scaleImage(BufferedImage originalImage,int scale){


            BufferedImage scaled = new BufferedImage(originalImage.getWidth()*scale,originalImage.getHeight()*scale,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g =scaled.createGraphics();
            g.scale(scale,scale);
            g.drawImage(originalImage,0,0,null);
             g.dispose();

            return  scaled;


    }
    @Override
    public void actionPerformed(ActionEvent e) {

        addScore();
        if(collided()){
            highScore = Math.max(highScore,score);
            repaint();
            timer.stop();
        }
        analyzeRocks();
        repaint();
    }

    public boolean collided(){
        Iterator <Rock> it = rocks.iterator();
        while (it.hasNext()){
            Rock rock = it.next();

            if(rock.intersected()) return true;

        }
        return false;

    }

    public void analyzeRocks(){



         current_rocks = score/ SPEED_INCREASER + INITIAL_ROCKS_NUMBER;

         Iterator <Rock> it = rocks.iterator();
         while (it.hasNext()){
             Rock rock = it.next();
             if(rock.isOutside()){
                 it.remove();
                 continue;
             }
             rock.moveRock();

         }


         //add new rocks
        for (int i = rocks.size(); i <= current_rocks &&  current_rocks <=ROCK_LIMIT_NUMBER; i++) {

            rocks.add(new Rock());
        }


    }

    public void addScore(){
        long difference = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        accumulatedTime += difference + score/100;
        int time_limit = 100 ;//in milisec
        if(accumulatedTime >= time_limit){
            accumulatedTime= 0 ;
            score ++;
        }

    }
    public void drawShip(Graphics2D g2d){
        g2d.setColor(Color.red);
        g2d.drawRect(ship_x-1,ship_y-1,ship_width+1,ship_height+1);
        g2d.drawImage(ship_image,ship_x,ship_y,null);

    }

    public void drawRocks(Graphics2D g2d){
        for (Rock rock:rocks
             ) {
            rock.drawRock(g2d);
        }

    }


    public void drawScore(Graphics2D g2d){
        Font largerFont = g2d.getFont(); // Set the desired font size (30f in this case)
        g2d.setColor(Color.white);
        g2d.setFont(largerFont.deriveFont(30f));
        g2d.drawString(score+"",10,60);

        g2d.setColor(Color.red);
        g2d.setFont(largerFont.deriveFont(30f));
        g2d.drawString(""+highScore,10,30);

    }
    @Override
    public void addNotify() {
        super.addNotify();
    }

     @Override
     public  void  paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
         g.setColor(Color.BLACK);

         // Fill the entire area with the black color
         g.fillRect(0, 0, getWidth(), getHeight());

         drawRocks(g2d);
        drawShip(g2d);
         drawScore(g2d);
     }
     public void moveShip(int x, int y ){

       this.ship_x= 50;
        this.ship_y = y- ship_height/2;


     }

    public  class Rock{
        int rock_width =20;
        int rock_height = 20;
        double rock_x;
        double rock_y;
        double rock_speed= 3;
        double rock_dir_x,rock_dir_y;

        private Random random = new Random();
        private BufferedImage image;
        private  int rock_scale = 3 ;
        private int [] validSizes = {1,2,3,4,5};

        public  Rock(){

            rock_speed = rock_speed  + (score / SPEED_INCREASER)*.5;

            rock_speed = Math.min(10,rock_speed);

            generateRock();

        }
        public void spawnRock(){
            // vertical
            if(random.nextBoolean()){
                // top or bottom
                rock_y = random.nextBoolean() ? -rock_height : SCREEN_HEIGHT-rock_height ;

                rock_x  =random.nextInt(SCREEN_WIDTH/2,(SCREEN_WIDTH-rock_width));
            }else{
                rock_x =  SCREEN_WIDTH-rock_width;

                rock_y = random.nextInt(SCREEN_HEIGHT/2,(SCREEN_HEIGHT-rock_height));
            }

        }

        public void generateRock(){
            setSize();
            spawnRock();
            setRockDirection();

        }
        public boolean isOutside(){
             return
                     rock_y< -rock_height
                || rock_y>SCREEN_HEIGHT
                        ||
                        rock_x < -rock_width
                        ||
                        rock_x>SCREEN_WIDTH;



        }
        public void  moveRock(){
            rock_x = rock_x + rock_dir_x * rock_speed;
            rock_y += rock_dir_y * rock_speed;

        }
        public void drawRock(Graphics2D g2d){
            g2d.drawImage(image,(int)rock_x,(int)rock_y,null);
        }
        public void setRockDirection(){
            if(rock_x >= ship_x){
                rock_dir_x = -1;
            }else{
                rock_dir_x = 1;
            }

            if(rock_y >= ship_y){
                rock_dir_y = -1;
            }else{
                rock_dir_y = 1;
            }

        }
        public void setSize(){

          rock_scale =  validSizes[random.nextInt(0,validSizes.length)];
          this.image = scaleImage(rock_image,rock_scale);

            rock_height=image.getHeight();
            rock_width= image.getWidth();

        }
        public boolean intersected(){


            return    ship_x+ship_width >=rock_x &&
                    ship_x <= rock_x+rock_width
                    &&
                    ship_y+ship_height >= rock_y
                    &&
                    ship_y <= rock_y+rock_height;

        }



    }

    public class mouseHandler implements MouseMotionListener {


        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

            moveShip(e.getX(),e.getY());
        }
    }
    public class keyboardHandler implements KeyListener {


        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {


            if(e.getKeyCode() == KeyEvent.VK_SPACE){
                gameStart();
            }
        }
    }



}
