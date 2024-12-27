package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import application.Main;
import damage.Explosion;
import entityTools.PushEntity;
import enums.BombType;
import enums.Curse;
import enums.Direction;
import enums.Elevation;
import enums.FindType;
import enums.PassThrough;
import enums.StageObjectives;
import enums.TileProp;
import frameset.FrameSet;
import frameset_tags.SetSprSource;
import frameset_tags.SetTicksPerFrame;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import maps.Item;
import maps.MapSet;
import objmoveutils.JumpMove;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import pathfinder.PathFinderOptmize;
import tools.FindProps;
import tools.GameConfigs;
import tools.Sound;
import tools.Tools;
import util.CollectionUtils;
import util.DurationTimerFX;
import util.FrameTimerFX;
import util.MyMath;

public class Bomb extends Entity {

	private static final int TIMER_FOR_DANGER_2 = 90;
	private static Map<TileCoord, Bomb> bombs = new HashMap<>();
	private static List<Bomb> bombList = new ArrayList<>();

	private boolean dangerMarked;
	private boolean dangerMarked2;
	private boolean nesBomb;
	private Entity owner;
	private BombType type;
	private int timer;
	private int fireDistance;
	private boolean ownerIsOver;
	private boolean isActive;
	private boolean wasExploded;
	private boolean isStucked;
	private long setTime;
	private double curseMulti;

	public Bomb(Bomb bomb) {
		super(bomb);
		owner = bomb.owner;
		type = bomb.type;
		timer = bomb.getTimer();
		fireDistance = bomb.fireDistance;
		ownerIsOver = bomb.ownerIsOver;
		nesBomb = bomb.nesBomb;
		isActive = bomb.isActive;
		isStucked = bomb.isStucked;
		setTime = bomb.setTime;
		dangerMarked = bomb.dangerMarked;
		dangerMarked2 = bomb.dangerMarked2;
	}

	public Bomb(TileCoord coord, BombType type, int fireDistance) {
		this(null, coord, type, fireDistance);
	}

	public Bomb(Entity owner, TileCoord coord, BombType type, int fireDistance) {
		super();
		setTime = System.currentTimeMillis();
		isActive = true;
		isStucked = false;
		dangerMarked = false;
		dangerMarked2 = false;
		wasExploded = false;
		nesBomb = type == BombType.NES || (owner instanceof BomberMan && ((BomberMan) owner).getBomberIndex() == 0);
		if (type == BombType.P && !(owner instanceof BomberMan))
			fireDistance = GameConfigs.MAX_EXPLOSION_DISTANCE;
		this.type = type;
		this.fireDistance = fireDistance;
		this.owner = owner;
		timer = type == BombType.LAND_MINE || type == BombType.SENSOR || type == BombType.REMOTE || type == BombType.SPIKED_REMOTE ? -1 : 180;
		curseMulti = 1;
		ownerIsOver = owner != null && owner.getTileCoordFromCenter().equals(coord);
		if (type == BombType.LAND_MINE) {
			addNewFrameSetFromIniFile(this, "LandingFrames", "FrameSets", "LAND_MINE_BOMB", "LandingFrames");
			addNewFrameSetFromIniFile(this, "LandedFrames", "FrameSets", "LAND_MINE_BOMB", "LandedFrames");
			addNewFrameSetFromIniFile(this, "UnlandingFrames", "FrameSets", "LAND_MINE_BOMB", "UnlandingFrames");
			setFrameSet("LandingFrames");
		}
		else {
			addNewFrameSetFromIniFile(this, "StandFrames", "FrameSets", "BOMB", "StandFrames");
			addNewFrameSetFromIniFile(this, "JumpingFrames", "FrameSets", "BOMB", "JumpingFrames");
			setFrameSet("StandFrames");
		}
		setPosition(coord.getPosition());
		setPassThroughHole(true);
		setPassThroughWater(true);
		setPassThroughMonster(true);
		setPassThroughPlayer(true);
		if (type == BombType.SPIKED || type == BombType.SPIKED_REMOTE) {
			setPassThroughItem(true);
			setPassThroughBomb(true);
			setPassThroughBrick(true);
		}		
	}
	
