package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import background_effects.BackgroundEffect;
import drawimage_stuffs.DrawImageEffects;
import enums.DrawType;
import enums.ImageFlip;
import enums.SpriteLayerType;
import fades.Fade;
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
import screen_pos_effects.TintScreen;
import screen_pos_effects.WavingImage;

public abstract class Draw {
	
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

	public static void loadStuffs() {
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
		for (SpriteLayerType layerType : SpriteLayerType.getList()) {
			if (drawParamsList.containsKey(layerType)) {
				if (layerType == SpriteLayerType.SPRITE)
					drawParamsList.get(layerType).sort((p1, p2) -> p1.getFrontValue() - p2.getFrontValue());
				for (DrawParams dp : drawParamsList.get(layerType)) {
					if (backgroundEffect != null && layerType == SpriteLayerType.BACKGROUND)
						backgroundEffect.apply(getTempCanvas());
					dp.draw(gcTemp);
				}
			}
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
		{ Draw.fade = fade; }
	
	public static void setWavingImage(WavingImage wavingImage)
		{ Draw.wavingImage = wavingImage; }
	
	public static void setTintScreen(TintScreen tintScreen)
		{ Draw.tintScreen = tintScreen; }
	
	public static void setBackgroundEffect(BackgroundEffect backgroundEffect)
		{ Draw.backgroundEffect = backgroundEffect; }

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
		Draw.applyAllDraws(getTempCanvas());
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
		{ Draw.pixelSize = pixelSize; }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) 
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, opacity, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, effects); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY)
		{ addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, opacity, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight)
		{ addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) 
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, opacity, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, DrawImageEffects effects)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, effects); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY)
		{ addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, null); }

	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		if (!drawParamsList.containsKey(layerType))
			drawParamsList.put(layerType, new ArrayList<>());
		drawParamsList.get(layerType).add(new DrawParams(frontValue, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, opacity, effects));
	}
	
	public static void addDrawQueue(SpriteLayerType layerType, DrawType drawType)
		{ addDrawQueue(Integer.MAX_VALUE, layerType, drawType, null, null); }

	public static void addDrawQueue(SpriteLayerType layerType, DrawType drawType, Color color)
		{ addDrawQueue(Integer.MAX_VALUE, layerType, drawType, color, null); }
	
	public static void addDrawQueue(SpriteLayerType layerType, DrawType drawType, double ... params)
		{ addDrawQueue(Integer.MAX_VALUE, layerType, drawType, null, params); }
	
	public static void addDrawQueue(SpriteLayerType layerType, DrawType drawType, Color color, double ... params)
		{ addDrawQueue(Integer.MAX_VALUE, layerType, drawType, color, params); }

	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType)
		{ addDrawQueue(frontValue, layerType, drawType, null, null); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType, Color color)
		{ addDrawQueue(frontValue, layerType, drawType, color, null); }
		
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType, double ... params)
		{ addDrawQueue(frontValue, layerType, drawType, null, params); }
	
	public static void addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType, Color color, double ... params) {
		if (!drawParamsList.containsKey(layerType))
			drawParamsList.put(layerType, new ArrayList<>());
		drawParamsList.get(layerType).add(new DrawParams(frontValue, drawType, color, params));
	}

}

class DrawParams {
	
	private Color color;
	private double[] params;
	private DrawType drawType;

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
	private int frontValue;
	
	DrawParams(int frontValue, DrawType drawType, Color color, double ... params) {
		this.frontValue = frontValue;
		this.color = color;
		this.params = params;
		this.drawType = drawType;
	}

	DrawParams(int frontValue, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		drawType = DrawType.IMAGE;
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
		this.frontValue = frontValue;
	}
	
	public int getFrontValue()
		{ return frontValue; }
	
	public void draw(GraphicsContext gc) {
		if (drawType == DrawType.IMAGE)
			ImageUtils.drawImage(gc, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, opacity, effects);
		else if (drawType == DrawType.FILL_OVAL)
			gc.fillOval(params[0], params[1], params[2], params[3]);
		else if (drawType == DrawType.FILL_RECT)
			gc.fillRect(params[0], params[1], params[2], params[3]);
		else if (drawType == DrawType.STROKE_OVAL)
			gc.strokeOval(params[0], params[1], params[2], params[3]);
		else if (drawType == DrawType.STROKE_RECT)
			gc.strokeRect(params[0], params[1], params[2], params[3]);
		else if (drawType == DrawType.STROKE_LINE)
			gc.strokeLine(params[0], params[1], params[2], params[3]);
		else if (drawType == DrawType.SET_FILL)
			gc.setFill(color);
		else if (drawType == DrawType.SET_STROKE)
			gc.setStroke(color);
		else if (drawType == DrawType.SET_LINE_WIDTH)
			gc.setLineWidth(params[0]);
		else if (drawType == DrawType.SAVE)
			gc.save();
		else if (drawType == DrawType.RESTORE)
			gc.restore();
		else if (drawType == DrawType.SET_GLOBAL_ALPHA)
			gc.setGlobalAlpha(params[0]);
	}

}