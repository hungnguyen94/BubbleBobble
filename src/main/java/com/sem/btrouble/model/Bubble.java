package com.sem.btrouble.model;

import com.sem.btrouble.controller.Collidable;
import com.sem.btrouble.controller.CollisionAction;
import com.sem.btrouble.controller.CollisionHandler;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bubble is a model, which represents the bubbles in the game.
 *
 */
public class Bubble extends Circle implements Drawable, Movable, Collidable {

    private static final long serialVersionUID = 1L;
    private static final float GRAVITY = .4f;
    private static final float INITIAL_HORIZONTAL_SPEED = 3f;
    private static final int HIT_SPEED_FACTOR = 30;
    private static final int BUBBLE_SCORE = 1000;
    private static final float GAME_SIZE = 10f;

    private int size;
    private float velocityX;
    private float velocityY;

    private float accelerationY;

    private boolean collided;

    /**
     * Bubble class, containing all the data about the bubble.
     *
     * @param size
     *            of the bubble.
     * @param xpos
     *            horizontal starting position of the bubble in the room
     * @param ypos
     *            vertical starting position of the bubble in the room
     */
    public Bubble(int size, float xpos, float ypos) {
        super(xpos, ypos, size * GAME_SIZE);
        this.size = size;
        this.accelerationY = GRAVITY;
        this.velocityX = INITIAL_HORIZONTAL_SPEED;
        this.collided = false;
    }

    /**
     * Bubble constructor with the ability to give initial speed as a variable.
     *
     * @param size
     *            of the bubble.
     * @param xpos
     *            horizontal starting position of the bubble in the room
     * @param ypos
     *            vertical starting position of the bubble in the room
     * @param velocityX
     *            horizontal starting speed of the bubble
     * @param velocityY
     *            vertical starting speed of the bubble
     */
    public Bubble(int size, float xpos, float ypos, float velocityX, float velocityY) {
        super(xpos, ypos, size * GAME_SIZE);
        this.size = size;
        this.accelerationY = GRAVITY;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.collided = false;
    }

    /**
     * Returns the size of the bubble in steps.
     * 
     * @return returns an integer representing the size of the bubble in steps.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the velocity of the bubble in x direction.
     * 
     * @return returns a float representing the velocity of the bubble in x
     *         direction.
     */
    public float getVelocityX() {
        return velocityX;
    }

    /**
     * Returns the velocity of the bubble in y direction.
     * 
     * @return returns a float representing the velocity of the bubble in y
     *         direction.
     */
    public float getVelocityY() {
        return velocityY;
    }

    /**
     * Slows the bubble down.
     */
    public void slowBubble() {
        this.accelerationY = (float) (this.getAccelerationY() * .9);
    }

    /**
     * Sets the acceleration of the bubble in y direction.
     * 
     * @param accelerationY
     *            should be an integer representing the acceleration of the
     *            bubble in y direction.
     */
    public void setAccelerationY(float accelerationY) {
        this.accelerationY = accelerationY;
    }

    /**
     * Returns the acceleration of the bubble in y direction.
     * 
     * @return returns a double representing the acceleration of the bubble in y
     *         direction.
     */
    public float getAccelerationY() {
        return accelerationY;
    }

    /**
     * Returns the score given to the player when this bubble is hit.
     * 
     * @return The score of this bubble.
     */
    public static int getBubbleScore() {
        return BUBBLE_SCORE;
    }

    /**
     * Calculates the next location of the Bubble.
     */
    public void move() {
        if (!collided) {
            this.velocityY += accelerationY;
            float newX = getCenterX() + velocityX;
            float newY = getCenterY() + velocityY;
            setCenterX(newX);
            setCenterY(newY);
        }
    }

    /**
     * Splits the bubble in two with a smaller size of each.
     * 
     * @return List of the splitted bubble.
     */
    public List<Bubble> split() {
        size--;
        setRadius(size * GAME_SIZE);
        List<Bubble> bubbleList = new ArrayList<>();
        if (size > 0) {
            velocityY = -Math.abs(accelerationY) * HIT_SPEED_FACTOR;
            velocityX = Math.abs(velocityX);
            Bubble leftBubble = new Bubble(size, x - 20, y, -velocityX, velocityY);
            Bubble rightBubble = new Bubble(size, x + 20, y, velocityX, velocityY);
            bubbleList.add(leftBubble);
            bubbleList.add(rightBubble);
        }
        return bubbleList;
    }

    /**
     * Checks whether the provided Object is the same as this Bubble.
     * 
     * @param other
     *            should be the Object to be checked for equality.
     * @return returns a boolean representing whether the provided Object is the
     *         same as this Bubble.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Bubble) {
            Bubble that = (Bubble) other;
            return this.size == that.size && Math.abs(this.x - that.x) == 0
                    && Math.abs(this.y - that.y) == 0
                    && Math.abs(this.velocityX - that.velocityX) == 0
                    && Math.abs(this.velocityY - that.velocityY) == 0;
        }
        return false;
    }

    private final int arbitraryconstant = 42;

    @Override
    public int hashCode() {
        return arbitraryconstant;
    }

    /**
     * Invert the y direction on collision.
     */
    public void bounceY() {
        velocityY = -velocityY;
    }

