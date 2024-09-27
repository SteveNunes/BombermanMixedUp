package tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import background_effects.BackgroundEffect;
import drawimage_stuffs.DrawImageEffects;
import entities.Bomb;
import entities.Explosion;
import enums.SpriteLayerType;
import fades.Fade;
import gameutil.FPSHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import light_spot_effects.ColoredLightSpot;
import light_spot_effects.LightSpot;
import maps.Brick;
import maps.Item;
import screen_pos_effects.TintScreen;
import screen_pos_effects.WaveScreen;

public abstract class Tools {
	
	private final static int DRAW_W = 1024;
	private final static int DRAW_H = 768;
	
	private static FPSHandler fpsHandler;
	private static Fade fade = null;
	private static BackgroundEffect backgroundEffect = null;
	private static WaveScreen waveScreen = null;
	private static TintScreen tintScreen = null;
	private static Map<SpriteLayerType, Canvas> canvasMap;
	private static Map<SpriteLayerType, GraphicsContext> gcMap;
	private static int pixelSize = 1;
	
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
	
	public static void loadTools() {
		fpsHandler = new FPSHandler(60);
		canvasMap = new HashMap<>();
		gcMap = new HashMap<>();
		for (SpriteLayerType t : SpriteLayerType.getList()) {
			canvasMap.put(t, new Canvas(t == SpriteLayerType.TEMP || t == SpriteLayerType.TINT ? 320 : DRAW_W,
																	t == SpriteLayerType.TEMP || t == SpriteLayerType.TINT ? 240 : DRAW_H));
			gcMap.put(t, canvasMap.get(t).getGraphicsContext2D());
			gcMap.get(t).setImageSmoothing(false);
			gcMap.get(t).clearRect(0, 0, canvasMap.get(t).getWidth(), canvasMap.get(t).getHeight());
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

	public static void clearAllCanvas() {
		for (SpriteLayerType t : SpriteLayerType.getList())
			gcMap.get(t).clearRect(0, 0, canvasMap.get(t).getWidth(), canvasMap.get(t).getHeight());
	}
	
	public static void drawAllCanvas(Canvas canvas, Color clearColor, int zoom, int offsetX, int offsetY) {
		if (clearColor == null)
			clearColor = Color.BLACK;
		GraphicsContext gc = canvas.getGraphicsContext2D();
		GraphicsContext gcTint = getTintCanvas().getGraphicsContext2D();
		GraphicsContext gcTemp = getTempCanvas().getGraphicsContext2D();
		gc.setFill(clearColor);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gcTint.clearRect(0, 0, getTintCanvas().getWidth(), getTintCanvas().getHeight());
		gcTemp.clearRect(0, 0, getTempCanvas().getWidth(), getTempCanvas().getHeight());
		for (SpriteLayerType layerType : SpriteLayerType.getList()) {
			if (layerType != SpriteLayerType.TEMP) {
				Canvas c = canvasMap.get(layerType);
				SnapshotParameters params = new SnapshotParameters();
				params.setFill(Color.TRANSPARENT);
				params.setViewport(new Rectangle2D(0, 0, c.getWidth(), c.getHeight()));
				if (backgroundEffect != null && layerType == SpriteLayerType.BACKGROUND)
					backgroundEffect.apply(getTempCanvas());
				if (layerType == SpriteLayerType.TINT) {
					pixelizeCanvas(pixelSize);
					if (tintScreen != null)
						tintScreen.apply(getTintCanvas());
					if (fade != null)
						fade.apply(getTintCanvas());
				}
				gcTemp.drawImage(c.snapshot(params, null), 0, 0);
			}
		}
		LightSpot.setMultipleLightSpots(gcTemp);
		LightSpot.setMultipleLightSpotsInDarkness(gcTemp);
		ColoredLightSpot.setMultipleColoredLightSpots(gcTemp);
		Image i = waveScreen != null ? waveScreen.apply(getTempCanvas()) : getTempCanvasSnapshot();
		Canvas c = getTempCanvas();
		gc.drawImage(i, 0, 0, c.getWidth(), c.getHeight(), offsetX, offsetY, c.getWidth() * zoom, c.getHeight() * zoom);
		ColoredLightSpot.clearTempColoredLightSpots();
		LightSpot.clearTempLightSpots();
	}
	
	public static Fade getFade()
		{ return fade; }
	
	public static WaveScreen getWaveScreen()
		{ return waveScreen; }
	
	public static TintScreen getTintScreen()
		{ return tintScreen; }
	
	public static BackgroundEffect getBackgroundEffect()
		{ return backgroundEffect; }
	
	public static void setFade(Fade fade)
		{ Tools.fade = fade; }
	
	public static void setWaveScreen(WaveScreen waveScreen)
		{ Tools.waveScreen = waveScreen; }
	
	public static void setTintScreen(TintScreen tintScreen)
		{ Tools.tintScreen = tintScreen; }
	
	public static void setBackgroundEffect(BackgroundEffect backgroundEffect)
		{ Tools.backgroundEffect = backgroundEffect; }

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
	
	public static Canvas getTempCanvas()
		{ return getCanvasMap().get(SpriteLayerType.TEMP); }

	public static Canvas getTintCanvas()
		{ return getCanvasMap().get(SpriteLayerType.TINT); }

	public static Image getTempCanvasSnapshot() {
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(0, 0, getTempCanvas().getWidth(), getTempCanvas().getHeight()));
		return getTempCanvas().snapshot(params, null);
	}

	public static void drawAllCanvasToTempCanvas() {
		getTempCanvas().getGraphicsContext2D().clearRect(0, 0, getTempCanvas().getWidth(), getTempCanvas().getHeight());
		Tools.drawAllCanvas(getTempCanvas());
	}

	public static void pixelizeCanvas(int pixelSize) {
		if (pixelSize > 1) {
			GraphicsContext gc = getTintCanvas().getGraphicsContext2D();
			Image i = getTempCanvasSnapshot();
			gc.setGlobalAlpha(1);
			for (int y = 0; y + pixelSize < getTintCanvas().getHeight(); y += pixelSize)
				for (int x = 0; x + pixelSize < getTintCanvas().getWidth(); x += pixelSize) {
					Color c = i.getPixelReader().getColor(x + pixelSize / 2, y + pixelSize / 2);
					gc.setFill(c);
					gc.fillRect(x, y, pixelSize, pixelSize);
				}
		}
	}
	
	public static void setOutputPixelSize(int pixelSize)
		{ Tools.pixelSize = pixelSize; }
	
	public static DrawImageEffects loadEffectsFromString(String arrayToString) {
		// NOTA: implementar
		return null;
	}

}