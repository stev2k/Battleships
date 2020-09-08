package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Grid class responsible for arranging and displaying the grid Game() class is
 * responsible for managing this class.
 *
 * @author Steve Haines
 * @version 1
 */
public class Grid extends StackPane {

	private final int GRID_SIZE = 25;
	public final int numberofrows = 10;
	public final int imageSeaId = 1;
	public final int imageMissId = 4;
	public final int imageShipId = 5;
	public final int imageHitId = 6;
	public int numberofcolumns = 10;
	public ArrayList<Image> imageArray = new ArrayList<Image>();
	public ArrayList<GridObject> gridArray = new ArrayList<GridObject>();
	private Pane gamePane = new Pane();
	private boolean isPlayer;
	private Game game;

	/**
	 * Grid basic constructor for the Grid class
	 * 
	 * @param boolean isPlayer, Game game
	 */
	public Grid(boolean isPlayer, Game game) {
		this.isPlayer = isPlayer;
		this.game = game;
		getChildren().add(gamePane);
		initImages();
		initGrid();
		refreshPane();

	}

	/**
	 * initImages populates an instance ArrayList<Image> with images used by the
	 * game.
	 * 
	 * @param none
	 * @return none
	 */
	private void initImages() {
		imageArray.add(new Image(this.getClass().getResource("/application/resources/1f30a.png").toExternalForm()));// 0
																													// sea
		imageArray.add(new Image(this.getClass().getResource("/application/resources/1f30b.png").toExternalForm()));// 1
																													// sea
		imageArray.add(new Image(this.getClass().getResource("/application/resources/1f30c.png").toExternalForm()));// 2
																													// sea
		imageArray.add(new Image(this.getClass().getResource("/application/resources/1f30d.png").toExternalForm()));// 3
																													// sea
		imageArray.add(new Image(this.getClass().getResource("/application/resources/cross.png").toExternalForm()));// 4
																													// miss
		imageArray
				.add(new Image(this.getClass().getResource("/application/resources/ship_1f6a2.png").toExternalForm()));// 5
																														// ship
		imageArray.add(
				new Image(this.getClass().getResource("/application/resources/collision_1f4a5.png").toExternalForm()));// 6
																														// hit
	}

	/**
	 * initGrid initialises the grid with empty sea squares
	 * 
	 * @param none
	 * @return none
	 */
	private void initGrid() {
		Random r = new Random();
		for (int y = 0; y < numberofrows; y++) {
			for (int x = 0; x < numberofcolumns; x++) {
				int rand = r.nextInt(4);
				ImageView image = prepImageView(x, y, new ImageView(imageArray.get(rand))); // first 4 random images
				GridObject gridObj = new GridObject(x, y, image, false, false, false, rand);
				gridArray.add(gridObj);
			}

		}
	}

	/**
	 * refreshPane refreshes the visual display with updated underlying data from
	 * gridArray
	 * 
	 * @param none
	 * @return none
	 */
	private void refreshPane() {
		gamePane.getChildren().clear();
		for (int x = 0; x < gridArray.size(); x++) {
			gamePane.getChildren().add(gridArray.get(x).gridImg);
		}
	}

	/**
	 * updateGridByIndex update a specific grid object using it's index and the
	 * whole GridObject.
	 * 
	 * @param int index, GridObject gridObj
	 * @return none
	 */
	public void updateGridByIndex(int index, GridObject gridObj) {
		gridArray.set(index, gridObj);
		refreshPane();

	}

	/**
	 * getGridInfoByIndex returns a grid object from the live grid by it's index
	 * reference.
	 * 
	 * @param int index
	 * @return GridObject
	 */
	public GridObject getGridInfoByIndex(int index) {
		try {
			return gridArray.get(index);
		} catch (Exception e) {
			return null;
		}
	}
	
	public ArrayList<Long> getGridInfo() {
		ArrayList<Long> result = new ArrayList<Long>();
		result.add(gridArray.stream().filter(x -> x.isHit).collect(Collectors.counting()));
		result.add(gridArray.stream().filter(x -> x.isMiss).collect(Collectors.counting()));
		result.add(gridArray.stream().filter(x -> x.isShip).collect(Collectors.counting()));
		return result;
	}

	/**
	 * prepImageView prepares all grid images in the same consistent way, allowing
	 * for adding an event handler and
	 * 
	 * @param int x, int y, ImageView image
	 * @return ImageView
	 */
	public ImageView prepImageView(int x, int y, ImageView image) {
		image.setFitWidth(GRID_SIZE);
		image.setFitHeight(GRID_SIZE);
		image.setTranslateY(y * GRID_SIZE);
		image.setTranslateX(x * GRID_SIZE);
		image.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (game.getView() == "Opponent") {
				int index = game.coordinateToIndex(x, y);
				game.fireShot(index, false);

			} else {
				int index = game.coordinateToIndex(x, y);
				game.fireShot(index, true);
			}
			event.consume();

		});
		return image;
	}
}