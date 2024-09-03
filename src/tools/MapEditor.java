package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import maps.Layer;
import maps.MapSet;
import util.FindFile;

public class MapEditor {

	private static List<KeyCode> holdedKeys;
	public static MapSet currentMapSet;
	public static int currentMapPos;
	public static int currentLayer;
	private static int mouseX = 0;
	private static int mouseY = 0;
	private static int mouseDX = 0;
	private static int mouseDY = 0;
	
	public static MapSet getCurrentMapSet()
		{ return currentMapSet; }
	
	public static int getCurrentLayer()
		{ return currentLayer; }

	public static void loadPrevMap() {
		Layer.tags = new String[200][200];
		List<File> maps = FindFile.findFile("appdata/maps/", "*", 0);
		if (--currentMapPos == -1)
			currentMapPos = maps.size() - 1;
		currentMapSet = new MapSet(maps.get(currentMapPos).getName().replace(".map", ""));
	}

	public static void loadNextMap() {
		Layer.tags = new String[200][200];
		List<File> maps = FindFile.findFile("appdata/maps/", "*", 0);
		if (++currentMapPos == maps.size())
			currentMapPos = 0;
		currentMapSet = new MapSet(maps.get(currentMapPos).getName().replace(".map", ""));
	}

	public static void start(Scene scene) {
		holdedKeys = new ArrayList<>();
		currentLayer = 26;
		currentMapPos = -1;
		loadNextMap();
		setKeyboardEvents(scene);
		setMouseEvents(scene);
	}
	
	private static void setKeyboardEvents(Scene scene) {
		scene.setOnKeyPressed(e -> {
			holdedKeys.add(e.getCode());
			if (e.getCode() == KeyCode.F8) {
				if (currentLayer < 0)
					currentLayer--;
				else
					currentLayer++;
				while (!currentMapSet.getLayersMap().containsKey(currentLayer)) {
					if (currentLayer < 0)
						currentLayer--;
					else
						currentLayer++;
					if (currentLayer > 10000)
						currentLayer = -1;
					else if (currentLayer < -10000) {
						currentLayer = 26;
						break;
					}
				}
			}
			else if (e.getCode() == KeyCode.F10)
				loadNextMap();
			else if (e.getCode() == KeyCode.F9)
				loadPrevMap();
		});
	}

	private static void setMouseEvents(Scene scene) {
		scene.setOnMouseMoved(e -> {
			mouseX = (int)e.getX() / Main.zoom;
			mouseY = (int)e.getY() / Main.zoom;
			mouseDX = (int)e.getX() / (Main.tileSize * Main.zoom);
			mouseDY = (int)e.getY() / (Main.tileSize * Main.zoom);
		});
	}
	
	public static void drawDrawCanvas() {
		Main.gcDraw.setFill(Color.BLACK);
		Main.gcDraw.fillRect(0, 0, Main.winW, Main.winH);
		currentMapSet.draw(Main.gcDraw);
	}

	public static void drawMainCanvas() { // Coisas que serão desenhadas no Canvas frontal (maior resolucao)
		boolean blink = System.currentTimeMillis() / 50 % 2 == 0;
		if (Layer.tags[mouseDY][mouseDX] != null) {
			Main.gcMain.setFill(Color.LIGHTBLUE);
			Main.gcMain.setStroke(Color.BLACK);
			Main.gcMain.setFont(new Font("Lucida Console", 15));
			Main.gcMain.setLineWidth(3);
			String str = Layer.tags[mouseDY][mouseDX];
			int x = mouseX * Main.zoom, y = mouseY * Main.zoom;
			Main.gcMain.strokeText(str, x, y);
			Main.gcMain.fillText(str, x, y);
			Main.gcMain.setStroke(Color.GREENYELLOW);
			Main.gcMain.setLineWidth(2);
			Main.gcMain.strokeRect(mouseDX * Main.zoom * Main.tileSize, mouseDY * Main.zoom * Main.tileSize, 16 * Main.zoom, 16 * Main.zoom);
		}
		for (int y = 0; blink && y < 200; y++)
			for (int x = 0; x < 200; x++)
				if (Layer.tags[y][x] != null) {
					Main.gcMain.setStroke(Color.WHITESMOKE);
					Main.gcMain.setLineWidth(2);
					Main.gcMain.strokeRect(x * Main.zoom * Main.tileSize, y * Main.zoom * Main.tileSize, 16 * Main.zoom, 16 * Main.zoom);
				}
 	}
	
	public static String getTitle() {
		return "";
	}

}
