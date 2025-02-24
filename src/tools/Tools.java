package tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import drawimage_stuffs.DrawImageEffects;
import entities.Bomb;
import entities.BomberMan;
import entities.Entity;
import entities.Monster;
import enums.Direction;
import enums.Elevation;
import enums.FindInRectType;
import enums.FindType;
import enums.PassThrough;
import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import util.CollectionUtils;

public abstract class Tools {

	public static void loadStuffs() {
		Draw.loadStuffs();
	}

	public static void iterateInsideCircleArea(TileCoord center, int radius, Consumer<TileCoord> consumer) {
		iterateInsideElliticArea(center, radius, radius, consumer);
	}
	
	public static void iterateInsideElliticArea(TileCoord center, int radiusX, int radiusY, Consumer<TileCoord> consumer) {
		for (int y = center.getY() - radiusY; y <= center.getY() + radiusY; y++)
			for (int x = center.getX() - radiusX; x <= center.getX() + radiusX; x++) {
				int dx = x - center.getX(), dy = y - center.getY();
				if ((dx * dx) / (double) (radiusX * radiusX) + (dy * dy) / (double) (radiusY * radiusY) <= 1)
					consumer.accept(new TileCoord(x, y));
			}
	}

	public static void iterateInsideSquareArea(TileCoord center, int size, Consumer<TileCoord> consumer) {
		iterateInsideRectangleArea(center, size, size, consumer);
	}

	public static void iterateInsideRectangleArea(TileCoord center, int width, int height, Consumer<TileCoord> consumer) {
		for (int y = center.getY() - height; y <= center.getY() + height; y++)
			for (int x = center.getX() - width; x <= center.getX() + width; x++)
				consumer.accept(new TileCoord(x, y));
	}
	
	/* Retorna de 0.0 a 1.0 baseado na distancia entre o playerTile e o targetTile (0.0 se a distancia for igual ou maior que maxDistance tiles de distancia, 1.0 se for igual ou menor que 1 tile.
	 * Distancia baseada em raio retangular ou circular em tiles.
	 */
	public double calculateProximity(TileCoord playerTile, TileCoord targetTile, int maxDistance, FindInRectType findInRectType) {
		int distance;
		if (findInRectType == FindInRectType.CIRCLE_AREA) 
			distance = calculateEuclideanDistance(playerTile, targetTile);
		else
			distance = calculateManhattanDistance(playerTile, targetTile);
		if (distance >= maxDistance)
			return 0.0;
		return Math.min(1.0, 1.0 - (distance - 1) / (double) (maxDistance - 1));
	}

	private int calculateManhattanDistance(TileCoord a, TileCoord b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}

	private int calculateEuclideanDistance(TileCoord a, TileCoord b) {
		double dx = a.getX() - b.getX();
		double dy = a.getY() - b.getY();
		return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
	}
	
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

	public static DrawImageEffects loadEffectsFromString(String arrayToString) {
		// NOTA: implementar
		return new DrawImageEffects();
	}

