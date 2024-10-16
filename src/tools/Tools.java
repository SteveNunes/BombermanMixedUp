package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import background_effects.BackgroundEffect;
import drawimage_stuffs.DrawImageEffects;
import entities.Bomb;
import entities.Explosion;
import enums.ImageFlip;
import enums.SpriteLayerType;
import fades.Fade;
import gameutil.FPSHandler;
import gui.util.ImageUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import light_spot_effects.ColoredLightSpot;
import light_spot_effects.LightSpot;
import maps.Brick;
import maps.Item;
import screen_pos_effects.TintScreen;
import screen_pos_effects.WavingImage;

public abstract class Tools {
	
	private static FPSHandler fpsHandler;
	private static Fade fade = null;
	private static BackgroundEffect backgroundEffect = null;
	private static WavingImage wavingImage = null;
	private static TintScreen tintScreen = null;
	private static Map<SpriteLayerType, List<DrawParams>> drawParamsList;
	private static Canvas canvasTemp;
	private static GraphicsContext gcTemp;
	private static int pixelSize = 1;
	
	public static Canvas getTempCanvas()
		{ return canvasTemp; }
	
	public static GraphicsContext getTempGc()
		{ return gcTemp;	}

	static int fadeN = 0;
	public static void loadTools() {
		fpsHandler = new FPSHandler(60);
		drawParamsList = new HashMap<>();
		canvasTemp = new Canvas(1000, 1000);
		gcTemp = canvasTemp.getGraphicsContext2D();
		gcTemp.setImageSmoothing(false);
	}
	
	public static void applyAllDraws(Canvas canvas)
		{ applyAllDraws(canvas, null, 1, 0, 0); }
	
	public static void applyAllDraws(Canvas canvas, int zoom)
		{ applyAllDraws(canvas, null, zoom, 0, 0); }
	
	public static void applyAllDraws(Canvas canvas, int offsetX, int offsetY)
		{ applyAllDraws(canvas, null, 1, offsetX, offsetY); }

	public static void applyAllDraws(Canvas canvas, int zoom, int offsetX, int offsetY)
		{ applyAllDraws(canvas, null, zoom, offsetX, offsetY); }
	
	public static void applyAllDraws(Canvas canvas, Color clearColor)
		{ applyAllDraws(canvas, clearColor, 1, 0, 0); }

	public static void applyAllDraws(Canvas canvas, Color clearColor, int zoom)
		{ applyAllDraws(canvas, clearColor, zoom, 0, 0); }
	
	public static void applyAllDraws(Canvas canvas, Color clearColor, int offsetX, int offsetY)
		{ applyAllDraws(canvas, clearColor, 1, offsetX, offsetY); }
	
	public static void applyAllDraws(Canvas canvas, Color clearColor, int zoom, int offsetX, int offsetY) {
		if (clearColor == null)
			clearColor = Color.BLACK;
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(clearColor);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (SpriteLayerType layerType : SpriteLayerType.getList())
			if (drawParamsList.containsKey(layerType))
				for (DrawParams dp : drawParamsList.get(layerType)) {
					if (backgroundEffect != null && layerType == SpriteLayerType.BACKGROUND)
						backgroundEffect.apply(getTempCanvas());
					dp.draw(gcTemp);
				}
		LightSpot.setMultipleLightSpots(gcTemp);
		LightSpot.setMultipleLightSpotsInDarkness(gcTemp);
		ColoredLightSpot.setMultipleColoredLightSpots(gcTemp);
		WritableImage i = wavingImage != null ? wavingImage.apply(getTempCanvasSnapshot()) : getTempCanvasSnapshot();
		Canvas c = getTempCanvas();
		gc.drawImage(i, 0, 0, c.getWidth(), c.getHeight(), offsetX, offsetY, c.getWidth() * zoom, c.getHeight() * zoom);
		ColoredLightSpot.clearTempColoredLightSpots();
		LightSpot.clearTempLightSpots();
		pixelizeCanvas(canvas, pixelSize);
		gcTemp.clearRect(0, 0, 1000, 1000);
		drawParamsList.clear();
		if (tintScreen != null)
			tintScreen.apply(canvas);
		if (fade != null)
			fade.apply(canvas);
	}
	
	public static Fade getFade()
		{ return fade; }
	
	public static WavingImage getWavingImage()
		{ return wavingImage; }
	
	public static TintScreen getTintScreen()
		{ return tintScreen; }
	
	public static BackgroundEffect getBackgroundEffect()
		{ return backgroundEffect; }
	
	public static void setFade(Fade fade)
		{ Tools.fade = fade; }
	
	public static void setWavingImage(WavingImage wavingImage)
		{ Tools.wavingImage = wavingImage; }
	
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
	
	public static WritableImage getTempCanvasSnapshot(WritableImage outputImage)
		{ return getCanvasSnapshot(getTempCanvas(), outputImage); }
	
