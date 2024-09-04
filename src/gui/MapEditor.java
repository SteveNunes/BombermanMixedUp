package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import maps.MapSet;
import maps.Tile;
import tools.GameMisc;
import util.FindFile;

public class MapEditor {

	private final static int WIN_W = 320;
	private final static int WIN_H = 240;
	private final static int TILE_SIZE = 16;
	
	private Scene sceneMain;
	@FXML
	private Canvas canvasMain;
	private GraphicsContext gcMain;
	private Canvas canvasDraw;
	private GraphicsContext gcDraw;
	private List<KeyCode> holdedKeys;
	private MapSet currentMapSet;
	private Font font;
	private int currentMapPos;
	private int currentLayer;
	private int mouseX;
	private int mouseY;
	private int mouseDX;
	private int mouseDY;
	private int zoom;
	
	public void init(Scene scene) {
	  sceneMain = scene;
		canvasMain.getGraphicsContext2D().setImageSmoothing(false);
		canvasDraw = new Canvas(WIN_W, WIN_H);
		canvasDraw.getGraphicsContext2D().setImageSmoothing(false);
		gcDraw = canvasDraw.getGraphicsContext2D();
		gcMain = canvasMain.getGraphicsContext2D();
		holdedKeys = new ArrayList<>();
		font = new Font("Lucida Console", 15);
		zoom = 3;
		currentLayer = 26;
		currentMapPos = -1;
		mouseX = 0;
		mouseY = 0;
		mouseDX = 0;
		mouseDY = 0;
		loadNextMap();
		setKeyboardEvents(sceneMain);
		setMouseEvents();
		mainLoop();
	}
	
	private void mainLoop() {
		drawDrawCanvas();
		drawMainCanvas();
		GameMisc.getFPSHandler().fpsCounter();
		if (!Main.close )
			Platform.runLater(() -> {
				String title = "Map Editor \t FPS: " + GameMisc.getFPSHandler().getFPS() + " \t "
						 + " Map: " + currentMapSet.getMapName() + " Layer: " + currentLayer;
				Main.stageMain.setTitle(title);
				mainLoop();
			});
	}
	
	public MapSet getCurrentMapSet()
		{ return currentMapSet; }
	
	public int getCurrentLayer()
		{ return currentLayer; }
	
	public void reloadMap()
		{ currentMapSet = new MapSet(currentMapSet.getMapName()); }

	public void loadPrevMap() {
		currentLayer = 26;
		Tile.tags = new String[200][200];
		List<File> maps = FindFile.findFile("appdata/maps/", "*", 0);
		if (--currentMapPos == -1)
			currentMapPos = maps.size() - 1;
		currentMapSet = new MapSet(maps.get(currentMapPos).getName().replace(".map", ""));
	}

	public void loadNextMap() {
		currentLayer = 26;
		Tile.tags = new String[200][200];
		List<File> maps = FindFile.findFile("appdata/maps/", "*", 0);
		if (++currentMapPos == maps.size())
			currentMapPos = 0;
		currentMapSet = new MapSet(maps.get(currentMapPos).getName().replace(".map", ""));
	}

	private void setKeyboardEvents(Scene scene) {
		scene.setOnKeyPressed(e -> {
			holdedKeys.add(e.getCode());
			if (e.getCode() == KeyCode.F7 || e.getCode() == KeyCode.F8) {
				if (e.getCode() == KeyCode.F7)
					currentLayer--;
				else
					currentLayer++;
				while (!currentMapSet.getLayersMap().containsKey(currentLayer)) {
					if (e.getCode() == KeyCode.F7)
						currentLayer--;
					else
						currentLayer++;
					if (currentLayer > 10000)
						currentLayer = -10000;
					else if (currentLayer < -10000)
						currentLayer = 10000;
				}
				Tile.tags = new String[200][200];
				reloadMap();
			}
			else if (e.getCode() == KeyCode.F10)
				loadNextMap();
			else if (e.getCode() == KeyCode.F9)
				loadPrevMap();
		});
	}

	private void setMouseEvents() {
		canvasMain.setOnMouseMoved(e -> {
			mouseX = (int)e.getX() / zoom;
			mouseY = (int)e.getY() / zoom;
			mouseDX = (int)e.getX() / (TILE_SIZE * zoom);
			mouseDY = (int)e.getY() / (TILE_SIZE * zoom);
		});
	}
	
	public void drawDrawCanvas() {
		gcDraw.setFill(Color.BLACK);
		gcDraw.fillRect(0, 0, WIN_W, WIN_H);
		if (currentMapSet.getLayersMap().containsKey(currentLayer))
			currentMapSet.getLayer(currentLayer).draw(gcDraw);
	}

	public void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
    gcMain.drawImage(canvasDraw.snapshot(null, null), 0, 0, WIN_W, WIN_H, 0, 0, WIN_W * zoom, WIN_H * zoom);
		boolean blink = System.currentTimeMillis() / 50 % 2 == 0;
		if (Tile.tags[mouseDY][mouseDX] != null) {
			gcMain.setFill(Color.LIGHTBLUE);
			gcMain.setStroke(Color.BLACK);
			gcMain.setFont(font);
			gcMain.setLineWidth(3);
			String str[] = Tile.tags[mouseDY][mouseDX].split(" ");
			int x = mouseX * zoom + 30, y = mouseY * zoom - 10, yy = str.length * 20;
			while (y + yy >= WIN_H * zoom - 30)
				y -= 20;
			yy = 0;
			for (String s : str) {
				Text text = new Text(s);
				text.setFont(font);
				while (x + (int)text.getBoundsInLocal().getWidth() + 20 >= canvasMain.getWidth())
					x -= 10;
			}
			for (String s : str) {
				gcMain.strokeText(s, x, y + (yy += 20));
				gcMain.fillText(s, x, y + yy);
			}
			gcMain.setStroke(Color.GREENYELLOW);
			gcMain.setLineWidth(2);
			gcMain.strokeRect(mouseDX * zoom * TILE_SIZE, mouseDY * zoom * TILE_SIZE, 16 * zoom, 16 * zoom);
		}
		for (int y = 0; blink && y < 200; y++)
			for (int x = 0; x < 200; x++)
				if (Tile.tags[y][x] != null) {
					gcMain.setStroke(Color.WHITESMOKE);
					gcMain.setLineWidth(2);
					gcMain.strokeRect(x * zoom * TILE_SIZE, y * zoom * TILE_SIZE, 16 * zoom, 16 * zoom);
				}
 	}
	
}
