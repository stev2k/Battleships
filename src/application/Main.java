package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for program entry and stage setup.
 *
 * @author Steve Haines
 * @version 1
 */
public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * start program entry, opens the stage and sets the scene.
	 * 
	 * @param int index
	 * @return GridObject
	 */
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("mainScene.fxml"));
		Parent parent = loader.load();
		stage.setScene(new Scene(parent));
		stage.setTitle("Battleships");
		stage.show();
				
	}

	/**
	 * stop stops the game.
	 * 
	 * @param none
	 * @return none
	 */
	public void stop() throws Exception {
		Platform.exit();
		System.exit(0);
	}

}