package application;

import enums.GameMode;
import gui.FrameSetEditor;
import gui.Game;
import gui.MapEditor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import player.GameInputs;
import tools.Materials;
import tools.Tools;
import util.IniFile;
import util.TimerFX;

public class Main extends Application {

	public final static int TILE_SIZE = 16;
	public final static GameMode GAME_MODE = GameMode.GAME;

	public static FrameSetEditor frameSetEditor = null;
	public static MapEditor mapEditor = null;
	public static Game game = null;
	public static Stage stageMain;
	public static Scene sceneMain;
	public static boolean close = false;

	@Override
	public void start(Stage stage) {
		try {
			Position.setGlobalTileSize(TILE_SIZE);
			TileCoord.setGlobalTileSize(TILE_SIZE);
			stageMain = stage;
			Materials.loadFromFiles();
			GameInputs.init();
			Tools.loadStuffs();
			if (GAME_MODE == GameMode.FRAMESET_EDITOR) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/FrameSetEditorView.fxml"));
				sceneMain = new Scene(loader.load());
				frameSetEditor = loader.getController();
				frameSetEditor.init();
			}
			else if (GAME_MODE == GameMode.MAP_EDITOR) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MapEditorView.fxml"));
				sceneMain = new Scene(loader.load());
				mapEditor = loader.getController();
				mapEditor.init();
			}
			else {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GameView.fxml"));
				sceneMain = new Scene(loader.load());
				game = loader.getController();
				game.init();
			}
			stageMain.setResizable(false);
			stageMain.setScene(sceneMain);
			stageMain.show();
			stageMain.setOnCloseRequest(e -> close());
		}
		catch (Exception e) {
			e.printStackTrace();
			close();
		}
	}

	public static void close() {
		close = true;
		GameInputs.close();
		TimerFX.stopAllTimers();
		Platform.exit();
		IniFile.closeAllOpenedIniFiles();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static boolean frameSetEditorIsPaused() {
		return (frameSetEditor != null && frameSetEditor.isPaused);
	}

}
