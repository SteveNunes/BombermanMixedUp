package tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import application.Main;
import background_effects.BackgroundEffect;
import drawimage_stuffs.DrawImageEffects;
import enums.DrawType;
import enums.GameMode;
import enums.ImageFlip;
import enums.SpriteLayerType;
import enums.TileProp;
import fades.Fade;
import gui.MapEditor;
import gui.util.ControllerUtils;
import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import light_spot_effects.ColoredLightSpot;
import light_spot_effects.LightSpot;
import maps.MapSet;
import maps.Tile;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import screen_pos_effects.TintScreen;
import screen_pos_effects.WavingImage;
import util.Misc;

public abstract class Draw {

	private static Fade fade = null;
	private static BackgroundEffect backgroundEffect = null;
	private static WavingImage wavingImage = null;
	private static TintScreen tintScreen = null;
	private static Map<SpriteLayerType, List<DrawParams>> drawParamsList;
	private static Canvas canvasTemp;
	private static GraphicsContext gcTemp;
	private static int pixelSize = 1;
	private static Map<Position, Pair<Color, String>> fixedMarks = new HashMap<>();
	private static Map<Position, Pair<Color, String>> marks = new HashMap<>();

	public static void clearFixedMarks() {
		fixedMarks.clear();
	}
	
	public static void removeFixedMarkTile(Position position) {
		fixedMarks.remove(position);
	}
	
	public static void removeFixedMarkTile(TileCoord coord) {
		fixedMarks.remove(coord.getPosition());
	}

	public static void markTile(Position position, Color color) {
		markTile(position, color, null);
	}

	public static void markTile(TileCoord coord, Color color) {
		markTile(coord, color, null);
	}

	public static void markTile(TileCoord coord, Color color, String hint) {
		markTile(coord.getPosition(), color, hint);
	}

	public static void markTile(Position position, Color color, String hint) {
		marks.put(position, new Pair<Color, String>(color, hint));
	}
	
	public static void addFixedMarkTile(TileCoord coord, Color color) {
		addFixedMarkTile(coord, color, null);
	}
	
	public static void addFixedMarkTile(Position position, Color color) {
		addFixedMarkTile(position, color, null);
	}
	
	public static void addFixedMarkTile(TileCoord coord, Color color, String hint) {
		addFixedMarkTile(coord.getPosition(), color, hint);
	}
	
	public static void addFixedMarkTile(Position position, Color color, String hint) {
		fixedMarks.put(position, new Pair<Color, String>(color, hint));
	}
	
	public static Canvas getTempCanvas() {
		return canvasTemp;
	}

	public static GraphicsContext getTempGc() {
		return gcTemp;
	}

	public static void loadStuffs() {
		drawParamsList = new HashMap<>();
		canvasTemp = new Canvas(1000, 1000);
		gcTemp = canvasTemp.getGraphicsContext2D();
		gcTemp.setImageSmoothing(false);
	}

	public static void applyAllDraws(Canvas canvas) {
		applyAllDraws(canvas, null, 1, 0, 0);
	}

	public static void applyAllDraws(Canvas canvas, int zoom) {
		applyAllDraws(canvas, null, zoom, 0, 0);
	}

	public static void applyAllDraws(Canvas canvas, int offsetX, int offsetY) {
		applyAllDraws(canvas, null, 1, offsetX, offsetY);
	}

	public static void applyAllDraws(Canvas canvas, int zoom, int offsetX, int offsetY) {
		applyAllDraws(canvas, null, zoom, offsetX, offsetY);
	}

	public static void applyAllDraws(Canvas canvas, Color clearColor) {
		applyAllDraws(canvas, clearColor, 1, 0, 0);
	}

	public static void applyAllDraws(Canvas canvas, Color clearColor, int zoom) {
		applyAllDraws(canvas, clearColor, zoom, 0, 0);
	}

	public static void applyAllDraws(Canvas canvas, Color clearColor, int offsetX, int offsetY) {
		applyAllDraws(canvas, clearColor, 1, offsetX, offsetY);
	}

