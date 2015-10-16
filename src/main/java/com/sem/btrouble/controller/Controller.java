package com.sem.btrouble.controller;

import com.sem.btrouble.SlickApp;
import com.sem.btrouble.event.ControllerEvent;
import com.sem.btrouble.event.GameEvent;
import com.sem.btrouble.event.PlayerEvent;
import com.sem.btrouble.model.Bubble;
import com.sem.btrouble.model.Model;
import com.sem.btrouble.model.Player;
import com.sem.btrouble.model.PowerUp;
import com.sem.btrouble.model.Rope;
import com.sem.btrouble.model.Timers;
import com.sem.btrouble.observering.EventObserver;
import com.sem.btrouble.observering.EventSubject;
import com.sem.btrouble.observering.LevelObserver;
import com.sem.btrouble.observering.LevelSubject;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller, recalculates the Model, on request of the view.
 */
public class Controller implements EventSubject, LevelSubject {

    private GameContainer gc;
    private CollisionHandler collisionHandler;
    private int timeLeft;
    private List<EventObserver> observers;
    private List<LevelObserver> levelObservers;

    /**
     * Starts a new game by loading data into the room and adding the players.
     * 
     * @param container
     *            GameContainer from Slick2D
     * @param sbg
     *            State of the game
     * @throws SlickException
     *             Throws exception on error
     */
    public Controller(GameContainer container, StateBasedGame sbg) throws SlickException {
        this.gc = container;
        this.observers = new ArrayList<EventObserver>();
        this.levelObservers = new ArrayList<LevelObserver>();

        collisionHandler = new CollisionHandler();

        Model.init(SlickApp.SCREEN_WIDTH, SlickApp.SCREEN_HEIGHT);
        Player p = new Player(0, 0);
        Model.addPlayer(p);
        collisionHandler.addCollidable(p);
        restartRoom();
    }

    /**
     * Returns the timers.
     * 
     * @return timer
     */
    public Timers getTimers() {
        return Model.getTimers();
    }

    /**
     * Updates the model, should be done on request of the view.
     * 
     * @param delta
     *            milliseconds between frames
     * @throws SlickException
     *             throw error on exception
     */
    public void update(int delta) throws SlickException {
        collisionHandler.checkCollision(Model.getCurrentRoom().getBubbles());

        for (Player player : Model.getPlayers()) {
            if (!collisionHandler.checkCollision(player)) {
                player.setFalling(true);
            }

            if (!player.isAlive()) {
                // Update observers.
                notifyObserver();
                loseLife(player);
            }
            collisionHandler.checkCollision(player.getRopes());
        }

        // Check if timer has run out.
        if (this.getTimers().getLevelTimeLeft() <= 0) {
            fireEvent(new ControllerEvent(this, ControllerEvent.OUTOFTIME, "Out of time"));
            // Update observers.
            notifyObserver();
            for (Player p : Model.getPlayers()) {
                loseLife(p);
            }
        }

        List<PowerUp> powers = Model.getShortPower();
        if (powers.size() > 0) {
            for (PowerUp power : powers) {
                if (collisionHandler.checkCollision(power)) {
                    if (timeLeft >= getTimers().getLevelTimeLeft() + 30000) {
                        Model.deleteShortPower(power);
                    }
                }
                power.move();
                if (!collisionHandler.checkCollision(power)) {
                    timeLeft = getTimers().getLevelTimeLeft();
                }
            }
        }

        for (Player p : Model.getPlayers()) {
            p.move();
        }
        // Update observers.
        notifyObserver();

        processInput(delta);
        // calculate movements
        updateRopes();
        updateBubble();
    }

    private static final double ROPE_OFFSET = 0.9;

    /**
     * Move the player on key presses.
     *
     * @param delta
     *            milliseconds between frames
     */
    public void processInput(int delta) {
        Input input = gc.getInput();
        Player p1 = Model.getPlayers().get(0);

        if (input.isKeyDown(Input.KEY_LEFT)) {
            p1.moveLeft(delta);
        } else if (input.isKeyDown(Input.KEY_RIGHT)) {
            p1.moveRight(delta);
        }

        if (input.isKeyPressed(Input.KEY_SPACE)) {
            Rope r = new Rope(p1.getX() + (int) (p1.getWidth() / 2),
                    (float) (p1.getY() + p1.getHeight() * ROPE_OFFSET));
            if (p1.fire(r)) {
                collisionHandler.addCollidable(r);
                fireEvent(new PlayerEvent(p1, PlayerEvent.SHOOT, "Shot a rope"));
            }
        }
    }

