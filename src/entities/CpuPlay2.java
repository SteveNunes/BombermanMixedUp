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
import enums.Curse;
import enums.Direction;
import enums.FindInRectType;
import enums.FindType;
import enums.FindTypeRestriction;
import enums.GameInput;
import enums.PassThrough;
import enums.TileProp;
import gui.GameTikTok;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import pathfinder.PathFinderDistance;
import pathfinder.PathFinderOptmize;
import tools.Draw;
import tools.FindProps;
import tools.Tools;
import util.DurationTimerFX;
import util.Misc;
import util.MyMath;

public class CpuPlay2 {
	
	/* PROXIMAS ETAPAS:
	 * Inteligencia em cima do Bomber Ship
	 * - Tentar alinhar com o jogador mais proximo (usar o metodo de localizar a
	 *   coord mais proxima, através de uma lista contendo as coordenadas de todos
	 *   os players vivos)
	 * - Estando alinhado com um jogador, arremessar a bomba para que ela caia a frente
	 *   do jogador se possivel, se não for possivel, tentar jogar 1 tile antes, e se nem
	 *   assim for possivel, tentar jogar na cabeça dele
	 * - Tentar arremessar bomba em tiles onde tem uma bomba proxima de explodir, para
	 *   aproveitar a explosão dessa bomba e explodir a bomba dele logo em seguida
	 *   
	 * Se estiver em um danger tile e não tiver para onde ir, e tiver o soca bomba, tentar
	 * localizar a bomba mais proxima, fazer um trajeto inmodificavel que leve ate encostar
	 * nessa bomba, e tente soca-la.
	 * 
	 * Se estiver em um danger tile e não tiver para onde ir, e tiver o chuta bomba, tentar
	 * localizar a bomba mais proxima, ver se tem espaço nas costas dela para chuta-la,
	 * e então fazer um trajeto inmodificavel que leve ate encostar nessa bomba,
	 * e tentar chuta-la.
	 * 
	 * Quando tiver com a luva, as vezes tentar fazer o truque da liva, agarrando a bomba perto
	 * de explodir, marcando uma variavel para saber que ele esta segurando uma bomba, e ficar
	 * segurando ela e andando aleatoriamente tentando chegar perto de alguem. Se detectar
	 * que ao arremessar a bomba, ela vai cair perto de alguem, soltar o botão para arremessa-la.
	 * Testar essas possibilidade até mesmo tentando arremessar contra a parede para que a bomba
	 * atravesse a tela e caia do outro lado.
	 * 
	 * Se tiver bomba com tijolos e player do outro lado, e tiver soco, socar a bomba para
	 * o outro lado, para cair do lado do outro player.
	 * 
	 * Tentar usar os poderes das montarias
	 * - Se tiver preso, e estiver com um canguro que pula, tentar pular.
	 * - Quando uma bomba que vai acetar a CPU estiver para explodir, e estiver com um
	 *   canguru que pula, tentar pular a explosão.
	 */
	
	@SuppressWarnings("serial")
	private static Map<Direction, GameInput> dirToInput = new HashMap<>() {{
		put(Direction.DOWN, GameInput.DOWN);
		put(Direction.RIGHT, GameInput.RIGHT);
		put(Direction.UP, GameInput.UP);
		put(Direction.LEFT, GameInput.LEFT);
	}};
	
	private CpuDificult dificult;
	private BomberMan bomberMan;
	private int pauseInFrames;
	private PathFinder pathFinder;
	private static long timerId = 0;
	private TileCoord[] lastTileCoords;
	private int lastTileCoordPos;
	private List<FindProps> lastFounds;
	private boolean runningFromDanger;
	private int framesForAction;
	
	public static boolean markTargets = true;

	public CpuPlay2(BomberMan bomberMan, CpuDificult dificult) {
		super();
		this.dificult = dificult;
		this.bomberMan = bomberMan;
		pathFinder = null;
		lastFounds = null;
		pauseInFrames = 0;
		lastTileCoords = new TileCoord[4];
		lastTileCoordPos = 0;
		framesForAction = 0;
		runningFromDanger = false;
	}
	
