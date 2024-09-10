package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.Main;
import entities.Entity;
import javafx.scene.canvas.GraphicsContext;
import util.IniFile;

public class Brick extends Entity {

	private static List<Brick> bricks = new ArrayList<>();
	private MapSet originMapSet;
	private Item item;

	public Brick(MapSet originMapSet, int tileX, int tileY) {
		this(originMapSet, tileX, tileY, null);
	}
	
	public Brick(MapSet originMapSet, int tileX, int tileY, Item item) {
		super();
		this.originMapSet = originMapSet;
		this.item = item;
		IniFile ini = IniFile.getNewIniFileInstance("appdata/maps/" + originMapSet.getMapName() + ".map");
		IniFile ini2 = IniFile.getNewIniFileInstance("appdata/tileset/" + ini.read("SETUP", "Tiles") + ".tiles");
		for (String frameSet : Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet"))
		addNewFrameSetFromString(frameSet, ini2.read("CONFIG", frameSet));
		setFrameSet("BrickStandFrameSet");
		setPosition(tileX * Main.tileSize, tileY * Main.tileSize);
	}

	public static void addBrick(MapSet originMapSet, int tileX, int tileY, Item item) {
		Brick brick = new Brick(originMapSet, tileX, tileY, item);
		bricks.add(brick);
	}
	
	public static void removeBrick(Brick brick)
		{ bricks.remove(brick); }
	
	public static void removeBrick(int brickIndex)
		{ bricks.remove(brickIndex); }
	
	public static void removeBrick(int tileX, int tileY) {
		for (Brick brick : bricks)
			if (brick.getTileX() == tileX && brick.getTileY() == tileY) {
				removeBrick(brick);
				return;
			}
	}
	
	public static void clearBricks()
		{ bricks.clear(); }
	
	public static void drawBricks(GraphicsContext gc) {
		for (Brick brick : bricks)
			brick.run(gc, false);
	}
	
}
