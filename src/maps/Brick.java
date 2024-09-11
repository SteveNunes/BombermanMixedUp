package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import entities.Entity;
import entities.FrameSet;
import entities.TilePos;
import javafx.scene.canvas.GraphicsContext;
import util.IniFile;

public class Brick extends Entity {

	private static Map<TilePos, Brick> bricks = new HashMap<>();
	private MapSet originMapSet;
	private Item item;
	
	public Brick(Brick brick) {
		super(brick);
		setTileSize(Main.tileSize);
		originMapSet = brick.originMapSet;
		item = brick.item;
	}

	public Brick(MapSet originMapSet, int tileX, int tileY)
		{ this(originMapSet, tileX, tileY, null); }
	
	public Brick(MapSet originMapSet, int tileX, int tileY, Item item) {
		super();
		setTileSize(Main.tileSize);
		this.originMapSet = originMapSet;
		this.item = item;
		IniFile ini = IniFile.getNewIniFileInstance("appdata/maps/" + originMapSet.getMapName() + ".map");
		IniFile ini2 = IniFile.getNewIniFileInstance("appdata/tileset/" + ini.read("SETUP", "Tiles") + ".tiles");
		for (String frameSet : Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet"))
		addNewFrameSetFromString(frameSet, ini2.read("CONFIG", frameSet));
		setFrameSet("BrickStandFrameSet");
		setPosition(tileX * Main.tileSize, tileY * Main.tileSize);
	}
	
	public static void addBrick(Brick brick)
		{ addBrick(brick, brick.getTileX(), brick.getTileY()); }

	public static void addBrick(Brick brick, int tileX, int tileY) {
		brick.setPosition(tileX * Main.tileSize, tileY * Main.tileSize);
		bricks.put(new TilePos(tileX, tileY), brick);
	}

	public static void addBrick(MapSet originMapSet, int tileX, int tileY, Item item) {
		Brick brick = new Brick(originMapSet, tileX, tileY, item);
		bricks.put(new TilePos(tileX, tileY), brick);
	}
	
	public static void removeBrick(Brick brick) {
		for (TilePos tilePos : bricks.keySet())
			if (bricks.get(tilePos) == brick) {
				bricks.remove(tilePos);
				return;
			}
	}
	
	public static void removeBrick(int tileX, int tileY) {
		for (Brick brick : bricks.values())
			if (brick.getTileX() == tileX && brick.getTileY() == tileY) {
				removeBrick(brick);
				return;
			}
	}
	
	public static void clearBricks()
		{ bricks.clear(); }
	
	public List<Brick> getBricks()
		{ return new ArrayList<>(bricks.values()); }
	
	public static void drawBricks(GraphicsContext gc) {
		for (Brick brick : bricks.values())
			brick.run(gc, false);
	}

	public static boolean haveBrickAt(int tileDX, int tileDY)
		{ return bricks.containsKey(new TilePos(tileDX, tileDY)); }
	
}
