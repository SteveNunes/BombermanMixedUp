package application;
	
import enums.GameMode;
import gameutil.FPSHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tools.FrameSetEditor;
import tools.MapEditor;
import tools.Materials;
import tools.SquaredBg;


public class Main extends Application {
	
	public static int winW = 320;
	public static int winH = 240;
	public final static int tileSize = 16;

	public static GameMode mode = GameMode.MAP_EDITOR;
	private static FPSHandler fpsHandler; 
	private static Stage stageMain;
	private static VBox vBoxMain;
	public static Canvas canvasDraw;
	public static Canvas canvasMain;
	public static GraphicsContext gcDraw;
	public static GraphicsContext gcMain;
	private static int fps = 0;
	private static int fps2 = 0;
	private static long fpsCTime = System.currentTimeMillis();
	public static int zoom = 3;
	private static boolean close = false;
	
	@Override
	public void start(Stage stage) {
		try {
			if (mode != GameMode.GAME) {
				winW = 512;
				winH = 256;
			}				
			Materials.loadFromFiles();
			stageMain = stage;
			fpsHandler = new FPSHandler(60);
			canvasDraw = new Canvas(winW, winH);
			canvasDraw.getGraphicsContext2D().setImageSmoothing(false);
			gcDraw = canvasDraw.getGraphicsContext2D();
			canvasMain = new Canvas(winW * zoom, winH * zoom);
			canvasMain.getGraphicsContext2D().setImageSmoothing(false);
			gcMain = canvasMain.getGraphicsContext2D();
			vBoxMain = new VBox();
			vBoxMain.getChildren().add(canvasMain);
			stageMain.setTitle("BomberMan Mixed Up!");
			stageMain.setResizable(false);
			Scene scene = new Scene(vBoxMain);
			stageMain.setScene(scene);
			stageMain.show();
			SquaredBg.setSquaredBg(4, 3, 50, 255);
			stageMain.setOnCloseRequest(e -> close());
			if (mode == GameMode.FRAMESET_EDITOR)
				FrameSetEditor.start(scene);
			else if (mode == GameMode.MAP_EDITOR)
				MapEditor.start(scene);
			mainLoop();
		}
		catch(Exception e)
			{ e.printStackTrace(); }
	}
	
	private void close() {
		close = true;
		FrameSetEditor.close();
	}

	private void mainLoop() {
		if (mode == GameMode.FRAMESET_EDITOR)
			FrameSetEditor.drawDrawCanvas();
		else if (mode == GameMode.MAP_EDITOR)
			MapEditor.drawDrawCanvas();
    gcMain.drawImage(canvasDraw.snapshot(null, null), 0, 0, winW, winH, 0, 0, winW * zoom, winH * zoom);
		if (mode == GameMode.FRAMESET_EDITOR)
			FrameSetEditor.drawMainCanvas();
		else if (mode == GameMode.MAP_EDITOR)
			MapEditor.drawMainCanvas();
		fpsHandler.fpsCounter();
		if (!close ) {
			Platform.runLater(() -> mainLoop());
			if (System.currentTimeMillis() >= fpsCTime) {
				fpsCTime = System.currentTimeMillis() + 1000;
				fps2 = fps;
				fps = 0;
			}
			else
				fps++;
			if (mode == GameMode.FRAMESET_EDITOR)
				stageMain.setTitle("Sprite Editor \t FPS: " + fps2 + " \t " + FrameSetEditor.getTitle());
			else if (mode == GameMode.MAP_EDITOR)
				stageMain.setTitle("Map Editor \t FPS: " + fps2 + " \t " + MapEditor.getTitle());
			else
				stageMain.setTitle("BomberMan Mixed Up! \t FPS: " + fps2);
		}
	}
	
	public static void main(String[] args)
		{ launch(args); }
	
}
