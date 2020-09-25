package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.time.format.DateTimeFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.util.Duration;
import javafx.scene.image.ImageView;

/**
 * Game class responsible for organising the flow of the game. this class
 * directs flow throughout the running of the program.
 *
 * @author Steve Haines
 * @version 1
 */
public class Game {
	private String playerName = null;
	Grid playerGrid = new Grid(true, this);
	Grid opponentGrid = new Grid(false, this);
	ArrayList<Ship> fleet = new ArrayList<Ship>();
	Integer turnNumber;
	String[] messages = new String[10];
	public boolean isPlayerView = true;

	/**
	 * newGame starts a new game by clearing screen and randomly filling both grids
	 * with ships. iterates through the fleet placing until fleet array is consumed.
	 * 
	 * @param none
	 * @return none
	 */
	public void newGame() {
		playerName = nameBox.getText();
		populateMessages();
		clearGame();
		populateMessages();
		fleet = new ArrayList<Ship>();
		populateFleet();
		// place player fleet in grid randomly
		for (int i = 0; i < fleet.size(); i++) {
			placeFleet(i, true);
			placeFleet(i, false);
		}
		endTurn.setVisible(true);
		nameBox.setVisible(false);
		playerTab.setText(playerName + "'s Board");
		if (isPlayerTurn()) {

		}

	}

	/**
	 * simpleAI takes a turn on behalf of the opponent
	 * 
	 * @param none
	 * @return none
	 */
	public void simpleAI() {

		int i = randBetween(3, 7);
		int index = randBetween(0, 99);
		KeyFrame start = new KeyFrame(Duration.ZERO,
				new KeyValue(progress.progressProperty(), ProgressBar.INDETERMINATE_PROGRESS));
		KeyFrame end = new KeyFrame(Duration.seconds(i), new KeyValue(progress.progressProperty(), 1));
		Timeline task = new Timeline(start, end);

		task.playFromStart();
		task.setOnFinished(event -> {
			fireShot(index, true);

		});

	}

	/**
	 * endTurn initialises turn taking and starts first turn.
	 * 
	 * @param none
	 * @return none
	 */
	public void endTurn() {
		incrementTurnNumber();
		if (isPlayerTurn()) {
			simpleAI();
		}

	}

	/**
	 * incrementTurnNumber increments turnNumber
	 * 
	 * @param none
	 * @return none
	 */
	private Boolean isPlayerTurn() {
		return (turnNumber % 2 == 0) ? true : false;
	}

	/**
	 * incrementTurnNumber increments turnNumber
	 * 
	 * @param none
	 * @return none
	 */
	private void incrementTurnNumber() {
		progress.setVisible(false);
		turnNumber++;
		tabPane.getSelectionModel().clearAndSelect(boolToInt(!isPlayerTurn()));
		if (isPlayerView == true) {
			progress.setVisible(true);
		}
	}

	@FXML
	TabPane tabPane;

	/**
	 * clearGame re-initialises both grids.
	 * 
	 * @param none
	 * @return none
	 */
	private void clearGame() {
		playerGrid = new Grid(true, this);
		opponentGrid = new Grid(false, this);
		turnNumber = -1;
		incrementTurnNumber();
		updateLabels();

	}

	private void updateLabels() {
		String name = playerName == null ? "Player's" : playerName + "'s ";
		playerStatsLabel.setText("Opponent's Statistics:\nHits: " + playerGrid.getGridInfo().get(0) + "\nMisses: "
				+ playerGrid.getGridInfo().get(1));
		opponentStatsLabel.setText(name + " Statistics:\nHits: " + opponentGrid.getGridInfo().get(0) + "\nMisses: "
				+ opponentGrid.getGridInfo().get(1));

	}

	/**
	 * placeFleet responsible for arranging the ship placement and passing in params
	 * to flow down the program.
	 * 
	 * @param int i, boolean isPlayer
	 * @return none
	 */
	public void placeFleet(int i, boolean isPlayer) {
		ArrayList<Boolean> boolArray = new ArrayList<Boolean>();
		Grid grid = isPlayer ? playerGrid : opponentGrid;
		positionShip(fleet.get(i), grid);
		placeShipAvoidingCollision(fleet.get(i), grid, true, boolArray, isPlayer); // test the placement
		placeShipAvoidingCollision(fleet.get(i), grid, false, boolArray, isPlayer); // place ship
	}

