package tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import drawimage_stuffs.DrawImageEffects;
import entities.Bomb;
import entities.BomberMan;
import entities.Entity;
import entities.Explosion;
import entities.Monster;
import entities.TileDamage;
import enums.Direction;
import enums.FindType;
import enums.PassThrough;
import gameutil.FPSHandler;
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.TileCoord;
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
	
	public static TileCoord findInRect(TileCoord coord, Direction direction, FindType type)
		{ return findInRect(null, coord, Set.of(direction), Set.of(type), null); }
	
	public static TileCoord findInRect(TileCoord coord, Direction direction, Set<FindType> types)
		{ return findInRect(null, coord, Set.of(direction), types, null); }
	
	public static TileCoord findInRect(TileCoord coord, Set<Direction> directions, FindType type)
		{ return findInRect(null, coord, directions, Set.of(type), null); }
	
	public static TileCoord findInRect(TileCoord coord, Set<Direction> directions, Set<FindType> types)
		{ return findInRect(null, coord, directions, types, null); }
	
	public static TileCoord findInRect(TileCoord coord, Direction direction, FindType type, Set<PassThrough> ignores)
		{ return findInRect(null, coord, Set.of(direction), Set.of(type), ignores); }
	
	public static TileCoord findInRect(TileCoord coord, Direction direction, Set<FindType> types, Set<PassThrough> ignores)
		{ return findInRect(null, coord, Set.of(direction), types, ignores); }
	
	public static TileCoord findInRect(TileCoord coord, Set<Direction> directions, FindType type, Set<PassThrough> ignores)
		{ return findInRect(null, coord, directions, Set.of(type), ignores); }
	
	public static TileCoord findInRect(TileCoord coord, Set<Direction> directions, Set<FindType> types, Set<PassThrough> ignores)
		{ return findInRect(null, coord, directions, types, ignores); }
	
	public static TileCoord findInRect(Entity entity, TileCoord coord, Direction direction, FindType type)
		{ return findInRect(entity, coord, Set.of(direction), Set.of(type), null); }
	
	public static TileCoord findInRect(Entity entity, TileCoord coord, Direction direction, Set<FindType> types)
		{ return findInRect(entity, coord, Set.of(direction), types, null); }
	
	public static TileCoord findInRect(Entity entity, TileCoord coord, Set<Direction> directions, FindType type)
		{ return findInRect(entity, coord, directions, Set.of(type), null); }
	
	public static TileCoord findInRect(Entity entity, TileCoord coord, Set<Direction> directions, Set<FindType> types)
		{ return findInRect(entity, coord, directions, types, null); }

	public static TileCoord findInRect(Entity entity, TileCoord coord, Direction direction, FindType type, Set<PassThrough> ignores)
		{ return findInRect(entity, coord, Set.of(direction), Set.of(type), ignores); }

	public static TileCoord findInRect(Entity entity, TileCoord coord, Direction direction, Set<FindType> types, Set<PassThrough> ignores)
		{ return findInRect(entity, coord, Set.of(direction), types, ignores); }

	public static TileCoord findInRect(Entity entity, TileCoord coord, Set<Direction> directions, FindType type, Set<PassThrough> ignores)
		{ return findInRect(entity, coord, directions, Set.of(type), ignores); }
	
	public static TileCoord findInRect(Entity entity, TileCoord coord, Set<Direction> directions, Set<FindType> types, Set<PassThrough> ignores) {
		for (Direction dir : directions)
			for (TileCoord c = coord.getNewInstance().incCoordsByDirection(dir); MapSet.haveTilesOnCoord(c); c.incCoordsByDirection(dir)) {
				if (!MapSet.tileIsFree(c, ignores))
					return null;
				else if (types.contains(FindType.EMPTY) ||
								(types.contains(FindType.BOMB) && Bomb.haveBombAt(entity, c)) ||
								(types.contains(FindType.BRICK) && Brick.haveBrickAt(c)) ||
								(types.contains(FindType.ITEM) && Item.haveItemAt(c)) ||
								(types.contains(FindType.MONSTER) && Entity.haveAnyEntityAtCoord(c) && Entity.getEntityListFromCoord(c) instanceof Monster) ||
								(types.contains(FindType.PLAYER) && Entity.haveAnyEntityAtCoord(c) && Entity.getEntityListFromCoord(c) instanceof BomberMan))
									return c;
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