	@Override
	public void setFrameSet(String frameSetName) {
		super.setFrameSet(frameSetName);
		int ticksPerFrame = type == BombType.LAND_MINE ? 3 : 16;
		if (owner != null) {
			if (owner instanceof BomberMan) {
				if (((BomberMan) owner).getCurse() == Curse.SLOW_EXPLODE_BOMB) {
					ticksPerFrame *= 2;
					if (timer != -1)
						timer = 270;
					curseMulti = 1.5;
				}
				else if (((BomberMan) owner).getCurse() == Curse.FAST_EXPLODE_BOMB) {
					ticksPerFrame /= 2;
					if (timer != -1)
						timer = 90;
					curseMulti = 0.5;
				}
			}
		}
		final int tpf = ticksPerFrame;
		final int y = 16 * type.getValue();
		for (FrameSet frameSet : getFrameSets())
			frameSet.iterateFrameTags(tag -> {
				if (tag instanceof SetSprSource && ((SetSprSource)tag).originSprSizePos.y == -1)
					((SetSprSource)tag).originSprSizePos.y = y;
				if (tag instanceof SetTicksPerFrame && ((SetTicksPerFrame)tag).value == -1)
					((SetTicksPerFrame)tag).value = tpf;
			});
	}

	public boolean isStucked() {
		return isStucked;
	}

	public void setStucked(boolean state) {
		isStucked = state;
	}

