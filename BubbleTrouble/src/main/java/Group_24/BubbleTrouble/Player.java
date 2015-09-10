package Group_24.BubbleTrouble;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import java.util.ArrayList;

/**
 * Player class, containing all the data about the player.
 *
 */
public class Player extends Rectangle {
    private int dx;
    private int lives;
    private int score;

    private SpriteSheet walkSheet;
    private Animation walkAnimation;
    private Image playerIdle;
    private boolean facingLeft = true;
    private boolean idle = true;
    
    private static final int PLAYER_SPEED = 5;
    private static final int INITIAL_LIVES = 2;
    private static final int INITIAL_SCORE = 0;

    private ArrayList<Rope> ropes;

    /**
     * Constructor for the Player class.
     * @param x x value for the Player from the sprite class.
     * @param y y value for the Player from the sprite class.
     */
    public Player(float x, float y) throws SlickException {
        super(x, y, 100f, 175f);
        playerIdle = new Image("Sprites/idle.png");
        walkSheet = new SpriteSheet("Sprites/walkAnimation.png", 100, 175);
        walkAnimation = new Animation(walkSheet, 100);
        ropes = new ArrayList<Rope>();
        lives = INITIAL_LIVES;
        score = INITIAL_SCORE;
    }

    public ArrayList<Rope> getRopes() {
        return ropes;
    }

    public void addLife(){
        lives ++;
    }

    public void loseLife(){
        lives --;
    }

    public boolean hasLives(){
        return lives >= 0;
    }

    public int getLives(){
        return lives;
    }

    public int getScore() {
        return score;
    }

    /**
     * @return the dx
     */
    public int getDx() {
        return dx;
    }

    /**
     * @param dx the dx to set
     */
    public void setDx(int dx) {
        this.dx = dx;
    }

    public void increaseScore(int amount) {
        score += amount;
    }

    /**
     * Function which allows the player to fire.
     * @throws SlickException 
     */
    public void fire() throws SlickException {
        float yvalue = super.getY();

        for (int i = 32; i < 321; i += 32) {
            ropes.add(new Rope((getX() + getWidth() / 2), yvalue));
            yvalue += 32;
        }
    }

    public void draw() throws SlickException {
      if (!idle) {
          walkAnimation.getCurrentFrame().getFlippedCopy(facingLeft, false).draw(x, y);
      } else {
          playerIdle.getFlippedCopy(facingLeft, false).draw(x, y);
      }
      for (int i = 0; i < ropes.size(); i++) {
        ropes.get(i).draw();
      }
    }
    
    public void move(GameContainer container, int delta) throws SlickException {
      Input input = container.getInput();
      
//      for(Floor floor: Model.getCurrentRoom().getFloors()) {
//        if(!this.collidesWith(floor))
//            y += 1;
//      }
      
      if (input.isKeyDown(Input.KEY_LEFT))
      {
        idle = false;
        facingLeft = true;
        walkAnimation.update(delta);
          super.setX(super.getX() - delta * 0.15f);
      }
      else if (input.isKeyDown(Input.KEY_RIGHT))
      {
        idle = false;
        facingLeft = false;
        walkAnimation.update(delta);
          super.setX(super.getX() + delta * 0.15f);
      } 
      else if (input.isKeyPressed(Input.KEY_SPACE))
      {
        idle = true;
        fire();
      } else {
        idle = true;
      }
    }

    /**
     * This functions deletes all the rope elements from the room.
     */
    public void resetRope() {
        ropes = new ArrayList<Rope>();
    }

    public void moveTo(int x) {
        super.setX(x);
        this.dx = 0;
    }
}