	public static WritableImage getTempCanvasSnapshot(int w, int h, WritableImage outputImage) 
		{ return getCanvasSnapshot(getTempCanvas(), w, h, outputImage); }
	
	public static WritableImage getTempCanvasSnapshot(int x, int y, int w, int h, WritableImage outputImage) 
		{ return getCanvasSnapshot(getTempCanvas(), x, y, w, h, outputImage); }
	
	public static WritableImage getCanvasSnapshot(Canvas canvas, WritableImage outputImage)
		{ return getCanvasSnapshot(canvas, 0, 0, (int)canvas.getWidth(), (int)canvas.getHeight(), outputImage); }
	
	public static WritableImage getCanvasSnapshot(Canvas canvas, int w, int h, WritableImage outputImage)
		{ return getCanvasSnapshot(canvas, 0, 0, w, h, outputImage); }
	
	public static WritableImage getTempCanvasSnapshot()
		{ return getCanvasSnapshot(getTempCanvas()); }
	
	public static WritableImage getTempCanvasSnapshot(int w, int h) 
		{ return getCanvasSnapshot(getTempCanvas(), w, h); }
	
	public static WritableImage getTempCanvasSnapshot(int x, int y, int w, int h) 
		{ return getCanvasSnapshot(getTempCanvas(), x, y, w, h); }
	
	public static WritableImage getCanvasSnapshot(Canvas canvas)
		{ return getCanvasSnapshot(canvas, 0, 0, (int)canvas.getWidth(), (int)canvas.getHeight()); }
	
	public static WritableImage getCanvasSnapshot(Canvas canvas, int w, int h)
		{ return getCanvasSnapshot(canvas, 0, 0, w, h); }
	
	public static WritableImage getCanvasSnapshot(Canvas canvas, int x, int y, int w, int h)
		{ return getCanvasSnapshot(canvas, 0, 0, w, h, null); }

	public static WritableImage getCanvasSnapshot(Canvas canvas, int x, int y, int w, int h, WritableImage outputImage) {
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(x, y, w, h));
		return canvas.snapshot(params, outputImage);
	}
	
	public static void applyAllDrawsToTempCanvas() {
		getTempCanvas().getGraphicsContext2D().clearRect(0, 0, getTempCanvas().getWidth(), getTempCanvas().getHeight());
		Tools.applyAllDraws(getTempCanvas());
	}
	
	public static void pixelizeCanvas(Canvas canvas, int pixelSize) {
		if (pixelSize > 1) {
			int w = (int)canvas.getWidth(), h = (int)canvas.getHeight(), w2 = w / pixelSize, h2 = h / pixelSize;
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gcTemp.drawImage(canvas.snapshot(null, null), 0, 0, w, h, 0, 0, w2, h2);
			gc.drawImage(getCanvasSnapshot(canvasTemp, w2, h2), 0, 0, w2, h2, 0, 0, w, h);
		}
	}
	
	public static int getOutputPixelSize()
		{ return pixelSize; }

	public static void setOutputPixelSize(int pixelSize)
		{ Tools.pixelSize = pixelSize; }
	
	public static DrawImageEffects loadEffectsFromString(String arrayToString) {
		// NOTA: implementar
		return new DrawImageEffects();
	}
	
	public static String SpriteEffectsToString(DrawImageEffects effects) {
		// NOTA: implementar
		return "-";
	}

	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight)
		{ addDrawImageQueue(layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) 
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, opacity, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, DrawImageEffects effects)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, effects); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null); }
	
	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY)
		{ addDrawImageQueue(layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, null); }

	public static void addDrawImageQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		if (!drawParamsList.containsKey(layerType))
			drawParamsList.put(layerType, new ArrayList<>());
		drawParamsList.get(layerType).add(new DrawParams(image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, opacity, effects));
	}

}

class DrawParams {
	
	private Image image;
	private Integer sourceX;
	private Integer sourceY;
	private Integer sourceWidth;
	private Integer sourceHeight;
	private Integer targetX;
	private Integer targetY;
	private Integer targetWidth;
	private Integer targetHeight;
	private ImageFlip flip;
	private Integer rotateAngle;
	private Double opacity;
	private DrawImageEffects effects;
	
	public DrawParams(Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		this.image = image;
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.sourceWidth = sourceWidth;
		this.sourceHeight = sourceHeight;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
		this.flip = flip;
		this.rotateAngle = rotateAngle;
		this.opacity = opacity;
		this.effects = effects;
	}
	
	public void draw(GraphicsContext gc)
		{ ImageUtils.drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, opacity, effects); }

	@Override
	public String toString() {
		return "draw(" + sourceX + "," + sourceY + "," + sourceWidth + "," + sourceHeight + "," + targetX + "," + targetY + "," + targetWidth + "," + targetHeight + "," + flip + "," + rotateAngle + "," + opacity + ")";
	}
	
}