	/**
	 * placeShipAvoidingCollision responsible for testing placement of ships
	 * (collision detection) and placing them. if collision detetcted, a new random
	 * location is generated until no collisions.
	 * 
	 * @param Ship ship, Grid grid, boolean testPlacement, ArrayList<Boolean>
	 *             boolArray,boolean isPlayer
	 * @return none
	 */
	private void placeShipAvoidingCollision(Ship ship, Grid grid, boolean testPlacement, ArrayList<Boolean> boolArray,
			boolean isPlayer) {
		if (ship.isVertical) {
			// vertical, ships are placed upwards from starting position
			for (int i = ship.position; i >= (ship.position - ((ship.size - 1) * 10)); i -= 10) {
				if (testPlacement) {
					boolArray = test(ship, grid, boolArray, i);
					// System.out.println("test placing ship at" + i);
				} else {

					placeShipPart(grid, i, constructParams(isPlayer, false, ship.size,i));
					// System.out.println("placing ship at" + i);
				}
				if (boolArray.contains(true)) {
					repositionShip(ship, grid, boolArray, isPlayer);
				}
			}
		} else {
			// horizontal, ships are placed leftwards from starting position.
			for (int i = ship.position; i > (ship.position - ship.size); i--) {
				if (testPlacement) {
					boolArray = test(ship, grid, boolArray, i);
					// System.out.println("test placing ship at" + i);
				} else {
					placeShipPart(grid, i, constructParams(isPlayer, false, ship.size,i));
					// System.out.println("placing ship at" + i);
				}
				if (boolArray.contains(true)) {
					repositionShip(ship, grid, boolArray, isPlayer);
				}
			}
		}
		// System.out.println("------------------------");
	}

	private Integer[] constructParams(Boolean isPlayer, Boolean isUpdate, Integer shipSize, Integer i) {
		Integer[] params = new Integer[9];
		params[0] = boolToInt(isPlayer);
		params[1] = indexToCoordinate(i,true);//x
		params[2] = indexToCoordinate(i,false);//y
		params[7] = shipSize;
		params[8] = boolToInt(isUpdate);
		return params;
	}

	/**
	 * test responsible for running a test placement and returning results.
	 * 
	 * @param Ship ship, Grid grid, ArrayList<Boolean> boolArray, int i
	 * @return ArrayList<Boolean>
	 */
	private ArrayList<Boolean> test(Ship ship, Grid grid, ArrayList<Boolean> boolArray, int i) {
		GridObject g = grid.getGridInfoByIndex(i);
		if (g == null) {
			boolArray.add(true);
		} else {
			boolArray.add(g.isShip);
		}
		return boolArray;
	}

	/**
	 * repositionShip a method to re-position a ship if a test placement has failed
	 * due to collision
	 * 
	 * @param Ship ship, Grid grid, ArrayList<Boolean> boolArray, boolean isPlayer
	 * @return none
	 */
	private void repositionShip(Ship ship, Grid grid, ArrayList<Boolean> boolArray, boolean isPlayer) {
		positionShip(ship, grid);
		boolArray.clear();
		placeShipAvoidingCollision(ship, grid, true, boolArray, isPlayer);
		// System.out.println("repositioning ship");
	}

	/**
	 * positionShip a method to provide a random position for a ship on the grid.
	 * 
	 * @param Ship ship, Grid grid
	 * @return none
	 */
	private void positionShip(Ship ship, Grid grid) {
		ship.isVertical = randBool();
		ship.position = ship.isVertical ? generateRandVertical(ship.size) : generateRandHorizontal(ship.size);
	}

