package Group_24.BubbleTrouble;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Controller, recalculates the Model, on request of the view.
 *
 */
public class Controller {
	
	private static final int REWARD_BUBBLE = 100;
	private static View view;
	private static ArrayList<Bubble> newBubbles;
    private static ArrayList<Bubble> oldBubbles;

    /**
     * Starts a new game by loading data into the room and adding the players.
     */
    public static void startNewGame(){
    	newBubbles = new ArrayList<Bubble>();
		oldBubbles = new ArrayList<Bubble>();
    	
    	// checks if the view is inactive
    	if(view == null || !view.isActive()){
    		view = new View();
    		
    		// TODO could add import RoomData from file
    		Model.init();
    		ArrayList<Bubble> bubbles = new ArrayList<Bubble>();
    		bubbles.add(new Bubble(3, 100, 50));
    		RoomData data = new RoomData(bubbles);
    		Model.addRoom(new Room(data));
    		
    		Model.addPlayer(new Player(100, Model.getRoomHeight()/2));
    	}
	}
	
    /**
     * Returns the current view
     * @return returns the current view
     */
	public static View getView() {
		return view;
	}
	
	/**
	 * Updates the model, should be done on request of the view.
	 */
	public static void update(){
		for(Bubble bubble: Model.getBubbles()) {
			
			for(Floor floor: Model.getCurrentRoom().getFloors())
				if(bubble.getY() + bubble.getWidth() >= floor.getY())
					bubble.collide(CollisionEvent.TYPE_FLOOR);
			
            // CollisionEvent detection for bubble against floor
            if (bubble.getY() + bubble.getWidth() >= Model.getRoomHeight()) {
                bubble.collide(CollisionEvent.TYPE_FLOOR);
            }
            
            // Collision detection for walls
            for(Wall wall: Model.getCurrentRoom().getWalls()) {
            	if(bubble.getX() <= (wall.getX() + wall.getWidth()))
            		bubble.collide(CollisionEvent.TYPE_WALL);
            	if(bubble.getX() + bubble.getWidth() >= wall.getX())
            		bubble.collide(CollisionEvent.TYPE_WALL);
            }
            
            // CollisionEvent detection for bubble against room left
            if (bubble.getX() <= 0) {
                bubble.collide(CollisionEvent.TYPE_WALL);
            }
            // CollisionEvent detection for bubble against room right
            if (bubble.getX() + bubble.getWidth() >= Model.getRoomWidth()) {
                bubble.collide(CollisionEvent.TYPE_WALL);
            }
            
            for(Player player : Model.getPlayers()){
            	
            	// Collision detection on walls.
            	for(Wall wall: Model.getCurrentRoom().getWalls()) {
            		if(player.collidesWith(wall)) 
            			player.setDx(-1*player.getDx());
            	}
            	
	            // CollisionEvent detection for bubble against player
	            if (player.collidesWith(bubble)) {
	                loseLife(player);
	            }
	
	            for (Rope rope : player.getRopes()) {
	                if (bubble.collidesWith(rope)) {
	                    bubble.collide(CollisionEvent.TYPE_ROPE);
	                    player.increaseScore(REWARD_BUBBLE);
	                    player.resetRope();
	                }
	            }
            }
        }
        
        // calculate movements
        updateRopes();
    	updateBubble();
    	updatePlayer();
	}
	
	/**
	 * Lets the player lose a life
	 * @param player should be the player who lost a life
	 */
	private static void loseLife(Player player) {
		player.loseLife();
        if(!player.hasLives()){
        	endGame("Game over...");
        } else {
        	Model.restartRoom();
        	player.resetRope();
        	view.start();
        }
		
	}
	
	/**
	 * Updates the bubble, checks whether bubbles should be removed or not, and calculates the new position of each bubble.
	 */
	private static void updateBubble() {
        for(Bubble bubble: newBubbles) {
            Model.getBubbles().add(bubble);
        }
        newBubbles.clear();
        for(Bubble bubble: oldBubbles) {
        	Model.getBubbles().remove(bubble);
        }
        oldBubbles.clear();
        if (Model.getBubbles().isEmpty()) endGame("You won the game!");
        for(Bubble bubble: Model.getBubbles())
            bubble.move();
    }

    /**
     * adds a bubble to the game
     * @param size size of the bubble
     * @param x horizontal position of the bubble
     * @param y vertical position of the bubble
     * @param vx horizontal speed of the bubble
     * @param vy vertical speed of the bubble
     */
    public static void addBubble(int size, int x, int y, double vx, double vy) {
        Bubble newBubble = new Bubble(size, x, y, vx, vy);
        newBubbles.add(newBubble);
    }

    /**
     * removes a bubble from the game
     * @param bubble the bubble to remove
     */
    public static void removeBubble(Bubble bubble) {
        oldBubbles.add(bubble);
    }
	
    /**
     * Updates the ropes, calculates the new position of the rope and removes the rope if it hits the ceiling. 
     */
    private static void updateRopes() {
    	for(Player player: Model.getPlayers()){
	        ArrayList<Rope> ropes = player.getRopes();
	
	        for (int i = 0; i < ropes.size(); i++) {
	
	            Rope rope = (Rope) ropes.get(i);
	
	            if (rope.isVisible()) {
	            	rope.move();
	            } else {
	                ropes.clear();
	            }
	        }
    	}
    }
    
    /**
     * Updates the player, calculates the new position.
     */
    private static void updatePlayer() {
        for(Player player: Model.getPlayers()){
        	player.move();
        }
    }
    
    /**
     * Ends the game by stopping the view, shows a message. 
     * @param message should be a String containing the message which is shown.
     */
	public static void endGame(String message) {
		view.pause();
		view.showMessage(message);
    }
	
	/**
	 * Pauses the game by pausing the view, shows a message. When the message is removed by the player, the game continues.
	 */
	public static void pauseGame(){
		if(view.isActive()){
			view.pause();
			view.showMessage("game paused");
			view.start();
		}
	}

	/**
	 * Decides which actions should be performed when a key is released.  
	 * @param e should be a KeyEvent which represents a released key.
	 */
	public static void keyReleased(KeyEvent e) {
		for(Player player: Model.getPlayers()){
			player.keyReleased(e);
		}
	}
	
	/**
	 * Decides which actions should be performed when a key is pressed.  
	 * @param e should be a KeyEvent which represents a pressed key.
	 */
	public static void keyPressed(KeyEvent e) {
		for(Player player: Model.getPlayers()){
			player.keyPressed(e);
		}
		
		int key = e.getKeyCode();

	    switch (key) {
		case KeyEvent.VK_PAUSE: pauseGame();
			break;

		default:
			break;
		}
	}
	
}
