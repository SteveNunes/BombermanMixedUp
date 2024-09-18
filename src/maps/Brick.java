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
import javafx.scene.canvas.GraphicsContext;

public class Brick extends Entity {

	private static Map<TileCoord, Brick> bricks = new HashMap<>();
	private MapSet originMapSet;
	private ItemType item;
	
	public Brick(Brick brick) {
		super(brick);
		setTileSize(Main.tileSize);
		originMapSet = brick.originMapSet;
		item = brick.item;
	}

	public Brick(MapSet originMapSet)
		{ this(originMapSet, new TileCoord(), null); }

	public Brick(MapSet originMapSet, TileCoord coord)
		{ this(originMapSet, coord, null); }
	
	public Brick(MapSet originMapSet, TileCoord coord, ItemType item) {
		super();
		setTileSize(Main.tileSize);
		this.originMapSet = originMapSet;
		this.item = item;
		for (String frameSet : Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet"))
			addNewFrameSetFromString(frameSet, originMapSet.getTileSetIniFile().read("CONFIG", frameSet));
		setFrameSet("BrickStandFrameSet");
		setPosition(coord.getPosition(Main.tileSize));
	}
	
	public ItemType getItem()
		{ return item; }

	public void setItem(ItemType item)
		{ this.item = item; }
	
	public void setItem(int itemId)
		{ item = ItemType.getItemById(itemId); }

	public void removeItem()
		{ item = null; }
	
	public static void addBrick(MapSet originMapSet, TileCoord coord)
		{ addBrick(new Brick(originMapSet, coord, null), true); }
	
	public static void addBrick(MapSet originMapSet, TileCoord coord, boolean updateLayer)
		{ addBrick(new Brick(originMapSet, coord, null), updateLayer); }

	public static void addBrick(MapSet originMapSet, TileCoord coord, ItemType item)
		{ addBrick(new Brick(originMapSet, coord, item), true); }

	public static void addBrick(MapSet originMapSet, TileCoord coord, ItemType item, boolean updateLayer)
		{ addBrick(new Brick(originMapSet, coord, item), updateLayer); }

	public static void addBrick(Brick brick)
		{ addBrick(brick, true); }

	public static void addBrick(Brick brick, boolean updateLayer) {
		TileCoord coord = brick.getTileCoord();
		if (!haveBrickAt(coord)) {
			TileCoord coord2 = coord.getNewInstance();
			coord2.setY(coord.getY() + 1);
			brick.setPosition(coord.getPosition(Main.tileSize));
			bricks.put(coord, brick);
			Tile.addTileShadow(brick.originMapSet, brick.originMapSet.getGroundWithBrickShadow(), coord2);
		}
	}

	public static void removeBrick(Brick brick)
		{ removeBrick(brick.getTileCoord()); }
	
	public static void removeBrick(TileCoord coord)
		{ removeBrick(coord, true); }

	public static void removeBrick(TileCoord coord, boolean updateLayer) {
		if (haveBrickAt(coord)) {
			Brick brick = bricks.get(coord);
			TileCoord coord2 = coord.getNewInstance();
			coord2.setY(coord.getY() + 1);
			bricks.remove(coord);
			Tile.removeTileShadow(brick.originMapSet, coord2);
		}
	}
	
	public static void clearBricks() {
		if (!bricks.isEmpty()) {
			Brick brick = null;
			while (!bricks.isEmpty()) {
				brick = bricks.values().iterator().next();
				removeBrick(brick.getTileCoord(), false);
			}
			brick.originMapSet.getLayer(26).buildLayer();
		}
	}
	
	public static int totalBricks()
		{ return bricks.size(); }

	public static List<Brick> getBricks()
		{ return new ArrayList<>(bricks.values()); }
	
	public static void drawBricks(GraphicsContext gc) {
		for (Brick brick : bricks.values())
			brick.run(gc, false);
	}

	public static boolean haveBrickAt(TileCoord coord)
		{ return bricks.containsKey(coord); }

}
