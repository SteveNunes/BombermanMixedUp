package gui;

import java.util.ArrayList;
import java.util.List;

import application.Main;
import entities.BomberMan;
import entities.CpuPlay;
import enums.CpuDificult;
import enums.GameInputMode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import maps.Brick;
import maps.Item;
import maps.MapSet;
import player.Player;
import tools.Draw;
import tools.Tools;

public class GameTikTok {

	public static final int ZOOM = 3;
	private final int WIN_W = Main.TILE_SIZE * 32;
	private final int WIN_H = Main.TILE_SIZE * 17;
	private List<KeyCode> holdedKeys;
	@FXML
	private Canvas canvasMain;
	private GraphicsContext gcMain;
	private Font font;
	private static List<String> echos = new ArrayList<>();
	private boolean showBlockMarks;

	public void init() {
		font = new Font("Lucida Console", 9);
		canvasMain.setWidth(WIN_W * ZOOM);
		canvasMain.setHeight(WIN_H * ZOOM);
		gcMain = canvasMain.getGraphicsContext2D();
		gcMain.setImageSmoothing(false);
		holdedKeys = new ArrayList<>();
		setEvents();
		BomberMan.addBomberMan(1, 0);
		Player.addPlayer();
		Player.getPlayer(0).setInputMode(GameInputMode.KEYBOARD);
		Player.getPlayer(0).setBomberMan(BomberMan.getBomberMan(0));
		MapSet.loadMap("TikTok-Small-Battle-01");
		fillWithCpu(0);
		mainLoop();
		showBlockMarks = false;
	}
	
	private void fillWithCpu(int total) { // TEMP
		for (int n = 1; n <= total; n++) {
			Player.addPlayer();
			Player player = Player.getPlayer(n);
			BomberMan bomber = BomberMan.addBomberMan(1, BomberMan.getTotalBomberMans());
			bomber.setPosition(MapSet.getInitialPlayerPosition(BomberMan.getTotalBomberMans() - 1));
			player.setBomberMan(bomber);
			player.setInputMode(GameInputMode.CPU);
			player.getBomberMan().setCpuPlay(new CpuPlay(player.getBomberMan(), CpuDificult.EASY));
		}
	}

	void setEvents() {
		Main.sceneMain.setOnKeyPressed(e -> {
			Player.convertOnKeyPressEvent(e);
			holdedKeys.add(e.getCode());
			if (e.getCode() == KeyCode.P) {
				for (Brick brick : new ArrayList<>(Brick.getBricks()))
					brick.destroy();
				for (Item item : new ArrayList<>(Item.getItems()))
					item.destroy();
			}
			if (e.getCode() == KeyCode.SPACE)
				CpuPlay.markTargets = !CpuPlay.markTargets; 
			if (e.getCode() == KeyCode.ESCAPE)
				Main.close();
			Game.checkFunctionKeys(e);
		});
		Main.sceneMain.setOnKeyReleased(e -> {
			Player.convertOnKeyReleaseEvent(e);
			holdedKeys.add(e.getCode());
		});
	}
	
	public static void addEcho(String string) {
		echos.add(0, string.toUpperCase());
		if (echos.size() == 66)
			echos.remove(echos.size() - 1);
	}

	void mainLoop() {
		try {
			Tools.getFPSHandler().fpsCounter();
			MapSet.run();
			Draw.applyAllDraws(canvasMain, ZOOM, -32 * ZOOM, -32 * ZOOM);
			gcMain.setFill(Color.YELLOW);
			gcMain.setFont(font);
			int y = (int)canvasMain.getHeight();
			for (String s : echos) {
				gcMain.fillText(s, canvasMain.getWidth() - 120, y -= 11);
			}
			if (showBlockMarks) // TEMP
				showBlockMarks();
			if (!Main.close)
				Platform.runLater(() -> {
					String title = "BomberMan Mixed Up!     FPS: " + Tools.getFPSHandler().getFPS() + "     Sobrecarga: " + Tools.getFPSHandler().getFreeTaskTicks();
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
		return ((shift == 0 && !isShiftHold()) || (shift == 1 && isShiftHold())) && ((ctrl == 0 && !isCtrlHold()) || (ctrl == 1 && isCtrlHold())) && ((alt == 0 && !isAltHold()) || (alt == 1 && isAltHold()));
	}

	boolean isCtrlHold() {
		return holdedKeys.contains(KeyCode.CONTROL);
	}

	boolean isShiftHold() {
		return holdedKeys.contains(KeyCode.SHIFT);
	}

	boolean isAltHold() {
		return holdedKeys.contains(KeyCode.ALT);
	}

	boolean isNoHolds() {
		return !isAltHold() && !isCtrlHold() && !isShiftHold();
	}

	void close() {}

	private void showBlockMarks() {
		Draw.drawBlockTypeMarks(gcMain, -32 * ZOOM, -32 * ZOOM, ZOOM, true, null);
	}

}