package application;

import java.io.File;

import enums.GameMode;
import gui.ColorMixEditor;
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
import util.Misc;

public class Main extends Application {

	public final static int TILE_SIZE = 16;
	public final static GameMode GAME_MODE = GameMode.PALLETE_EDITOR;

	public static FrameSetEditor frameSetEditor = null;
	public static PalleteEditor palleteEditor = null;
	public static ColorMixEditor explosionEditor = null;
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
			Tools.loadStuffs();
			boolean loadStuffs = true;
			if (GAME_MODE == GameMode.COLOR_MIX_EDITOR) {
				FXMLLoader loader = new FXMLLoader(new File("./src/gui/ColorMixEditorView.fxml").toURI().toURL());
				sceneMain = new Scene(loader.load());
				explosionEditor = loader.getController();
				explosionEditor.init();
				loadStuffs = false;
			}
			else if (GAME_MODE == GameMode.PALLETE_EDITOR) {
				FXMLLoader loader = new FXMLLoader(new File("./src/gui/PalleteEditorView.fxml").toURI().toURL());
				sceneMain = new Scene(loader.load());
				palleteEditor = loader.getController();
				palleteEditor.init();
				loadStuffs = false;
			}
			else if (GAME_MODE == GameMode.GIFT_VIEWER) {
				FXMLLoader loader = new FXMLLoader(new File("./src/gui/GiftViewerView.fxml").toURI().toURL());
				sceneMain = new Scene(loader.load());
				giftViewer = loader.getController();
				giftViewer.init();
				loadStuffs = false;
			}
			if (loadStuffs) {
				Sound.setMasterGain(0.2);
				Position.setGlobalTileSize(TILE_SIZE);
				TileCoord.setGlobalTileSize(TILE_SIZE);
				Materials.loadFromFiles();
				GameFonts.loadFonts();
				GameInput.init();
			}
			if (GAME_MODE == GameMode.FRAMESET_EDITOR) {
				FXMLLoader loader = new FXMLLoader(new File("./src/gui/FrameSetEditorView.fxml").toURI().toURL());
				sceneMain = new Scene(loader.load());
				frameSetEditor = loader.getController();
				frameSetEditor.init();
			}
			else if (GAME_MODE == GameMode.MAP_EDITOR) {
				FXMLLoader loader = new FXMLLoader(new File("./src/gui/MapEditorView.fxml").toURI().toURL());
				sceneMain = new Scene(loader.load());
				mapEditor = loader.getController();
				mapEditor.init();
			}
			else if (GAME_MODE == GameMode.GAME_TIKTOK) {
				FXMLLoader loader = new FXMLLoader(new File("./src/gui/GameTikTokView.fxml").toURI().toURL());
				sceneMain = new Scene(loader.load());
				gameTikTok = loader.getController();
				gameTikTok.init();
			}
			else if (GAME_MODE == GameMode.GAME) {
				FXMLLoader loader = new FXMLLoader(new File("./src/gui/GameView.fxml").toURI().toURL());
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
		if (gameTikTok != null)
			gameTikTok.disconnectTikTokLive();
		Misc.runShutdownEvents();
		Platform.exit();
	}

	public static void main(String[] args) {
		if (args.length > 0 && args[0].equals("usedlls")) {
			System.load(System.getProperty("user.dir") + "/dlls/jinput-wintab.dll");
			System.load(System.getProperty("user.dir") + "/dlls/jinput-dx8_64.dll");
			System.load(System.getProperty("user.dir") + "/dlls/jinput-raw_64.dll");
		}
		launch(args);
	}

}