	public static void applyAllDraws(Canvas canvas, Color clearColor, int zoom, int offsetX, int offsetY) {
		if (clearColor == null)
			clearColor = Color.BLACK;
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(clearColor);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (SpriteLayerType layerType : SpriteLayerType.values()) {
			if (drawParamsList.containsKey(layerType)) {
				if (layerType == SpriteLayerType.SPRITE)
					drawParamsList.get(layerType).sort((p1, p2) -> p1.getFrontValue() - p2.getFrontValue());
				List<DrawParams> draws = new ArrayList<>(drawParamsList.get(layerType));
				for (DrawParams dp : draws) {
					if (backgroundEffect != null && layerType == SpriteLayerType.BACKGROUND)
						backgroundEffect.apply(getTempCanvas());
					dp.draw(gcTemp, -offsetX / zoom, -offsetY / zoom, (int)canvas.getWidth() / zoom, (int)canvas.getHeight() / zoom);
					if (!dp.isGhosting())
						drawParamsList.get(layerType).remove(dp);
				}
			}
		}
		LightSpot.setMultipleLightSpots(gcTemp);
		LightSpot.setMultipleLightSpotsInDarkness(gcTemp);
		ColoredLightSpot.setMultipleColoredLightSpots(gcTemp);
		if (Main.GAME_MODE != GameMode.MAP_EDITOR || MapEditor.isPlaying()) {
			int minX = (int)MapSet.getMapMinLimit().getX(), minY = (int)MapSet.getMapMinLimit().getY(),
					maxX = (int)MapSet.getMapMaxLimit().getX(), maxY = (int)MapSet.getMapMaxLimit().getY();
			gcTemp.setFill(Color.BLACK);
			gcTemp.fillRect(minX - Main.TILE_SIZE * 2, minY - Main.TILE_SIZE * 2, Main.TILE_SIZE * 3, maxY + Main.TILE_SIZE);
			gcTemp.fillRect(minX - Main.TILE_SIZE * 2, minY - Main.TILE_SIZE * 2, maxX + Main.TILE_SIZE, Main.TILE_SIZE * 3);
			gcTemp.fillRect(maxX, minY - Main.TILE_SIZE * 2, Main.TILE_SIZE * 3, maxY + Main.TILE_SIZE * 3);
			gcTemp.fillRect(minX - Main.TILE_SIZE * 2, maxY, maxX + Main.TILE_SIZE * 5, Main.TILE_SIZE * 3);
		}
		WritableImage i = getTempCanvasSnapshot((int)-offsetX / zoom, (int)-offsetY / zoom, (int)canvas.getWidth() / zoom, (int)canvas.getHeight() / zoom);
		i = wavingImage != null ? wavingImage.apply(i) : i;
		Canvas c = getTempCanvas();
		gc.drawImage(i, 0, 0, c.getWidth(), c.getHeight(), 0, 0, c.getWidth() * zoom, c.getHeight() * zoom);
		gc.save();
		gc.setFont(new Font("Lucida Console", 20));
		for (var p : Arrays.asList(marks, fixedMarks))
			for (Position pos : p.keySet()) {
				gc.setLineWidth(3);
				gc.setFill(p.get(pos).getKey());
				gc.setStroke(p.get(pos).getKey());
				gc.strokeRect(pos.getX() * zoom + offsetX, pos.getY() * zoom + offsetY, Main.TILE_SIZE * zoom - 3, Main.TILE_SIZE * zoom - 3);
				String s = p.get(pos).getValue();
				if (s != null) {
					gc.setLineWidth(1);
					gc.fillText(s, pos.getX() * zoom + offsetX + Main.TILE_SIZE / 3 * zoom, pos.getY() * zoom + offsetY + Main.TILE_SIZE / 2 * zoom);
					gc.strokeText(s, pos.getX() * zoom + offsetX + Main.TILE_SIZE / 3 * zoom, pos.getY() * zoom + offsetY + Main.TILE_SIZE / 2 * zoom);
				}
			}
		marks.clear();
		gc.restore();
		ColoredLightSpot.clearTempColoredLightSpots();
		LightSpot.clearTempLightSpots();
		pixelizeCanvas(canvas, pixelSize);
		gcTemp.clearRect(0, 0, 1000, 1000);
		if (tintScreen != null)
			tintScreen.apply(canvas);
		if (fade != null)
			fade.apply(canvas);
	}

