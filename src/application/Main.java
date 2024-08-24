package application;
	
import java.security.SecureRandom;

import gameutil.FPSHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tools.FrameSetEditor;
import tools.Materials;
import tools.Sound;
import tools.SquaredBg;


public class Main extends Application {
	
	public final static int winW = 320;
	public final static int winH = 240;
	public final static int tileSize = 16;

	private static FPSHandler fpsHandler = new FPSHandler(60); 
	private static SecureRandom random;
	private static Stage stageMain;
	private static VBox vBoxMain;
	public static Canvas canvasDraw;
	public static Canvas canvasMain;
	public static GraphicsContext gcDraw;
	public static GraphicsContext gcMain;
	private static int fps = 0, fps2 = 0;
	private static long fpsCTime = System.currentTimeMillis();
	public static int zoom = 3;
	public static boolean spriteEditor = true;
	private static boolean close = false;
	
	@Override
	public void start(Stage stage) {
		try {
			random = new SecureRandom();
			stageMain = stage;
			canvasDraw = new Canvas(winW, winH);
			canvasMain = new Canvas(winW * zoom, winH * zoom);
			gcDraw = canvasDraw.getGraphicsContext2D();
			gcMain = canvasMain.getGraphicsContext2D();
			vBoxMain = new VBox();
			vBoxMain.getChildren().add(canvasMain);
			stageMain.setTitle("BomberMan for TikTok live");
			stageMain.setResizable(false);
			Scene scene = new Scene(vBoxMain);
			stageMain.setScene(scene);
			canvasDraw.getGraphicsContext2D().setImageSmoothing(false);
			canvasMain.getGraphicsContext2D().setImageSmoothing(false);
			Materials.loadFromFiles();
			stageMain.show();
			SquaredBg.setSquaredBg(3, 3, 50, 255);
			stageMain.setOnCloseRequest(e -> close());
			
			if (spriteEditor)
				FrameSetEditor.start(scene);
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
		gcDraw.setFill(Color.BLACK);
		gcDraw.fillRect(0, 0, winW, winH);
		SquaredBg.draw(gcDraw);
		if (spriteEditor)
			FrameSetEditor.drawDrawCanvas();
    gcMain.drawImage(canvasDraw.snapshot(null, null), 0, 0, winW, winH, 0, 0, winW * zoom, winH * zoom);
		if (spriteEditor)
			FrameSetEditor.drawMainCanvas();
		Sound.playAllSoundsFromQueue();
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
			if (spriteEditor)
				stageMain.setTitle("Sprite Editor \t FPS: " + fps2 + " \t " + FrameSetEditor.getTitle());
			else
				stageMain.setTitle("BomberMan Mixed Up! \t FPS: " + fps2);
		}
	}
	
	public static void main(String[] args)
		{ launch(args); }
	
	public static int getRandom(int min, int max)
		{ return random.nextInt(++max - min) + min; }
	
}
