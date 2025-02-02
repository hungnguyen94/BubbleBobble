package com.sem.btrouble.game;

import com.sem.btrouble.model.Player;
import com.sem.btrouble.model.Room;
import com.sem.btrouble.observering.LevelObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractGame class.
 */
public class MultiPlayerGame extends AbstractGame {
    private List<Player> players;

    /**
     * Constructor for a game.
     * @param room The room to play
     */
    public MultiPlayerGame(Room room) {
        super(room);
        players = new ArrayList<>();
    }

    /**
     * Constructor for a game.
     * @param room Room.
     * @param view View connected to this controller.
     */
    public MultiPlayerGame(Room room, LevelObserver view) {
        super(room, view);
        players = new ArrayList<>();
    }

    /**
     * Start the level.
     */
    @Override
    public void startLevel() {
        getLevelTimer().start();
        getLevel().start();
    }

    /**
     * Code in this method is run in the game loop.
     */
    @Override
    public void runGameLoop() {

    }

    /**
     * This method adds a player to the game.
     *
     * @param player This player will be added.
     */
    @Override
    public void addPlayer(Player player) {
        players.add(player);
        getLevel().addPlayer(player);
    }

    /**
     * Returns true if theres a player with
     * a life left.
     * @return
     */
    private boolean anyPlayerHasLife() {
        for(Player player : players) {
            if(player.hasLives()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is called when a level is won.
     */
    @Override
    public void levelWon() {
        getLevel().stop();
        getLevelTimer().stop();
        System.out.println("Level won");
    }

    /**
     * This method is called when a level is lost.
     */
    @Override
    public void levelLost() {
        getLevel().stop();
        getLevelTimer().stop();
        if(anyPlayerHasLife()) {
            System.out.println("Level restart");
        } else {
            // End game.
            System.out.println("Game has ended");
        }
    }
}
