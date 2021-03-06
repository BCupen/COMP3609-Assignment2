import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.sound.sampled.FloatControl;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayList;

public class Player {
    private GamePanel panel;
    private int x, y;
    private int width, height;

    private int dx, dy;

    private int numLives;

    private Image playerIdle;
    public static Animation playerShoot;
    public static Animation playerWalkRight;
    public static Animation playerWalkLeft;
    public static Animation flickeringCyborg;

    public static boolean isShooting;
    public static boolean isWalking;
    public static boolean isHurt; 


    public Player(GamePanel p){
        panel = p;
        x = 50;
        y = 500;
        playerIdle = ImageManager.loadImage("images/Cyborg_idle.png");
        width = playerIdle.getWidth(null);
        height = playerIdle.getHeight(null);

        playerShoot = new Animation(panel, width+10, height, true);
        playerWalkRight = new Animation(panel, width+10, height, false);
        playerWalkLeft = new Animation(panel, width+10, height, false);

        flickeringCyborg = new Animation(panel, width+10, height, true); // bug or feature? 
                                                                        // when set to true animation does not play

        loadAnimationFrames(playerShoot,"images/Cyborg_attack3.png", 8, false);
        loadAnimationFrames(playerWalkRight, "images/Cyborg_run.png", 6, false);
        loadAnimationFrames(playerWalkLeft,"images/Cyborg_run_left.png", 6, true);
        loadGrayscaleAnimation(flickeringCyborg, "images/Cyborg_idle.png", 8);
        
        isShooting = false;
        isWalking = false;
        isHurt = false;

        dx = 10;
        dy = 20;

        numLives=3;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getPWidth(){
        return width;
    }

    public int getPHeight(){
        return height;
    }

    public void setIsShooting(boolean value){
        isShooting = value;
    }

    public void setIsWalking(boolean value){
        isWalking = value;
    }

    public void increaseLives(){
        if(numLives < 3)
            numLives++;
    }

    public void decreaseLives(){
        numLives--;
    }

    public int getLives(){
        return numLives;
    }

    //load frames from stripFiles
    public void loadAnimationFrames(Animation animation,String path, int amt, boolean loadReverse){  //loading images from strip file
        //load attack animation
        Image stripImage = ImageManager.loadImage(path);
        int imageWidth = (int) stripImage.getWidth(null) / amt;
        int imageHeight = stripImage.getHeight(null);

        if(!loadReverse){
           for(int i=0; i < amt; i++){
            BufferedImage frameImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) frameImage.getGraphics();

            g.drawImage(stripImage, 0, 0, imageWidth, imageHeight, i*imageWidth, 0, (i*imageWidth)+imageWidth, imageHeight, null);
        
            animation.addFrame(frameImage, 100);
            } 
        }else{
            for(int i=amt-1; i >= 0; i--){
                BufferedImage frameImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D) frameImage.getGraphics();
    
                g.drawImage(stripImage, 0, 0, imageWidth, imageHeight, i*imageWidth, 0, (i*imageWidth)+imageWidth, imageHeight, null);
            
                animation.addFrame(frameImage, 150);
            } 
        }
    }

    public void loadGrayscaleAnimation(Animation animation,String path, int amt) {

		BufferedImage image = ImageManager.loadBufferedImage("images/Cyborg_idle.png");
        BufferedImage copy;

		for (int i=0; i<amt; i++) {
            copy = ImageManager.copyImage(image);
            
            if (i%2 == 0){
                copy = GrayScaleFX.toGrayScaleFX(copy);
            }
            animation.addFrame(copy, 300);
		}
	}

    public void updateAnimation(){
        if(isShooting)
            playerShoot.update();

        if(!playerShoot.isActive())
            isShooting = false;
            
        if(isWalking){
            if(dx < 0)
                playerWalkLeft.update();
            else if(dx > 0)
                playerWalkRight.update();
        }

        if(isHurt)
            flickeringCyborg.update();
        
        if(!flickeringCyborg.isActive())
            isHurt = false;

    }

    public void draw(Graphics g2){
        if(isShooting){
            playerShoot.draw((Graphics2D) g2, x, y);
            //g2.drawImage(playerIdle, x+50, y, width, height, null);
        }else if(isWalking && dx > 0){
            playerWalkRight.draw((Graphics2D) g2, x, y);
        }else if(isWalking && dx < 0){
            playerWalkLeft.draw((Graphics2D) g2, x, y);
        }
        else if(isHurt){
            flickeringCyborg.draw((Graphics2D) g2, x, y);
            // g2.drawImage(playerIdle, x, y, width, height, null); 
        }

        else{
           g2.drawImage(playerIdle, x, y, width, height, null); 
        }    
    }

    public void move(int direction){
        if(!panel.isVisible()) return;

        //playerShoot.update();

        if(direction == 1){ 
            y -= dy;
        }
        if(direction == 2){
            y += dy;
        }

        if(isWalking){
            if(direction == 3){
                if(dx > 0)
                    dx = -dx;
            }
            if(direction == 4){
                if(dx < 0)
                    dx = -dx;
            }
            x += dx;
        }

        // stay within the road
        if(y+height < 355){
            y = 360 - height;
        }else if(y + height > 550){
            y = 550 - height;
        }
        if(x < 0){
            x = 0;
        }else if(x+width > 650){
            x = 650 - width;
        }
    }

    public Rectangle2D.Double getBoundingRectangle(){
        return new Rectangle2D.Double(x, y, width, height);
    }

    public boolean collidesWithEnemy(Enemy e){
        Rectangle2D.Double myRect = getBoundingRectangle();
        Rectangle2D.Double enemyRect = e.getBoundingRectangle();

        return myRect.intersects(enemyRect);
    }

}
