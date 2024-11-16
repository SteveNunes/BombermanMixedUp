package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
			out:
			for (TileCoord c = coord.getNewInstance().incCoordsByDirection(dir); distance-- > 0 && MapSet.haveTilesOnCoord(c); c.incCoordsByDirection(dir)) {
				if (!MapSet.tileIsFree(c, ignores)) {
					if ((types.contains(FindType.BOMB) && Bomb.haveBombAt(entity, c)) ||
							(types.contains(FindType.BRICK) && Brick.haveBrickAt(c)) ||
							(types.contains(FindType.GOOD_ITEM) && Item.haveItemAt(c) && !Item.getItemAt(c).getItemType().isBadItem()) ||
							(types.contains(FindType.BAD_ITEM) && Item.haveItemAt(c) && Item.getItemAt(c).getItemType().isBadItem()) ||
							(types.contains(FindType.ITEM) && Item.haveItemAt(c)) ||
							(types.contains(FindType.MONSTER) && Entity.haveAnyEntityAtCoord(c, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(c, Monster.class)) ||
							(types.contains(FindType.PLAYER) && Entity.haveAnyEntityAtCoord(c, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(c, BomberMan.class)))
								list.add(new FindProps(c.getNewInstance(), dir));
					break out;
				}
				else if (types.contains(FindType.EMPTY) ||
						(types.contains(FindType.MONSTER) && Entity.haveAnyEntityAtCoord(c, ignoreEntity) && Entity.getFirstEntityFromCoord(c) instanceof Monster) ||
						(types.contains(FindType.PLAYER) && Entity.haveAnyEntityAtCoord(c, ignoreEntity) && Entity.getFirstEntityFromCoord(c) instanceof BomberMan)) {
							list.add(new FindProps(c.getNewInstance(), dir));
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
		radiusInTiles--;
		List<FindProps> list = new ArrayList<>();
		TileCoord coord2 = new TileCoord();
		for (int radius = 1; radius <= radiusInTiles; radius++)
			for (int y = coord.getY() - radius; y <= coord.getY() + radius; y++)
				for (int x = coord.getX() - radius; x <= coord.getX() + radius; x++)
					if (x == coord.getX() - radius || x == coord.getX() + radius ||
							y == coord.getY() - radius || y == coord.getY() + radius) {
									int dx = x - coord.getX(), dy = y - coord.getY();
									if (findType == null || findType == FindInRectType.RECTANGLE_AREA || (dx * dx) / (radiusInTiles * radiusInTiles) + (dy * dy) / (radiusInTiles * radiusInTiles) <= 1) {
										coord2.setCoords(x, y);
										boolean found = false;
										if (!MapSet.tileIsFree(coord2, ignores)) {
											if ((types.contains(FindType.BOMB) && Bomb.haveBombAt(entity, coord2)) || (types.contains(FindType.BRICK) && Brick.haveBrickAt(coord2)) || (types.contains(FindType.ITEM) && Item.haveItemAt(coord2)) || (types.contains(FindType.MONSTER) && Entity.haveAnyEntityAtCoord(coord2, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(coord2, Monster.class)) || (types.contains(FindType.PLAYER) && Entity.haveAnyEntityAtCoord(coord2, ignoreEntity) && Entity.entitiesInCoordContaisAnInstanceOf(coord2, BomberMan.class)))
												found = true;
										}
										else if (types.contains(FindType.EMPTY) || (types.contains(FindType.MONSTER) && Entity.haveAnyEntityAtCoord(coord2, ignoreEntity) && Entity.getFirstEntityFromCoord(coord2) instanceof Monster) || (types.contains(FindType.PLAYER) && Entity.haveAnyEntityAtCoord(coord2, ignoreEntity) && Entity.getFirstEntityFromCoord(coord2) instanceof BomberMan))
											found = true;
										if (found) {
											Function<TileCoord, Boolean> tileIsFree = t -> {
												return MapSet.tileIsFree(t, ignores) || t.equals(coord) || t.equals(coord2);
											};
											PathFinder pf = new PathFinder(coord, coord2, Direction.DOWN, tileIsFree);
											if (pf.pathWasFound())
												list.add(new FindProps(coord2.getNewInstance(), pf.getNextDirectionToGo()));
										}
									}
			}
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