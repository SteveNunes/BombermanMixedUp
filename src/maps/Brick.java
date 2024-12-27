package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Bomb;
import entities.BomberMan;
import entities.Entity;
import entities.Monster;
import entityTools.PushEntity;
import enums.Curse;
import enums.Direction;
import enums.Elevation;
import enums.TileProp;
import frameset_tags.SetSprIndex;
import frameset_tags.SetSprSource;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import objmoveutils.JumpMove;
import objmoveutils.TileCoord;
import tools.GameConfigs;
import tools.Sound;
import util.DurationTimerFX;

public class Brick extends Entity {

	private static Map<TileCoord, Brick> bricksMap = new HashMap<>();
	private static List<Brick> brickList = new ArrayList<>();
	private Item item;
	private int regenTimeInFrames;
	private boolean isWall;

	public Brick(Brick brick) {
		super(brick);
		item = brick.item;
	}

	public Brick() {
		this(new TileCoord(), null);
	}

	public Brick(TileCoord coord) {
		this(coord, null);
	}
	
	public Brick(TileCoord coord, Item item) {
		super();
		setPosition(coord.getPosition());
		regenTimeInFrames = 0;
		this.item = item;
		isWall = false;
		Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet", "BrickRollingFrameSet").forEach(frameSet -> {
			String s = MapSet.getTileSetIniFile().read("CONFIG", frameSet);
			if (s != null) {
				addNewFrameSetFromIniFile(this, frameSet, MapSet.getTileSetIniFile().fileName(), "CONFIG", frameSet);
				if (s.equals("BrickBreakFrameSet"))
					getFrameSet(frameSet).getFrameSetTagsFrom(0).addTagsFromString("{SetSprFrontValue;2}");
			}
		});
		setFrameSet("BrickStandFrameSet");
		setPassThroughItem(true);
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void removeItem() {
		item = null;
	}

	public static void addBrick(TileCoord coord) {
		addBrick(new Brick(coord, null), true);
	}

	public static void addBrick(TileCoord coord, boolean updateLayer) {
		addBrick(new Brick(coord, null), updateLayer);
	}

	public static void addBrick(TileCoord coord, Item item) {
		addBrick(new Brick(coord, item), true);
	}

	public static void addBrick(TileCoord coord, Item item, boolean updateLayer) {
		addBrick(new Brick(coord, item), updateLayer);
	}

	public static void addBrick(Brick brick) {
		brickList.add(brick);
	}

	public static void addBrick(Brick brick, boolean updateLayer) {
		TileCoord coord = brick.getTileCoordFromCenter().getNewInstance();
		if (!haveBrickAt(coord, false)) {
			brick.setPosition(coord.getPosition());
			brickList.add(brick);
			brick.setBrickShadow();
			if (brick.getElevation() == Elevation.ON_GROUND)
				MapSet.checkTileTrigger(brick, coord, TileProp.TRIGGER_BY_BRICK);
		}
	}

	private void setBrickShadow() {
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		coord.setY(coord.getY() + 1);
		Tile.addTileShadow(MapSet.getGroundWithBrickShadow(), coord);
	}

	private void unsetBrickShadow() {
		unsetBrickShadow(getTileCoordFromCenter());
	}
	
	private void unsetBrickShadow(TileCoord previewTileCoord) {
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		coord.setY(coord.getY() + 1);
		Tile.removeTileShadow(coord);
	}

	public static void removeBrick(TileCoord coord) {
		removeBrick(coord, true);
	}
	
	public static void removeBrick(TileCoord coord, boolean updateLayer) {
		if (bricksMap.containsKey(coord))
			removeBrick(bricksMap.get(coord));
		bricksMap.remove(coord);
	}

	public static void removeBrick(Brick brick) {
		removeBrick(brick, true);
	}

	public static void removeBrick(Brick brick, boolean updateLayer) {
		brickList.remove(brick);
		brick.unsetBrickShadow();
		bricksMap.remove(brick.getTileCoordFromCenter());
	}

	public static void clearBricks() {
		List<Brick> list = new ArrayList<>(brickList);
		list.forEach(brick -> removeBrick(brick.getTileCoordFromCenter()));
		brickList.clear();
	}

	public static int totalBricks() {
		return brickList.size();
	}

	public static List<Brick> getBricks() {
		return brickList;
	}

	public static Map<TileCoord, Brick> getBrickMap() {
		return bricksMap;
	}

	public static void drawBricks() {
		bricksMap.clear();
		List<Brick> list = new ArrayList<>(brickList);
		for (Brick brick : list)
			bricksMap.put(brick.getTileCoordFromCenter(), brick);
		for (Brick brick : list) {
			if (brick.getPreviewTileCoord() != null && brick.getElevation() == Elevation.ON_GROUND && !brick.isMoving() && !brick.getPreviewTileCoord().equals(brick.getTileCoordFromCenter())) {
				brick.unsetBrickShadow(brick.getPreviewTileCoord());
				brick.setBrickShadow();
			}
			String cFSet = brick.getCurrentFrameSetName();
			if (!cFSet.equals("BrickBreakFrameSet")) {
				if (MapSet.tileContainsProp(brick.getTileCoordFromCenter(), TileProp.DAMAGE_BRICK) ||
						MapSet.tileContainsProp(brick.getTileCoordFromCenter(), TileProp.EXPLOSION) ||
						MapSet.tileContainsProp(brick.getTileCoordFromCenter(), TileProp.INSTAKILL))
							brick.destroy();
				else if (cFSet.equals("BrickRegenFrameSet") && !brick.getCurrentFrameSet().isRunning()) {
					brick.setFrameSet("BrickStandFrameSet");
					brick.setBrickShadow();
				}
			}
			else if (!brick.getCurrentFrameSet().isRunning() && brick.regenTimeInFrames > 0) {
				brick.regenTimeInFrames--;
				if (brick.regenTimeInFrames == 0 && !MapSet.getTileProps(brick.getTileCoordFromCenter()).contains(TileProp.DAMAGE_BRICK) && !MapSet.tileIsOccuped(brick.getTileCoordFromCenter(), brick.getPassThrough()) && !Entity.haveAnyEntityAtCoord(brick.getTileCoordFromCenter()))
					brick.setFrameSet("BrickRegenFrameSet");
			}
			brick.run();
			if (brick.isBreaked() && brick.regenTimeInFrames == 0) {
				brick.unsetBrickShadow();
				if (MapSet.getBricksRegenTimeInFrames() == 0)
					removeBrick(brick);
				else
					brick.regenTimeInFrames = MapSet.getBricksRegenTimeInFrames();
				if (brick.getItem() != null) {
					Item.addItem(brick.getItem());
					brick.getItem().setPosition(brick.getTileCoordFromCenter().getPosition());
					brick.setItem(null);
				}
			}
			if (brick.getElevation() == Elevation.ON_GROUND && !brick.isMoving() && brick.getPreviewTileCoord() != brick.getTileCoordFromCenter())
				bricksMap.put(brick.getTileCoordFromCenter().getNewInstance(), brick);
		}
	}

	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		super.run(gc, isPaused);
		if (!isBlockedMovement() && tileWasChanged()) {
			TileCoord prevCoord = getPreviewTileCoord().getNewInstance();
			TileCoord coord = getTileCoordFromCenter().getNewInstance();
			if (getElevation() == Elevation.ON_GROUND) {
				MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_BRICK);
				MapSet.checkTileTrigger(this, prevCoord, TileProp.TRIGGER_BY_BRICK, true);
			}
		}
	}

	public void destroy() {
		if (!isWall && !getCurrentFrameSetName().equals("BrickBreakFrameSet")) {
			bricksMap.remove(getTileCoordFromCenter());
			setFrameSet("BrickBreakFrameSet");
		}
	}

	private boolean isBreaked() {
		return getCurrentFrameSetName().equals("BrickBreakFrameSet") && !getCurrentFrameSet().isRunning();
	}

	public static boolean haveBrickAt(TileCoord coord, boolean ignoreDestroyedRegenBricks) {
		return bricksMap.containsKey(coord) && (!ignoreDestroyedRegenBricks || !bricksMap.get(coord).getCurrentFrameSetName().equals("BrickBreakFrameSet") || bricksMap.get(coord).getCurrentFrameSet().isRunning());
	}

	public static boolean haveBrickAt(TileCoord coord) {
		return haveBrickAt(coord, true);
	}

	public static Brick getBrickAt(TileCoord tileCoord) {
		return haveBrickAt(tileCoord, true) ? bricksMap.get(tileCoord) : null;
	}

	public void setRegenTime(int bricksRegenTimeInSecs) {
		regenTimeInFrames = bricksRegenTimeInSecs * 60;
	}

	public void kick(Direction direction, double speed) {
		kick(direction, speed, "BombKick", "BombSlam");
	}

	public void kick(Direction direction, double speed, String kickSound, String slamSound) {
		TileCoord c = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction);
		if (getPushEntity() == null && (Brick.haveBrickAt(c) || MapSet.tileIsFree(c))) {
			if (kickSound != null)
				Sound.playWav(kickSound);
			PushEntity pushEntity = new PushEntity(this, speed, direction);
			pushEntity.setOnStopEvent(e -> {
				if (slamSound != null)
					Sound.playWav(slamSound);
				setShake(2d, -0.05, 0d);
				unsetGhosting();
				TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction);
				if (haveBrickAt(coord))
					DurationTimerFX.createTimer("chainKickBrick" + hashCode(), Duration.millis(50), () -> Brick.getBrickAt(coord).kick(direction, speed, kickSound, slamSound));
			});
			setPushEntity(pushEntity);
			setGhosting(2, 0.2);
		}
	}

	public void punch(Direction direction, String punchSound) {
		setDirection(direction);
		TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction, 4);
		jumpTo(coord.getNewInstance(), 4, 1.2, 20, punchSound);
	}

	@Override
	public void onBeingHoldEvent(Entity holder) {
		unsetBrickShadow();
	}

	@Override
	public void onSetPushEntityTrigger() {
		unsetBrickShadow();
	}

	@Override
	public void onSetGotoMoveTrigger() {
		unsetBrickShadow();
	}
	
	@Override
	public void onSetJumpMoveTrigger() {
		unsetBrickShadow();
	}

	@Override
	public void onPushEntityStop() {
		setBrickShadow();
	}

	@Override
	public void onJumpFallAtFreeTileEvent(JumpMove jumpMove) {
		centerToTile();
		checkEntitiesAbove();
		Sound.playWav("BrickDrop");
		destroy();
	}

	@Override
	public void onJumpFallAtOccupedTileEvent(JumpMove jumpMove) {
		centerToTile();
		if (Bomb.haveBombAt(getTileCoordFromCenter()))
			Bomb.getBombAt(getTileCoordFromCenter()).detonate();
		else if (Item.haveItemAt(getTileCoordFromCenter()))
			Item.getItemAt(getTileCoordFromCenter()).destroy();
		if (bricksMap.containsKey(getTileCoordFromCenter()) && getBrickAt(getTileCoordFromCenter()) != this)
			bricksMap.get(getTileCoordFromCenter()).destroy();
		Sound.playWav("BrickDrop");
		destroy();
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

	public void checkEntitiesAboveWallVersion() {
		if (Entity.haveAnyEntityAtCoord(getTileCoordFromCenter()))
			for (Entity entity : Entity.getEntityListFromCoord(getTileCoordFromCenter()))
				if (entity instanceof BomberMan || entity instanceof Monster) {
					if (!entity.isBlockedMovement()) {
						entity.setHitPoints(1);
						entity.takeDamage(isWall);
					}
				}
		if (Item.haveItemAt(getTileCoordFromCenter()))
			Item.getItemAt(getTileCoordFromCenter()).destroy();
		if (haveBrickAt(getTileCoordFromCenter()))
			getBrickAt(getTileCoordFromCenter()).destroy();
	}

	public static Brick dropBrickFromSky(TileCoord coord) {
		return dropBrickFromSky(coord, null, false);
	}

	public static Brick dropBrickFromSky(TileCoord coord, Item itemType) {
		return dropBrickFromSky(coord, itemType, false);
	}

	static Brick dropBrickFromSky(TileCoord coord, Item itemType, boolean isWall) {
		coord = coord.getNewInstance();
		final TileCoord coord2 = coord.getNewInstance(); 
		Brick brick = new Brick(coord, itemType);
		brick.isWall = isWall;
		Brick.addBrick(brick);
		if (isWall) {
			MapSet.getCurrentLayer().addTileProp(coord2, TileProp.CPU_DANGER_2);
			brick.getCurrentFrameSet().iterateFrameTags(tag -> {
				if (tag instanceof SetSprSource) {
					((SetSprSource)tag).originSprSizePos.x = (int)MapSet.getWallTile().getX();
					((SetSprSource)tag).originSprSizePos.y = (int)MapSet.getWallTile().getY();
				}
				else if (tag instanceof SetSprIndex)
					((SetSprIndex)tag).value = 0;
			});
		}
		brick.setJumpMove(8, 0, GameConfigs.FALLING_FROM_SKY_STARTING_HEIGHT);
		brick.getJumpMove().skipToFall();
		brick.setShadow(0, 0, -12 ,-6 ,0.35f);
		brick.setGhosting(2, 0.2);
		brick.getJumpMove().setOnCycleCompleteEvent(e -> {
			if (isWall) {
				brickList.remove(brick);
				MapSet.getCurrentLayer().removeAllTilesFromCoord(coord2);
				Tile tile = new Tile(MapSet.getCurrentLayer(), (int)MapSet.getWallTile().getX(), (int)MapSet.getWallTile().getY(), (int)coord2.getPosition().getX(), (int)coord2.getPosition().getY());
				MapSet.getCurrentLayer().addTile(tile);
				MapSet.getCurrentLayer().addTileProp(coord2, TileProp.WALL, TileProp.INSTAKILL);
				if (MapSet.getCurrentLayer().tileHaveTags(coord2))
					MapSet.getCurrentLayer().clearTileTags(coord2);
				MapSet.getCurrentLayer().buildLayer();
				Sound.playWav(brick, "BlockSlam");
				brick.checkEntitiesAboveWallVersion();
				return;
			}
			brick.setElevation(Elevation.ON_GROUND);
			if (brick.checkEntitiesAbove())
				brick.destroy();
			else if (haveBrickAt(coord2)) {
				getBrickAt(coord2).destroy();
				brick.destroy();
			}
			else if (brick.tileIsFree(brick.getTileCoordFromCenter())) {
				brick.setShake(2d, -0.05, 0d);
				brick.removeShadow();
				brick.unsetGhosting();
				Sound.playWav(brick, "BrickDrop");
				bricksMap.put(coord2, brick);
			}
			else
				brick.onJumpFallAtOccupedTileEvent(brick.getJumpMove());
		});
		return brick;
	}

}
