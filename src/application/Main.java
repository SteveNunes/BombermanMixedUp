package application;

import java.io.File;

import enums.GameMode;
import gameutil.ColorMixEditor;
import gameutil.PalleteEditor;
import gui.FrameSetEditor;
import gui.Game;
import gui.GameTikTok;
import gui.GiftViewer;
import gui.MapEditor;
import gui.util.ImageUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
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
	public final static GameMode GAME_MODE = GameMode.GAME_TIKTOK;

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
			stageMain.setResizable(false);
			stageMain.setOnCloseRequest(e -> close());
			stageMain.setX(0);
			stageMain.setY(0);
			Main.stageMain.setTitle("BomberMan - Mixed up!");
			if (GAME_MODE == GameMode.COLOR_MIX_EDITOR)
				ColorMixEditor.openEditor();
			else if (GAME_MODE == GameMode.PALLETE_EDITOR)
				PalleteEditor.openEditor();
			else if (GAME_MODE == GameMode.GIFT_VIEWER) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GiftViewerView.fxml"));
				sceneMain = new Scene(loader.load());
				giftViewer = loader.getController();
				giftViewer.init();
			}
			else if (GAME_MODE == GameMode.MAP_EDITOR) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MapEditorView.fxml"));
				sceneMain = new Scene(loader.load());
				mapEditor = loader.getController();
				mapEditor.init();
			}
			else if (GAME_MODE == GameMode.GAME_TIKTOK) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GameTikTokView.fxml"));
				sceneMain = new Scene(loader.load());
				gameTikTok = loader.getController();
				gameTikTok.init();
			}
			else if (GAME_MODE == GameMode.GAME) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GameView.fxml"));
				sceneMain = new Scene(loader.load());
				game = loader.getController();
				game.init();
			}
			stageMain.setScene(sceneMain);
			stageMain.show();
		}
		catch (Exception e) {
			e.printStackTrace();
			close();
		}
	}
	
	public static void playHudsonLoading(Runnable afterFadeIn, Runnable afterFadeOut) {
		WritableImage image = (WritableImage) ImageUtils.removeBgColor(new Image("file:./appdata/sprites/HUD.png"), Materials.getGreenColor());
		float[] fade = { 0.0f };
		final int w = (int) getMainCanvas().getWidth(), h = (int) getMainCanvas().getHeight();
		Canvas c = new Canvas(w, h);
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		gc.setFill(Color.WHITE);
		gc.setGlobalAlpha(1);
		gc.fillRect(0, 0, w, h);
		gc.drawImage(image, 544, 828, 154, 28, w / 2 - 77 * getZoom(), h / 2 - 14 * getZoom(), 152 * getZoom(), 28 * getZoom());
		Image image2 = ImageUtils.getCanvasSnapshot(c);
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(16), e -> {
			try {
				fade[0] += 0.015;
				if (fade[0] > 1) {
			    timeline.stop();
					fade[0] = 1;
					Sound.setMasterGain(0.2);
					Sound.playWav("/voices/Hudson");
					Tools.loadStuffs();
					Materials.loadFromFiles();
					Position.setGlobalTileSize(TILE_SIZE);
					TileCoord.setGlobalTileSize(TILE_SIZE);
					GameFonts.loadFonts();
					GameInput.init();
					Platform.runLater(() -> {
						long l = System.currentTimeMillis();
						if (afterFadeIn != null) {
							try {
								afterFadeIn.run();
							}
							catch (Exception e2) {
					    	timeline.stop();
								e2.printStackTrace();
								Main.close();
								return;
							}
						}
						Misc.sleep(1000 - (int)(System.currentTimeMillis() - l));
						Timeline timeline2 = new Timeline();
						timeline2.getKeyFrames().add(new KeyFrame(Duration.millis(16), ex -> {
							try {
								fade[0] -= 0.015;
								if (fade[0] < 0) {
							    timeline2.stop();
									afterFadeOut.run();
								}
								else {
									mainGc.setFill(Color.BLACK);
									mainGc.setGlobalAlpha(1);
									mainGc.fillRect(0, 0, w, h);
									mainGc.setGlobalAlpha(fade[0]);
									mainGc.drawImage(image2, 0, 0);
								}
							}
							catch (Exception e2) {
								timeline2.stop();
								e2.printStackTrace();
								Main.close();
							}
						}));
						timeline2.setCycleCount(Timeline.INDEFINITE);
						timeline2.play();
					});
				}
				else {
					mainGc.setFill(Color.BLACK);
					mainGc.setGlobalAlpha(1);
					mainGc.fillRect(0, 0, w, h);
					mainGc.setGlobalAlpha(fade[0]);
					mainGc.drawImage(image2, 0, 0);
				}
			}
			catch (Exception e2) {
				timeline.stop();
				e2.printStackTrace();
				Main.close();
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
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
			System.load(System.getProperty("user.dir") + "\\dlls\\jinput-wintab.dll");
			System.load(System.getProperty("user.dir") + "\\dlls\\jinput-dx8_64.dll");
			System.load(System.getProperty("user.dir") + "\\dlls\\jinput-raw_64.dll");
		}
		launch(args);
	}
	
	public static void openFrameSetEditor() {
		try {
			FXMLLoader loader = new FXMLLoader(new File("./src/gui/FrameSetEditorView.fxml").toURI().toURL());
			Scene scene = new Scene(loader.load());
			FrameSetEditor frameSetEditor = loader.getController();
			frameSetEditor.init();
		  Stage stage = new Stage();
			stage.setResizable(false);
			stage.setScene(scene);
			stage.setResizable(true);
			stage.sizeToScene();
			stage.show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
