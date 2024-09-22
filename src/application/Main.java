package application;
	
import enums.GameMode;
import gui.FrameSetEditor;
import gui.MapEditor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tools.GameMisc;
import tools.Materials;
import tools.SquaredBg;
import util.IniFile;
import util.Misc;


public class Main extends Application {
	
	public final static int winW = 320;
	public final static int winH = 240;
	public final static int tileSize = 16;
	public static FrameSetEditor frameSetEditor = null;
	public static MapEditor mapEditor = null;

	public static GameMode mode = GameMode.MAP_EDITOR;
	public static Canvas canvasMain;
	public static Canvas canvasOverall;
	public static GraphicsContext gcMain;
	public static GraphicsContext gcOverall;
	private static VBox vBoxMain;
	public static Stage stageMain;
	public static int zoom = 3;
	public static boolean close = false;
	
	@Override
	public void start(Stage stage) {
		try {
			stageMain = stage;
			Materials.loadFromFiles();
			SquaredBg.setSquaredBg(4, 3, 50, 255);
			Scene scene = null;
			if (mode == GameMode.GAME) {
				canvasMain = new Canvas(winW * zoom, winH * zoom);
				canvasMain.getGraphicsContext2D().setImageSmoothing(false);
				gcMain = canvasMain.getGraphicsContext2D();
				canvasOverall = new Canvas(winW * zoom, winH * zoom);
				canvasOverall.getGraphicsContext2D().setImageSmoothing(false);
				gcOverall = canvasOverall.getGraphicsContext2D();
				vBoxMain = new VBox();
				vBoxMain.getChildren().add(canvasMain);
				scene = new Scene(vBoxMain);
				stageMain.setTitle("BomberMan Mixed Up!");
				mainLoop();
			}
			else if (mode == GameMode.FRAMESET_EDITOR) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/FrameSetEditorView.fxml"));
				scene = new Scene(loader.load());
				frameSetEditor = loader.getController();
				frameSetEditor.init(scene);
			}
			else if (mode == GameMode.MAP_EDITOR) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MapEditorView.fxml"));
				scene = new Scene(loader.load());
				mapEditor = loader.getController();
				mapEditor.init(scene);
			}
			stageMain.setResizable(false);
			stageMain.setScene(scene);
			stageMain.show();
			stageMain.setOnCloseRequest(e -> close(false));
		}
		catch(Exception e)
			{ e.printStackTrace(); }
	}
	
	public static void close()
		{ close(true); }
	
	public static void close(boolean wait) {
		close = true;
		if (wait)
			Misc.sleep(1000); 
		IniFile.closeAllOpenedIniFiles();
		Platform.exit();
	}

	private void mainLoop() {
		drawDrawCanvas();
		drawMainCanvas();
		GameMisc.getFPSHandler().fpsCounter();
		if (!close )
			Platform.runLater(() -> {
				stageMain.setTitle("BomberMan Mixed Up!     FPS: " + GameMisc.getFPSHandler());
				mainLoop();
			});
	}
	
	private void drawMainCanvas() {
		GameMisc.drawAllCanvas(canvasMain, zoom);
	}

	private void drawDrawCanvas() {
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static boolean frameSetEditorIsPaused()
		{ return (frameSetEditor != null && frameSetEditor.isPaused); }
	
}
