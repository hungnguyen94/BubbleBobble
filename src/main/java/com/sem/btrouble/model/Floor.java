package com.sem.btrouble.model;

import org.newdawn.slick.geom.Rectangle;

/**
 * Class representing a floor
 */
@SuppressWarnings("serial")
public class Floor extends Rectangle {

    private int speed;

    /**
     * Constructor for floor class
     * @param x - X position of the floor
     * @param y - Y position of the floor
     * @param width - width of the floor
     * @param height - height of the floor
     */
    public Floor(float x, float y, float width, float height) {
        super(x, y, width, height);
        speed = 1;
    }

    /**
     * Move the floor to the right
     */
    public void moveRight() {
        x += speed;
    }

    /**
     * Move the floor to the left
     */
    public void moveLeft() {
        x -= speed;
    }

    /**
     * Move the floor up
     */
    public void moveUp() {
        y += speed;
    }

    /**
     * Move the floor down
     */
    public void moveDown() {
        y -= speed;
    }
}
