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
		setPosition(coord.getPosition(Main.tileSize));
		this.item = item;
		Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet").forEach(frameSet ->
			addNewFrameSetFromString(frameSet, MapSet.getTileSetIniFile().read("CONFIG", frameSet)));
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
		if (!haveBrickAt(coord)) {
			TileCoord coord2 = coord.getNewInstance();
			coord2.setY(coord.getY() + 1);
			brick.setPosition(coord.getPosition(Main.tileSize));
			bricks.put(coord, brick);
			Tile.addTileShadow(MapSet.getGroundWithBrickShadow(), coord2);
		}
	}

	public static void removeBrick(Brick brick)
		{ removeBrick(brick.getTileCoord()); }
	
	public static void removeBrick(TileCoord coord)
		{ removeBrick(coord, true); }

	public static void removeBrick(TileCoord coord, boolean updateLayer) {
		if (haveBrickAt(coord)) {
			TileCoord coord2 = coord.getNewInstance();
			coord2.setY(coord.getY() + 1);
			bricks.remove(coord);
			Tile.removeTileShadow(coord2);
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
			if (brick.getCurrentFrameSetName().equals("BrickStandFrameSet") &&
					MapSet.tileContainsProp(brick.getTileCoord(), TileProp.EXPLOSION))
				brick.setFrameSet("BrickBreakFrameSet");
			brick.run();
			if (brick.isBreaked()) {
				removeBricks.add(brick);
				if (brick.getItem() != null)
					Item.addItem(brick.getTileCoord(), brick.getItem());
			}
		}
		removeBricks.forEach(brick -> removeBrick(brick));
	}

	private boolean isBreaked()
		{ return getCurrentFrameSetName().equals("BrickBreakFrameSet") && !getCurrentFrameSet().isRunning(); }

	public static boolean haveBrickAt(TileCoord coord)
		{ return bricks.containsKey(coord); }

	public static Brick getBrickAt(TileCoord tileCoord)
		{ return haveBrickAt(tileCoord) ? bricks.get(tileCoord) : null; }
	
}