    /**
     * Lets the player lose a life.
     *
     * @param player
     *            should be the player who lost a life
     */
    public void loseLife(Player player) {
        fireEvent(new PlayerEvent(player, PlayerEvent.LIFE_LOST, "Lost a life"));
        player.loseLife();
        if (!player.hasLives()) {
            endGame("Game over...");
        } else {
            collisionHandler.removeCollidable(Model.getCurrentRoom().getCollidables());
            // Hardcoded player 1
            collisionHandler.removeCollidable(Model.getPlayers().get(0).getRopes());
            restartRoom();
            player.setAlive(true);
        }
    }

    private void restartRoom() {
        fireEvent(new ControllerEvent(this, ControllerEvent.RESTARTROOM, "Room restarted"));
        Model.restartRoom();
        List<PowerUp> powers = Model.getPowerUps();
        for (PowerUp power: powers) {
            power.reset();
        }
        collisionHandler.addCollidable(Model.getCurrentRoom().getCollidables());
    }

    /**
     * Updates the bubble, checks whether bubbles should be removed or not, and
     * calculates the new position of each bubble.
     */
    private void updateBubble() {
        if (!Model.getCurrentRoom().hasBubbles()) {
            collisionHandler.removeCollidable(Model.getCurrentRoom().getCollidables());
            Model.getNextRoom();
            restartRoom();
            //sbg.enterState(2, new FadeOutTransition(), new FadeInTransition());
        }
        Model.getCurrentRoom().moveBubbles();
    }

    /**
     * Adds a bubble to the game.
     *
     * @param bubble
     *            Bubble to add to the room
     */
    public void addBubble(Bubble bubble) {
        Model.getCurrentRoom().addBubble(bubble);
        collisionHandler.addCollidable(bubble);
    }

    /**
     * Removes a bubble from the game.
     *
     * @param bubble
     *            the bubble to remove
     */
    public void removeBubble(Bubble bubble) {
        Model.getCurrentRoom().removeBubble(bubble);
        collisionHandler.removeCollidable(bubble);
    }

    /**
     * Updates the ropes, calculates the new position of the rope and removes
     * the rope if it hits the ceiling.
     */
    private void updateRopes() throws SlickException {
        for (Player player : Model.getPlayers()) {
            player.moveRopes();
            collisionHandler.removeCollidable(player.removeCollidedRopes());
        }
    }

    /**
     * Ends the game by stopping the view, shows a message.
     *
     * @param message
     *            should be a String containing the message which is shown.
     */
    public void endGame(String message) {
        fireEvent(new ControllerEvent(this, ControllerEvent.GAMEOVER, message));
        gc.exit();
    }

    @Override
    public void fireEvent(GameEvent gameEvent) {
        for (EventObserver observer : observers) {
            observer.update(gameEvent);
        }
    }

    @Override
    public void registerObserver(EventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(EventObserver observer) {
        observers.remove(observer);
    }

    /**
     * Register an observer to the subject.
     *
     * @param observer Observer to be added.
     */
    @Override
    public void registerObserver(LevelObserver observer) {
        if(observer == null || levelObservers.contains(observer)) {
            return;
        }
        levelObservers.add(observer);
    }

    /**
     * Remove an observer from the observers list.
     *
     * @param observer Observer to be removed.
     */
    @Override
    public void removeObserver(LevelObserver observer) {
        levelObservers.remove(observer);
    }

    /**
     * Method to notify the observers about a change.
     */
    @Override
    public void notifyObserver() {
        for(LevelObserver levelObserver : levelObservers) {
            levelObserver.update(this, collisionHandler);
        }
        if(!Model.getCurrentRoom().hasBubbles()) {
            for(LevelObserver levelObserver : levelObservers) {
                levelObserver.levelWon();
            }
        }

        // Hardcoded player 1.
        if(!Model.getPlayers().get(0).isAlive()) {
            for(LevelObserver levelObserver : levelObservers) {
                levelObserver.levelLost();
            }
        }

        if(getTimers().getLevelTimeLeft() <= 0) {
            for(LevelObserver levelObserver : levelObservers) {
                levelObserver.levelLost();
            }
        }

    }
}
