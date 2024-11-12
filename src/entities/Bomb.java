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
import enums.FindType;
import enums.PassThrough;
import enums.TileProp;
import javafx.scene.canvas.GraphicsContext;
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
import util.MyMath;
import util.TimerFX;

public class Bomb extends Entity {

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
		nesBomb = type == BombType.NES || (owner instanceof BomberMan && ((BomberMan) owner).getBomberIndex() == 0);
		if (type == BombType.P)
			fireDistance = GameConfigs.MAX_EXPLOSION_DISTANCE;
		this.type = type;
		this.fireDistance = fireDistance;
		this.owner = owner;
		timer = type == BombType.SENSOR || type == BombType.REMOTE || type == BombType.SPIKED_REMOTE ? -1 : 180;
		curseMulti = 1;
		int ticksPerFrame = type == BombType.LAND_MINE ? 3 : 16;
		ownerIsOver = owner != null && owner.getTileCoordFromCenter().equals(coord);
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
		int y = 16 * type.getValue();
		if (type == BombType.LAND_MINE) {
			String frameSet = "{SetSprSource;Bombs;0;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;0},{PlayWav;Mine}|{SetSprIndex;-}|{SetSprIndex;1}|{SetSprIndex;-}|{SetSprIndex;2}|{SetSprIndex;-}|{SetSprIndex;3}|{SetSprIndex;-}|{SetSprIndex;0}|{Goto;1;1}|{SetFrameSet;LandedFrames}";
			addNewFrameSetFromString("LandingFrames", frameSet);
			setFrameSet("LandingFrames");
			frameSet = "{SetSprSource;Bombs;0;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;-}|{}|{Goto;-1}";
			addNewFrameSetFromString("LandedFrames", frameSet);
			frameSet = "{SetSprSource;Bombs;0;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;0},{PlayWav;Mine}|{SetSprIndex;-}|{SetSprIndex;1}|{SetSprIndex;-}|{SetSprIndex;2}|{SetSprIndex;-}|{SetSprIndex;3}|{SetSprIndex;-}|{SetSprIndex;0}|{Goto;1;1}|{ExplodeBomb}";
			addNewFrameSetFromString("UnlandingFrames", frameSet);
		}
		else {
			String frameSet = "{SetSprSource;Bombs;0;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;0}|{SetSprIndex;1}|{SetSprIndex;2}|{SetSprIndex;3}|{Goto;0}";
			addNewFrameSetFromString("StandFrames", frameSet);
			setFrameSet("StandFrames");
			frameSet = "{SetSprSource;Bombs;0;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;0},{SetEntityShadow;0;0;16;8;0.35}|{SetSprIndex;1}|{SetSprIndex;2}|{SetSprIndex;3}|{Goto;0}";
			addNewFrameSetFromString("JumpingFrames", frameSet);
			setFrameSet("JumpingFrames");
		}
		setPosition(coord.getPosition());
		setPassThroughItem(true);
		setDangerMarks(timer == -1 ? TileProp.CPU_DANGER_2 : TileProp.CPU_DANGER);
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
			bombs.put(coord, bomb);
			bombList.add(bomb);
			MapSet.checkTileTrigger(bomb, coord, TileProp.TRIGGER_BY_BOMB);
			MapSet.checkTileTrigger(bomb, coord, TileProp.TRIGGER_BY_STOPPED_BOMB);
			return bomb;
		}
		return null;
	}

	public static void addBomb(Bomb bomb) {
		addBomb(bomb, false);
	}

	public static void addBomb(Bomb bomb, boolean checkTile) {
		addBomb(bomb.owner, bomb.getTileCoordFromCenter(), bomb.type, bomb.fireDistance, checkTile);
	}

	public static void removeBomb(Bomb bomb) {
		removeBomb(bomb.getTileCoordFromCenter());
	}

	public static void removeBomb(TileCoord coord) {
		final Bomb bomb = bombs.get(coord);
		if (bomb.dangerMarked || bomb.dangerMarked2)
			TimerFX.createTimer("removeMarkTilesAsDanger-" + bomb.hashCode(), 1000, () -> {
				if (bomb.dangerMarked)
					bomb.removeDangerMarks(TileProp.CPU_DANGER);
				if (bomb.dangerMarked2)
					bomb.removeDangerMarks(TileProp.CPU_DANGER_2);
			});
		bombList.remove(bombs.get(coord));
		bombs.remove(coord);
	}

	public static void clearBombs() {
		for (TileCoord coord : new ArrayList<>(bombs.keySet()))
			removeBomb(coord);
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
			if (bomb.type == BombType.LAND_MINE) {
				bomb.run();
				if (bomb.currentFrameSetNameIsEqual("LandedFrames") && Entity.haveAnyEntityAtCoord(bomb.getTileCoordFromCenter(), bomb))
					bomb.setFrameSet("UnlandingFrames");
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
			if ((!bomb.dangerMarked2 && bomb.getTimer() <= 60 * bomb.curseMulti) || MapSet.tileContainsProp(bomb.getTileCoordFromCenter(), TileProp.CPU_DANGER)) {
				if (bomb.dangerMarked)
					bomb.removeDangerMarks(TileProp.CPU_DANGER);
				bomb.setDangerMarks(TileProp.CPU_DANGER_2);
			}
			if (bomb.getTimer() != -1 && !bomb.isBlockedMovement())
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
		isActive = false;
		centerToTile();
		unsetPushEntity();
		Sound.playWav("explosion/Explosion" + (nesBomb ? "" : fireDistance < 3 ? "1" : (int) (fireDistance / 3)));
		if (getBombType() != BombType.MAGMA)
			Explosion.addExplosion(this, getTileCoordFromCenter(), fireDistance, getBombType().getValue(), canPassThroughBricks());
		else {
			int range = fireDistance / 2;
			TileCoord coord = new TileCoord();
			for (int y = getTileCoordFromCenter().getY() - range; y <= getTileCoordFromCenter().getY() + range; y++)
				for (int x = getTileCoordFromCenter().getX() - range; x <= getTileCoordFromCenter().getX() + range; x++) {
					int dx = x - getTileCoordFromCenter().getX();
					int dy = y - getTileCoordFromCenter().getY();
					if ((dx * dx) / (range * range) + (dy * dy) / (range * range) <= 1) {
						coord.setCoords(x, y);
						if (MapSet.haveTilesOnCoord(coord))
							Explosion.addExplosion(this, coord, 1, getBombType().getValue(), true).setPassThroughAny(true);
					}
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
			bombs.put(getTileCoordFromCenter(), this);
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

	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		if (getBombType() == BombType.FOLLOW && !isBlockedMovement() && getPathFinder() == null) {
			List<FindProps> founds = Tools.findInRect(this, getTileCoordFromCenter(), owner, 4, FindType.PLAYER);
			if (founds != null) {
				TileCoord coord = founds.get(0).getCoord();
				Function<TileCoord, Boolean> tileIsFree = t -> {
					return MapSet.tileIsFree(t) || t.equals(coord) || t.equals(getTileCoordFromCenter());
				};
				setPathFinder(new PathFinder(getTileCoordFromCenter(), coord, getDirection(), PathFinderOptmize.OPTIMIZED, tileIsFree));
				if (!getPathFinder().pathWasFound() || getPathFinder().getCurrentPath().size() == 1) {
					setPathFinder(null);
					if (!bombs.containsKey(getTileCoordFromCenter()))
						bombs.put(getTileCoordFromCenter(), this);
				}
				else {
					removeThisFromTile(getTileCoordFromCenter());
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
			List<FindProps> founds = Tools.findInLine(this, owner, getTileCoordFromCenter(), 2, Set.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT), FindType.PLAYER);
			if (founds != null)
				detonate();
		}
		super.run(gc, isPaused);
		if (!isBlockedMovement() && tileWasChanged() && isActive()) {
			TileCoord prevCoord = getPreviewTileCoord().getNewInstance();
			removeThisFromTile(prevCoord);
			TileCoord coord = getTileCoordFromCenter().getNewInstance();
			MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_BOMB);
			MapSet.checkTileTrigger(this, prevCoord, TileProp.TRIGGER_BY_BOMB, true);
			if (!isMoving() && !bombs.containsKey(coord)) {
				bombs.put(coord, this);
				MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_STOPPED_BOMB);
			}
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
			entityTools.PushEntity pushEntity = new entityTools.PushEntity(this, speed, direction);
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
					bombs.put(getTileCoordFromCenter(), this);
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
							bomb.kick(direction, speed, kickSound, slamSound);
					}
				}
			});
			setPushEntity(pushEntity);
			setGhosting(2, 0.2);
		}
	}

	private void removeThisFromTile(TileCoord coord) {
		if (bombs.containsKey(coord) && bombs.get(coord) == this)
			bombs.remove(coord);
	}
	
	@Override
	public void onBeingHoldEvent(Entity holder) {
		removeThisFromTile(getTileCoordFromCenter());
	}

	@Override
	public void onSetPushEntityTrigger() {
		removeThisFromTile(getTileCoordFromCenter());
	}

	@Override
	public void onSetGotoMoveTrigger() {
		removeThisFromTile(getTileCoordFromCenter());
	}
	
	@Override
	public void onSetJumpMoveTrigger() {
		setFrameSet("JumpingFrames");
		removeThisFromTile(getTileCoordFromCenter());
	}

	@Override
	public void onPushEntityStop() {
		bombs.put(getTileCoordFromCenter(), this);
		MapSet.checkTileTrigger(this, getTileCoordFromCenter(), TileProp.TRIGGER_BY_STOPPED_BOMB);
	}

	public void punch(Direction direction, String punchSound) {
		setDirection(direction);
		TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction, 4);
		jumpTo(this, coord.getNewInstance(), 4, 1.2, 20, punchSound);
	}

	@Override
	public void onJumpFallAtFreeTileEvent(JumpMove jumpMove) {
		if (getBombType() == BombType.RUBBER && MyMath.getRandom(1, 5) != 1) {
			onJumpFallAtOccupedTileEvent(jumpMove);
			return;
		}
		checkOutScreenCoords();
		centerToTile();
		if (checkEntitiesAbove()) {
			onJumpFallAtOccupedTileEvent(jumpMove);
			return;
		}
		Sound.playWav(getBombType() == BombType.RUBBER ? "BombBounce" : "BombHittingGround");
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		bombs.put(coord, this);
		setFrameSet("StandFrames");
		MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_BOMB);
		MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_STOPPED_BOMB);
	}

	@Override
	public void onJumpFallAtOccupedTileEvent(JumpMove jumpMove) {
		if (getBombType() == BombType.RUBBER)
			forceDirection(getDirection().getNext8WayClockwiseDirection((int)(1 - MyMath.getRandom(0, 2))));
		Sound.playWav(getBombType() == BombType.RUBBER ? "BombBounce" : "BombHittingGround");
		checkOutScreenCoords();
		centerToTile();
		checkEntitiesAbove();
		jumpMove.resetJump(4, 1.2, 14);
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
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
		if ((dangerProp == TileProp.CPU_DANGER && dangerMarked != !remove) ||
				(dangerProp == TileProp.CPU_DANGER_2 && dangerMarked2 != !remove)) {
			if (dangerProp == TileProp.CPU_DANGER)
				dangerMarked = !remove;
			else
				dangerMarked2 = !remove;
			Set<PassThrough> passThrough = new HashSet<>(Set.of(PassThrough.PLAYER));
			if (canPassThroughBricks())
				passThrough.add(PassThrough.BRICK);
			markTilesAsDanger(passThrough, getTileCoordFromCenter(), fireDistance, dangerProp, remove);
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
				if (x > 0 && (!MapSet.haveTilesOnCoord(coord2) || MapSet.getCurrentLayer().getTileProps(coord2).contains(TileProp.GROUND_NO_FIRE) || !MapSet.tileIsFree(coord2, passThrough)))
					break;
				coord2.incCoordsByDirection(dir);
			}
		}
	}

}