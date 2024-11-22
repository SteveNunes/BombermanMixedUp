package application;

import enums.GameMode;
import gui.ExplosionEditor;
import gui.FrameSetEditor;
import gui.Game;
import gui.GameTikTok;
import gui.GiftViewer;
import gui.MapEditor;
import gui.PalleteEditor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import player.GameInput;
import tools.GameFonts;
import tools.Materials;
import tools.Sound;
import tools.Tools;
import util.DurationTimerFX;
import util.FrameTimerFX;
import util.IniFile;

public class Main extends Application {

	public final static int TILE_SIZE = 16;
	public final static GameMode GAME_MODE = GameMode.GAME_TIKTOK;

	public static FrameSetEditor frameSetEditor = null;
	public static PalleteEditor palleteEditor = null;
	public static ExplosionEditor explosionEditor = null;
	public static MapEditor mapEditor = null;
	public static Game game = null;
	public static GameTikTok gameTikTok = null;
	public static GiftViewer giftViewer = null;
	public static Stage stageMain;
	public static Scene sceneMain;
	public static boolean close = false;
	public static boolean freezeAll = false;
	public static int zoom = 3;
	public static Long uniqueTimerId = 0L;
	public static Canvas mainCanvas;
	public static GraphicsContext mainGc;

	@Override
	public void start(Stage stage) {
		try {
			stageMain = stage;
			mainCanvas = null;
			mainGc = null;
			Sound.setMasterGain(0.5);
			Position.setGlobalTileSize(TILE_SIZE);
			TileCoord.setGlobalTileSize(TILE_SIZE);
			Materials.loadFromFiles();
			GameFonts.loadFonts();
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
			else if (GAME_MODE == GameMode.GIFT_VIEWER) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GiftViewerView.fxml"));
				sceneMain = new Scene(loader.load());
				giftViewer = loader.getController();
				giftViewer.init();
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
	
	public static boolean frameSetEditorIsPaused() {
		return (frameSetEditor != null && frameSetEditor.isPaused);
	}

	public static int getZoom() {
		return zoom;
	}
	
	public static void setZoom(int zoom) {
		Main.zoom = zoom;
	}

	public static void freezeAll() {
		freezeAll = true;
	}
	
	public static void unFreezeAll() {
		freezeAll = false;
	}
	
	public static boolean isFreeze() {
		return freezeAll;
	}
	
	public static void setMainCanvas(Canvas canvas) {
		mainCanvas = canvas;
		mainGc = canvas.getGraphicsContext2D();
	}
	
	public static Canvas getMainCanvas() {
		return mainCanvas;
	}

	public static GraphicsContext getMainGraphicsContext() {
		return mainGc;
	}

	public static void close() {
		close = true;
		GameInput.close();
		DurationTimerFX.close();
		FrameTimerFX.close();
		if (gameTikTok != null)
			gameTikTok.disconnectTikTokLive();
		Platform.exit();
		IniFile.closeAllOpenedIniFiles();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