	/**
	 * placeShipPart a method to place a ship on a given grid. has a switch for
	 * updating or creating, also allows usage by passing in a String[] of
	 * parameters
	 * [0]isplayer,[1]x-coord,[2]y-coord,[3]imageid,[4]isship,[5]ishit,[6]ismiss,[7]shipid,[8]isupdate
	 * 
	 * 
	 * @param Grid grid, int i, boolean isPlayer, String[] gridParams, Boolean
	 *             isUpdate
	 * @return none
	 */
	private void placeShipPart(Grid grid, Integer[] params) {
		int i = coordinateToIndex(params[1],params[2]);
		GridObject gridObj = grid.getGridInfoByIndex(i);
		gridObj.x = params[1];
		gridObj.y = params[2];

		if (!(params[8] == null)) {
			if (gridObj.isShip && intToBool(params[8])) {
				gridObj.isHit = true;
				gridObj.imageId = grid.imageHitId;
				setLabel(getMessage(2, getShipNameBySize(gridObj.shipType))); // hit message
			}
			if (!gridObj.isShip && intToBool(params[8])) {
				gridObj.isMiss = true;
				gridObj.imageId = grid.imageMissId;
				setLabel(getMessage(3, null)); // miss message
			}
			if (!intToBool(params[8])) { // initial run populating grid
				gridObj.isShip = true;
				gridObj.isHit = false;
				gridObj.isMiss = false;
				gridObj.imageId = intToBool(params[0]) ? grid.imageShipId : grid.imageSeaId;
				gridObj.shipType = params[7];
			}

		} else {
			gridObj.imageId = params[3];
			gridObj.isShip = intToBool(params[4]);
			gridObj.isHit = intToBool(params[5]);
			gridObj.isMiss = intToBool(params[6]);
			gridObj.shipType = params[7];

		}
		gridObj.gridImg = grid.prepImageView(gridObj.y, gridObj.x, new ImageView(grid.imageArray.get(gridObj.imageId)));
		grid.updateGridByIndex(i, gridObj);
	}

	/**
	 * fireShot fires a shot on the grid, triggered by a mouse click event in the
	 * grid class.
	 * 
	 * @param int i, boolean isPlayer
	 * @return none
	 */
	public void fireShot(int i, boolean isPlayer) {
		placeShipPart(isPlayer ? playerGrid : opponentGrid, constructParams(isPlayer, true, null,i));
		updateLabels();
	}

	/**
	 * generateRandVertical generates a random vertical placement for a ship, ships
	 * cannot be placed so close to the bounds that they would intersect, so we use
	 * the size to set them at an appropriate distance.
	 * 
	 * @param int size
	 * @return int
	 */
	// must place ships upwards
	private int generateRandVertical(int size) {
		// System.out.println("generateRandVertical running");
		int result = randBetween((size--) * 10, 99);
		return result;
	}

	/**
	 * generateRandHorizontal generates a random horizontal placement for a ship,
	 * ships cannot be placed so close to the bounds that they would intersect, so
	 * we use the size to set them at an appropriate distance.
	 * 
	 * @param int size
	 * @return int
	 */
	private int generateRandHorizontal(int size) {
		// System.out.println("generateRandHorizontal running");
		Integer result = randBetween(0, 99);
		while (indexToCoordinate(result, false) <= size) {
			result = randBetween(0, 99);
		}
		return result;
	}

	/**
	 * indexToCoordinate helper method: take a grid index and turn it into an x or y
	 * coordinate.
	 * 
	 * @param Integer index, boolean isX
	 * @return int
	 */
	private int indexToCoordinate(Integer index, boolean isX) {
		String intAsString = (index.toString().length() == 1) ? "0" + index.toString() : index.toString();
		Integer result = isX ? Integer.valueOf(intAsString.substring(0, 1))
				: Integer.valueOf(intAsString.substring(1, 2));
		return result;
	}

	/**
	 * coordinateToIndex helper method: take a pair of coordinates and turn it into
	 * an index.
	 * 
	 * @param Integer x, Integer y
	 * @return int
	 */
	public int coordinateToIndex(Integer x, Integer y) {
		String intAsString = y.toString() + x.toString();
		Integer result = Integer.valueOf(intAsString);
		return result;
	}

