package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Entity;
import entities.TileCoord;
import javafx.scene.canvas.GraphicsContext;
import util.IniFile;

public class Brick extends Entity {

	private static Map<TileCoord, Brick> bricks = new HashMap<>();
	private MapSet originMapSet;
	private Item item;
	
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
	
	public Brick(MapSet originMapSet, TileCoord coord, Item item) {
		super();
		setTileSize(Main.tileSize);
		this.originMapSet = originMapSet;
		this.item = item;
		IniFile ini = IniFile.getNewIniFileInstance("appdata/maps/" + originMapSet.getMapName() + ".map");
		IniFile ini2 = IniFile.getNewIniFileInstance("appdata/tileset/" + ini.read("SETUP", "Tiles") + ".tiles");
		for (String frameSet : Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet"))
		addNewFrameSetFromString(frameSet, ini2.read("CONFIG", frameSet));
		setFrameSet("BrickStandFrameSet");
		setPosition(coord.getPosition());
	}
	
	public static void addBrick(Brick brick)
		{ addBrick(brick, brick.getTileCoord()); }

	public static void addBrick(Brick brick, TileCoord coord) {
		brick.setPosition(coord.getPosition());
		bricks.put(coord, brick);
	}

	public static void addBrick(MapSet originMapSet, TileCoord coord, Item item) {
		Brick brick = new Brick(originMapSet, coord, item);
		bricks.put(coord, brick);
	}
	
	public static void removeBrick(Brick brick) {
		for (TileCoord tilePos : bricks.keySet())
			if (bricks.get(tilePos) == brick) {
				bricks.remove(tilePos);
				return;
			}
	}
	
	public static void removeBrick(TileCoord coord)
		{ bricks.remove(coord); }
	
	public static void clearBricks()
		{ bricks.clear(); }
	
	public List<Brick> getBricks()
		{ return new ArrayList<>(bricks.values()); }
	
	public static void drawBricks(GraphicsContext gc) {
		for (Brick brick : bricks.values())
			brick.run(gc, false);
	}

	public static boolean haveBrickAt(TileCoord coord)
		{ return bricks.containsKey(coord); }
	
}
