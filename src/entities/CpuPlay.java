package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import enums.CpuDificult;
import enums.Direction;
import enums.FindType;
import enums.GameInput;
import enums.PassThrough;
import enums.TileProp;
import gui.GameTikTok;
import javafx.scene.paint.Color;
import maps.MapSet;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import pathfinder.PathFinderDistance;
import pathfinder.PathFinderOptmize;
import tools.Draw;
import tools.FindProps;
import tools.Tools;
import util.MyMath;
import util.TimerFX;

public class CpuPlay {
	
	/* PROXIMAS ETAPAS:
	 * - Procurar jogadores proximos e soltar bomba perto deles (PRIORIZAR APENAS ABAIXO DE SAIR DO TILE DANGER)
	 * - Procurar por item proximos e tentar pega-los (priorizar mais do que chegar no tijolo mais proximo)
	 * - Se tiver andando de cara pra parede e não tiver como sair do tile atual, largar os inputs pra ele parar de ficar andando contra a parede
	 * - Verificação de vitoria
	 * - Adicionar mais itens aos tijolos 
	 */
	
	@SuppressWarnings("serial")
	private static Map<Direction, GameInput> dirToInput = new HashMap<>() {{
		put(Direction.DOWN, GameInput.DOWN);
		put(Direction.RIGHT, GameInput.RIGHT);
		put(Direction.UP, GameInput.UP);
		put(Direction.LEFT, GameInput.LEFT);
	}};
	
	private int dificult;
	private BomberMan bomberMan;
	private int pauseInFrames;
	private PathFinder pathFinder;
	private static long timerId = 0;

	public static boolean justMark = true;

	public CpuPlay(BomberMan bomberMan, CpuDificult dificult) {
		super();
		this.dificult = dificult.getValue();
		this.bomberMan = bomberMan;
		pathFinder = null;
		pauseInFrames = 0;
	}
	
	public void run() {
		if (pauseInFrames == -1 && !justMark) {
			pauseInFrames = 0;
			Draw.clearFixedMarks();
		}
		if (!bomberMan.isBlockedMovement()) {
			if (isOverDangerTile()) {
				pauseInFrames = 0;
				return;
			}
			if (pauseInFrames > 0)
				pauseInFrames--;
			else if (!isMoving() || isPerfectlyBlockedDir() || tileWasChanged()) {
				releaseAllInputs();
				if (tileWasChanged() && getPathFinder() != null) {
					Direction dir = getPathFinder().getNextDirectionToGoAndRemove();
					if (dir != null) {
						echo("PathFinder: " + dir.name());
						holdButton(dirToInput(dir));
						return;
					}
					setPathFinder(null);
				}
				if (checkForBricksAround())			
					return;
				if (doRandomPause())			
					return;
				goToRandomFreeDir();
			}
		}
	}

	private boolean isOverDangerTile() {
		if (MapSet.tileContainsProp(getCurrentTileCoord(), TileProp.CPU_DANGER)) {
			echo("Over danger tile");
			if (findSafeSpotAndGo(getCurrentTileCoord(), getCurrentDir(), true))
				return true;
		}
		return false;
	}

	private boolean setPathFinder(PathFinder pathFinder) {
		this.pathFinder = pathFinder;
		if (pathFinder != null) {
			if (!this.pathFinder.pathWasFound())
				this.pathFinder = null;
			else {
				releaseAllInputs();
				Direction dir = this.pathFinder.getNextDirectionToGoAndRemove();
				holdButton(dirToInput(dir));
				echo("Start PF to " + dir.name());
				return true;
			}
		}
		return false;
	}

	private void goToRandomFreeDir() {
		List<Direction> dirs = Tools.getFreeDirections(bomberMan, getCurrentTileCoord(), passThrough());
		if (dirs != null) {
			int i = (int)MyMath.getRandom(0, dirs.size() - 1), ii = i;
			Direction dir;
			TileCoord coord;
			do {
				dir = dirs.get(i);
				coord = getCurrentTileCoord().getNewInstance().incCoordsByDirection(dir);
				if (++i == dirs.size())
					i = 0;
			}
			while (i != ii && !tileIsSafe(coord));
			if (tileIsSafe(coord)) {
				holdButton(dirToInput(dir));
				echo("Random go to " + dir.name());
			}
		}
	}
	
	private void echo(String string) {
		GameTikTok.addEcho(string);
		System.out.println(string);
	}
	
