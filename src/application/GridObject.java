package application;

import javafx.scene.image.ImageView;
/**
 *Grid object is a custom extension of ImageView, allowing the program
 *to store all of it's key data within the main Pane() shown on stage.
 *
 * @author Steve Haines
 * @version 1
 */
public class GridObject extends ImageView {

	Integer x;
	Integer y;
	ImageView gridImg;
	Integer	shipType;
	Boolean isShip;
	Boolean isHit;
	Boolean isMiss;
	Integer imageId;
	/**
	 * GridObject constructor for GridObject class, allows creation 
	 * of a new GridObjecxt if you have all the info at hand.
	 * 
	 * @param Integer x, Integer y, ImageView gridImg, Boolean isShip, Boolean isHit, Boolean isMiss, Integer imageId
	 */
	public GridObject(Integer x, Integer y, ImageView gridImg, Boolean isShip, Boolean isHit, Boolean isMiss, Integer imageId) {
		this.x = x;
		this.y = y;
		this.gridImg = gridImg;
		this.isShip = isShip;
		this.isHit = isHit;
		this.isMiss = isMiss;
		this.imageId = imageId;
	}
	/**
	 * GridObject constructor for GridObject class, allows creation 
	 * of a new blank GridObject with default values where required.
	 * 
	 * @param none
	 */
	public GridObject() {
		this.isShip = false;
		this.isHit = false;
		this.isMiss = false;
	}

}