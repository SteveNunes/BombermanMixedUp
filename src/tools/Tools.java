package tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import drawimage_stuffs.DrawImageEffects;
import entities.Bomb;
import entities.BomberMan;
import entities.Entity;
import entities.Explosion;
import entities.Monster;
import entities.TileDamage;
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
import util.MyMath;

public abstract class Tools {
	
	private static FPSHandler fpsHandler;
	
	public static void loadStuffs() {
		fpsHandler = new FPSHandler(60);
		Draw.loadStuffs();
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
		TileDamage.runTileDamages();
	}
	
	public static DrawImageEffects loadEffectsFromString(String arrayToString) {
		// NOTA: implementar
		return new DrawImageEffects();
	}
	
	public static String SpriteEffectsToString(DrawImageEffects effects) {
		// NOTA: implementar
		return "-";
	}
	
	public static TileCoord findInLine(TileCoord coord, Direction direction, FindType type)
		{ return findInLine(null, coord, Set.of(direction), Set.of(type), null); }
	
	public static TileCoord findInLine(TileCoord coord, Direction direction, Set<FindType> types)
		{ return findInLine(null, coord, Set.of(direction), types, null); }
	
	public static TileCoord findInLine(TileCoord coord, Set<Direction> directions, FindType type)
		{ return findInLine(null, coord, directions, Set.of(type), null); }
	
	public static TileCoord findInLine(TileCoord coord, Set<Direction> directions, Set<FindType> types)
		{ return findInLine(null, coord, directions, types, null); }
	
	public static TileCoord findInLine(TileCoord coord, Direction direction, FindType type, Set<PassThrough> ignores)
		{ return findInLine(null, coord, Set.of(direction), Set.of(type), ignores); }
	
	public static TileCoord findInLine(TileCoord coord, Direction direction, Set<FindType> types, Set<PassThrough> ignores)
		{ return findInLine(null, coord, Set.of(direction), types, ignores); }
	
	public static TileCoord findInLine(TileCoord coord, Set<Direction> directions, FindType type, Set<PassThrough> ignores)
		{ return findInLine(null, coord, directions, Set.of(type), ignores); }
	
	public static TileCoord findInLine(TileCoord coord, Set<Direction> directions, Set<FindType> types, Set<PassThrough> ignores)
		{ return findInLine(null, coord, directions, types, ignores); }
	
	public static TileCoord findInLine(Entity entity, TileCoord coord, Direction direction, FindType type)
		{ return findInLine(entity, coord, Set.of(direction), Set.of(type), null); }
	
	public static TileCoord findInLine(Entity entity, TileCoord coord, Direction direction, Set<FindType> types)
		{ return findInLine(entity, coord, Set.of(direction), types, null); }
	
	public static TileCoord findInLine(Entity entity, TileCoord coord, Set<Direction> directions, FindType type)
		{ return findInLine(entity, coord, directions, Set.of(type), null); }
	
	public static TileCoord findInLine(Entity entity, TileCoord coord, Set<Direction> directions, Set<FindType> types)
		{ return findInLine(entity, coord, directions, types, null); }

	public static TileCoord findInLine(Entity entity, TileCoord coord, Direction direction, FindType type, Set<PassThrough> ignores)
		{ return findInLine(entity, coord, Set.of(direction), Set.of(type), ignores); }

	public static TileCoord findInLine(Entity entity, TileCoord coord, Direction direction, Set<FindType> types, Set<PassThrough> ignores)
		{ return findInLine(entity, coord, Set.of(direction), types, ignores); }

	public static TileCoord findInLine(Entity entity, TileCoord coord, Set<Direction> directions, FindType type, Set<PassThrough> ignores)
		{ return findInLine(entity, coord, directions, Set.of(type), ignores); }
	
	public static TileCoord findInLine(Entity entity, TileCoord coord, Set<Direction> directions, Set<FindType> types, Set<PassThrough> ignores) {
		for (Direction dir : directions)
			for (TileCoord c = coord.getNewInstance().incCoordsByDirection(dir); MapSet.haveTilesOnCoord(c); c.incCoordsByDirection(dir)) {
				if (!MapSet.tileIsFree(c, ignores)) {
					if ((types.contains(FindType.BOMB) && Bomb.haveBombAt(entity, c)) ||
							(types.contains(FindType.BRICK) && Brick.haveBrickAt(c)) ||
							(types.contains(FindType.ITEM) && Item.haveItemAt(c)) ||
							(types.contains(FindType.MONSTER) && Entity.haveAnyEntityAtCoord(c) && Entity.getEntityListFromCoord(c) instanceof Monster) ||
							(types.contains(FindType.PLAYER) && Entity.haveAnyEntityAtCoord(c) && Entity.getEntityListFromCoord(c) instanceof BomberMan))
					return c;
					return null;
				}
				else if (types.contains(FindType.EMPTY))
					return c;
			}
		return null;
	}

	public static TileCoord findInRect(TileCoord coord, int radiusInTiles, FindType type)
		{ return findInRect(null, coord, null, radiusInTiles, Set.of(type), null); }
		
	public static TileCoord findInRect(Entity entity, TileCoord coord, int radiusInTiles, FindType type)
		{ return findInRect(entity, coord, null, radiusInTiles, Set.of(type), null); }
	
	public static TileCoord findInRect(TileCoord coord, int radiusInTiles, FindType type, Set<PassThrough> ignores)
		{ return findInRect(null, coord, null, radiusInTiles, Set.of(type), ignores); }
	
	public static TileCoord findInRect(Entity entity, TileCoord coord, int radiusInTiles, FindType type, Set<PassThrough> ignores)
		{ return findInRect(entity, coord, null, radiusInTiles, Set.of(type), ignores); }
	
