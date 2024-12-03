package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import drawimage_stuffs.DrawImageEffects;
import entities.Bomb;
import entities.BomberMan;
import entities.Entity;
import entities.Monster;
import enums.Direction;
import enums.FindInRectType;
import enums.FindType;
import enums.PassThrough;
import gameutil.FPSHandler;
import gui.util.ImageUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

	private static FPSHandler fpsHandler;

	public static void loadStuffs() {
		fpsHandler = new FPSHandler(60);
		Draw.loadStuffs();
	}

	public static FPSHandler getFPSHandler() {
		return fpsHandler;
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

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, Set<FindType> types, Set<PassThrough> ignores) {
		List<FindProps> list = new ArrayList<>();
		for (Direction dir : directions) {
			int distance = distanceInTiles;
			FindType findType = null;
			out:
			for (TileCoord c = coord.getNewInstance().incCoordsByDirection(dir); distance-- > 0 && MapSet.haveTilesOnCoord(c); c.incCoordsByDirection(dir)) {
				if (!MapSet.tileIsFree(c, ignores)) {
					if ((types.contains(findType = FindType.GOOD_ITEM) && Item.haveItemAt(c) && !Item.getItemAt(c).getItemType().isBadItem()) ||
							(types.contains(findType = FindType.ITEM) && Item.haveItemAt(c)) ||
							(types.contains(findType = FindType.BAD_ITEM) && Item.haveItemAt(c) && Item.getItemAt(c).getItemType().isBadItem()) ||
							(types.contains(findType = FindType.BOMB) && Bomb.haveBombAt(entity, c)) ||
							(types.contains(findType = FindType.BRICK) && Brick.haveBrickAt(c)) ||
							(types.contains(findType = FindType.MONSTER) && Entity.haveAnyEntityAtCoord(c, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(c, Monster.class)) ||
							(types.contains(findType = FindType.PLAYER) && Entity.haveAnyEntityAtCoord(c, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(c, BomberMan.class)))
								list.add(new FindProps(findType, c.getNewInstance(), dir));
					break out;
				}
				else if (types.contains(findType = FindType.EMPTY) ||
						(types.contains(findType = FindType.MONSTER) && Entity.haveAnyEntityAtCoord(c, ignoreEntity) && Entity.getFirstEntityFromCoord(c) instanceof Monster) ||
						(types.contains(findType = FindType.PLAYER) && Entity.haveAnyEntityAtCoord(c, ignoreEntity) && Entity.getFirstEntityFromCoord(c) instanceof BomberMan)) {
							list.add(new FindProps(findType, c.getNewInstance(), dir));
							break out;
				}
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
			FindType ft = null; 
			boolean found = false;
			if (!MapSet.tileIsFree(coord2, ignores)) {
				if ((types.contains(ft = FindType.BOMB) && Bomb.haveBombAt(entity, coord2)) ||
						(types.contains(ft = FindType.BRICK) && Brick.haveBrickAt(coord2)) ||
						(types.contains(ft = FindType.MONSTER) && Entity.haveAnyEntityAtCoord(coord2, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(coord2, Monster.class)) ||
						(types.contains(ft = FindType.PLAYER) && Entity.haveAnyEntityAtCoord(coord2, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(coord2, BomberMan.class)))
					found = true;
			}
			else if (types.contains(ft = FindType.EMPTY) ||
					(types.contains(ft = FindType.GOOD_ITEM) && Item.haveItemAt(coord2) && !Item.getItemAt(coord2).getItemType().isBadItem()) ||
					(types.contains(ft = FindType.ITEM) && Item.haveItemAt(coord2)) ||
					(types.contains(ft = FindType.BAD_ITEM) && Item.haveItemAt(coord2) && Item.getItemAt(coord2).getItemType().isBadItem()) ||
					(types.contains(ft = FindType.MONSTER) && Entity.haveAnyEntityAtCoord(coord2, ignoreEntity) && Entity.getFirstEntityFromCoord(coord2) instanceof Monster) ||
					(types.contains(ft = FindType.PLAYER) && Entity.haveAnyEntityAtCoord(coord2, ignoreEntity) && Entity.getFirstEntityFromCoord(coord2) instanceof BomberMan))
				found = true;
			if (found) {
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

	public static int[] stringToColorPattern(String string) {
		try {
			String[] split = string.replace("{", "").replace("}", "").replace(" ", "").split(",");
			int r = Integer.parseInt(split[0]), rv = Integer.parseInt(split[1]),
					g = Integer.parseInt(split[2]), gv = Integer.parseInt(split[3]),
					b = Integer.parseInt(split[4]), bv = Integer.parseInt(split[5]);
			return new int[] { r, rv, g, gv, b, bv };
		}
		catch (Exception ex) {
			throw new RuntimeException(string + " - Formato inválido de padrão de cor!");
		}
	}
	
	public static String colorPatternToString(int[] currentColorPattern) {
		return String.format(Locale.US, "{ %d, %d, %d, %d, %d, %d }\n",
				(int)currentColorPattern[0], (int)currentColorPattern[1], (int)currentColorPattern[2],
				(int)currentColorPattern[3], (int)currentColorPattern[4], (int)currentColorPattern[5]);
	}

	public static int[] convertColorsToColorPattern(Color color1, Color color2) {
		/* Um par de cores no topo do sprite significa que se trata de uma paleta de cores
		 * feita através de troca de cores. Passando esse par de cores para esse método,
		 * resultará em uma array de int com o pattern para ser passado para o método
		 * Tools.getImageWithColorChanged()
		 * O sprite pode conter vãrios pares de cores com diferentes patterns, o que
		 * resultará na geração de sprites com cores diferentes, porem cada
		 * par deve estar separado por um pixel da mesma cor da transparência.
		 */
		int[] argb1 = ImageUtils.getRgbaArray(ImageUtils.colorToArgb(color1));
		int[] argb2 = ImageUtils.getRgbaArray(ImageUtils.colorToArgb(color2));
		return new int[] {argb1[1], argb2[1], argb1[2], argb2[2], argb1[3], argb2[3]};
	}

	public static int[] convertColorsToColorPattern(List<Color> colors) {
		if (colors == null)
			throw new RuntimeException("colors is null");
		if (colors.size() != 2)
			throw new RuntimeException("Invalid color list for conversion (Must have exactly 2 colors)");
		return convertColorsToColorPattern(colors.get(0), colors.get(1));
	}
	
	public static Color[] convertColorPatternToColors(int[] colorPattern) {
		Color color1 = ImageUtils.argbToColor(ImageUtils.getRgba(colorPattern[0], colorPattern[2], colorPattern[4]));
		Color color2 = ImageUtils.argbToColor(ImageUtils.getRgba(colorPattern[1], colorPattern[3], colorPattern[5]));
		return new Color[] {color1, color2};
	}

	public static WritableImage applyColorChangeOnImage(WritableImage originalImage, int[] colorPattern) {
		return applyColorChangeOnImage(originalImage, colorPattern, Color.TRANSPARENT);
	}

	public static WritableImage applyColorChangeOnImage(WritableImage originalImage, int[] colorPattern, Color transparentColor) {
		// {2, 50, 1, 75, 3, 20} - Trocar R por G, com 50% do valor do R, e 75% do valor do G e 20% do valor de B
		int w = (int)originalImage.getWidth(), h = (int)originalImage.getHeight();
		Canvas c = new Canvas(w, h);
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setImageSmoothing(false);
		WritableImage i = new WritableImage(w, h);
		PixelWriter pw = i.getPixelWriter();
		int r = colorPattern[0], g = colorPattern[2], b = colorPattern[4];
		double rv = colorPattern[1] / 100f, gv = colorPattern[3] / 100f, bv = colorPattern[5] / 100f;
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				int[] rgba = ImageUtils.getRgbaArray(originalImage.getPixelReader().getArgb(x, y));
				int rr = (int)(rgba[r] * rv), gg = (int)(rgba[g] * gv), bb = (int)(rgba[b] * bv);
				pw.setColor(x, y, !originalImage.getPixelReader().getColor(x, y).equals(transparentColor) ?
						ImageUtils.argbToColor(ImageUtils.getRgba(rr, gg, bb)) : transparentColor);
			}
		return i;
	}

	public static WritableImage applyColorPalleteOnImage(WritableImage originalImage, List<Color> originalPallete, List<Color> currentPallete) {
		return applyColorPalleteOnImage(originalImage, originalPallete, currentPallete, Color.TRANSPARENT);
	}

	public static WritableImage applyColorPalleteOnImage(WritableImage originalImage, List<Color> originalPallete, List<Color> currentPallete, Color transparentColor) {
		int w = (int)originalImage.getWidth(), h = (int)originalImage.getHeight();
		WritableImage wi = new WritableImage(w, h);
		PixelReader pr = originalImage.getPixelReader();
		PixelWriter pw = wi.getPixelWriter();
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				Color color = ImageUtils.argbToColor(pr.getArgb(x, y));
				if (y == 0)
					pw.setColor(x, y, transparentColor);
				else if (!color.equals(transparentColor) && originalPallete.contains(color))
					pw.setColor(x, y, currentPallete.get(originalPallete.indexOf(color)));
				else
					pw.setColor(x, y, color);
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
			Color color = ImageUtils.argbToColor(pr.getArgb(x, 0));
			if (color.equals(transparentColor)) {
				if (!pallete.isEmpty()) {
					palletes.add(new ArrayList<>(pallete));
					pallete.clear();
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
				Color color = ImageUtils.argbToColor(pr.getArgb(x, y));
				if (!color.equals(transparentColor) && !pallete.contains(color))
					pallete.add(color);
			}
		return pallete.isEmpty() ? null : pallete;
	}
	
}