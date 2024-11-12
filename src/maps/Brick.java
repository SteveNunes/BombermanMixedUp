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
import enums.Curse;
import enums.Direction;
import enums.ItemType;
import enums.TileProp;
import javafx.scene.canvas.GraphicsContext;
import objmoveutils.JumpMove;
import objmoveutils.TileCoord;
import tools.Sound;

public class Brick extends Entity {

	private static Map<TileCoord, Brick> bricks = new HashMap<>();
	private static List<Brick> brickList = new ArrayList<>();
	private ItemType item;
	private int regenTimeInFrames;

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

	public Brick(TileCoord coord, ItemType item) {
		super();
		setPosition(coord.getPosition());
		regenTimeInFrames = 0;
		this.item = item;
		Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet", "BrickRollingFrameSet").forEach(frameSet -> {
			String s = MapSet.getTileSetIniFile().read("CONFIG", frameSet);
			if (s != null) {
				addNewFrameSetFromIniFile(frameSet, MapSet.getTileSetIniFile().fileName(), "CONFIG", frameSet);
				if (s.equals("BrickBreakFrameSet"))
					getFrameSet(frameSet).getFrameSetTagsFrom(0).addTagsFromString("{SetSprFrontValue;2}");
			}
		});
		setFrameSet("BrickStandFrameSet");
		setPassThroughItem(true);
	}

	public ItemType getItem() {
		return item;
	}

	public void setItem(ItemType item) {
		this.item = item;
	}

	public void setItem(int itemId) {
		item = ItemType.getItemById(itemId);
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

	public static void addBrick(TileCoord coord, ItemType item) {
		addBrick(new Brick(coord, item), true);
	}

	public static void addBrick(TileCoord coord, ItemType item, boolean updateLayer) {
		addBrick(new Brick(coord, item), updateLayer);
	}

	public static void addBrick(Brick brick) {
		addBrick(brick, true);
	}

	public static void addBrick(Brick brick, boolean updateLayer) {
		TileCoord coord = brick.getTileCoordFromCenter().getNewInstance();
		if (!haveBrickAt(coord, false)) {
			brick.setPosition(coord.getPosition());
			bricks.put(coord, brick);
			brickList.add(brick);
			brick.setBrickShadow();
			MapSet.checkTileTrigger(brick, coord, TileProp.TRIGGER_BY_BRICK);
		}
	}

	private void setBrickShadow() {
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		coord.setY(coord.getY() + 1);
		Tile.addTileShadow(MapSet.getGroundWithBrickShadow(), coord);
	}

	private void unsetBrickShadow() {
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		coord.setY(coord.getY() + 1);
		Tile.removeTileShadow(coord);
	}

	public static void removeBrick(TileCoord coord) {
		removeBrick(coord, true);
	}
	
	public static void removeBrick(TileCoord coord, boolean updateLayer) {
		if (bricks.containsKey(coord))
			removeBrick(bricks.get(coord));
	}

	public static void removeBrick(Brick brick) {
		removeBrick(brick, true);
	}

	public static void removeBrick(Brick brick, boolean updateLayer) {
		brickList.remove(brick);
		brick.unsetBrickShadow();
		bricks.remove(brick.getTileCoordFromCenter());
	}

	public static void clearBricks() {
		List<Brick> list = new ArrayList<>(brickList);
		list.forEach(brick -> removeBrick(brick.getTileCoordFromCenter()));
	}

	public static int totalBricks() {
		return bricks.size();
	}

	public static List<Brick> getBricks() {
		return brickList;
	}

	public static Map<TileCoord, Brick> getBrickMap() {
		return bricks;
	}

	public static void drawBricks() {
		List<Brick> tempBricks = new ArrayList<>(brickList);
		for (Brick brick : tempBricks) {
			String cFSet = brick.getCurrentFrameSetName();
			if (!cFSet.equals("BrickBreakFrameSet")) {
				if (MapSet.tileContainsProp(brick.getTileCoordFromCenter(), TileProp.DAMAGE_BRICK) ||
						MapSet.tileContainsProp(brick.getTileCoordFromCenter(), TileProp.EXPLOSION))
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
					Item.addItem(brick.getTileCoordFromCenter(), brick.getItem());
					brick.setItem(null);
				}
			}
		}
	}

	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		super.run(gc, isPaused);
		if (!isBlockedMovement() && tileWasChanged()) {
			TileCoord prevCoord = getPreviewTileCoord().getNewInstance();
			TileCoord coord = getTileCoordFromCenter().getNewInstance();
			MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_BRICK);
			MapSet.checkTileTrigger(this, prevCoord, TileProp.TRIGGER_BY_BRICK, true);
			removeThisFromTile(prevCoord);
			if (!bricks.containsKey(coord))
				bricks.put(coord, this);
		}
	}

	public void destroy() {
		if (!getCurrentFrameSetName().equals("BrickBreakFrameSet")) {
			removeThisFromTile(getTileCoordFromCenter());
			setFrameSet("BrickBreakFrameSet");
		}
	}

	private boolean isBreaked() {
		return getCurrentFrameSetName().equals("BrickBreakFrameSet") && !getCurrentFrameSet().isRunning();
	}

	public static boolean haveBrickAt(TileCoord coord, boolean ignoreDestroyedRegenBricks) {
		return bricks.containsKey(coord) && (!ignoreDestroyedRegenBricks || !bricks.get(coord).getCurrentFrameSetName().equals("BrickBreakFrameSet") || bricks.get(coord).getCurrentFrameSet().isRunning());
	}

	public static boolean haveBrickAt(TileCoord coord) {
		return haveBrickAt(coord, true);
	}

	public static Brick getBrickAt(TileCoord tileCoord) {
		return haveBrickAt(tileCoord, true) ? bricks.get(tileCoord) : null;
	}

	public void setRegenTime(int bricksRegenTimeInSecs) {
		regenTimeInFrames = bricksRegenTimeInSecs * 60;
	}

	public void kick(Direction direction, double speed) {
		kick(direction, speed, "BombKick", "BombSlam");
	}

	public void kick(Direction direction, double speed, String kickSound, String slamSound) {
		if (getPushEntity() == null && MapSet.tileIsFree(getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction))) {
			Sound.playWav(kickSound);
			entityTools.PushEntity pushEntity = new entityTools.PushEntity(this, speed, direction);
			pushEntity.setOnStopEvent(e -> {
				Sound.playWav(slamSound);
				setShake(2d, -0.05, 0d);
				unsetGhosting();
				bricks.put(getTileCoordFromCenter(), this);
				TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction);
				if (haveBrickAt(coord))
					Brick.getBrickAt(coord).kick(direction, speed, kickSound, slamSound);
			});
			setPushEntity(pushEntity);
			setGhosting(2, 0.2);
		}
	}

	public void punch(Direction direction, String punchSound) {
		setDirection(direction);
		TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction, 4);
		jumpTo(this, coord.getNewInstance(), 4, 1.2, 20, punchSound);
	}

	@Override
	public void onBeingHoldEvent(Entity holder) {
		removeThisFromTile(getTileCoordFromCenter());
		Tile.removeTileShadow(getTileCoordFromCenter().getNewInstance().incCoordsByDirection(Direction.DOWN));
	}

	private void removeThisFromTile(TileCoord coord) {
		if (bricks.containsKey(coord) && bricks.get(coord) == this)
			bricks.remove(coord);
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
		removeThisFromTile(getTileCoordFromCenter());
	}

	@Override
	public void onPushEntityStop() {
		bricks.put(getTileCoordFromCenter(), this);
	}

	@Override
	public void onJumpFallAtFreeTileEvent(JumpMove jumpMove) {
		checkOutScreenCoords();
		centerToTile();
		if (Entity.haveAnyEntityAtCoord(getTileCoordFromCenter()))
			for (Entity entity : Entity.getEntityListFromCoord(getTileCoordFromCenter())) {
				if (!entity.isBlockedMovement()) {
					if (entity instanceof BomberMan)
							((BomberMan)entity).dropItem(true, false);
					else if (entity instanceof Monster) {
						entity.setCurse(Curse.STUNNED);
						entity.setCurseDuration(120);
					}
				}
			}
		Sound.playWav("BrickDrop");
		destroy();
	}

	@Override
	public void onJumpFallAtOccupedTileEvent(JumpMove jumpMove) {
		checkOutScreenCoords();
		centerToTile();
		if (Bomb.haveBombAt(getTileCoordFromCenter()))
			Bomb.getBombAt(getTileCoordFromCenter()).detonate();
		else if (Item.haveItemAt(getTileCoordFromCenter()))
			Item.getItemAt(getTileCoordFromCenter()).destroy();
		if (bricks.containsKey(getTileCoordFromCenter()))
			bricks.get(getTileCoordFromCenter()).destroy();
		Sound.playWav("BrickDrop");
		destroy();
	}

}
