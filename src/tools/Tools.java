package tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public abstract class Tools {
	
	private final static int DRAW_W = 1024;
	private final static int DRAW_H = 768;
	
	private static FPSHandler fpsHandler; 
	private static Map<SpriteLayerType, Canvas> canvasMap;
	private static Map<SpriteLayerType, GraphicsContext> gcMap;
	private static Double fadeValue = null;
	private static Double fadeValueInc = null;
	private static Color fadeColor;
	private static Double tintStrenght = null;
	private static Color tintColor;
	static Integer waveSpeed = null;
	static int waveSpeedTick = 0;
	static int wavePosStart = 0;
	static int[] wave = {1, 1, 0, 0, 0, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, 0, 0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
	
	static {
		fpsHandler = new FPSHandler(60);
	}
	
	public static void setScreenWave(int speed, int[] wavePattern) {
		waveSpeed = speed;
		waveSpeedTick = 0;
		wavePosStart = 0;
	}
	
	public static void disableScreenWave()
		{ waveSpeed = null; }

	public static void setScreenTint(Color color, double strenght) {
		if (color == null)
			throw new RuntimeException("color is null");
		if (strenght < 0 || strenght > 1)
			throw new RuntimeException("startValue must be between 0.0 and 1.0");
		tintColor = color;
		tintStrenght = strenght;
	}
	
	public static void disableScreenTint()
		{ tintColor = null; }

	public static void setScreenFadeIn(Color fromColor, double speed) {
		if (fromColor == null)
			throw new RuntimeException("fromColor is null");
		if (speed < 0.001 || speed > 1)
			throw new RuntimeException("speed must be between 0.001 and 1.0");
		setScreenFade(fromColor, 1d, -speed);
	}
	
	public static void setScreenFadeOut(Color toColor, double speed) {
		if (toColor == null)
			throw new RuntimeException("fromColor is null");
		if (speed < 0.001 || speed > 1)
			throw new RuntimeException("speed must be between 0.001 and 1.0");
		setScreenFade(toColor, 0d, speed);
	}
	
	public static void disableScreenFade()
		{ fadeColor = null; }

	private static void setScreenFade(Color color, Double startValue, Double incValue) {
		if (color == null)
			throw new RuntimeException("color is null");
		if (startValue == null)
			throw new RuntimeException("startValue is null");
		if (startValue < 0 || startValue > 1)
			throw new RuntimeException("startValue must be between 0.0 and 1.0");
		if (incValue < -1 || incValue > 1)
			throw new RuntimeException("incValue must be between -1.0 and 1.0");
		fadeValue = startValue;
		fadeValueInc = incValue;
		fadeColor = color;
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
		for (SpriteLayerType t : SpriteLayerType.getList()) {
			canvasMap.put(t, new Canvas(t == SpriteLayerType.TEMP || t == SpriteLayerType.TINT ? 320 : DRAW_W,
																	t == SpriteLayerType.TEMP || t == SpriteLayerType.TINT ? 240 : DRAW_H));
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
		GraphicsContext gcTint = getTintCanvas().getGraphicsContext2D();
		GraphicsContext gcTemp = getTempCanvas().getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		if (clearColor != null) {
			gc.setFill(clearColor);
			gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
		gcTint.clearRect(0, 0, 320, 240);
		if (tintColor != null) {
			gcTint.setFill(tintColor);
			gcTint.setGlobalAlpha(tintStrenght);
			gcTint.fillRect(0, 0, 320, 240);
		}
		if (fadeColor != null) {
			gcTint.setFill(fadeColor);
			gcTint.setGlobalAlpha(fadeValue);
			if ((fadeValue += fadeValueInc) > 1)
				fadeValueInc = 1d;
			else if (fadeValue < -1)
				fadeValue = -1d;
			gcTint.fillRect(0, 0, 320, 240);
		}
		gcTemp.clearRect(0, 0, 320, 240);
		for (SpriteLayerType layerType : SpriteLayerType.getList()) {
			if (layerType != SpriteLayerType.TEMP) {
				Canvas c = canvasMap.get(layerType);
				SnapshotParameters params = new SnapshotParameters();
				params.setFill(Color.TRANSPARENT);
				params.setViewport(new Rectangle2D(0, 0, c.getWidth(), c.getHeight()));
				gcTemp.drawImage(c.snapshot(params, null), 0, 0);
			}
		}
		Image i = getTempCanvasSnapshot();
		if (waveSpeed != null) {
			int wavePos = wavePosStart;
			for (int y = 0; y < 240; y++) {
				gcTemp.drawImage(i, 0, y, 320, 1, wave[wavePos], y, 320, 1);
				if (++wavePos == wave.length)
					wavePos = 0;
			}
			if (waveSpeed == 1 || ++waveSpeedTick == waveSpeed) {
				waveSpeedTick = 0;
				if (++wavePosStart == wave.length)
					wavePosStart = 0;
			}
			i = getTempCanvasSnapshot();
		}
		Canvas c = getTempCanvas();
		gc.drawImage(i, 0, 0, c.getWidth(), c.getHeight(), offsetX, offsetY, c.getWidth() * zoom, c.getHeight() * zoom);
	}
	
	public static FPSHandler getFPSHandler()
		{ return fpsHandler; }

	public static <T> void moveItemTo(List<T> list, T item, int index) {
		if (list.contains(item)) {
			int max = list.size();
			if (index < -1 || index > max)
				throw new RuntimeException(index + " - Invalid Index (Min: -1, Max: " + max + ")");
			if (index == -1)
				index = max - 1;
			else if (index == max)
				index = 0;
			list.remove(item);
			list.add(index, item);
		}
	}
	
	public static void runAllStuffs() {
		Explosion.drawExplosions();
		Brick.drawBricks();
		Bomb.drawBombs();
		Item.drawItems();
	}
	
	private static Canvas getTempCanvas()
		{ return getCanvasMap().get(SpriteLayerType.TEMP); }

	private static Canvas getTintCanvas()
		{ return getCanvasMap().get(SpriteLayerType.TINT); }

	public static Image getTempCanvasSnapshot() {
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(0, 0, getTempCanvas().getWidth(), getTempCanvas().getHeight()));
		return getTempCanvas().snapshot(params, null);
	}

	public static void drawAllCanvasToTempCanvas()
		{ Tools.drawAllCanvas(getTempCanvas()); }

	public static DrawImageEffects loadEffectsFromString(String arrayToString) {
		// NOTA: implementar
		return null;
	}

}
