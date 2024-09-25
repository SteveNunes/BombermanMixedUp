package gui;
	
import java.util.List;

import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import tools.Tools;


public class Game {
	
	private final int winW = 312;
	private final int winH = 240;
	private List<KeyCode> holdedKeys;
	private Scene sceneMain;
	@FXML
	private Canvas canvasMain;
	private GraphicsContext gcMain;
	private Font font;
	
	public void init(Scene scene) {
		Tools.loadTools();
		sceneMain = scene;
		font = new Font("Lucida Console", 15);
		canvasMain.setWidth(winW * 3);
		canvasMain.setHeight(winH * 3);
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		
		mainLoop();
		
	}
	
	void mainLoop() {
		try {
			Tools.clearAllCanvas();
			Tools.getFPSHandler().fpsCounter();
			if (!Main.close )
				Platform.runLater(() -> {
					String title = "BomberMan Mixed Up!     FPS: " + Tools.getFPSHandler().getFPS() + "     ";
					Main.stageMain.setTitle(title);
					mainLoop();
				});
		}
		catch (Exception e) {
			e.printStackTrace();
			Main.close();
		}
	}

	boolean isHold(int shift, int ctrl, int alt) {
		return ((shift == 0 && !isShiftHold()) || (shift == 1 && isShiftHold())) &&
					 ((ctrl == 0 && !isCtrlHold()) || (ctrl == 1 && isCtrlHold())) &&
					 ((alt == 0 && !isAltHold()) || (alt == 1 && isAltHold()));
	}
	
	boolean isCtrlHold()
		{ return holdedKeys.contains(KeyCode.CONTROL); }
	
	boolean isShiftHold()
		{ return holdedKeys.contains(KeyCode.SHIFT); }

	boolean isAltHold()
		{ return holdedKeys.contains(KeyCode.ALT); }
	
	boolean isNoHolds()
		{ return !isAltHold() && !isCtrlHold() && !isShiftHold(); }

	void close() {
	}

}