	private boolean checkForBricksAround() {
		if (bomberMan.getMaxBombs() == 0 || !tileIsSafe(getCurrentTileCoord()))
			return false;
		FindProps find = Tools.findInLine(bomberMan, getCurrentTileCoord(), getFireRange(), Set.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), FindType.BRICK);
		if (find != null) {
			echo("Brick found at " + find.getDir().name());
			if (justMark) {
				pauseInFrames = -1;
				Draw.addFixedMarkTile(find.getCoord(), Color.ORANGE);
			}
			if (findSafeSpotAndGo(getCurrentTileCoord(), find.getDir(), getFireRange(), false, c -> {
					if (justMark)
						Draw.addFixedMarkTile(c, Color.LIGHTBLUE);
					else
						setBomb();
				}))
					return true;
		}
		TileCoord coord = Tools.findInRect(getCurrentTileCoord(), null, 9, FindType.BRICK);
		Function<TileCoord, Boolean> tileIsFree = t -> {
			return tileIsSafe(t) || t.equals(coord) || t.equals(getCurrentTileCoord());
		};
		return coord != null && setPathFinder(new PathFinder(getCurrentTileCoord(), coord, getCurrentDir(), PathFinderDistance.SHORTEST, PathFinderOptmize.OPTIMIZED, tileIsFree));
	}

	private boolean findSafeSpotAndGo(TileCoord coord, Direction direction, boolean ignoreUnsafeSpots) {
		return findSafeSpotAndGo(coord, direction, 0, ignoreUnsafeSpots, null);
	}

	private boolean findSafeSpotAndGo(TileCoord coord, Direction direction, boolean ignoreUnsafeSpots, Consumer<TileCoord> onLeaveTileEvent) {
		return findSafeSpotAndGo(coord, direction, 0, ignoreUnsafeSpots, onLeaveTileEvent);
	}
	
	private boolean findSafeSpotAndGo(TileCoord coord, Direction direction, int minTileDistanceFromCoord, boolean ignoreUnsafeSpots) {
		return findSafeSpotAndGo(coord, direction, minTileDistanceFromCoord, ignoreUnsafeSpots, null);
	}

	private boolean findSafeSpotAndGo(TileCoord coord, Direction direction, int minTileDistanceFromCoord, boolean ignoreUnsafeSpots, Consumer<TileCoord> onLeaveTileEvent) {
		coord = coord.getNewInstance();
		TileCoord start = coord.getNewInstance();
		for (int n = 0; n < 4; n++) {
			coord.setCoords(start);
			while ((ignoreUnsafeSpots && tileIsFree(coord)) || (!ignoreUnsafeSpots && tileIsSafe(coord))) {
				List<Direction> dirs = Tools.getFreeDirections(bomberMan, coord, passThrough());
				if (n > minTileDistanceFromCoord || dirs != null) {
					Direction dir3 = null;
					TileCoord coord3 = null;
					if (n > minTileDistanceFromCoord) {
						dir3 = direction;
						coord3 = coord.getNewInstance();
					}
					else if (dirs != null)
						for (Direction dir2 : dirs) {
							TileCoord coord2 = coord.getNewInstance().incCoordsByDirection(dir2);
							if (tileIsSafe(coord2)) {
								dir3 = dir2;
								coord3 = coord2.getNewInstance();
								break;
							}
						}
					else
						continue;
					if (setPathFinder(new PathFinder(start.getNewInstance(), coord3, dir3, PathFinderDistance.SHORTEST, PathFinderOptmize.OPTIMIZED, t -> (ignoreUnsafeSpots && tileIsFree(t)) || (!ignoreUnsafeSpots && tileIsSafe(t))))) {
						if (onLeaveTileEvent != null)
							onLeaveTileEvent.accept(coord3);
						return true;
					}
				}
				coord.incCoordsByDirection(direction);
			}
			direction = direction.getNext4WayClockwiseDirection();
		}
		return false;
	}
	
	private boolean doRandomPause() {
		if (tileWasChanged() && (int)MyMath.getRandom(0, 10) == 0) {
			pauseInFrames = (int)MyMath.getRandom(30, 180);
			echo("Doing pause " + pauseInFrames);
			return true;
		}
		return false;
	}

	private void setBomb() {
		pressButton(GameInput.B);
		echo("Set Bomb");
	}

	private Set<PassThrough> explosionPassThrough() {
		return Set.of(PassThrough.PLAYER, PassThrough.MONSTER, PassThrough.HOLE, PassThrough.ITEM);
	}

	private void pressButton(GameInput button) {
		if (pauseInFrames == 0) {
			bomberMan.keyPress(button);
			TimerFX.createTimer("releaseButton-" + timerId++, 10, () -> releaseButton(button));
		}
	}

	private void holdButton(GameInput button) {
		if (pauseInFrames == 0)
			bomberMan.keyPress(button);
	}

	private void releaseButton(GameInput button) {
		bomberMan.keyRelease(button);
	}

	private boolean tileIsSafe(TileCoord coord) {
		return !MapSet.tileContainsProp(coord, TileProp.CPU_DANGER) && tileIsFree(coord);
	}
	
	private boolean tileIsFree(TileCoord coord) {
		return !MapSet.tileContainsProp(coord, TileProp.DAMAGE_PLAYER) &&
					 !MapSet.tileContainsProp(coord, TileProp.EXPLOSION) &&
					 	MapSet.tileIsFree(bomberMan, coord, passThrough());
	}

	private int getFireRange() {
		return bomberMan.getFireRange();
	}

	private boolean isMoving() {
		return bomberMan.isMoving();
	}

	private void releaseAllInputs() {
		for (GameInput input : new ArrayList<>(bomberMan.getHoldedInputs()))
			releaseButton(input);
	}
	
	private boolean tileWasChanged() {
		return bomberMan.tileWasChanged();
	}

	public TileCoord getCurrentTileCoord() {
		return bomberMan.getTileCoordFromCenter();
	}

	public Direction getCurrentDir() {
		return bomberMan.getDirection();
	}

	private Set<PassThrough> passThrough() {
		return bomberMan.getPassThrough();
	}

	private PathFinder getPathFinder() {
		return pathFinder;
	}

	private boolean isPerfectlyBlockedDir() {
		return bomberMan.isPerfectlyBlockedDir(getCurrentDir());
	}

	private GameInput dirToInput(Direction dir) {
		return dirToInput.get(dir);
	}
	
}
