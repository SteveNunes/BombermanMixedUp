package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import application.Main;
import enums.BombType;
import enums.CpuDificult;
import enums.Direction;
import enums.FindInRectType;
import enums.FindType;
import enums.GameInput;
import enums.ItemType;
import enums.PassThrough;
import enums.TileProp;
import gui.GameTikTok;
import javafx.util.Duration;
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import pathfinder.PathFinderDistance;
import pathfinder.PathFinderOptmize;
import tools.FindProps;
import tools.Tools;
import util.DurationTimerFX;
import util.MyMath;

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

	public static boolean markTargets = true;

	public CpuPlay(BomberMan bomberMan, CpuDificult dificult) {
		super();
		this.dificult = dificult.getValue();
		this.bomberMan = bomberMan;
		pathFinder = null;
		pauseInFrames = 0;
	}
	
	public void run() {
		if (!Main.isFreeze() && !MapSet.stageObjectiveIsCleared() && !bomberMan.isBlockedMovement()) {
			// Se estiver em um tile perigoso, tenta encontrar uma forma de sair dele
			if (isOverDangerTile()) {
				pauseInFrames = 0;
				return;
			}
			if (pauseInFrames > 0)
				pauseInFrames--;
			else if (isStucked()) {
				releaseAllInputs();
				setPathFinder(null); // Se o PathFinder local estiver ativo, mas o personagem estiver preso, desativa o PathFinder local.
				if (isInvencible()) // Se tiver preso e invencivel, soltar uma bomba para tentar se desprender
					pressButton(GameInput.B);
			}
			else if (!isMoving() || isPerfectlyBlockedDir() || tileWasChanged()) {
				releaseAllInputs();
				// Se o PathFinder local estiver ativo, vira e anda para a próxima direção designada pelo PathFinder
				if (tileWasChanged() && getPathFinder() != null) {
					Direction dir = getPathFinder().getNextDirectionToGoAndRemove();
					if (dir != null && tileIsFree(getCurrentTileCoord().getNewInstance().incCoordsByDirection(dir))) {
						holdButton(dirToInput(dir));
						return;
					}
					setPathFinder(null);
				}
				// PODE fazer uma pausa aleatoria
				if (doRandomPause())			
					return;
				// Detona bombas do tipo REMOTE que a explosão não vá o acertar
				if (tileIsSafe(getCurrentTileCoord()))
					for (Bomb bomb : bomberMan.getBombs())
						if (bomb.getBombType() == BombType.REMOTE || bomb.getBombType() == BombType.SPIKED_REMOTE) {
							pressButton(GameInput.A);
							pauseInFrames = 20;
							return;
						}
				// Solta uma bomba se ele estiver invencivel em cima de uma explosão com algum player no alcance da explosão da bomba dele
				if (isInvencible() && MapSet.tileContainsProp(getCurrentTileCoord(), TileProp.EXPLOSION)) {
					List<FindProps> founds = Tools.findInLine(bomberMan, getCurrentTileCoord(), getFireRange(), Set.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), FindType.PLAYER);
					if (founds != null)
						pressButton(GameInput.B);
				}
				// Procura por outros jogadores ou tijolos ou itens ruins em linha reta para soltar uma bomba que o destrua.
				if (checkForBricksAndBadItemsAndPlayersAround())			
					return;
				// Procura por item ou tijolos proximos e vai em direção a eles
				if (checkForSomethingAround(Set.of(FindType.GOOD_ITEM, FindType.BRICK), 5, FindInRectType.RECTANGLE_AREA))
					return;
				// Se não estiver focado em algum item ou tijolo, tenta chegar perto de algum jogador acessivel
				for (Entity entity : Entity.getEntityList())
					if (entity != bomberMan && entity instanceof BomberMan && setPathFinder(new PathFinder(getCurrentTileCoord(), entity.getTileCoordFromCenter(), getCurrentDir(), PathFinderDistance.SHORTEST, PathFinderOptmize.OPTIMIZED, t -> tileIsSafe(t))))
						return;
				goToRandomFreeDir();
			}
		}
	}

	private boolean isStucked() {
		List<TileCoord> coords = Tools.getFreeTileCoordsAround(bomberMan, getCurrentTileCoord(), passThrough());
		if (coords == null)
			return true;
		for (TileCoord coord : coords)
			if (tileIsSafe(coord))
				return false;
		return true;
	}

	private boolean isOverDangerTile() {
		return MapSet.tileContainsProp(getCurrentTileCoord(), TileProp.CPU_DANGER) &&
						findSafeSpotAndGo(getCurrentTileCoord(), getCurrentDir(), true, t -> tileIsHalfSafe(t));
	}

	private boolean setPathFinder(PathFinder pathFinder) {
		this.pathFinder = pathFinder;
		if (pathFinder != null) {
			if (Brick.haveBrickAt(getPathFinder().getTargetCoord()) ||
					Entity.haveAnyEntityAtCoord(getCurrentTileCoord(), bomberMan) ||
					Item.haveItemAt(getPathFinder().getTargetCoord()))
						getPathFinder().removeLastCoordFromPath();
			if (!getPathFinder().pathWasFound())
				this.pathFinder = null;
			else {
				releaseAllInputs();
				Direction dir = getPathFinder().getNextDirectionToGoAndRemove();
				holdButton(dirToInput(dir));
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
			if (tileIsSafe(coord))
				holdButton(dirToInput(dir));
		}
	}
	
	private void echo(String string) {
		GameTikTok.addEcho(string);
		System.out.println(string);
	}
	
	private boolean checkForBricksAndBadItemsAndPlayersAround() {
		if (!canSetBomb())
			return false;
		List<FindProps> founds = Tools.findInLine(bomberMan, getCurrentTileCoord(), getFireRange(), Set.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), Set.of(FindType.PLAYER, FindType.BRICK, FindType.BAD_ITEM));
		if (founds != null)
			for (FindProps found : founds)
				if (!MapSet.tileContainsProp(found.getCoord(), TileProp.CPU_DANGER) &&
						!MapSet.tileContainsProp(found.getCoord(), TileProp.CPU_DANGER_2) &&
						findSafeSpotAndGo(getCurrentTileCoord(), found.getDir(), getFireRange(), false, t -> pressButton(GameInput.B), t -> tileIsSafe(t)))
							return true;
		return false;
	}

	private boolean checkForSomethingAround(FindType something, int radiusInTiles, FindInRectType rectType) {
		return checkForSomethingAround(Set.of(something), radiusInTiles, rectType);
	}

	private boolean checkForSomethingAround(Set<FindType> somethings, int radiusInTiles, FindInRectType rectType) {
		List<FindProps> founds = Tools.findInRect(getCurrentTileCoord(), bomberMan, radiusInTiles, somethings);
		Function<TileCoord, Boolean> tileIsFree = t -> {
			return tileIsSafe(t) || t.equals(founds.get(0).getCoord()) || t.equals(getCurrentTileCoord());
		};
		return founds != null && getPathFinder() == null && setPathFinder(new PathFinder(getCurrentTileCoord(), founds.get(0).getCoord(), getCurrentDir(), PathFinderDistance.SHORTEST, PathFinderOptmize.OPTIMIZED, tileIsFree));
	}

	private boolean findSafeSpotAndGo(TileCoord coord, Direction direction, boolean ignoreUnsafeSpots) {
		return findSafeSpotAndGo(coord, direction, 0, ignoreUnsafeSpots, null, null);
	}

	private boolean findSafeSpotAndGo(TileCoord coord, Direction direction, int minTileDistanceFromCoord, boolean ignoreUnsafeSpots, Consumer<TileCoord> onLeaveTileEvent) {
		return findSafeSpotAndGo(coord, direction, 0, ignoreUnsafeSpots, onLeaveTileEvent, null);
	}

	private boolean findSafeSpotAndGo(TileCoord coord, Direction direction, boolean ignoreUnsafeSpots, Function<TileCoord, Boolean> tileIsFreeFuncForPathFinder) {
		return findSafeSpotAndGo(coord, direction, 0, ignoreUnsafeSpots, null, tileIsFreeFuncForPathFinder);
	}

	private boolean findSafeSpotAndGo(TileCoord coord, Direction direction, int minTileDistanceFromCoord, boolean ignoreUnsafeSpots, Consumer<TileCoord> onLeaveTileEvent, Function<TileCoord, Boolean> tileIsFreeFuncForPathFinder) {
		coord = coord.getNewInstance();
		TileCoord start = coord.getNewInstance();
		setDangerMarks(start, minTileDistanceFromCoord);
		for (int n = 0; n < 4; n++) {
			coord.setCoords(start);
			while (tileIsFree(coord)) {
				List<Direction> dirs = Tools.getFreeDirections(bomberMan, coord, passThrough());
				if (tileIsSafe(coord) || dirs != null) {
					Direction dir3 = null;
					TileCoord coord3 = null;
					if (tileIsSafe(coord)) {
						dir3 = direction;
						coord3 = coord.getNewInstance();
					}
					else if (dirs != null) {
						for (Direction dir2 : dirs) {
							TileCoord coord2 = coord.getNewInstance().incCoordsByDirection(dir2);
							if (tileIsSafe(coord2)) {
								dir3 = dir2;
								coord3 = coord2;
								break;
							}
						}
						if (coord3 == null) {
							coord.incCoordsByDirection(direction);
							continue;
						}
					}
					else {
						coord.incCoordsByDirection(direction);
						continue;
					}
					unsetDangerMarks(start, minTileDistanceFromCoord);
					if (setPathFinder(new PathFinder(start.getNewInstance(), coord3.getNewInstance(), dir3, PathFinderDistance.SHORTEST, PathFinderOptmize.OPTIMIZED, tileIsFreeFuncForPathFinder == null ? t -> tileIsSafe(t) : tileIsFreeFuncForPathFinder))) {
						if (onLeaveTileEvent != null)
							onLeaveTileEvent.accept(coord3);
						return true;
					}
				}
				coord.incCoordsByDirection(direction);
			}
			direction = direction.getNext4WayClockwiseDirection();
		}
		if (getPathFinder() == null)
			releaseAllInputs();
		unsetDangerMarks(start, minTileDistanceFromCoord);
		return false;
	}
	
	private void setDangerMarks(TileCoord coord, int distance) {
		markTilesAsDanger(coord, distance, false);
	}

	private void unsetDangerMarks(TileCoord coord, int distance) {
		markTilesAsDanger(coord, distance, true);
	}

	private void markTilesAsDanger(TileCoord coord, int distance, boolean remove) {
		Set<PassThrough> passThrough = new HashSet<>(Set.of(PassThrough.PLAYER, PassThrough.MONSTER, PassThrough.HOLE));
		if (remove || bomberMan.getBombType() == BombType.SPIKED || bomberMan.getBombType() == BombType.SPIKED_REMOTE) {
			passThrough.add(PassThrough.BRICK);
			passThrough.add(PassThrough.ITEM);
		}
		Bomb.markTilesAsDanger(passThrough, coord, distance, TileProp.CPU_DANGER, remove);		
	}

	private boolean doRandomPause() {
		if (tileWasChanged() && (int)MyMath.getRandom(0, 10) == 0) {
			pauseInFrames = (int)MyMath.getRandom(30, 180);
			return true;
		}
		return false;
	}
	
	private boolean canSetBomb() {
		boolean haveGlove = bomberMan.haveItem(ItemType.POWER_GLOVE);
		boolean haveHyperGlove = bomberMan.haveItem(ItemType.HYPER_GLOVE);
		boolean haveAnyGlove = haveGlove || haveHyperGlove;
		return bomberMan.getMaxBombs() > 0 && !Bomb.haveBombAt(getCurrentTileCoord()) && tileIsSafe(getCurrentTileCoord()) &&
				(!haveAnyGlove || !Entity.haveAnyEntityAtCoord(getCurrentTileCoord(), bomberMan)) &&
				(!haveHyperGlove || !Brick.haveBrickAt(getCurrentTileCoord()));
	}

	private void pressButton(GameInput button) {
		if (pauseInFrames == 0) {
			bomberMan.keyPress(button);
			DurationTimerFX.createTimer("releaseButton-" + timerId++, Duration.millis(25), () -> releaseButton(button));
		}
	}

	private void holdButton(GameInput button) {
		if (pauseInFrames == 0)
			bomberMan.keyPress(button);
	}

	private void releaseButton(GameInput button) {
		bomberMan.keyRelease(button);
	}
	
	private boolean isInvencible() {
		return bomberMan.isInvencible();
	}

	private boolean tileIsHalfSafe(TileCoord coord) {
		return (isInvencible() || !MapSet.tileContainsProp(coord, TileProp.CPU_DANGER_2) || MapSet.tileContainsProp(getCurrentTileCoord(), TileProp.CPU_DANGER_2)) && tileIsFree(coord);
	}
	
	private boolean tileIsSafe(TileCoord coord) {
		return (isInvencible() || !MapSet.tileContainsProp(coord, TileProp.CPU_DANGER)) && tileIsHalfSafe(coord);
	}
	
	private boolean tileIsFree(TileCoord coord) {
		return (isInvencible() ||  
						(!MapSet.tileContainsProp(coord, TileProp.DAMAGE_PLAYER) &&
						 !MapSet.tileContainsProp(coord, TileProp.EXPLOSION))) &&
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