	public static Fade getFade() {
		return fade;
	}

	public static WavingImage getWavingImage() {
		return wavingImage;
	}

	public static TintScreen getTintScreen() {
		return tintScreen;
	}

	public static BackgroundEffect getBackgroundEffect() {
		return backgroundEffect;
	}

	public static void setFade(Fade fade) {
		Draw.fade = fade;
	}

	public static void setWavingImage(WavingImage wavingImage) {
		Draw.wavingImage = wavingImage;
	}

	public static void setTintScreen(TintScreen tintScreen) {
		Draw.tintScreen = tintScreen;
	}

	public static void setBackgroundEffect(BackgroundEffect backgroundEffect) {
		Draw.backgroundEffect = backgroundEffect;
	}

	public static WritableImage getTempCanvasSnapshot(WritableImage outputImage) {
		return ImageUtils.getCanvasSnapshot(getTempCanvas(), outputImage);
	}

	public static WritableImage getTempCanvasSnapshot(int w, int h, WritableImage outputImage) {
		return ImageUtils.getCanvasSnapshot(getTempCanvas(), w, h, outputImage);
	}

	public static WritableImage getTempCanvasSnapshot(int x, int y, int w, int h, WritableImage outputImage) {
		return ImageUtils.getCanvasSnapshot(getTempCanvas(), x, y, w, h, outputImage);
	}

	public static WritableImage getTempCanvasSnapshot() {
		return ImageUtils.getCanvasSnapshot(getTempCanvas());
	}

	public static WritableImage getTempCanvasSnapshot(int w, int h) {
		return ImageUtils.getCanvasSnapshot(getTempCanvas(), w, h);
	}

	public static WritableImage getTempCanvasSnapshot(int x, int y, int w, int h) {
		return ImageUtils.getCanvasSnapshot(getTempCanvas(), x, y, w, h);
	}

	public static void applyAllDrawsToTempCanvas() {
		getTempCanvas().getGraphicsContext2D().clearRect(0, 0, getTempCanvas().getWidth(), getTempCanvas().getHeight());
		Draw.applyAllDraws(getTempCanvas());
	}

	public static void pixelizeCanvas(Canvas canvas, int pixelSize) {
		if (pixelSize > 1) {
			int w = (int) canvas.getWidth(), h = (int) canvas.getHeight(), w2 = w / pixelSize, h2 = h / pixelSize;
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gcTemp.drawImage(canvas.snapshot(null, null), 0, 0, w, h, 0, 0, w2, h2);
			gc.drawImage(ImageUtils.getCanvasSnapshot(canvasTemp, w2, h2), 0, 0, w2, h2, 0, 0, w, h);
		}
	}

	public static int getOutputPixelSize() {
		return pixelSize;
	}