	public boolean isNesBomb() {
		return nesBomb;
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean wasExploded() {
		return !isActive && wasExploded;
	}

	public BombType getBombType() {
		return type;
	}

	public static Bomb addBomb(TileCoord coord, BombType type, int fireDistance) {
		return addBomb(null, coord, type, fireDistance, false);
	}

	public static Bomb addBomb(Entity owner, TileCoord coord, BombType type, int fireDistance) {
		return addBomb(owner, coord, type, fireDistance, false);
	}

	public static Bomb addBomb(TileCoord coord, BombType type, int fireDistance, boolean checkTile) {
		return addBomb(null, coord, type, fireDistance, checkTile);
	}

	public static Bomb addBomb(Entity owner, TileCoord coord, BombType type, int fireDistance, boolean checkTile) {
		coord = coord.getNewInstance();
		if (!checkTile || (MapSet.tileIsFree(coord) && !MapSet.getTileProps(coord).contains(TileProp.GROUND_NO_BOMB))) {
			Bomb bomb = new Bomb(owner, coord, type, fireDistance);
			putOnMap(coord, bomb);
			bombList.add(bomb);
			MapSet.checkTileTrigger(bomb, coord, TileProp.TRIGGER_BY_BOMB);
			MapSet.checkTileTrigger(bomb, coord, TileProp.TRIGGER_BY_STOPPED_BOMB);
			return bomb;
		}
		return null;
	}

	public static void addBomb(Bomb bomb) {
		bombList.add(bomb);
	}

	public static void removeBomb(Bomb bomb) {
		if (bomb != null) {
			bomb.isActive = false;
			DurationTimerFX.createTimer("removeMarkTilesAsDanger-" + bomb.hashCode(), Duration.millis(800), () -> {
				bomb.removeDangerMarks(TileProp.CPU_DANGER);
				bomb.removeDangerMarks(TileProp.CPU_DANGER_2);
				bomb.wasExploded = true;
			});
			bombList.remove(bomb);
			bombs.remove(bomb.getTileCoordFromCenter());
		}
	}

	public static void removeBomb(TileCoord coord) {
		removeBomb(getBombAt(coord));
		bombs.remove(coord);
	}

	public static void clearBombs() {
		for (TileCoord coord : new ArrayList<>(bombs.keySet()))
			removeBomb(coord);
		bombList.clear();
	}

	public static int totalBombs() {
		return bombList.size();
	}

	public static List<Bomb> getBombs() {
		return bombList;
	}

	public static Map<TileCoord, Bomb> getBombMap() {
		return bombs;
	}

	public static void drawBombs() {
		List<Bomb> bombs = new ArrayList<>(bombList);
		for (Bomb bomb : bombs) {
			if (MapSet.tileContainsProp(bomb.getTileCoordFromCenter(), TileProp.INSTAKILL)) {
				bomb.detonate();
				continue;
			}
			if (bomb.type == BombType.LAND_MINE) {
				bomb.run();
				if (bomb.currentFrameSetNameIsEqual("LandedFrames")) {
					if (Entity.haveAnyEntityAtCoord(bomb.getTileCoordFromCenter(), bomb))
						bomb.setFrameSet("UnlandingFrames");
				}
				else if (bomb.currentFrameSetNameIsEqual("UnlandingFrames") && !bomb.getCurrentFrameSet().isRunning())
					bomb.detonate();
				else if (MapSet.tileContainsProp(bomb.getTileCoordFromCenter(), TileProp.DAMAGE_BOMB) ||
								MapSet.tileContainsProp(bomb.getTileCoordFromCenter(), TileProp.EXPLOSION))
									bomb.detonate();
				continue;
			}
			if (bomb.ownerIsOver(bomb.owner)) {
				int x = (int) bomb.owner.getX() + Main.TILE_SIZE / 2,
						y = (int) bomb.owner.getY() + Main.TILE_SIZE / 2,
						xx = (int) bomb.getX() + Main.TILE_SIZE / 2,
						yy = (int) bomb.getY() + Main.TILE_SIZE / 2;
				if (x <= xx - Main.TILE_SIZE / 2 || x >= xx + Main.TILE_SIZE / 2 ||
						y <= yy - Main.TILE_SIZE / 2 || y >= yy + Main.TILE_SIZE / 2)
							bomb.ownerIsOver = false;
			}
			if (bomb.getTimer() != -1 && !bomb.isBlockedMovement() &&
					((MapSet.getStageClearCriterias().contains(StageObjectives.LAST_PLAYER_SURVIVOR) && !MapSet.stageObjectiveIsCleared())) ||
					((!MapSet.getStageClearCriterias().contains(StageObjectives.LAST_PLAYER_SURVIVOR) && !MapSet.stageIsCleared())))
						bomb.decTimer();
			if ((bomb.getTimer() == -1 || bomb.getTimer() > 0) && (!bomb.isBlockedMovement() || bomb.isStucked()) &&
					(MapSet.tileContainsProp(bomb.getTileCoordFromCenter(), TileProp.DAMAGE_BOMB) ||
					 MapSet.tileContainsProp(bomb.getTileCoordFromCenter(), TileProp.EXPLOSION)))
							bomb.setTimer(0);
			if (bomb.getTimer() == 0)
				bomb.detonate();
			else
				bomb.run();
		}
	}
	
	public Entity getOwner() {
		return owner;
	}

	public boolean ownerIsOver(Entity entity) {
		return entity != null && owner == entity && ownerIsOver;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public void incTimer() {
		incTimer(1);
	}

	public void incTimer(int value) {
		timer += value;
	}

	public void decTimer() {
		decTimer(1);
	}

	public void decTimer(int value) {
		timer -= value;
	}

	public long getSetTime() {
		return setTime;
	}

	public void detonate() {
		removeBomb(this);
		isActive = false;
		centerToTile();
		unsetPushEntity();
		Sound.playWav("explosion/Explosion" + (nesBomb ? "" : fireDistance < 3 ? "1" : (int) (fireDistance / 3)));
		if (getBombType() == BombType.LAND_MINE)
			FrameTimerFX.createTimer("LandMineRemoveDanger@" + hashCode(), 10, () ->
				MapSet.removeTileProp(getTileCoordFromCenter(), TileProp.CPU_DANGER));
		if (getBombType() != BombType.MAGMA)
			Explosion.addExplosion(this, getTileCoordFromCenter(), fireDistance, getBombType().getValue(), canPassThroughBricks());
		else {
			Effect effect = Effect.runEffect(getPosition(), "MagmaBombExplosion");
			effect.getCurrentFrameSet().iterateFrameTags(tag -> {
				if (tag instanceof SetSprSource) {
					SetSprSource t = (SetSprSource)tag;
					t.outputSprSizePos.width = (int)(Main.TILE_SIZE * fireDistance * 2.2);
					t.outputSprSizePos.height = (int)(Main.TILE_SIZE * fireDistance * 2.2);
				}
			});
			final int[] ranges = {fireDistance / 4, fireDistance / 2, fireDistance, fireDistance, fireDistance / 2, fireDistance / 4, fireDistance / 6, fireDistance / 8};
			for (int n = 0; n < 8; n++) {
				final int n2 = n;
				FrameTimerFX.createTimer("MagmaBombExplosionAdd@" + n + "@" + hashCode(), n2 * 6, () -> {
					Tools.iterateInsideCircleArea(getTileCoordFromCenter(), ranges[n2], t -> {
						if (MapSet.haveTilesOnCoord(t))
							MapSet.addTileProp(t, TileProp.EXPLOSION);
					});
				});
				FrameTimerFX.createTimer("MagmaBombExplosionRemove@" + n + "@" + hashCode(), (n2 + 1) * 6, () -> {
					Tools.iterateInsideCircleArea(getTileCoordFromCenter(), ranges[n2], t -> {
						if (MapSet.haveTilesOnCoord(t))
							MapSet.removeTileProp(t, TileProp.EXPLOSION);
					});
				});
			}
		}
		removeBomb(this);
	}

	private boolean canPassThroughBricks() {
		return type == BombType.SPIKED || type == BombType.SPIKED_REMOTE;
	}

	public void stopKick() {
		if (getPushEntity() != null) {
			getPushEntity().stop();
			centerToTile();
			unsetGhosting();
		}
	}

	@Override
	public void setPathFinder(PathFinder pathFinder) {
		super.setPathFinder(pathFinder);
		if (pathFinder == null || !pathFinder.pathWasFound())
			setSpeed(0);
	}

	@Override
	public boolean isBlockedMovement() {
		return isStucked() || getJumpMove() != null || getHolder() != null;
	}

	// TEMP (Pra forçar a sombra da bomba quando ela ta caindo, ate eu resolver o que ta acontecendo que a MECANICA da Bomba, Brick e Item caindo e a mesma, mas com Item e Brick a sombra aparece, mas com a bomba ela some no segundo frame
	private boolean falling = false;
	
	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		if (falling) // TEMP (Pra forçar a sombra da bomba quando ela ta caindo, ate eu resolver o que ta acontecendo que a MECANICA da Bomba, Brick e Item caindo e a mesma, mas com Item e Brick a sombra aparece, mas com a bomba ela some no segundo frame 
			setShadow(0, 0, -12, -6, 0.35f);
		if (getBombType() == BombType.LAND_MINE)
			MapSet.removeTileProp(getTileCoordFromCenter(), TileProp.CPU_DANGER);
		else {
			removeDangerMarks(TileProp.CPU_DANGER);
			removeDangerMarks(TileProp.CPU_DANGER_2);
		}
		if (getBombType() == BombType.FOLLOW && !isBlockedMovement() && !isMoving() && getPathFinder() == null) {
			List<FindProps> founds = Tools.findInRect(this, getTileCoordFromCenter(), owner, 4, FindType.PLAYER);
			if (getTargetingEntity() != null || founds != null) {
				TileCoord coord = (getTargetingEntity() != null ? getTargetingEntity().getTileCoordFromCenter() : founds.get(0).getCoord()).getNewInstance();
				final TileCoord c = coord.getNewInstance();
				Function<TileCoord, Boolean> tileIsFree = t -> {
					return MapSet.tileIsFree(t) || t.equals(c) || t.equals(getTileCoordFromCenter());
				};
				setPathFinder(new PathFinder(getTileCoordFromCenter(), coord, getDirection(), PathFinderOptmize.OPTIMIZED, tileIsFree));
				if ((!getPathFinder().pathWasFound() || getPathFinder().getCurrentPath().size() == 1) && getTargetingEntity() != null)
					setPathFinder(new PathFinder(getTileCoordFromCenter(), coord = getTargetingEntity().getTileCoordFromCenter().getNewInstance(), getDirection(), PathFinderOptmize.OPTIMIZED, tileIsFree));
				if (!getPathFinder().pathWasFound() || getPathFinder().getCurrentPath().size() == 1) {
					setPathFinder(null);
					unsetTargetingEntity();
				}
				else {
					setTargetingEntity(Entity.getFirstEntityFromCoord(coord));
					setSpeed(0.5);
				}
			}
		}
		else if (getBombType() == BombType.MAGNET && !isBlockedMovement() && getFocusedOn() == null) {
			List<FindProps> founds = Tools.findInLine(this, owner, getTileCoordFromCenter(), 5, Set.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT), FindType.PLAYER);
			if (founds != null) {
				Direction dir = getTileCoordFromCenter().get4wayDirectionToReach(founds.get(0).getCoord());
				if (!getTileCoordFromCenter().getNewInstance().incCoordsByDirection(dir).equals(founds.get(0).getCoord())) {
					setFocusedOn(Entity.getFirstEntityFromCoord(founds.get(0).getCoord()));
					kick(dir, 4, "MagnetBomb", "BombSlam");
				}
			}
		}
		else if (getBombType() == BombType.HEART && !isBlockedMovement() && isPerfectTileCentred()) {
			List<Direction> dirs = Tools.getFreeDirections(owner, getTileCoordFromCenter(), null);
			Direction dir = Tools.getRandomFreeDirection(owner, getTileCoordFromCenter());
			while (dirs != null && dirs.size() > 1 && dir == getDirection().getReverseDirection())
				dir = CollectionUtils.getRandomItemFromList(dirs);
			if (dirs != null) {
				setDirection(dir);
				setSpeed(0.5);
			}
		}
		else if (getBombType() == BombType.SENSOR && !isBlockedMovement()) {
			List<FindProps> founds = Tools.findInLine(this, owner, getTileCoordFromCenter(), 1, Set.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT), FindType.PLAYER);
			if (founds != null)
				detonate();
		}
		if (getTargetingEntity() != null && getPathFinder() != null)
			getPathFinder().recalculatePath(getTileCoordFromCenter(), getTargetingEntity().getTileCoordFromCenter(), getDirection());
		removeFromMap(getTileCoordFromCenter(), this);
		super.run(gc, isPaused);
		if (!isBlockedMovement() && tileWasChanged() && isActive()) {
			TileCoord prevCoord = getPreviewTileCoord().getNewInstance();
			TileCoord coord = getTileCoordFromCenter().getNewInstance();
			MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_BOMB);
			MapSet.checkTileTrigger(this, prevCoord, TileProp.TRIGGER_BY_BOMB, true);
			if (!bombs.containsKey(coord))
				MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_STOPPED_BOMB);
		}
		if (!isBlockedMovement())
			putOnMap(getTileCoordFromCenter(), this);
		if (isActive && getElevation() == Elevation.ON_GROUND) {
			if (getBombType() == BombType.LAND_MINE)
				MapSet.addTileProp(getTileCoordFromCenter(), TileProp.CPU_DANGER);
			else
				setDangerMarks(getTimer() <= TIMER_FOR_DANGER_2 * curseMulti ? TileProp.CPU_DANGER_2 : TileProp.CPU_DANGER);
		}
	}

	public static boolean haveBombAt(TileCoord coord) {
		return haveBombAt(null, coord);
	}

	public static boolean haveBombAt(Entity entity, TileCoord coord) {
		if (bombs.containsKey(coord)) {
			Bomb bomb = getBombAt(coord);
			return bomb.owner == null || entity == null || !bomb.ownerIsOver(entity);
		}
		return false;
	}

	public static Bomb getBombAt(TileCoord tileCoord) {
		return bombs.containsKey(tileCoord) ? bombs.get(tileCoord) : null;
	}

	public void kick(Direction direction, double speed) {
		kick(direction, speed, "BombKick", "BombSlam");
	}

	public void kick(Direction direction, double speed, String kickSound, String slamSound) {
		if (getPushEntity() == null) {
			if (kickSound != null)
				Sound.playWav(kickSound);
			PushEntity pushEntity = new PushEntity(this, speed, direction);
			final HashSet<PassThrough> oldPassThrough = new HashSet<>(getPassThrough());
			setPassThroughItem(true);
			setPassThroughPlayer(false);
			setPassThroughMonster(false);
			pushEntity.setOnStopEvent(e -> {
				if (getBombType() == BombType.RUBBER) {
					Sound.playWav("BombBounce");
					Direction dir = Tools.getRandomFreeDirection(this, getTileCoordFromCenter(), Set.of());
					if (dir != null) {
						PushEntity pe = new PushEntity(getPushEntity());
						pe.setDirection(dir);
						setPushEntity(pe);
					}
					else
						unsetGhosting();
				}
				else {
					if (slamSound != null)
						Sound.playWav(slamSound);
					setShake(2d, -0.05, 0d);
					unsetGhosting();
					setPassThrough(oldPassThrough);
				}
				List<Bomb> list = new ArrayList<>(bombList);
				for (Bomb bomb : list) {
					TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction);
					if (bomb != this && bomb.getTileCoordFromCenter().equals(coord)) {
						if (bomb.getPushEntity() != null) {
							int distance = (fireDistance + bomb.fireDistance) / 2;
							Bomb.removeBomb(bomb);
							Bomb.removeBomb(this);
							Bomb.addBomb(coord, BombType.MAGMA, distance);
							return;
						}
						if (!bomb.isBlockedMovement())
							DurationTimerFX.createTimer("chainKickBomb" + hashCode(), Duration.millis(50), () -> bomb.kick(direction, speed, kickSound, slamSound));
					}
				}
			});
			setPushEntity(pushEntity);
			setGhosting(2, 0.2);
		}
	}

	@Override
	public void onBeingHoldEvent(Entity holder) {
		removeFromMap(getTileCoordFromCenter(), this);
	}

	@Override
	public void onSetPushEntityTrigger() {
		removeFromMap(getTileCoordFromCenter(), this);
	}

	@Override
	public void onSetGotoMoveTrigger() {
		removeFromMap(getTileCoordFromCenter(), this);
	}
	
	@Override
	public void onSetJumpMoveTrigger() {
		setFrameSet("JumpingFrames");
		removeFromMap(getTileCoordFromCenter(), this);
	}

	@Override
	public void onPushEntityStop() {
		putOnMap(getTileCoordFromCenter(), this);
		MapSet.checkTileTrigger(this, getTileCoordFromCenter(), TileProp.TRIGGER_BY_STOPPED_BOMB);
	}

	public void punch(Direction direction, String punchSound) {
		setDirection(direction);
		TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction, 4);
		jumpTo(coord.getNewInstance(), 4, 1.2, 20, punchSound);
	}
	
	@Override
	public void onJumpFallAtFreeTileEvent(JumpMove jumpMove) {
		centerToTile();
		if (getBombType() == BombType.RUBBER && (int)MyMath.getRandom(1, 5) != 1) {
			setElevation(Elevation.FLYING);
			onJumpFallAtOccupedTileEvent(jumpMove);
			return;
		}
		if (checkEntitiesAbove()) {
			onJumpFallAtOccupedTileEvent(jumpMove);
			return;
		}
		Sound.playWav(getBombType() == BombType.RUBBER ? "BombBounce" : "BombHittingGround");
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		setFrameSet("StandFrames");
		MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_BOMB);
		MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_STOPPED_BOMB);
	}

	@Override
	public void onJumpFallAtOccupedTileEvent(JumpMove jumpMove) {
		if (getBombType() == BombType.RUBBER)
			forceDirection(getDirection().getNext8WayClockwiseDirection((int)(1 - MyMath.getRandom(0, 2))));
		Sound.playWav(getBombType() == BombType.RUBBER ? "BombBounce" : "BombHittingGround");
		centerToTile();
		checkEntitiesAbove();
		jumpMove.resetJump(4, 1.2, 14);
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		if (bombs.get(coord) == this)
			bombs.remove(coord);
		setGotoMove(coord.incCoordsByDirection(getDirection()).getPosition(), jumpMove.getDurationFrames());
	}
	
	public boolean checkEntitiesAbove() {
		if (Entity.haveAnyEntityAtCoord(getTileCoordFromCenter()))
			for (Entity entity : Entity.getEntityListFromCoord(getTileCoordFromCenter())) {
				if (entity instanceof BomberMan) {
					if (!entity.isBlockedMovement())
						((BomberMan)entity).dropItem(true, false);
					return true;
				}
				else if (entity instanceof Monster) {
					if (!entity.isBlockedMovement()) {
						entity.setCurse(Curse.STUNNED);
						entity.setCurseDuration(120);
					}
					return true;
				}
			}
		if (Item.haveItemAt(getTileCoordFromCenter()))
			Item.getItemAt(getTileCoordFromCenter()).destroy();
		return false;
	}

	private void removeDangerMarks(TileProp dangerProp) {
		markTilesAsDanger(dangerProp, true);
	}
		
	private void setDangerMarks(TileProp dangerProp) {
		markTilesAsDanger(dangerProp, false);
	}
		
	private void markTilesAsDanger(TileProp dangerProp, boolean remove) {
		if ((dangerProp == TileProp.CPU_DANGER && dangerMarked == remove) ||
				(dangerProp == TileProp.CPU_DANGER_2 && dangerMarked2 == remove)) {
			if (dangerProp == TileProp.CPU_DANGER)
				dangerMarked = !remove;
			else
				dangerMarked2 = !remove;
			if (getBombType() == BombType.MAGMA)
				Tools.iterateInsideCircleArea(getTileCoordFromCenter(), fireDistance, t -> {
					if (MapSet.haveTilesOnCoord(t)) {
						if (remove)
							MapSet.removeTileProp(t, dangerProp);
						else
							MapSet.addTileProp(t, dangerProp);
					}
				});
			else
				markTilesAsDanger(getPassThrough(), getTileCoordFromCenter(), fireDistance, dangerProp, remove);
		}
	}
	
	public static void markTilesAsDanger(Set<PassThrough> passThrough, TileCoord coord, int distance, TileProp dangerProp, boolean remove) {
		TileCoord coord2 = new TileCoord();
		Direction dir = Direction.LEFT;
		for (int d = 0; d < 4; d++) {
			dir = dir.getNext4WayClockwiseDirection();
			coord2.setCoords(coord);
			for (int x = 0; x <= distance; x++) {
				if (MapSet.haveTilesOnCoord(coord2)) {
					if (!remove)
						MapSet.addTileProp(coord2, dangerProp);
					else if (!MapSet.getCurrentLayer().getTileProps(coord2).contains(TileProp.GROUND_NO_FIRE))
						MapSet.removeTileProp(coord2, dangerProp);
				}
				if (x > 0 && (!MapSet.haveTilesOnCoord(coord2) || MapSet.getCurrentLayer().getTileProps(coord2).contains(TileProp.GROUND_NO_FIRE) || (!remove && !MapSet.tileIsFree(coord2, passThrough))))
					break;
				coord2.incCoordsByDirection(dir);
			}
		}
	}

	public static Bomb dropBombFromSky(TileCoord coord) {
		return dropBombFromSky(coord, BombType.NORMAL, 2);
	}

	public static Bomb dropBombFromSky(TileCoord coord, BombType bombType) {
		return dropBombFromSky(coord, bombType, 2);
	}

	public static Bomb dropBombFromSky(TileCoord coord, int fireDistance) {
		return dropBombFromSky(coord, BombType.NORMAL, fireDistance);
	}

	public static Bomb dropBombFromSky(TileCoord coord, BombType bombType, int fireDistance) {
		Bomb bomb = new Bomb(null, coord, bombType, fireDistance);
		Bomb.addBomb(bomb);
		bomb.falling = true;
		bomb.setJumpMove(8, 0, GameConfigs.FALLING_FROM_SKY_STARTING_HEIGHT);
		bomb.getJumpMove().skipToFall();
		bomb.setShadow(0, 0, -12, -6, 0.35f);
		bomb.setGhosting(2, 0.2);
		bomb.getJumpMove().setOnCycleCompleteEvent(e -> {
			bomb.setElevation(Elevation.ON_GROUND);
			if (!bomb.checkEntitiesAbove() && bomb.tileIsFree(bomb.getTileCoordFromCenter())) {
				bomb.setShake(2d, -0.05, 0d);
				bomb.removeShadow();
				bomb.unsetGhosting();
				Sound.playWav(bomb, "BombSlam");
				bombs.put(bomb.getTileCoordFromCenter(), bomb);
				bomb.setFrameSet("StandFrames");
				bomb.falling = false;
			}
			else {
				bomb.setElevation(Elevation.FLYING);
				bomb.onJumpFallAtOccupedTileEvent(bomb.getJumpMove());
			}
		});
		return bomb;
	}
	
	private static void removeFromMap(TileCoord coord, Bomb bomb) {
		if (bombs.containsKey(coord) && bombs.get(coord) == bomb)
			bombs.remove(coord);
	}

	private static void putOnMap(TileCoord coord, Bomb bomb) {
		if (bomb.getElevation() == Elevation.ON_GROUND)
			bombs.put(coord, bomb);
	}

}