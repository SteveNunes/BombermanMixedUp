package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Entity;
import entities.TileCoord;
import enums.ItemType;
import enums.TileProp;

public class Brick extends Entity {

	private static Map<TileCoord, Brick> bricks = new HashMap<>();
	private ItemType item;
	private int regenTimeInFrames;
	
	public Brick(Brick brick) {
		super(brick);
		item = brick.item;
	}

	public Brick()
		{ this(new TileCoord(), null); }

	public Brick(TileCoord coord)
		{ this(coord, null); }
	
	public Brick(TileCoord coord, ItemType item) {
		super();
		regenTimeInFrames = 0;
		setPassThroughBrick(true);
		setPosition(coord.getPosition(Main.TILE_SIZE));
		this.item = item;
		Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet", "BrickRollingFrameSet").forEach(frameSet -> {
			String s = MapSet.getTileSetIniFile().read("CONFIG", frameSet);
			addNewFrameSetFromString(frameSet, s == null ? "" : s);
		});
		setFrameSet("BrickStandFrameSet");
	}
	
	public ItemType getItem()
		{ return item; }

	public void setItem(ItemType item)
		{ this.item = item; }
	
	public void setItem(int itemId)
		{ item = ItemType.getItemById(itemId); }

	public void removeItem()
		{ item = null; }
	
	public static void addBrick(TileCoord coord)
		{ addBrick(new Brick(coord, null), true); }
	
	public static void addBrick(TileCoord coord, boolean updateLayer)
		{ addBrick(new Brick(coord, null), updateLayer); }

	public static void addBrick(TileCoord coord, ItemType item)
		{ addBrick(new Brick(coord, item), true); }

	public static void addBrick(TileCoord coord, ItemType item, boolean updateLayer)
		{ addBrick(new Brick(coord, item), updateLayer); }

	public static void addBrick(Brick brick)
		{ addBrick(brick, true); }

	public static void addBrick(Brick brick, boolean updateLayer) {
		TileCoord coord = brick.getTileCoord();
		if (!haveBrickAt(coord, false)) {
			brick.setPosition(coord.getPosition(Main.TILE_SIZE));
			bricks.put(coord, brick);
			brick.setBrickShadow();
		}
	}
	
	private void setBrickShadow() {
		TileCoord coord = getTileCoord().getNewInstance();
		coord.setY(coord.getY() + 1);
		Tile.addTileShadow(MapSet.getGroundWithBrickShadow(), coord);
	}
	
	private void unsetBrickShadow() {
		TileCoord coord = getTileCoord().getNewInstance();
		coord.setY(coord.getY() + 1);
		Tile.removeTileShadow(coord);
	}

	public static void removeBrick(Brick brick)
		{ removeBrick(brick.getTileCoord()); }
	
	public static void removeBrick(TileCoord coord)
		{ removeBrick(coord, true); }

	public static void removeBrick(TileCoord coord, boolean updateLayer) {
		if (haveBrickAt(coord, false)) {
			bricks.get(coord).unsetBrickShadow();
			bricks.remove(coord);
		}
	}
	
	public static void clearBricks() {
		List<Brick> list = new ArrayList<>(bricks.values());
		list.forEach(brick -> removeBrick(brick.getTileCoord()));
	}
	
	public static int totalBricks()
		{ return bricks.size(); }

	public static List<Brick> getBricks()
		{ return new ArrayList<>(bricks.values()); }
	
	public static void drawBricks() {
		List<Brick> removeBricks = new ArrayList<>();
		for (Brick brick : bricks.values()) {
			String cFSet = brick.getCurrentFrameSetName();
			if (!cFSet.equals("BrickBreakFrameSet") && MapSet.tileContainsProp(brick.getTileCoord(), TileProp.EXPLOSION))
				brick.breakIt();
			else if (cFSet.equals("BrickRegenFrameSet") && !brick.getCurrentFrameSet().isRunning()) {
				brick.setFrameSet("BrickStandFrameSet");
				brick.setBrickShadow();
			}
			if (cFSet.equals("BrickBreakFrameSet") && brick.regenTimeInFrames > 0 &&
					(brick.regenTimeInFrames > 30 || !MapSet.tileIsOccuped(brick.getTileCoord(), brick.getPassThrough())) && --brick.regenTimeInFrames == 0)
						brick.setFrameSet("BrickRegenFrameSet");
			brick.run();
			if (brick.isBreaked() && brick.regenTimeInFrames == 0) {
				brick.unsetBrickShadow();
				if (MapSet.getBricksRegenTimeInFrames() == 0)
					removeBricks.add(brick);
				else
					brick.regenTimeInFrames = MapSet.getBricksRegenTimeInFrames();
				if (brick.getItem() != null) {
					Item.addItem(brick.getTileCoord(), brick.getItem());
					brick.setItem(null);
				}
			}
		}
		removeBricks.forEach(brick -> removeBrick(brick));
	}
	
	public void breakIt() {
		if (getCurrentFrameSetName().equals("BrickStandFrameSet"))
			setFrameSet("BrickBreakFrameSet");
	}

	private boolean isBreaked()
		{ return getCurrentFrameSetName().equals("BrickBreakFrameSet") && !getCurrentFrameSet().isRunning(); }

	public static boolean haveBrickAt(TileCoord coord, boolean ignoreDestroyedRegenBricks)
		{ return bricks.containsKey(coord) && (ignoreDestroyedRegenBricks || !bricks.get(coord).getCurrentFrameSetName().equals("BrickRegenFrameSet")); }

	public static boolean haveBrickAt(TileCoord coord)
		{ return haveBrickAt(coord, false); }
	
	public static Brick getBrickAt(TileCoord tileCoord)
		{ return haveBrickAt(tileCoord, false) ? bricks.get(tileCoord) : null; }

	public void setRegenTime(int bricksRegenTimeInSecs)
		{ regenTimeInFrames = bricksRegenTimeInSecs * 60; }
	
}
