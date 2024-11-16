package application;

import enums.GameMode;
import gui.ExplosionEditor;
import gui.FrameSetEditor;
import gui.Game;
import gui.GameTikTok;
import gui.MapEditor;
import gui.PalleteEditor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import player.GameInput;
import tools.Materials;
import tools.Sound;
import tools.Tools;
import util.IniFile;
import util.TimerFX;

public class Main extends Application {

	public final static int TILE_SIZE = 16;
	public final static GameMode GAME_MODE = GameMode.GAME_TIKTOK;

	public static FrameSetEditor frameSetEditor = null;
	public static PalleteEditor palleteEditor = null;
	public static ExplosionEditor explosionEditor = null;
	public static MapEditor mapEditor = null;
	public static Game game = null;
	public static GameTikTok gameTikTok = null;
	public static Stage stageMain;
	public static Scene sceneMain;
	public static boolean close = false;

	@Override
	public void start(Stage stage) {
		try {
			stageMain = stage;
			Sound.setMasterGain(0.25);
			Position.setGlobalTileSize(TILE_SIZE);
			TileCoord.setGlobalTileSize(TILE_SIZE);
			Materials.loadFromFiles();
			GameInput.init();
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
			else if (GAME_MODE == GameMode.EXPLOSION_EDITOR) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ExplosionEditorView.fxml"));
				sceneMain = new Scene(loader.load());
				explosionEditor = loader.getController();
				explosionEditor.init();
			}
			else if (GAME_MODE == GameMode.PALLETE_EDITOR) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/PalleteEditorView.fxml"));
				sceneMain = new Scene(loader.load());
				palleteEditor = loader.getController();
				palleteEditor.init();
			}
			else if (GAME_MODE == GameMode.GAME_TIKTOK) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GameTikTokView.fxml"));
				sceneMain = new Scene(loader.load());
				gameTikTok = loader.getController();
				gameTikTok.init();
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
		GameInput.close();
		TimerFX.close();
		if (gameTikTok != null)
			gameTikTok.disconnectTikTokLive();
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