	public static String SpriteEffectsToString(DrawImageEffects effects) {
		// NOTA: implementar
		return "-";
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, FindType type) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, Set.of(direction), Set.of(type), null);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, Set<FindType> types) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, Set.of(direction), types, null);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, FindType type) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, directions, Set.of(type), null);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, Set<FindType> types) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, directions, types, null);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, FindType type, Set<PassThrough> ignores) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, Set.of(direction), Set.of(type), ignores);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, Set<FindType> types, Set<PassThrough> ignores) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, Set.of(direction), types, ignores);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, FindType type, Set<PassThrough> ignores) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, directions, Set.of(type), ignores);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, Set<FindType> types, Set<PassThrough> ignores) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, directions, types, ignores);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, FindType type) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, Set.of(direction), Set.of(type), null);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, Set<FindType> types) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, Set.of(direction), types, null);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, FindType type) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, directions, Set.of(type), null);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, Set<FindType> types) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, directions, types, null);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, FindType type, Set<PassThrough> ignores) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, Set.of(direction), Set.of(type), ignores);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, Set<FindType> types, Set<PassThrough> ignores) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, Set.of(direction), types, ignores);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, FindType type, Set<PassThrough> ignores) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, directions, Set.of(type), ignores);
	}
	
	public static FindType getWichObjectIsOnTile(TileCoord coord) {
		return getWichObjectIsOnTile(null, null, coord, null);
	}

	public static FindType getWichObjectIsOnTile(Entity entity, Entity ignoreEntity, TileCoord coord, Set<FindType> types) {
		return getWichObjectIsOnTile(entity, ignoreEntity, coord, types, null);
	}
	
	public static FindType getWichObjectIsOnTile(TileCoord coord, Set<PassThrough> ignores) {
		return getWichObjectIsOnTile(null, null, coord, null, ignores);
	}

	public static FindType getWichObjectIsOnTile(TileCoord coord, Set<FindType> types, Set<PassThrough> ignores) {
		return getWichObjectIsOnTile(null, null, coord, types, ignores);
	}

	public static FindType getWichObjectIsOnTile(Entity entity, Entity ignoreEntity, TileCoord coord, Set<FindType> types, Set<PassThrough> ignores) {
		FindType findType = null;
		if (types == null)
			types = Set.of(FindType.BAD_ITEM, FindType.BOMB, FindType.BRICK, FindType.EMPTY, FindType.GOOD_ITEM, FindType.ITEM, FindType.MONSTER, FindType.PLAYER, FindType.WALL);
		if ((entity == null || entity.getElevation() == Elevation.ON_GROUND) &&
				((types.contains(findType = FindType.EMPTY) && MapSet.tileIsFree(coord, ignores)) ||
				(types.contains(findType = FindType.GOOD_ITEM) && Item.haveItemAt(coord) && !Item.getItemAt(coord).getItemType().isBadItem()) ||
				(types.contains(findType = FindType.ITEM) && Item.haveItemAt(coord)) ||
				(types.contains(findType = FindType.BAD_ITEM) && Item.haveItemAt(coord) && Item.getItemAt(coord).getItemType().isBadItem()) ||
				(types.contains(findType = FindType.BOMB) && Bomb.haveBombAt(entity, coord)) ||
				(types.contains(findType = FindType.BRICK) && Brick.haveBrickAt(coord)) ||
				(types.contains(findType = FindType.MONSTER) && Entity.haveAnyEntityAtCoord(coord, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(coord, Monster.class)) ||
				(types.contains(findType = FindType.PLAYER) && Entity.haveAnyEntityAtCoord(coord, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(coord, BomberMan.class)) ||
				(types.contains(findType = FindType.WALL) && !MapSet.tileIsFree(coord))))
					return findType;
		return null;
	}
	
	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, Set<FindType> types, Set<PassThrough> ignores) {
		List<FindProps> list = new ArrayList<>();
		for (Direction dir : directions) {
			int distance = distanceInTiles;
			FindType findType = null;
			for (TileCoord c = coord.getNewInstance().incCoordsByDirection(dir); distance-- > 0 && MapSet.haveTilesOnCoord(c); c.incCoordsByDirection(dir))
				if ((findType = getWichObjectIsOnTile(entity, ignoreEntity, c, types, ignores)) != null) {
						list.add(new FindProps(findType, c.getNewInstance(), dir));
					break;
				}
		}
		return list.isEmpty() ? null : list;
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, int radiusInTiles, FindType type) {
		return findInRect(null, coord, ignoreEntity, null, radiusInTiles, Set.of(type), null);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, int radiusInTiles, FindType type) {
		return findInRect(entity, coord, ignoreEntity, null, radiusInTiles, Set.of(type), null);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, int radiusInTiles, FindType type, Set<PassThrough> ignores) {
		return findInRect(null, coord, ignoreEntity, null, radiusInTiles, Set.of(type), ignores);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, int radiusInTiles, FindType type, Set<PassThrough> ignores) {
		return findInRect(entity, coord, ignoreEntity, null, radiusInTiles, Set.of(type), ignores);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, int radiusInTiles, Set<FindType> types) {
		return findInRect(null, coord, ignoreEntity, null, radiusInTiles, types, null);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, int radiusInTiles, Set<FindType> types) {
		return findInRect(entity, coord, ignoreEntity, null, radiusInTiles, types, null);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores) {
		return findInRect(null, coord, ignoreEntity, null, radiusInTiles, types, ignores);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores) {
		return findInRect(entity, coord, ignoreEntity, null, radiusInTiles, types, ignores);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, FindType type) {
		return findInRect(null, coord, ignoreEntity, findType, radiusInTiles, Set.of(type), null);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, FindType type) {
		return findInRect(entity, coord, ignoreEntity, findType, radiusInTiles, Set.of(type), null);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, FindType type, Set<PassThrough> ignores) {
		return findInRect(null, coord, ignoreEntity, findType, radiusInTiles, Set.of(type), ignores);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, FindType type, Set<PassThrough> ignores) {
		return findInRect(entity, coord, ignoreEntity, findType, radiusInTiles, Set.of(type), ignores);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, Set<FindType> types) {
		return findInRect(null, coord, ignoreEntity, findType, radiusInTiles, types, null);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, Set<FindType> types) {
		return findInRect(entity, coord, ignoreEntity, findType, radiusInTiles, types, null);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores) {
		return findInRect(null, coord, ignoreEntity, findType, radiusInTiles, types, ignores);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores) {
		List<FindProps> list = new ArrayList<>();
		Consumer<TileCoord> consumer = coord2 -> {
			FindType ft; 
			if ((ft = getWichObjectIsOnTile(entity, ignoreEntity, coord2, types, ignores)) != null) {
				list.add(new FindProps(ft, coord2, coord.get4wayDirectionToReach(coord2)));
				Function<TileCoord, Boolean> tileIsFree = t -> {
					return MapSet.tileIsFree(t, ignores) || t.equals(coord) || t.equals(coord2);
				};
				PathFinder pf = new PathFinder(coord, coord2, Direction.DOWN, tileIsFree);
				if (pf.pathWasFound())
					list.add(new FindProps(ft, coord2.getNewInstance(), pf.getNextDirectionToGo()));
			}
		};
		if (findType == null || findType == FindInRectType.RECTANGLE_AREA)
			iterateInsideRectangleArea(coord, radiusInTiles, radiusInTiles, consumer);
		else
			iterateInsideElliticArea(coord, radiusInTiles, radiusInTiles, consumer);
		return list.isEmpty() ? null : list;
	}

	public static Direction getRandomFreeDirection(Entity entity, TileCoord coord) {
		return getRandomFreeDirection(entity, coord, null, null);
	}
	
	public static Direction getRandomFreeDirection(Entity entity, TileCoord coord, Set<PassThrough> passThrough) {
		return getRandomFreeDirection(entity, coord, null, passThrough);
	}
	
	public static Direction getRandomFreeDirection(Entity entity, TileCoord coord, Set<Direction> ignoreDirections, Set<PassThrough> passThrough) {
		List<Direction> dirs = getFreeDirections(entity, coord, ignoreDirections, passThrough);
		return dirs == null ? null : CollectionUtils.getRandomItemFromList(dirs);
	}
	
	public static List<TileCoord> getFreeTileCoordsAround(Entity entity, TileCoord coord) {
		return getFreeTileCoordsAround(entity, coord, null, null); 
	}

	public static List<TileCoord> getFreeTileCoordsAround(Entity entity, TileCoord coord, Set<PassThrough> passThrough) {
		return getFreeTileCoordsAround(entity, coord, null, passThrough); 
	}

	public static List<Direction> getFreeDirections(Entity entity, TileCoord coord) {
		return getFreeDirections(entity, coord, null, null); 
	}

	public static List<Direction> getFreeDirections(Entity entity, TileCoord coord, Set<PassThrough> passThrough) {
		return getFreeDirections(entity, coord, null, passThrough); 
	}

	public static List<TileCoord> getFreeTileCoordsAround(Entity entity, TileCoord coord, Set<Direction> ignoreDirections, Set<PassThrough> passThrough) {
		List<TileCoord> freeTileCoords = new ArrayList<>();
		List<Direction> tiles = getFreeDirections(entity, coord, ignoreDirections, passThrough);
		if (tiles == null)
			return null;
		for (Direction dir : tiles)
			freeTileCoords.add(coord.getNewInstance().incCoordsByDirection(dir));
		return freeTileCoords.isEmpty() ? null : freeTileCoords;
	}

	public static List<Direction> getFreeDirections(Entity entity, TileCoord coord, Set<Direction> ignoreDirections, Set<PassThrough> passThrough) {
		List<Direction> freeDirs = new ArrayList<>();
		for (Direction dir : Direction.values4Directions())
			if ((ignoreDirections == null || !ignoreDirections.contains(dir)) &&
					MapSet.tileIsFree(entity, coord.getNewInstance().incCoordsByDirection(dir), passThrough))
						freeDirs.add(dir);
		return freeDirs.isEmpty() ? null : freeDirs;
	}

	public static boolean isColorMixPallete(List<Color> pallete) {
		if (pallete == null || pallete.size() != 9)
			return false;
		return pallete.get(0).equals(Color.valueOf("#123456FF")) && pallete.get(8).equals(Color.valueOf("#654321FF"));
	}
	
	public static List<Color> newColorMixPallete() { //EM NENHUM CASO O ULTIMO VALOR DA COR (OPACITY) DEVE SER DIFERENTE DE 1 POIS ISSO ZOA COM O VALOR FINAL DA COR
		return new ArrayList<>(Arrays.asList(
				Color.valueOf("#123456FF"), //IDENTIFICADOR DE COLOR_MIX_PALLETE
				Color.valueOf("#003366FF"), //R * 5, G * 5, B * 5 (POSICAO DAS CORES)
				Color.WHITE, // R, G, B (%)
				Color.WHITE, // COLOR_ADJUST (HUE, SATURATION, BRIGHTNESS)
				Color.WHITE, // COLOR_TINT (RED, GREEN, BLUE)
				Color.valueOf("#FF0000FF"), // GLOBAL OPACITY, COLOR_ADJUST STATE, COLOR_TINT OPACITY
				Color.valueOf("#00FF00FF"), // SEPIA_TONE STATE, SEPIA_TONE LEVEL, GLOW STATE
				Color.valueOf("#0000FFFF"),  // BLOOM STATE, BLOOM LEVEL, GLOW LEVEL
				Color.valueOf("#654321FF"))); //IDENTIFICADOR DE COLOR_MIX_PALLETE
	}

	public static WritableImage applyColorMixPalleteOnImage(WritableImage originalImage, List<Color> pattern) {
		return applyColorMixPalleteOnImage(originalImage, pattern, Color.TRANSPARENT);
	}

	public static WritableImage applyColorMixPalleteOnImage(WritableImage originalImage, List<Color> colorMixPallete, Color transparentColor) {
		if (!isColorMixPallete(colorMixPallete))
			throw new RuntimeException("Invalid Color Mix Pallete");
		ColorMix colorMix = new ColorMix(colorMixPallete);
		int w = (int)originalImage.getWidth(), h = (int)originalImage.getHeight() - 1;
		Canvas c = new Canvas(w, h);
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		WritableImage i = new WritableImage(w, h);
		PixelReader pr = originalImage.getPixelReader();
		PixelWriter pw = i.getPixelWriter();
		int r = colorMix.getRedIndex(), g = colorMix.getGreenIndex(), b = colorMix.getBlueIndex();
		double opacity = colorMix.getGlobalOpacity(), rv = colorMix.getRedStrenght(),
					 gv = colorMix.getGreenStrenght(), bv = colorMix.getBlueStrenght();
		for (int y = 1; y <= h; y++)
			for (int x = 0; x < w; x++) {
				Color col = pr.getColor(x, y);
				double[] rgba = {col.getRed(), col.getGreen(), col.getBlue(), col.getOpacity()};
				Color col2 = new Color(rgba[r] * rv, rgba[g] * gv, rgba[b] * bv, opacity);
				pw.setColor(x, y - 1, !col.equals(transparentColor) ?	col2 : transparentColor);
			}
		gc.setFill(transparentColor);
		gc.fillRect(0, 0, w, h);
		DrawImageEffects effects = new DrawImageEffects();
		if (colorMix.getColorAdjustState())
			effects.setColorAdjust(colorMix.getColorAdjustHue(),
														 colorMix.getColorAdjustSaturation(),
														 colorMix.getColorAdjustBrightness(), BlendMode.SRC_ATOP);
		if (colorMix.getColorTintOpacity() > 0.0)
			effects.setColorTint(colorMix.getColorTintRed(),
													 colorMix.getColorTintGreen(),
													 colorMix.getColorTintBlue(),
													 colorMix.getColorTintOpacity(), BlendMode.SRC_ATOP);
		if (colorMix.getSepiaToneState())
			effects.setSepiaTone(colorMix.getSepiaToneLevel(), BlendMode.SRC_ATOP);
		if (colorMix.getBloomState())
			effects.setBloom(colorMix.getBloomThreshold(), BlendMode.SRC_ATOP);
		if (colorMix.getGlowState())
			effects.setGlow(colorMix.getGlowLevel(), BlendMode.SRC_ATOP);
		ImageUtils.drawImage(gc, i, 0, 0, effects);
		return Draw.getCanvasSnapshot(c);
	}

	public static WritableImage applyColorPalleteOnImage(WritableImage originalImage, List<Color> originalPallete, List<Color> currentPallete) {
		return applyColorPalleteOnImage(originalImage, originalPallete, currentPallete, Color.TRANSPARENT);
	}

	public static WritableImage applyColorPalleteOnImage(WritableImage originalImage, List<Color> originalPallete, List<Color> currentPallete, Color transparentColor) {
		int w = (int)originalImage.getWidth(), h = (int)originalImage.getHeight();
		WritableImage wi = new WritableImage(w, h - 1);
		PixelReader pr = originalImage.getPixelReader();
		PixelWriter pw = wi.getPixelWriter();
		for (int y = 1; y < h; y++)
			for (int x = 0; x < w; x++) {
				Color color = pr.getColor(x, y);
				if (y == 0)
					pw.setColor(x, y - 1, transparentColor);
				else if (!color.equals(transparentColor) && originalPallete.contains(color))
					pw.setColor(x, y - 1, currentPallete.get(originalPallete.indexOf(color)));
				else
					pw.setColor(x, y - 1, color);
			}
		return wi;
	}

	public static List<List<Color>> getPalleteListFromImage(WritableImage image) {
		return getPalleteListFromImage(image, Color.TRANSPARENT);
	}

	public static List<List<Color>> getPalleteListFromImage(WritableImage image, Color transparentColor) {
		List<List<Color>> palletes = new ArrayList<>();
		List<Color> pallete = new ArrayList<>();
		PixelReader pr = image.getPixelReader();
		Color previewColor = transparentColor;
		for (int x = 0; x < image.getWidth(); x++) {
			Color color = pr.getColor(x, 0);
			if (color.equals(transparentColor)) {
				if (!pallete.isEmpty()) {
					palletes.add(new ArrayList<>(pallete));
					pallete.clear();
					if (palletes.size() > 1 && palletes.get(palletes.size() - 1).size() != palletes.get(0).size())
						return null;
				}
				if (color.equals(previewColor))
					break;
			}
			else
				pallete.add(color);
			previewColor = color;
		}
		return palletes.isEmpty() ? null : palletes;
	}

	public static List<Color> getPalleteFromImage(WritableImage image) {
		return getPalleteFromImage(image, Color.TRANSPARENT);
	}

	public static List<Color> getPalleteFromImage(WritableImage image, Color transparentColor) {
		List<Color> pallete = new ArrayList<>();
		int w = (int)image.getWidth(), h = (int)image.getHeight();
		PixelReader pr = image.getPixelReader();
		for (int y = 1; y < h; y++)
			for (int x = 0; x < w; x++) {
				Color color = pr.getColor(x, y);
				if (!color.equals(transparentColor) && !pallete.contains(color))
					pallete.add(color);
			}
		return pallete.isEmpty() ? null : pallete;
	}
	
}