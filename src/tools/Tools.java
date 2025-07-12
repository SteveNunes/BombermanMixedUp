package tools;

import java.util.ArrayList;
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
import enums.FindTypeRestriction;
import enums.PassThrough;
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
		List<TileCoord> list = new ArrayList<>();
		for (int y = center.getY() - radiusY; y <= center.getY() + radiusY; y++)
			for (int x = center.getX() - radiusX; x <= center.getX() + radiusX; x++) {
				int dx = x - center.getX(), dy = y - center.getY();
				if ((dx * dx) / (double) (radiusX * radiusX) + (dy * dy) / (double) (radiusY * radiusY) <= 1)
					list.add(new TileCoord(x, y));
			}
		list.sort((a1, a2) -> (int)(20 * calculateProximity(center, a2, 20)) - (int)(20 * calculateProximity(center, a1, 20)));
		for (TileCoord coord : list)
			consumer.accept(coord);
	}

	public static void iterateInsideSquareArea(TileCoord center, int size, Consumer<TileCoord> consumer) {
		iterateInsideRectangleArea(center, size, size, consumer);
	}

	public static void iterateInsideRectangleArea(TileCoord center, int width, int height, Consumer<TileCoord> consumer) {
		List<TileCoord> list = new ArrayList<>();
		for (int y = center.getY() - height; y <= center.getY() + height; y++)
			for (int x = center.getX() - width; x <= center.getX() + width; x++)
				list.add(new TileCoord(x, y));
		list.sort((a1, a2) -> (int)(20 * calculateProximity(center, a2, 20)) - (int)(20 * calculateProximity(center, a1, 20)));
		for (TileCoord coord : list)
			consumer.accept(coord);
	}
	
	/* Retorna de 0.0 a 1.0 baseado na distancia entre o playerTile e o targetTile (0.0 se a distancia for igual ou maior que maxDistance tiles de distancia, 1.0 se for igual ou menor que 1 tile.
	 * Distancia baseada em raio retangular ou circular em tiles.
	 */
	public static double calculateProximity(TileCoord playerTile, TileCoord targetTile, int maxDistance, FindInRectType findInRectType) {
		int distance;
		if (findInRectType == FindInRectType.CIRCLE_AREA) 
			distance = calculateEuclideanDistance(playerTile, targetTile);
		else
			distance = calculateManhattanDistance(playerTile, targetTile);
		if (distance >= maxDistance)
			return 0.0;
		return Math.min(1.0, 1.0 - (distance - 1) / (double) (maxDistance - 1));
	}

	public static double calculateProximity(TileCoord playerTile, TileCoord targetTile, int maxDistance) {
		return calculateProximity(playerTile, targetTile, maxDistance, FindInRectType.RECTANGLE_AREA);
	}

	private static int calculateManhattanDistance(TileCoord a, TileCoord b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}

	private static int calculateEuclideanDistance(TileCoord a, TileCoord b) {
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

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, FindType type, FindTypeRestriction restriction) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, Set.of(direction), Set.of(type), null, restriction);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, Set<FindType> types, FindTypeRestriction restriction) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, Set.of(direction), types, null, restriction);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, FindType type, FindTypeRestriction restriction) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, directions, Set.of(type), null, restriction);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, Set<FindType> types, FindTypeRestriction restriction) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, directions, types, null, restriction);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, FindType type, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, Set.of(direction), Set.of(type), ignores, restriction);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, Set<FindType> types, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, Set.of(direction), types, ignores, restriction);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, FindType type, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, directions, Set.of(type), ignores, restriction);
	}

	public static List<FindProps> findInLine(Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, Set<FindType> types, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInLine(null, ignoreEntity, coord, distanceInTiles, directions, types, ignores, restriction);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, FindType type, FindTypeRestriction restriction) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, Set.of(direction), Set.of(type), null, restriction);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, Set<FindType> types, FindTypeRestriction restriction) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, Set.of(direction), types, null, restriction);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, FindType type, FindTypeRestriction restriction) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, directions, Set.of(type), null, restriction);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, Set<FindType> types, FindTypeRestriction restriction) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, directions, types, null, restriction);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, FindType type, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, Set.of(direction), Set.of(type), ignores, restriction);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Direction direction, Set<FindType> types, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, Set.of(direction), types, ignores, restriction);
	}

	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, FindType type, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInLine(entity, ignoreEntity, coord, distanceInTiles, directions, Set.of(type), ignores, restriction);
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
	
	public static List<FindProps> findInLine(Entity entity, Entity ignoreEntity, TileCoord coord, int distanceInTiles, Set<Direction> directions, Set<FindType> types, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		List<FindProps> list = new ArrayList<>();
		for (Direction dir : directions) {
			boolean isAcessible = true;
			int distance = distanceInTiles;
			FindType findType = null;
			for (TileCoord c = coord.getNewInstance().incCoordsByDirection(dir); distance-- > 0 && MapSet.haveTilesOnCoord(c); c.incCoordsByDirection(dir)) {
				if ((findType = getWichObjectIsOnTile(entity, ignoreEntity, c, types, ignores)) != null)
						list.add(new FindProps(findType, c.getNewInstance(), dir, isAcessible));
				if (isAcessible && !MapSet.tileIsFree(entity, c, entity == null ? null : entity.getPassThrough())) {
					isAcessible = false;
					if (restriction == FindTypeRestriction.ONLY_IF_IS_ACESSIBLE)
						break;
				}
			}
		}
		return list.isEmpty() ? null : list;
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, int radiusInTiles, FindType type, FindTypeRestriction restriction) {
		return findInRect(null, coord, ignoreEntity, null, radiusInTiles, Set.of(type), null, restriction);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, int radiusInTiles, FindType type, FindTypeRestriction restriction) {
		return findInRect(entity, coord, ignoreEntity, null, radiusInTiles, Set.of(type), null, restriction);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, int radiusInTiles, FindType type, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInRect(null, coord, ignoreEntity, null, radiusInTiles, Set.of(type), ignores, restriction);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, int radiusInTiles, FindType type, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInRect(entity, coord, ignoreEntity, null, radiusInTiles, Set.of(type), ignores, restriction);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, int radiusInTiles, Set<FindType> types, FindTypeRestriction restriction) {
		return findInRect(null, coord, ignoreEntity, null, radiusInTiles, types, null, restriction);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, int radiusInTiles, Set<FindType> types, FindTypeRestriction restriction) {
		return findInRect(entity, coord, ignoreEntity, null, radiusInTiles, types, null, restriction);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInRect(null, coord, ignoreEntity, null, radiusInTiles, types, ignores, restriction);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInRect(entity, coord, ignoreEntity, null, radiusInTiles, types, ignores, restriction);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, FindType type, FindTypeRestriction restriction) {
		return findInRect(null, coord, ignoreEntity, findType, radiusInTiles, Set.of(type), null, restriction);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, FindType type, FindTypeRestriction restriction) {
		return findInRect(entity, coord, ignoreEntity, findType, radiusInTiles, Set.of(type), null, restriction);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, FindType type, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInRect(null, coord, ignoreEntity, findType, radiusInTiles, Set.of(type), ignores, restriction);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, FindType type, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInRect(entity, coord, ignoreEntity, findType, radiusInTiles, Set.of(type), ignores, restriction);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, Set<FindType> types, FindTypeRestriction restriction) {
		return findInRect(null, coord, ignoreEntity, findType, radiusInTiles, types, null, restriction);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, Set<FindType> types, FindTypeRestriction restriction) {
		return findInRect(entity, coord, ignoreEntity, findType, radiusInTiles, types, null, restriction);
	}

	public static List<FindProps> findInRect(TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		return findInRect(null, coord, ignoreEntity, findType, radiusInTiles, types, ignores, restriction);
	}

	public static List<FindProps> findInRect(Entity entity, TileCoord coord, Entity ignoreEntity, FindInRectType findType, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores, FindTypeRestriction restriction) {
		List<FindProps> list = new ArrayList<>();
		Consumer<TileCoord> consumer = coord2 -> {
			FindType ft; 
			if ((ft = getWichObjectIsOnTile(entity, ignoreEntity, coord2, types, ignores)) != null) {
				Function<TileCoord, Boolean> tileIsFree = t -> {
					return MapSet.tileIsFree(t, ignores) || t.equals(coord) || t.equals(coord2);
				};
				PathFinder pf = new PathFinder(coord, coord2, Direction.DOWN, tileIsFree);
				if (restriction == FindTypeRestriction.EVERYTHING || pf.pathWasFound())
					list.add(new FindProps(ft, coord2.getNewInstance(), pf.getNextDirectionToGo(), pf.pathWasFound()));
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
	
}