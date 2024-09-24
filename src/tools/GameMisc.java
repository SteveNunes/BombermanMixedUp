package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import drawimage_stuffs.DrawImageEffects;
import entities.Bomb;
import entities.Explosion;
import enums.SpriteLayerType;
import gameutil.FPSHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import maps.Brick;
import maps.Item;

public abstract class GameMisc {
	
	private final static int DRAW_W = 1024;
	private final static int DRAW_H = 768;
	
	private static FPSHandler fpsHandler; 
	private static Map<SpriteLayerType, Canvas> canvasMap;
	private static Map<SpriteLayerType, GraphicsContext> gcMap;
	
	static {
		fpsHandler = new FPSHandler(60);
	}

	public static Map<SpriteLayerType, GraphicsContext> getGcMap()
		{ return gcMap; }
	
	public static Map<SpriteLayerType, Canvas> getCanvasMap()
		{ return canvasMap; }
	
	public static Canvas getCanvas()
		{ return canvasMap.get(SpriteLayerType.GROUND);	}
	
	public static Canvas getCanvas(SpriteLayerType layerType)
		{ return canvasMap.get(layerType); }

	public static GraphicsContext getGc()
		{ return gcMap.get(SpriteLayerType.GROUND);	}
	
	public static GraphicsContext getGc(SpriteLayerType layerType)
		{ return gcMap.get(layerType); }
	
	public static void generateDrawCanvasMap() {
		canvasMap = new HashMap<>();
		gcMap = new HashMap<>();
		List<SpriteLayerType> list = new ArrayList<>();
		for (SpriteLayerType t : SpriteLayerType.getList())
			list.add(t);
		list.add(SpriteLayerType.TEMP);
		for (SpriteLayerType t : list) {
			canvasMap.put(t, new Canvas(t == SpriteLayerType.TEMP ? 320 : DRAW_W, t == SpriteLayerType.TEMP ? 240 : DRAW_H));
			gcMap.put(t, canvasMap.get(t).getGraphicsContext2D());
			gcMap.get(t).setImageSmoothing(false);
		}
	}
	
	public static void drawAllCanvas(Canvas canvas)
		{ drawAllCanvas(canvas, null, 1, 0, 0); }
	
	public static void drawAllCanvas(Canvas canvas, int zoom)
		{ drawAllCanvas(canvas, null, zoom, 0, 0); }
	
	public static void drawAllCanvas(Canvas canvas, int offsetX, int offsetY)
		{ drawAllCanvas(canvas, null, 1, offsetX, offsetY); }

	public static void drawAllCanvas(Canvas canvas, int zoom, int offsetX, int offsetY)
		{ drawAllCanvas(canvas, null, zoom, offsetX, offsetY); }
	
	public static void drawAllCanvas(Canvas canvas, Color clearColor)
		{ drawAllCanvas(canvas, clearColor, 1, 0, 0); }

	public static void drawAllCanvas(Canvas canvas, Color clearColor, int zoom)
		{ drawAllCanvas(canvas, clearColor, zoom, 0, 0); }
	
	public static void drawAllCanvas(Canvas canvas, Color clearColor, int offsetX, int offsetY)
		{ drawAllCanvas(canvas, clearColor, 1, offsetX, offsetY); }

	public static void drawAllCanvas(Canvas canvas, Color clearColor, int zoom, int offsetX, int offsetY) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		if (clearColor != null) {
			gc.setFill(clearColor);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
		for (SpriteLayerType layerType : SpriteLayerType.getList()) {
			Canvas c = canvasMap.get(layerType);
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
			params.setViewport(new Rectangle2D(0, 0, c.getWidth(), c.getHeight()));
			gc.drawImage(c.snapshot(params, null), 0, 0, c.getWidth(), c.getHeight(), offsetX, offsetY, c.getWidth() * zoom, c.getHeight() * zoom);
		}
	}

	public static FPSHandler getFPSHandler()
		{ return fpsHandler; }

	public static <T> void moveItemTo(List<T> list, T item, int index) {
		if (list.contains(item)) {
			int max = list.size();
			if (index < -1 || index > max)
				GameMisc.throwRuntimeException(index + " - Invalid Index (Min: -1, Max: " + max + ")");
			if (index == -1)
				index = max - 1;
			else if (index == max)
				index = 0;
			list.remove(item);
			list.add(index, item);
		}
	}
	
	public static DrawImageEffects loadEffectsFromString(String string) {
		// NOTA: Implementar método
		return null;
	}

	public static void throwRuntimeException(String string) {
		Main.close();
		throw new RuntimeException(string);
	}
	
	public static void runAllStuffs() {
		Explosion.drawExplosions();
		Brick.drawBricks();
		Bomb.drawBombs();
		Item.drawItems();
	}
	
	public static Canvas getTempCanvas()
		{ return getCanvasMap().get(SpriteLayerType.TEMP); }

	public static Image getTempCanvasSnapshot() {
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(0, 0, getTempCanvas().getWidth(), getTempCanvas().getHeight()));
		return getTempCanvas().snapshot(params, null);
	}

	public static void drawAllCanvasToTempCanvas()
		{ GameMisc.drawAllCanvas(getTempCanvas()); }

}