	/**
	 * loadGame load a game from a file, this is called from the loadGamePress()
	 * method in the MainSceneController
	 * 
	 * @param File file
	 * @return none
	 */
	public void loadGame(File file) {
		clearGame();
		FileReader fileReader = null;
		BufferedReader reader;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		reader = new BufferedReader(fileReader);

		try {
			String data = reader.readLine();
			processRow(data);

			while (data != null) {
				processRow(data);
				data = reader.readLine();

			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * processRow helper method for loadGame(), processes a row and adds data to the
	 * game grid. in the MainSceneController
	 * 
	 * @param String data
	 * @return none
	 */
	private void processRow(String data) {
		Integer[] params = Arrays.stream(data.split(",")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
		Integer index = coordinateToIndex(params[2], params[1]);
		if (params[0] == 1) {// isPlayer true
			placeShipPart(playerGrid, index, params);
		} else {
			placeShipPart(opponentGrid, index, params);
		}
	}

	/**
	 * saveGame takes the results of the game and writes them into a file for
	 * loading later on.
	 * 
	 * 
	 * file format:
	 * [0]isplayer,[1]x-coord,[2]y-coord,[3]imageid,[4]isship,[5]ishit,[6]ismiss,[7]shipid
	 * 
	 * @param File file
	 * @return None
	 */
	public void saveGame(File file) {
		FileOutputStream outputStream = null;
		PrintWriter printWriter;
		try {
			outputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		printWriter = new PrintWriter(outputStream);
		for (int i = 0; i < playerGrid.numberofcolumns * playerGrid.numberofrows; i++) {
			GridObject g = playerGrid.getGridInfoByIndex(i);
			Integer x = indexToCoordinate(i, true);
			Integer y = indexToCoordinate(i, false);
			String line = "1" + "," + x.toString() + "," + y.toString() + "," + g.imageId.toString() + ","
					+ boolToInt(g.isShip).toString() + "," + boolToInt(g.isHit).toString() + ","
					+ boolToInt(g.isMiss).toString() + "," + g.shipType.toString();
			printWriter.println(line);
		}
		for (int i = 0; i < opponentGrid.numberofcolumns * opponentGrid.numberofrows; i++) {
			GridObject g = opponentGrid.getGridInfoByIndex(i);
			Integer x = indexToCoordinate(i, true);
			Integer y = indexToCoordinate(i, false);
			String line = "0" + "," + x.toString() + "," + y.toString() + "," + g.imageId.toString() + ","
					+ boolToInt(g.isShip).toString() + "," + boolToInt(g.isHit).toString() + ","
					+ boolToInt(g.isMiss).toString() + "," + g.shipType.toString();
			printWriter.println(line);

		}
		printWriter.flush();
		printWriter.close();

	}

	/**
	 * boolToInt helper method: take a bool and turn it into an int 0=false, 1=true
	 * called by the saveGame method to make data in file easier to access when
	 * loading.
	 * 
	 * @param Boolean b
	 * @return Integer
	 */
	public Integer boolToInt(Boolean b) {
		return b ? 1 : 0;
	}

	/**
	 * intToBool helper method: take an int and turn it into an bool 0=false, 1=true
	 * called when processing a loaded game
	 * 
	 * @param Boolean b
	 * @return Integer
	 */
	public boolean intToBool(int i) {
		return i == 1 ? true : false;
	}

	/**
	 * RandBetween generates a random integer between the passed in min and max
	 * integers
	 * 
	 * @param int min, int max
	 * @return int
	 */
	public int randBetween(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	/**
	 * RandBool generates a random boolean
	 * 
	 * @param none
	 * @return boolean
	 */
	public boolean randBool() {
		Random rand = new Random();
		return rand.nextBoolean();

	}

	/**
	 * getMessage gets message from the message array with subsitution;
	 * 
	 * @param Integer
	 * @return String
	 */
	public String getMessage(int i, String shipType) {
		shipType = shipType == null ? "" : shipType;
		String name = playerName == null ? "Player" : playerName;
		return messages[i].replace("?", isPlayerTurn() ? "Opponent" : name)
				.replace("^", isPlayerTurn() ? name : "Opponent").replace("£", shipType);
	}

	/**
	 * populateMessages populates in game messages to an array more easily passed.
	 * 
	 * @param none
	 * @return none
	 */
	public void populateMessages() {
		messages[0] = "Welcome to Battleships!";
		messages[1] = "Starting ?'s Turn";
		messages[2] = "? fired and hit ^'s £!";
		messages[3] = "? has fired a shot and missed!";
		messages[4] = "";
		messages[5] = "";
		messages[6] = "";
		messages[7] = "";
		messages[8] = "";
		messages[9] = "";

	}

	/**
	 * populateFleet populates an instance of fleet with all of the ships.
	 * 
	 * @param none
	 * @return none
	 */
	public void populateFleet() {

		fleet.add(new Ship("Submarine", 1));
		fleet.add(new Ship("Submarine", 1));
		fleet.add(new Ship("Submarine", 1));
		fleet.add(new Ship("Destoyer", 2));
		fleet.add(new Ship("Destoyer", 2));
		fleet.add(new Ship("Destoyer", 2));
		fleet.add(new Ship("Cruiser", 3));
		fleet.add(new Ship("Cruiser", 3));
		fleet.add(new Ship("Battleship", 4));

	}

	/**
	 * getShipNameByFleetId accessor method for fleet ship name.
	 * 
	 * @param int i
	 * @return none
	 */
	public String getShipNameBySize(int i) {
		String s = fleet.stream().filter(x -> x.size == i).findAny().orElse(null).type;
		return s;

	}

	@FXML
	private Label playerStatsLabel;

	@FXML
	private Label opponentStatsLabel;

	@FXML
	private Button endTurn;

	@FXML
	private TextField nameBox;

	@FXML
	private BorderPane opponentBoard;

	@FXML
	private Tab playerTab;

	@FXML
	private ProgressBar progress;

	/**
	 * opponentTabClicked handles button click on opponentTab
	 * 
	 * @param none
	 * @return none
	 */
	@FXML
	private void opponentTabClicked() {
		opponentBoard.setCenter(opponentGrid);
		isPlayerView = false;
	}

	/**
	 * getView() determines which view is currently visible to user
	 * 
	 * @param none
	 * @return String
	 */
	public String getView() {
		return isPlayerView ? "Player" : "Opponent";
	}

	@FXML
	private BorderPane playerBoard;

	/**
	 * playerTabClicked() handles button click on playerTab
	 * 
	 * @param none
	 * @return none
	 */
	@FXML
	public void playerTabClicked() {

		playerBoard.setCenter(playerGrid);
		isPlayerView = true;

	}

	/**
	 * closePress() handles button click on close button in file menu.
	 * 
	 * @param none
	 * @return none
	 */
	@FXML
	private void closePress() throws Exception {
		Main m = new Main();
		m.stop();
	}

	/**
	 * savePress() handles button click on save button in file menu. creates
	 * filechooser for saving to a file then starts saveGame method in Game class.
	 * 
	 * @param none
	 * @return none
	 */
	@FXML
	private void savePress() throws Exception {
		FileChooser fileChooser = new FileChooser();
		DateTimeFormatter datetime = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		fileChooser.setTitle("Save Game");
		fileChooser.setInitialFileName("Battleships_" + datetime.format(java.time.LocalDateTime.now()) + ".sav");
		File file = fileChooser.showSaveDialog(new Popup());
		if (file != null) {
			try {
				saveGame(file);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());

			}
		}

	}

	/**
	 * newGamePress() handles button click on new button in file menu. refreshes
	 * display when starting new game.
	 * 
	 * @param none
	 * @return none
	 */
	@FXML
	private void newGamePress() throws Exception {
		newGame();
		playerBoard.setCenter(playerGrid);
		opponentBoard.setCenter(opponentGrid);
		setLabel(getMessage(1, null));

	}

	/**
	 * savePress() handles button click on load button in file menu. creates
	 * filechooser for load to a file then starts loadGame method in Game class.
	 * 
	 * @param none
	 * @return none
	 */
	@FXML
	private void loadGamePress() throws Exception {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Game");
		File file = fileChooser.showOpenDialog(new Popup());
		if (file != null) {
			try {
				loadGame(file);
				playerBoard.setCenter(playerGrid);
				opponentBoard.setCenter(opponentGrid);
			} catch (Exception ex) {
				throw ex;

			}
		}

	}

	@FXML
	private Label playerLabel;

	@FXML
	private Label opponentLabel;

	/**
	 * setLabel() responsible for setting value of label.
	 * 
	 * @param String message
	 * @return none
	 */
	public void setLabel(String message) {
		if (getView().compareTo("Player") == 0) {
			playerLabel.setText(message);
		} else {
			opponentLabel.setText(message);
		}

	}

}