    /**
     * Invert the x direction on collision.
     */
    public void bounceX() {
        velocityX = -velocityX;
    }

    /**
     * Bounce to left direction on collision.
     *
     */
    public void bounceXLeft() {
        velocityX = -Math.abs(velocityX);
    }

    /**
     * Bounce to right direction on collision.
     *
     */
    public void bounceXRight() {
        velocityX = Math.abs(velocityX);
    }

    /**
     * Bounce to up direction on collision.
     */
    public void bounceYUp() {
        velocityY = -Math.abs(velocityY);
    }

    /**
     * Bounce to down direction on collision.
     */
    public void bounceYDown() {
        velocityY = Math.abs(velocityY);
    }

    private final int bounceconstant = 11;

    /**
     * Bounce up on collision with floor.
     */
    public void bounceYFloor() {
        velocityY = -Math.abs(bounceconstant + 2 * (size));
    }

    @Override
    public String toString() {
        return "Bubble{" + "size=" + size + ", x=" + x + ", y=" + y + ", velocityX=" + velocityX
                + ", velocityY=" + velocityY + ", accelerationY=" + accelerationY + '}';
    }

    /**
     * Draws the object.
     * 
     * @param graphics
     *            the graphics
     */
    @Override
    public void draw(Graphics graphics) {
        if (!collided) {
            graphics.setColor(Color.black);
            graphics.fill(this);
            graphics.draw(this);
        }
    }

    /**
     * Every collidable should return a Map with all CollisionActions that
     * collidable should process. To prevent class checking, simply use the
     * class as the key, and a CollisionAction instance as value.
     * 
     * @return A map of all actions this collidable can do on a collision.
     */
    public Map<Class<? extends Collidable>, CollisionAction> getCollideActions() {
        Map<Class<? extends Collidable>, CollisionAction> collisionActionMap = new HashMap<>();

        // Method called on Wall collision
        collisionActionMap.put(Wall.class, new WallCollision());

        // Method called on Floor collision
        collisionActionMap.put(Floor.class, new FloorCollision());

        // Method called on Bubble collision
        collisionActionMap.put(Bubble.class, new BubbleCollision());

        // Method called on Rope collision
        collisionActionMap.put(Rope.class, new RopeCollision());

        // Method called on Rope collision
        collisionActionMap.put(StayRope.class, new RopeCollision());

        return collisionActionMap;
    }

    /**
     * This method is to check if a collidable should be removed from the level.
     * If this method returns true, it will be removed.
     *
     * @return True if object should be removed.
     */
    @Override
    public boolean getCollidedStatus() {
        return collided;
    }

    /**
     * Class to method on collision with Wall.
     */
    private class WallCollision implements CollisionAction {
        @Override
        public void onCollision(Collidable wall) {
            switch (CollisionHandler.checkCollisionSideX(Bubble.this, wall)) {
            case LEFT:
                bounceXLeft();
                break;
            case RIGHT:
                bounceXRight();
                break;
            default:
                bounceX();
                break;
            }
        }
    }

    /**
     * Class to call method on collision with Floor.
     */
    private class FloorCollision implements CollisionAction {
        @Override
        public void onCollision(Collidable floor) {
            switch (CollisionHandler.checkCollisionSideY(Bubble.this, floor)) {
            case TOP:
                bounceYFloor();
                break;
            case BOTTOM:
                bounceY();
                break;
            default:
                break;
            }
        }
    }

    /**
     * Class to call method on collision with bubble.
     */
    private class BubbleCollision implements CollisionAction {
        @Override
        public void onCollision(Collidable b) {
            switch (CollisionHandler.checkCollisionSideX(Bubble.this, b)) {
            case LEFT:
                bounceXLeft();
                break;
            case RIGHT:
                bounceXRight();
                break;
            default:
                break;
            }
            switch (CollisionHandler.checkCollisionSideY(Bubble.this, b)) {
            case TOP:
                bounceYUp();
                break;
            case BOTTOM:
                bounceYDown();
                break;
            default:
                break;
            }
        }
    }

    /**
     * Class to call method on collision with rope.
     */
    private class RopeCollision implements CollisionAction {
        @Override
        public void onCollision(Collidable collider) {
            collided = true;
        }
    }

    /**
     * Checks for intersection with another Collidable.
     * 
     * @param collidable
     *            Check if this collidable intersects with that collidable.
     * @return True if this object intersects with collidable.
     */
    @Override
    public boolean intersectsCollidable(Collidable collidable) {
        return intersects((Shape) collidable);
    }
}
