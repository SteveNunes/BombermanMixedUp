package maps;

import java.util.Arrays;

import entities.Entity;
import util.IniFile;

public class Brick extends Entity {

	private MapSet originMapSet;
	private Item item;

	public Brick(MapSet originMapSet, int x, int y) {
		this(originMapSet, x, y, null);
	}
	
	public Brick(MapSet originMapSet, int x, int y, Item item) {
		super();
		this.originMapSet = originMapSet;
		this.item = item;
		IniFile ini = IniFile.getNewIniFileInstance("appdata/maps/" + originMapSet.getMapName() + ".map");
		IniFile ini2 = IniFile.getNewIniFileInstance("appdata/tileset/" + ini.read("SETUP", "Tiles") + ".tiles");
		for (String frameSet : Arrays.asList("BrickStandFrameSet", "BrickBreakFrameSet", "BrickRegenFrameSet"))
		addNewFrameSetFromString(frameSet, ini2.read("CONFIG", frameSet));
		setFrameSet("BrickStandFrameSet");
		setPosition(x, y);
	}
	
}