	public void run() {
		if (MapSet.stageObjectiveIsCleared() || MapSet.stageIsCleared()) {
			releaseAllInputs();
			setPathFinder(null);
			pauseInFrames = 0;
			return;
		}
		if (!Misc.alwaysTrue() && lastFounds != null) // Debug para sinalizar no que a CPU esta focada no momento
			for (FindProps prop : lastFounds)
				Draw.markTile(prop.getCoord(), Misc.blink(100) ? Color.LIGHTGREEN : Color.YELLOW);

		// Dropa o ultimo item se for a MINA e ele só puder soltar 1 bomba por vez
		if (bomberMan.getBombType() == BombType.LAND_MINE && bomberMan.getMaxBombs() == 1) {
			releaseAllInputs();
			pressButton(GameInput.E);
			pauseInFrames = 30;
			return;
		}
		if (!Main.isFreeze() && !bomberMan.isBlockedMovement()) {
			if (!runningFromDanger && isOverDangerTile()) {
				pauseInFrames = 0;
				return;
			}
			if (pauseInFrames > 0)
				pauseInFrames--;
			if (isMoving() && isPerfectlyBlockedDir())
				framesForAction = 0;
			if (bomberMan.tileWasChanged() || (isMoving() && isPerfectlyBlockedDir()) || (!isMoving() && ++framesForAction >= (tileIsSafe(getCurrentTileCoord()) ? 30 : 10))) {
				framesForAction = 0;
				// Se estiver em um tile perigoso, tenta encontrar uma forma de sair dele
				if (isStucked()) {
					releaseAllInputs();
					setPathFinder(null); // Se o PathFinder local estiver ativo, mas o personagem estiver preso, desativa o PathFinder local.
					if (canSetBomb() && bomberMan.getInvencibleFrames() > 90 || bomberMan.getHitPoints() > 1) {// Se tiver preso e invencivel ou ter coraçao, soltar uma bomba para tentar se desprender
						echo("BOMBA PRESO");
						pressButton(GameInput.B);
					}
				}
				else { //if (!isMoving() || isPerfectlyBlockedDir()) {
					releaseAllInputs();
					if (cpuIsPacing())
						return;
					// Se o PathFinder local estiver ativo, vira e anda para a próxima direção designada pelo PathFinder
					if (tileWasChanged() && getPathFinder() != null) {
						Direction dir = getPathFinder().getNextDirectionToGoAndRemove();
						if (dir != null && tileIsFree(getCurrentTileCoord().getNewInstance().incCoordsByDirection(dir))) {
							holdButton(dirToInput(dir));
							return;
						}
						runningFromDanger = false;
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
					// Solta uma bomba se ele estiver invencivel ou com coração extra em cima de uma explosão com algum player no alcance da explosão da bomba dele e que não esteja invencivel nem com coração extra
					if (canSetBomb() && (bomberMan.getInvencibleFrames() > 90 || bomberMan.getHitPoints() > 1) && MapSet.tileContainsProp(getCurrentTileCoord(), TileProp.EXPLOSION)) {
						// MELHORAR QUANDO TIVER IMPLEMENTADO MONTARIA, pra ele tb fazer isso se ele estiver numa montaria
						List<FindProps> founds = Tools.findInLine(bomberMan, getCurrentTileCoord(), getFireRange(), Set.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), FindType.PLAYER, FindTypeRestriction.ONLY_IF_IS_ACESSIBLE);
						if (founds != null) {
							for (FindProps found : founds) {
								Entity entity = Entity.getFirstEntityFromCoord(found.getCoord());
								if (!(entity instanceof BomberMan) || ((BomberMan)entity).isInvencible() || ((BomberMan)entity).getHitPoints() > 1) {
									founds = null;
									break;
								}
							}
							if (founds != null) {
								lastFounds = founds;
								echo("BOMBA " + founds.get(0).getFoundType());
								pressButton(GameInput.B);
							}
						}
					}
					if (doSearchs()) // Procura por coisas prõximas...
						return;
					// Se não estiver focado em algum item ou tijolo, tenta chegar perto de algum jogador acessivel
					for (Entity entity : Entity.getEntityList())
						if (entity != bomberMan && entity instanceof BomberMan && setPathFinder(new PathFinder(getCurrentTileCoord(), entity.getTileCoordFromCenter(), getCurrentDir(), PathFinderDistance.SHORTEST, PathFinderOptmize.OPTIMIZED, t -> tileIsSafe(t))))
							return;
					goToRandomFreeDir();
				}
			}
		}
	}
	
	private boolean cpuIsPacing() { // Verifica se a Cpu esta andando de um lado pro outro sem parar entre 2 tiles
		if (tileWasChanged())
			lastTileCoords[lastTileCoordPos] = getCurrentTileCoord().getNewInstance();
		if (lastTileCoords[lastTileCoordPos] != null && ++lastTileCoordPos == 4) {
			lastTileCoordPos = 0;
			if (lastTileCoords[0].equals(lastTileCoords[2]) && lastTileCoords[1].equals(lastTileCoords[3])) {
				pauseInFrames = (int)MyMath.getRandom(60, 150);
				return true;
			}
		}
		return false;
	}

	public CpuDificult getDificult() {
		return dificult;
	}

	private boolean doSearchs() {
		// Procura por itens proximos e vai em direção a eles
		if (checkForSomethingAround(FindType.GOOD_ITEM, 5, FindInRectType.RECTANGLE_AREA))
			return true;
		// Procura por tijolos proximos e vai em direção a eles
		if (canSetBomb() && checkForSomethingAround(FindType.BRICK, 5, FindInRectType.RECTANGLE_AREA))
			return true;
		// Procura por outros jogadores ou tijolos ou itens ruins em linha reta para soltar uma bomba que o destrua.
		if (checkForBricksAndBadItemsAndPlayersAround())			
			return true;
		return false;
	}

