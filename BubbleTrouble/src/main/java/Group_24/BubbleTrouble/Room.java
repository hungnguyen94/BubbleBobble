package Group_24.BubbleTrouble;

import org.newdawn.slick.geom.Rectangle;

import java.util.ArrayList;

/**
 * Room contains all objects within the room (except for the players), and draws them on the screen.
 *
 */
public class Room {
	
	private RoomData data;
	private int startingposition;

	private ArrayList<Rectangle> walls;
	private ArrayList<Rectangle> floors;
	
	private ArrayList<Bubble> bubbles;
	
	/**
	 * Constructs a new Room using the data contained by a RoomData data container.
	 * @param data should be a RoomData object containing the data which should be loaded into the Room.
	 */
	public Room(RoomData data){
		this.data = data;
		
		walls = new ArrayList<Rectangle>();
		walls.add(new Rectangle(0, 0, 10, 800));
		walls.add(new Rectangle(843, 0, 10, 800));
		
		floors = new ArrayList<Rectangle>();
		floors.add(new Rectangle(0, 596, 800, 2 ));
		this.reload();
	}
	
	/**
	 * Returns the collection of bubbles within this Room.
	 * @return returns the collection of bubbles within this Room.
	 */
	public ArrayList<Bubble> getBubbles() {
		return bubbles;
	}
	
	/**
	 * Reloads the room, loads the initial data into the Room and places the Players in the room without touching the Players themself. 
	 */
	public void reload() {
		for(Player player: Model.getPlayers()){
			player.moveTo(startingposition);
		}
		startingposition = data.getStartingposition();
		bubbles = data.getBubbles();
	}

	/**
	 * @return the walls
	 */
	public ArrayList<Rectangle> getWalls() {
		return walls;
	}

	/**
	 * @return the floor
	 */
	public ArrayList<Rectangle> getFloors() {
		return floors;
	}

}


