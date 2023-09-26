package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

public class Race extends JPanel{
    int tick;
    int[] crossY;
    int lightY;
    int nCross;
    int carX;
    int carY;
    int changeX;
    int changeY;
    int score;
    int rating;
    int[] enemyY;
    int[] enemyX;
    int[] enemySpeed;
    int nEnemy;
    int enemyMaxCount;
    boolean isFinished;

    Socket socket;
    BufferedWriter out;
    BufferedReader in;

    public Race(){
        try {

            socket = new Socket("localhost", 1234);

            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        setFocusable(true);
        crossY = new int []{-500,-1500,-2500};
        nCross = 0;
        lightY = -294;

        carX = 212;
        carY = 350;
        changeX = changeY = 0;

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {moveCar(e);}
            @Override
            public void keyReleased(KeyEvent e) {stopCar(e);}
        });

        enemyMaxCount = 4;
        enemyX = new int[]{0, 0, 0, 0};
        enemyY = new int[]{-130,-130,-130,-130};
        enemySpeed = new int[]{0, 0, 0, 0};
        nEnemy = 0;

        score = 0;
        isFinished = false;

    }

    public void paint(Graphics g){

        Graphics2D obj = (Graphics2D) g;
        super.paint(obj);
        obj.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try{
            obj.drawImage(getToolkit().getImage("src/Images/st_road.png"), 0, 0 ,this);
            for(int i=0;i<3;i++){
                obj.drawImage(getToolkit().getImage("src/Images/crossroad_"+(i+1)+".png"),0,crossY[i],this);
            }
            obj.drawImage(getToolkit().getImage("src/Images/car_self.png"),carX,carY,this);
            for(int i=0;i<enemyMaxCount;i++){
                obj.drawImage(getToolkit().getImage("src/Images/car_"+(i+1)+".png"),enemyX[i],enemyY[i],this);
            }
            obj.drawImage(getToolkit().getImage("src/Images/trafficLight.png"),151,lightY,this);
            if(isFinished)obj.drawImage(getToolkit().getImage("src/Images/boom.png"),carX-70,carY-70,this);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void gameTick(){
        carX+=changeX;
        carY+=changeY;

        for(int i = 0;i<3;i++){
            crossY[i]++;
            if(crossY[i]==500)crossY[i]=-2500;
        }
        lightY++;
        if(lightY==706)lightY=-1294;

        for(int i=0;i<enemyMaxCount;i++){
            enemyY[i] += enemySpeed[i];
        }

        if(carY < 0)
            carY = 0;
        if(carY >= 370)
            carY = 370;
        if(carX <= 115)
            carX = 115;
        if(carX >= 315)
            carX = 315;

        for(int i=0;i<enemyMaxCount;i++){
            if((enemyX[i] >= carX && enemyX[i] <= carX+39) || (enemyX[i]+39 >= carX && enemyX[i]+39 <= carX+39)){
                if(carY+89 >= enemyY[i] && !(carY >= enemyY[i]+88)){
                    finish();
                }
            }
        }
    }
    public void spawnEnemy(){
            enemyY[nEnemy] = -130;
            int cur = (int) (Math.random() * 100) % 4;
            switch (cur) {
                case 0 -> enemyX[nEnemy] = 250;
                case 1 -> enemyX[nEnemy] = 300;
                case 2 -> enemyX[nEnemy] = 185;
                default -> enemyX[nEnemy] = 130;
            }
            enemySpeed[nEnemy] = (int) (Math.random() * 100) % 2 + 2;
            nEnemy++;
            score++;
            if (nEnemy == enemyMaxCount) nEnemy = 0;
    }
    private void restart(){
        isFinished = false;
        score = 0;
        carX = 212;
        carY = 350;
        changeX = changeY = 0;

        enemyX = new int[]{0, 0, 0, 0};
        enemyY = new int[]{-130,-130,-130,-130};
        enemySpeed = new int[]{0, 0, 0, 0};
        nEnemy = 0;
        tick = 0;
    }
    private void finish(){
        isFinished = true;
        repaint();

        try {
            out.write(""+score);
            out.newLine();
            out.flush();

            rating = Integer.parseInt(in.readLine());

            Object[] options = { "Restart", "Exit Game" };
            String message;
            if(rating == 1){message = "Congratulations, you're top 1 now!!!";}
            else message = "Rating: "+rating;
            int option = JOptionPane.showOptionDialog(this, "Score: "+score+"\n"+message, "Result",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            if(option == 1){
                out.write("exit");
                out.newLine();
                out.flush();

                in.close();
                out.close();
                socket.close();
                System.exit(0);
            }else{
                restart();
            }


        }catch (IOException e){
            e.printStackTrace();
        }


    }
    private void moveCar(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_UP){
            changeY = -1;
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            changeY = 2;
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            changeX = 1;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            changeX = -1;
        }
    }
    private void stopCar(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_UP){
            changeY = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            changeY = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            changeX = 0;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            changeX = 0;
        }
    }
    public void start(){
        for(tick = 0;;tick++){
            gameTick();
            repaint();
            try {
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(tick%150==0&&tick>250){
                spawnEnemy();
            }
        }
    }


    public static void main(String[] args) {
        JFrame fr = new JFrame("Car Game");
        Race race = new Race();

        fr.add(race);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setSize(500, 500);
        fr.setLocationRelativeTo(null);
        fr.setVisible(true);

        race.start();
    }
}
