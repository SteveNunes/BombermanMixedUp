package tools;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import entities.TileCoord;
import enums.Direction;
import enums.PathFindDistance;
import enums.PathFindIgnoreInitialBackDirection;
import enums.PathFindOptmize;
import javafx.util.Pair;
import util.Misc;

public class PathFinder {
	
	private Random random = new Random(new SecureRandom().nextInt(Integer.MAX_VALUE));
	private List<Pair<TileCoord, Direction>> directions;
	private Map<TileCoord, Boolean> blocks = new HashMap<>();
	private Map<TileCoord, Boolean> path = new HashMap<>();
	private Function<TileCoord, Boolean> functionIsTileFree;
	private Direction initialDirection;
	private PathFindIgnoreInitialBackDirection ignoreInitialBackDirection;
	private PathFindDistance distance;
	private PathFindOptmize optimize;
		
	public PathFinder(TileCoord initialCoord, TileCoord targetCoord, Direction initialDirection, Function<TileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, null, null, null, functionIsTileFree); }

	public PathFinder(TileCoord initialCoord, TileCoord targetCoord, Direction initialDirection, PathFindIgnoreInitialBackDirection ignoreInitialBackDirection, Function<TileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, ignoreInitialBackDirection, null, null, functionIsTileFree); }
	
	public PathFinder(TileCoord initialCoord, TileCoord targetCoord, Direction initialDirection, PathFindOptmize optimize, Function<TileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, null, null, optimize, functionIsTileFree); }
	
	public PathFinder(TileCoord initialCoord, TileCoord targetCoord, Direction initialDirection, PathFindDistance distance, Function<TileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, null, distance, null, functionIsTileFree); }

	public PathFinder(TileCoord initialCoord, TileCoord targetCoord, Direction initialDirection, PathFindIgnoreInitialBackDirection ignoreInitialBackDirection, PathFindDistance distance, Function<TileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, ignoreInitialBackDirection, distance, null, functionIsTileFree); }
	
	public PathFinder(TileCoord initialCoord, TileCoord targetCoord, Direction initialDirection, PathFindIgnoreInitialBackDirection ignoreInitialBackDirection, PathFindOptmize optimize, Function<TileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, ignoreInitialBackDirection, null, optimize, functionIsTileFree); }
	
	public PathFinder(TileCoord initialCoord, TileCoord targetCoord, Direction initialDirection, PathFindDistance distance, PathFindOptmize optimize, Function<TileCoord, Boolean> functionIsTileFree)
		{ this(initialCoord, targetCoord, initialDirection, null, distance, optimize, functionIsTileFree); }

	public PathFinder(TileCoord initialCoord, TileCoord targetCoord, Direction initialDirection, PathFindIgnoreInitialBackDirection ignoreInitialBackDirection, PathFindDistance distance, PathFindOptmize optimize, Function<TileCoord, Boolean> functionIsTileFree) {
		this.initialDirection = initialDirection == null ? Direction.LEFT : initialDirection;
		this.ignoreInitialBackDirection = ignoreInitialBackDirection == null ? PathFindIgnoreInitialBackDirection.NO_IGNORE : ignoreInitialBackDirection;
		this.distance = distance == null ? PathFindDistance.RANDOM : distance;
		this.optimize = optimize == null ? PathFindOptmize.NOT_OPTIMIZED : optimize;
		this.functionIsTileFree = functionIsTileFree;
		directions = new ArrayList<>();
		recalculatePath(initialCoord, targetCoord, this.initialDirection);
	}
	
	public Direction getNextDirectionToGo() {
		Direction dir = directions.isEmpty() ? null : directions.get(0).getValue();
		if (!directions.isEmpty())
			directions.remove(0);
		return dir;
	}
	
	public List<Pair<TileCoord, Direction>> getCurrentPath()
		{ return directions.isEmpty() ? null : directions; }
	
	public void recalculatePath(TileCoord currentCoord, TileCoord targetCoord, Direction currentDirection) {
		blocks = new HashMap<>();
		path = new HashMap<>();
		if (currentCoord.equals(targetCoord) || !isTileFree(currentCoord) || !isTileFree(targetCoord)) {
			directions.clear();
			return;
		}
		if (continueCurrentPath(currentCoord, targetCoord, currentDirection))
			return;
		directions.clear();
		Direction dir = currentDirection;
		TileCoord unMark = null;
		TileCoord coord = currentCoord.getNewInstance();
		List<List<Pair<TileCoord, Direction>>> foundPaths = new ArrayList<>();
		List<Pair<TileCoord, Direction>> dirs = new ArrayList<>();
		while (!coord.equals(targetCoord)) {
			path.put(coord.getNewInstance(), true);
			if (coord.equals(currentCoord)) {
				if (ignoreInitialBackDirection == PathFindIgnoreInitialBackDirection.IGNORE || 
						(ignoreInitialBackDirection == PathFindIgnoreInitialBackDirection.ONLY_IF_THERES_NO_AVAILABLE_DIRECTION &&
						 (!foundPaths.isEmpty() || getFreeDirs(coord).size() > 1)))
								dir = getRandomFreeDir(coord, initialDirection.getReverseDirection());
				else
					dir = getRandomFreeDir(coord);
			}
			else
				dir = getRandomFreeDir(coord);
			if (dir == null)
				return;
			dirs.add(new Pair<>(coord.getNewInstance(), dir));
			blocks.put(coord.getNewInstance(), true);
			coord.incByDirection(dir);
			path.put(coord.getNewInstance(), true);
			if (unMark != null) {
				blocks.remove(unMark);
				unMark = null;
			}
			TileCoord first = dirs.get(0).getKey();
			if (coord.equals(targetCoord) || getFreeDirs(coord) == null) {
				blocks.put(coord.getNewInstance(), true);
				if (coord.equals(targetCoord)) {
					optimizePath(dirs);
					foundPaths.add(new ArrayList<>(dirs));
					directions = new ArrayList<>(dirs);
					unMark = coord.getNewInstance();
				}
				do {
					dir = dirs.get(dirs.size() - 1).getValue();
					path.remove(coord);
					coord.incByDirection(dir.getReverseDirection());
					dirs.remove(dirs.size() - 1);
				}
				while (getFreeDirs(coord) == null && !coord.equals(first));
			}
			if (coord.equals(first) || getFreeDirs(coord) == null)
				return;
		}
	}
	
	private boolean continueCurrentPath(TileCoord currentCoord, TileCoord targetCoord, Direction currentDirection) {
		// Fixar esse método
		if (Misc.alwaysTrue())
			return false;
		if (!directions.isEmpty()) {
			if (directions.get(0).getKey().equals(currentCoord) &&
					directions.get(directions.size() - 1).getKey().equals(targetCoord))
						return true;
			for (Pair<TileCoord, Direction> t : directions)
				if (!isTileFree(t.getKey()))
					return false;
			if (!directions.get(0).getKey().equals(currentCoord)) // Corta parte do caminho inicial se o 'currentCoord' ainda estiver dentro do caminho atual
				for (int n = 0; n < directions.size(); n++) {
					TileCoord coord = directions.get(n).getKey();
					if (coord.equals(currentCoord)) {
						directions = directions.subList(n, directions.size());
						break;
					}
					else if (n + 1 == directions.size()) // Se o 'currentCoord' não fizer mais parte do caminho atual
						return false;
					else if (coord.equals(targetCoord)) // Se o 'targetCoord' for encontrado no caminho atual antes do 'currentCoord'
						return false;
				}
			if (!Misc.alwaysTrue() && !directions.get(directions.size() - 1).getKey().equals(targetCoord)) { // Corta parte do caminho final se o 'targetCoord' ainda estiver dentro do caminho atual, e após o 'currentCoord'
				for (int n = directions.size() - 1; n >= 0; n--) {
					TileCoord coord = directions.get(n).getKey();
					if (coord.equals(targetCoord)) {
						directions = directions.subList(0, n + 1);
						return true;
					}
				}
				TileCoord oldTarget = directions.get(directions.size() - 1).getKey();
				Direction lastDir = directions.get(directions.size() - 1).getValue();
				PathFinder pf = new PathFinder(oldTarget, targetCoord, lastDir, ignoreInitialBackDirection, distance, optimize, functionIsTileFree);
				for (int n = 1; n < pf.getCurrentPath().size(); n++)
					directions.add(pf.getCurrentPath().get(n));
				optimizePath(directions);
			}
			return true;
		}		
		return false;
	}

	private void optimizePath(List<Pair<TileCoord, Direction>> dirs) {
		boolean restart;
		TileCoord current;
		TileCoord target = dirs.get(dirs.size() - 1).getKey().getNewInstance();
		do {
			restart = false;
	    for (int n = 0; n < dirs.size() - 4; n++) {
				Direction dir = dirs.get(n).getValue();
				current = dirs.get(n).getKey().getNewInstance();
				for (int d = 0; d < 4; d++) {
					TileCoord c = current.getNewInstance();
					c.incByDirection(dir);
					if (path.containsKey(c) && !c.equals(target)) {
						dir = dir.getNext4WayClockwiseDirection();
						continue;
					}
					int steps = 1;
					while (isTileFree(c, true)) {
						if (!c.equals(target)) {
							c.incByDirection(dir);
							steps++;
						}
						if (path.containsKey(c)) {
							TileCoord c2;
							while (n + 1 < dirs.size() && !(c2 = dirs.get(n + 1).getKey().getNewInstance()).equals(c)) {
								path.remove(c2);
								dirs.remove(n + 1);
							}
							c2 = current.getNewInstance();
							dirs.set(n, new Pair<>(c2.getNewInstance(), dir));
							int x = n;
							while (--steps > 0) {
								c2.incByDirection(dir);
								path.put(c2.getNewInstance(), true);
								dirs.add(++x, new Pair<>(c2.getNewInstance(), dir));
							}
							restart = true;
							break;
						}
					}
					if (restart)
						break;
					dir = dir.getNext4WayClockwiseDirection();
				}
				if (restart)
					break;
			}
		}
		while (restart);
	}

	private List<Direction> getFreeDirs(TileCoord coord)
		{ return getFreeDirs(coord, null); }
		
	private List<Direction> getFreeDirs(TileCoord coord, Direction ignoredDir) {
		Direction d = Direction.LEFT;
		List<Direction> freeDirs = new ArrayList<>();
		for (int n = 0; n < 4; n++) {
			TileCoord coord2 = coord.getNewInstance();
			coord2.incByDirection(d);
			if ((ignoredDir == null || d != ignoredDir) && isTileFree(coord2))
				freeDirs.add(d);
			d = d.getNext4WayClockwiseDirection();
		}
		return freeDirs.isEmpty() ? null : freeDirs;
	}
	
	private Direction getRandomFreeDir(TileCoord coord)
		{ return getRandomFreeDir(coord, null); }
	
	private Direction getRandomFreeDir(TileCoord coord, Direction ignoredDir) {
		List<Direction> freeDirs = getFreeDirs(coord, ignoredDir);
		return freeDirs == null ? null : freeDirs.get(random.nextInt(freeDirs.size()));
	}
	
	private boolean isTileFree(TileCoord coord)
		{ return isTileFree(coord, false); }
	
	private boolean isTileFree(TileCoord coord, boolean ignoreBlocks)
		{ return functionIsTileFree.apply(coord) && (ignoreBlocks || !blocks.containsKey(coord)); }

}