	public static void setOutputPixelSize(int pixelSize) {
		Draw.pixelSize = pixelSize;
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, opacity, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, effects);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer targetX, Integer targetY) {
		return addDrawQueue(0, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(0, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, opacity, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Double opacity) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, opacity, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle, Double opacity) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, opacity, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Integer rotateAngle) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, Double opacity) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, opacity, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, null, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight) {
		return addDrawQueue(frontValue, layerType, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, null, null, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, opacity, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, DrawImageEffects effects) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, effects);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Integer rotateAngle) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip, Double opacity) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle, Double opacity) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Integer rotateAngle) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, rotateAngle, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, Double opacity) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, opacity, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY, ImageFlip flip) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, flip, null, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer targetX, Integer targetY) {
		return addDrawQueue(frontValue, layerType, image, null, null, null, null, targetX, targetY, null, null, null, null, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, Image image, Integer sourceX, Integer sourceY, Integer sourceWidth, Integer sourceHeight, Integer targetX, Integer targetY, Integer targetWidth, Integer targetHeight, ImageFlip flip, Integer rotateAngle, Double opacity, DrawImageEffects effects) {
		if (!drawParamsList.containsKey(layerType))
			drawParamsList.put(layerType, new ArrayList<>());
		DrawParams drawParams = new DrawParams(frontValue, image, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, targetWidth, targetHeight, flip, rotateAngle, opacity, effects);
		drawParamsList.get(layerType).add(drawParams);
		return drawParams;
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, DrawType drawType) {
		return addDrawQueue(Integer.MAX_VALUE, layerType, drawType, null, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, DrawType drawType, Color color) {
		return addDrawQueue(Integer.MAX_VALUE, layerType, drawType, color, null);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, DrawType drawType, double... params) {
		return addDrawQueue(Integer.MAX_VALUE, layerType, drawType, null, params);
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, DrawType drawType, Color color, double... params) {
		return addDrawQueue(Integer.MAX_VALUE, layerType, drawType, color, params);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType) {
		return addDrawQueue(frontValue, layerType, drawType, null, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType, Color color) {
		return addDrawQueue(frontValue, layerType, drawType, color, null);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType, double... params) {
		return addDrawQueue(frontValue, layerType, drawType, null, params);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType, Color color, double... params) {
		if (!drawParamsList.containsKey(layerType))
			drawParamsList.put(layerType, new ArrayList<>());
		DrawParams drawParams = new DrawParams(frontValue, drawType, color, params);
		drawParamsList.get(layerType).add(drawParams);
		return drawParams;
	}

	public static DrawParams addDrawQueue(SpriteLayerType layerType, DrawType drawType, Font font) {
		return addDrawQueue(0, layerType, drawType, null, font, null, null);
	}
	
	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType, Font font) {
		return addDrawQueue(frontValue, layerType, drawType, null, font, null, null);
	}
	
	public static DrawParams addDrawQueue(SpriteLayerType layerType, DrawType drawType, Color color, Font font, String text, double... params) {
		return addDrawQueue(0, layerType, drawType, color, font, text, params);
	}

	public static DrawParams addDrawQueue(int frontValue, SpriteLayerType layerType, DrawType drawType, Color color, Font font, String text, double... params) {
		if (!drawParamsList.containsKey(layerType))
			drawParamsList.put(layerType, new ArrayList<>());
		DrawParams drawParams = new DrawParams(frontValue, drawType, color, font, text, params);
		drawParamsList.get(layerType).add(drawParams);
		return drawParams;
	}
	
	public static void drawTilePropsOverCursor(Canvas canvas, Font font, TileCoord coord, int offsetX, int offsetY) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (MapSet.getCurrentLayer().haveTilesOnCoord(coord)) {
			Tile tile = MapSet.getFirstBottomTileFromCoord(coord);
			if (tile != null) {
				int x, y = tile.outY * Main.getZoom() + (Main.TILE_SIZE * Main.getZoom()) / 2 - 20 + offsetY;
				gc.setFill(Color.LIGHTBLUE);
				gc.setStroke(Color.BLACK);
				gc.setFont(font);
				gc.setLineWidth(3);
				while (y + MapSet.getTotalTileProps(tile.getTileCoord()) * 20 >= canvas.getHeight() - 10)
					y -= 10;
				for (TileProp prop : MapSet.getTileProps(tile.getTileCoord())) {
					String s = prop.name();
					Text text = new Text(s);
					ControllerUtils.setNodeFont(text, "Lucida Console", 15);
					x = tile.outX * Main.getZoom() + offsetX;
					while (x + (int) text.getBoundsInLocal().getWidth() + 60 >= canvas.getWidth())
						x -= 20;
					gc.strokeText(s, x, y += 20);
					gc.fillText(s, x, y);
				}
			}
		}
	}

	public static void drawTileTagsOverCursor(Canvas canvas, Font font, TileCoord coord, int offsetX, int offsetY) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (MapSet.getCurrentLayer().haveTilesOnCoord(coord)) {
			Tile tile = MapSet.getFirstBottomTileFromCoord(coord);
			String tileTags;
			if (MapSet.getCurrentLayer().tileHaveTags(coord))
				tileTags = MapSet.getCurrentLayer().getTileTags(coord).toString();
			else if (MapSet.getCurrentLayer().getFirstBottomTileFromCoord(coord).getStringTags() != null)
				tileTags = MapSet.getCurrentLayer().getFirstBottomTileFromCoord(coord).getStringTags();
			else
				return;
			gc.setFill(Color.LIGHTBLUE);
			gc.setStroke(Color.BLACK);
			gc.setFont(font);
			gc.setLineWidth(3);
			String str[] = tileTags.split(tileTags.charAt(0) == '{' ? "\\," : " ");
			int x, y = tile.outY * Main.getZoom() + (Main.TILE_SIZE * Main.getZoom()) / 2 - offsetY, yy = str.length * 20;
			while (y + yy >= canvas.getHeight() - 30)
				y -= 20;
			yy = 0;
			for (String s : str) {
				Text text = new Text(s);
				ControllerUtils.setNodeFont(text, "Lucida Console", 15);
				int w = (int) text.getBoundsInLocal().getWidth();
				x = tile.outY * Main.TILE_SIZE * Main.getZoom() - w / 2 - offsetX;
				while (x + w >= canvas.getWidth() - 130)
					x -= 10;
				gc.strokeText(s, x, y + (yy += 20));
				gc.fillText(s, x, y + yy);
			}
			gc.setStroke(Color.GREENYELLOW);
			gc.setLineWidth(2);
			gc.strokeRect(coord.getX() * Main.getZoom() * Main.TILE_SIZE + offsetX, coord.getY() * Main.getZoom() * Main.TILE_SIZE + offsetY, 16 * Main.getZoom(), 16 * Main.getZoom());
		}
	}

	public static void drawBlockTypeMarks(GraphicsContext gc, int zoom, boolean showTilesWith2OrMoreSprites, boolean showTilesWith2OrMoreProps, boolean showTilesWith2OrMoreTags) {
		drawBlockTypeMarks(gc, 0, 0, zoom, showTilesWith2OrMoreSprites, showTilesWith2OrMoreProps, showTilesWith2OrMoreTags, null);
	}

	public static void drawBlockTypeMarks(GraphicsContext gc, int offsetX, int offsetY, int zoom, boolean showTilesWith2OrMoreSprites, boolean showTilesWith2OrMoreProps, boolean showTilesWith2OrMoreTags, Function<Tile, Color> consumerForExtraColors) {
		Set<TileCoord> ok = new HashSet<>();
		gc.save();
		MapSet.getTileListFromCurrentLayer().forEach(tile -> {
			Color color;
			if (!ok.contains(tile.getTileCoord())) {
				List<TileProp> tileProps = MapSet.getTileProps(tile.getTileCoord());
				if (consumerForExtraColors != null && consumerForExtraColors.apply(tile) != null)
					color = consumerForExtraColors.apply(tile);
				else if (tileProps.contains(TileProp.INSTAKILL))
					color = Color.ROSYBROWN;
				else if (tileProps.contains(TileProp.CPU_DANGER_2) || tileProps.contains(TileProp.EXPLOSION) || tileProps.contains(TileProp.DAMAGE_PLAYER) || tileProps.contains(TileProp.DAMAGE_ENEMY) || tileProps.contains(TileProp.DAMAGE_BOMB) || tileProps.contains(TileProp.DAMAGE_BRICK) || tileProps.contains(TileProp.DAMAGE_ITEM))
					color = Color.INDIANRED;
				else if (tileProps.contains(TileProp.CPU_DANGER))
					color = Color.PALEVIOLETRED;
				else if (tileProps.contains(TileProp.MIN_SCREEN_TILE_LIMITER) || tileProps.contains(TileProp.MAX_SCREEN_TILE_LIMITER)) {
					if (tileProps.contains(TileProp.MIN_SCREEN_TILE_LIMITER))
						MapSet.getMapMinLimit().setPosition(tile.outX, tile.outX);
					if (tileProps.contains(TileProp.MAX_SCREEN_TILE_LIMITER))
						MapSet.getMapMaxLimit().setPosition(tile.outX, tile.outX);
					color = Color.LIGHTGRAY;
				}
				else if (MapSet.getInitialPlayerPositions().contains(tile.getTileCoord()) ||
								 tileProps.contains(TileProp.PLAYER_INITIAL_POSITION))
									 color = Color.DEEPPINK;
				else if (MapSet.getInitialMonsterPositions().contains(tile.getTileCoord()) ||
								 tileProps.contains(TileProp.MOB_INITIAL_POSITION))
									 color = Color.INDIANRED;
				else if (tileProps.contains(TileProp.REDIRECT_BOMB_TO_DOWN) || tileProps.contains(TileProp.REDIRECT_BOMB_TO_RIGHT) || tileProps.contains(TileProp.REDIRECT_BOMB_TO_UP) || tileProps.contains(TileProp.REDIRECT_BOMB_TO_LEFT))
					color = Color.MEDIUMPURPLE;
				else if (tileProps.contains(TileProp.RAIL_DL) || tileProps.contains(TileProp.RAIL_DR) || tileProps.contains(TileProp.RAIL_UL) || tileProps.contains(TileProp.RAIL_UR) || tileProps.contains(TileProp.RAIL_H) || tileProps.contains(TileProp.RAIL_V) || tileProps.contains(TileProp.RAIL_JUMP) || tileProps.contains(TileProp.RAIL_START) || tileProps.contains(TileProp.RAIL_END) || tileProps.contains(TileProp.TREADMILL_TO_LEFT) || tileProps.contains(TileProp.TREADMILL_TO_UP) || tileProps.contains(TileProp.TREADMILL_TO_RIGHT) || tileProps.contains(TileProp.TREADMILL_TO_DOWN) || tileProps.contains(TileProp.BOMBER_SHIP_LEFT) || tileProps.contains(TileProp.BOMBER_SHIP_UP) || tileProps.contains(TileProp.BOMBER_SHIP_RIGHT) || tileProps.contains(TileProp.BOMBER_SHIP_DOWN) || tileProps.contains(TileProp.BOMBER_SHIP_CORNER))
					color = Color.SADDLEBROWN;
				else if (tileProps.contains(TileProp.GROUND_NO_MOB) || tileProps.contains(TileProp.GROUND_NO_PLAYER) || tileProps.contains(TileProp.GROUND_NO_BOMB) || tileProps.contains(TileProp.GROUND_NO_FIRE))
					color = Color.LIGHTGOLDENRODYELLOW;
				else if (tileProps.contains(TileProp.TRIGGER_BY_BRICK) || tileProps.contains(TileProp.TRIGGER_BY_BOMB) || tileProps.contains(TileProp.TRIGGER_BY_EXPLOSION) || tileProps.contains(TileProp.TRIGGER_BY_ITEM) || tileProps.contains(TileProp.TRIGGER_BY_MOB) || tileProps.contains(TileProp.TRIGGER_BY_PLAYER) || tileProps.contains(TileProp.TRIGGER_BY_RIDE) || tileProps.contains(TileProp.TRIGGER_BY_UNRIDE_PLAYER) || tileProps.contains(TileProp.TRIGGER_BY_STOPPED_BOMB) || tileProps.contains(TileProp.NO_TRIGGER_WHILE_HAVE_BOMB) || tileProps.contains(TileProp.NO_TRIGGER_WHILE_HAVE_BRICK) || tileProps.contains(TileProp.NO_TRIGGER_WHILE_HAVE_ITEM) || tileProps.contains(TileProp.NO_TRIGGER_WHILE_HAVE_MOB) || tileProps.contains(TileProp.NO_TRIGGER_WHILE_HAVE_PLAYER))
					color = Color.DARKORANGE;
				else if (tileProps.contains(TileProp.BRICK_RANDOM_SPAWNER))
					color = Color.LIGHTGREEN;
				else if (tileProps.contains(TileProp.FIXED_BRICK))
					color = Color.GREEN;
				else if (tileProps.contains(TileProp.FIXED_ITEM))
					color = Color.CORAL;
				else if (tileProps.contains(TileProp.GROUND_HOLE))
					color = Color.ALICEBLUE;
				else if (tileProps.contains(TileProp.DEEP_HOLE))
					color = Color.GRAY;
				else if (tileProps.contains(TileProp.JUMP_OVER))
					color = Color.CORAL;
				else if (tileProps.contains(TileProp.MAP_EDGE))
					color = Color.SADDLEBROWN;
				else if (tileProps.contains(TileProp.WATER))
					color = Color.LIGHTBLUE;
				else if (tileProps.contains(TileProp.DEEP_WATER))
					color = Color.DARKBLUE;
				else if (tileProps.contains(TileProp.TELEPORT_FROM_FLOATING_PLATFORM))
					color = Color.ROSYBROWN;
				else if (tileProps.contains(TileProp.STAGE_CLEAR))
					color = Color.AQUA;
				else if (tileProps.contains(TileProp.GROUND))
					color = Color.YELLOW;
				else if (tileProps.contains(TileProp.WALL) || tileProps.contains(TileProp.HIGH_WALL))
					color = Color.RED;
				else
					color = Color.ORANGE;
				gc.setFill(color);
				gc.setLineWidth(1);
				gc.setGlobalAlpha(0.6);
				gc.fillRect(tile.getTileCoord().getX() * Main.TILE_SIZE * zoom + offsetX, tile.getTileCoord().getY() * Main.TILE_SIZE * zoom + offsetY, Main.TILE_SIZE * zoom, Main.TILE_SIZE * zoom);
				ok.add(tile.getTileCoord());
			}
			int totalTiles = MapSet.getTileListFromCoord(tile.getTileCoord()).size();
			int totalProps = !MapSet.tileHaveProps(tile.getTileCoord()) ? 0 : MapSet.getTileProps(tile.getTileCoord()).size();
			int totalTags = !MapSet.tileHaveTags(tile.getTileCoord()) ? 0 : MapSet.getTileTags(tile.getTileCoord()).size();
			gc.setFill(Misc.blink(50) ? Color.LIGHTBLUE : Color.YELLOW);
			gc.setStroke(Misc.blink(50) ? Color.LIGHTBLUE : Color.YELLOW);
			gc.setLineWidth(1);
			gc.setGlobalAlpha(1);
			gc.setFont(new Font("Lucida Console", 20));
			if (showTilesWith2OrMoreSprites && totalTiles > 1) {
				gc.fillText("" + totalTiles, tile.getTileCoord().getX() * Main.TILE_SIZE * zoom + offsetX + Main.TILE_SIZE / 3 * zoom,
																		 tile.getTileCoord().getY() * Main.TILE_SIZE * zoom + offsetY + Main.TILE_SIZE / 1.5 * zoom);
				gc.strokeRect(tile.getTileCoord().getX() * Main.TILE_SIZE * zoom + offsetX, tile.getTileCoord().getY() * Main.TILE_SIZE * zoom + offsetY, Main.TILE_SIZE * zoom, Main.TILE_SIZE * zoom);
			}
			if (showTilesWith2OrMoreProps && totalProps > 1) {
				gc.fillText("" + totalProps, tile.getTileCoord().getX() * Main.TILE_SIZE * zoom + offsetX + Main.TILE_SIZE / 3 * zoom,
																		 tile.getTileCoord().getY() * Main.TILE_SIZE * zoom + offsetY + Main.TILE_SIZE / 1.5 * zoom);
				gc.strokeRect(tile.getTileCoord().getX() * Main.TILE_SIZE * zoom + offsetX, tile.getTileCoord().getY() * Main.TILE_SIZE * zoom + offsetY, Main.TILE_SIZE * zoom, Main.TILE_SIZE * zoom);
			}
			if (showTilesWith2OrMoreTags && totalTags > 1) {
				gc.fillText("" + totalTags, tile.getTileCoord().getX() * Main.TILE_SIZE * zoom + offsetX + Main.TILE_SIZE / 3 * zoom,
																		 tile.getTileCoord().getY() * Main.TILE_SIZE * zoom + offsetY + Main.TILE_SIZE / 1.5 * zoom);
				gc.strokeRect(tile.getTileCoord().getX() * Main.TILE_SIZE * zoom + offsetX, tile.getTileCoord().getY() * Main.TILE_SIZE * zoom + offsetY, Main.TILE_SIZE * zoom, Main.TILE_SIZE * zoom);
			}
		});
		gc.restore();
	}
	
}