	private boolean isStucked() {
		return Tools.getFreeTileCoordsAround(bomberMan, getCurrentTileCoord(), passThrough()) == null;
	}

	private boolean isOverDangerTile() {
		boolean b = MapSet.tileContainsProp(getCurrentTileCoord(), TileProp.CPU_DANGER) &&
						findSafeSpotAndGo(getCurrentTileCoord(), getCurrentDir(), true, t -> tileIsHalfSafe(t));
		runningFromDanger = b;
		return b;
	}

	private boolean setPathFinder(PathFinder pathFinder) {
		this.pathFinder = pathFinder;
		if (pathFinder != null) {
			if (Brick.haveBrickAt(getPathFinder().getTargetCoord()) ||
					Entity.haveAnyEntityAtCoord(getCurrentTileCoord(), bomberMan) ||
					(Item.haveItemAt(getPathFinder().getTargetCoord()) && Item.getItemAt(getPathFinder().getTargetCoord()).getItemType().isBadItem()))
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

	private <T extends Number> void echo(T number) {
		echo("" + number);
	}
	
	private void echo(String string) {
		GameTikTok.addEcho(string);
	}
	
	private boolean checkForBricksAndBadItemsAndPlayersAround() {
		if (!canSetBomb())
			return false;
		HashSet<FindType> targets = new HashSet<>(Set.of(FindType.PLAYER));
		if (canSetBomb())
			targets.addAll(Set.of(FindType.BRICK, FindType.BAD_ITEM));
		List<FindProps> founds = Tools.findInLine(bomberMan, getCurrentTileCoord(), getFireRange(), Set.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), targets, FindTypeRestriction.ONLY_IF_IS_ACESSIBLE);
		if (founds != null)
			for (FindProps found : founds)
				if (!MapSet.tileContainsProp(found.getCoord(), TileProp.CPU_DANGER) &&
						!MapSet.tileContainsProp(found.getCoord(), TileProp.CPU_DANGER_2) &&
						findSafeSpotAndGo(getCurrentTileCoord(), found.getDir(), getFireRange(), false, t -> {
							echo("BOMBA " + founds.get(0).getFoundType());
							pressButton(GameInput.B);
						}, t -> tileIsSafe(t))) {
							lastFounds = founds;
							return true;
				}
		return false;
	}

	private boolean checkForSomethingAround(FindType something, int radiusInTiles, FindInRectType rectType) {
		return checkForSomethingAround(Set.of(something), radiusInTiles, rectType);
	}

	private boolean checkForSomethingAround(Set<FindType> somethings, int radiusInTiles, FindInRectType rectType) {
		List<FindProps> founds = Tools.findInRect(getCurrentTileCoord(), bomberMan, radiusInTiles, somethings, FindTypeRestriction.ONLY_IF_IS_ACESSIBLE);
		Function<TileCoord, Boolean> tileIsFree = t -> {
			return tileIsSafe(t) || t.equals(founds.get(0).getCoord()) || t.equals(getCurrentTileCoord());
		};
		boolean b = founds != null && getPathFinder() == null && setPathFinder(new PathFinder(getCurrentTileCoord(), founds.get(0).getCoord(), getCurrentDir(), PathFinderDistance.SHORTEST, PathFinderOptmize.OPTIMIZED, tileIsFree));
		if (b)
			lastFounds = founds;
		return b;
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
		return bomberMan.getMaxBombs() > 0 && tileIsSafe(getCurrentTileCoord()) &&
				(!bomberMan.isCursed() || bomberMan.getCurse() != Curse.NO_BOMB) &&
				!Bomb.haveBombAt(getCurrentTileCoord()) && !Brick.haveBrickAt(getCurrentTileCoord());
	}
	
	private void pressButton(GameInput button) {
		if (pauseInFrames == 0) {
			final GameInput button2 = button;
			bomberMan.keyPress(button2);
			DurationTimerFX.createTimer("releaseButton-" + timerId++, Duration.millis(25), () -> releaseButton(button2));
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
		return bomberMan.isInvencible() && (bomberMan.getInvencibleFrames() > 300 || bomberMan.getInvencibleFrames() < 0);
	}

	private boolean tileIsHalfSafe(TileCoord coord) {
		return (isInvencible() || !MapSet.tileContainsProp(coord, TileProp.CPU_DANGER_2) || MapSet.tileContainsProp(getCurrentTileCoord(), TileProp.CPU_DANGER_2)) && tileIsFree(coord) &&
						(!Item.haveItemAt(coord) || !Item.getItemAt(coord).getItemType().isBadItem());
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
		List<GameInput> list = new ArrayList<>(bomberMan.getHoldedInputs());
		for (GameInput input : list)
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
