package application;
/**
 * Ship class detailing ship parameters to aid with grid display
 *
 * @author Steve Haines
 * @version 1
 */
public class Ship {

	String type;
	int size;
	boolean isVertical;
	int position;
	/**
	 * Ship constructor for Ship class, allows creation 
	 * of a new ship if you have all the info at hand.
	 * 
	 * @param String type,Integer size,Boolean isVertical,Integer position
	 */
public Ship(String type,Integer size,Boolean isVertical,Integer position) {
	this.type = type;
	this.size = size;
	this.isVertical = isVertical;
	this.position = position;

    }

/**
 * Ship constructor for Ship class, allows
 * creation of a ship without knowing it's position and
 * orientation yet.
 * 
 * @param String type,Integer size
 */
public Ship(String type,Integer size) {
	this.type = type;
	this.size = size;

    }


}