	public static TileCoord findInRect(TileCoord coord, int radiusInTiles, Set<FindType> types)
		{ return findInRect(null, coord, null, radiusInTiles, types, null); }
		
	public static TileCoord findInRect(Entity entity, TileCoord coord, int radiusInTiles, Set<FindType> types)
		{ return findInRect(entity, coord, null, radiusInTiles, types, null); }
	
	public static TileCoord findInRect(TileCoord coord, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores)
		{ return findInRect(null, coord, null, radiusInTiles, types, ignores); }
	
	public static TileCoord findInRect(Entity entity, TileCoord coord, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores)
		{ return findInRect(entity, coord, null, radiusInTiles, types, ignores); }
	
	public static TileCoord findInRect(TileCoord coord, FindInRectType findType, int radiusInTiles, FindType type)
		{ return findInRect(null, coord, findType, radiusInTiles, Set.of(type), null); }
		
	public static TileCoord findInRect(Entity entity, TileCoord coord, FindInRectType findType, int radiusInTiles, FindType type)
		{ return findInRect(entity, coord, findType, radiusInTiles, Set.of(type), null); }
	
	public static TileCoord findInRect(TileCoord coord, FindInRectType findType, int radiusInTiles, FindType type, Set<PassThrough> ignores)
		{ return findInRect(null, coord, findType, radiusInTiles, Set.of(type), ignores); }
	
	public static TileCoord findInRect(Entity entity, TileCoord coord, FindInRectType findType, int radiusInTiles, FindType type, Set<PassThrough> ignores)
		{ return findInRect(entity, coord, findType, radiusInTiles, Set.of(type), ignores); }

	public static TileCoord findInRect(TileCoord coord, FindInRectType findType, int radiusInTiles, Set<FindType> types)
		{ return findInRect(null, coord, findType, radiusInTiles, types, null); }
		
	public static TileCoord findInRect(Entity entity, TileCoord coord, FindInRectType findType, int radiusInTiles, Set<FindType> types)
		{ return findInRect(entity, coord, findType, radiusInTiles, types, null); }

	public static TileCoord findInRect(TileCoord coord, FindInRectType findType, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores)
		{ return findInRect(null, coord, findType, radiusInTiles, types, ignores); }
	
	public static TileCoord findInRect(Entity entity, TileCoord coord, FindInRectType findType, int radiusInTiles, Set<FindType> types, Set<PassThrough> ignores) {
		radiusInTiles--;
		TileCoord coord2 = new TileCoord();
		for (int y = coord.getY() - radiusInTiles; y <= coord.getY() + radiusInTiles; y++)
			for (int x = coord.getX() - radiusInTiles; x <= coord.getX() + radiusInTiles; x++) {
				int dx = x - coord.getX();
        int dy = y - coord.getY();
				if (findType == null || findType == FindInRectType.RECTANGLE_AREA || (dx * dx) / (radiusInTiles * radiusInTiles) + (dy * dy) / (radiusInTiles * radiusInTiles) <= 1) {
					coord2.setCoords(x, y);
					boolean found = false;
					if (!MapSet.tileIsFree(coord2, ignores)) {
						if ((types.contains(FindType.BOMB) && Bomb.haveBombAt(entity, coord2)) ||
								(types.contains(FindType.BRICK) && Brick.haveBrickAt(coord2)) ||
								(types.contains(FindType.ITEM) && Item.haveItemAt(coord2)) ||
								(types.contains(FindType.MONSTER) && Entity.haveAnyEntityAtCoord(coord2) && Entity.getEntityListFromCoord(coord2) instanceof Monster) ||
								(types.contains(FindType.PLAYER) && Entity.haveAnyEntityAtCoord(coord2) && Entity.getEntityListFromCoord(coord2) instanceof BomberMan))
									found = true;
					}
					else if (types.contains(FindType.EMPTY))
							found = true;
					if (found) {
						Function<TileCoord, Boolean> tileIsFree = t ->
							{ return MapSet.tileIsFree(t) || t.equals(coord2); };
						PathFinder pf = new PathFinder(coord, coord2, Direction.DOWN, tileIsFree);
						if (pf.pathWasFound())
							return coord2;
					}
				}
			}
		return null;
	}
	
	
	public static TileCoord getRandomFreeTileCoordAround(Entity entity, TileCoord coord)
		{ return getRandomFreeTileCoordAround(entity, coord, null); }
	
	public static TileCoord getRandomFreeTileCoordAround(Entity entity, TileCoord coord, Set<PassThrough> passThrough) {
		Direction dir = getRandomFreeDirection(entity, coord, passThrough);
		return dir == null ? null : coord.getNewInstance().incCoordsByDirection(dir);
	}
	
	public static Direction getRandomFreeDirection(Entity entity, TileCoord coord)
		{ return getRandomFreeDirection(entity, coord, null); }
	
	public static Direction getRandomFreeDirection(Entity entity, TileCoord coord, Set<PassThrough> passThrough) {
		List<Direction> dirs = new ArrayList<>(Arrays.asList(Direction.LEFT, Direction.UP,	Direction.RIGHT, Direction.DOWN));
		TileCoord coord2 = null;
		Direction dir = null;
		do {
			int n = (int)MyMath.getRandom(0, dirs.size() - 1);
			dir = dirs.get(n);
			coord2 = coord.getNewInstance().incCoordsByDirection(dir);
			if (dirs.isEmpty()) {
				dir = null;
				break;
			}
			dirs.remove(n);
		}
		while (!MapSet.tileIsFree(entity, coord2, passThrough));
		return dir;
	}
	
}