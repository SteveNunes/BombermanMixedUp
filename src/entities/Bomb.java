package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import enums.BombType;
import enums.Curse;
import enums.TileProp;
import maps.MapSet;
import tools.Sound;

public class Bomb extends Entity {

	private static Map<TileCoord, Bomb> bombs = new HashMap<>();
	
	private Entity owner;
	BombType type;
	private int timer;
	private int fireDistance;
	
	public Bomb(Bomb bomb) {
		super(bomb);
		setTileSize(Main.tileSize);
		owner = bomb.owner;
		type = bomb.type;
		timer = bomb.timer;
		fireDistance = bomb.fireDistance;
	}

	public Bomb(TileCoord coord, BombType type, int fireDistance)
		{ this(null, coord, type, fireDistance); }
	
	public Bomb(Entity owner, TileCoord coord, BombType type, int fireDistance) {
		super();
		setTileSize(Main.tileSize);
		this.type = type;
		this.fireDistance = fireDistance;
		int y = 32 + 16 * type.getValue();
		timer = type == BombType.REMOTE || type == BombType.SPIKED_REMOTE ? -1 : 180;
		int ticksPerFrame = 17;
		if (owner instanceof BomberMan) {
			if (((BomberMan)owner).getCurses().contains(Curse.SLOW_EXPLODE_BOMB)) {
				ticksPerFrame = 25;
				if (timer != -1)
					timer = 270;
			}
			else if (((BomberMan)owner).getCurses().contains(Curse.FAST_EXPLODE_BOMB)) {
				ticksPerFrame = 8;
				if (timer != -1)
					timer = 90;
			}
		}
		String fragileGroundFrameSet = "{SetSprSource;MainSprites;64;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;0}|{SetSprIndex;1}|{SetSprIndex;2}|{SetSprIndex;3}|{Goto;0}";
		addNewFrameSetFromString("StandFrames", fragileGroundFrameSet);
		setFrameSet("StandFrames");
		setPosition(coord.getPosition(Main.tileSize));
	}

	public static void addBomb(TileCoord coord, BombType type, int fireDistance)
		{ addBomb(null, coord, type, fireDistance, false); }

	public static void addBomb(Entity owner, TileCoord coord, BombType type, int fireDistance)
		{ addBomb(owner, coord, type, fireDistance, false); }
	
	public static void addBomb(TileCoord coord, BombType type, int fireDistance, boolean checkTile)
		{ addBomb(null, coord, type, fireDistance, checkTile); }

	public static void addBomb(Entity owner, TileCoord coord, BombType type, int fireDistance, boolean checkTile) {
		if (!checkTile || MapSet.tileIsFree(coord))
			bombs.put(coord, new Bomb(owner, coord, type, fireDistance));
	}
	
	public static void addBomb(Bomb bomb)
		{ addBomb(bomb, false); }
	
	public static void addBomb(Bomb bomb, boolean checkTile) {
		if (!checkTile || MapSet.tileIsFree(bomb.getTileCoord()))
			bombs.put(bomb.getTileCoord(), bomb);
	}

	public static void removeBomb(Bomb bomb)
		{ bombs.remove(bomb.getTileCoord()); }
	
	public static void removeBomb(TileCoord coord) {
		if (haveBombAt(coord))
			bombs.remove(coord);
	}
	
	public static void clearBombs() {
		if (!bombs.isEmpty()) {
			Bomb bomb = null;
			while (!bombs.isEmpty()) {
				bomb = bombs.values().iterator().next();
				removeBomb(bomb.getTileCoord());
			}
			MapSet.getLayer(26).buildLayer();
		}
	}
	
	public static int totalBombs()
		{ return bombs.size(); }

	public static List<Bomb> getBombs()
		{ return new ArrayList<>(bombs.values()); }
	
	public static void drawBombs() {
		List<Bomb> removeBombs = new ArrayList<>();
		for (Bomb bomb : bombs.values()) {
			if ((bomb.timer == -1 || --bomb.timer > 0) && MapSet.tileContainsProp(bomb.getTileCoord(), TileProp.EXPLOSION))
				bomb.timer = 0;
			if (bomb.timer == 0) {
				Sound.playWav("Explosion" + (int)(bomb.fireDistance / 3));
				Explosion.addExplosion(bomb.owner, bomb.getTileCoord(), bomb.fireDistance, bomb.type == BombType.SPIKED || bomb.type == BombType.SPIKED_REMOTE);
				removeBombs.add(bomb);
			}
			else
				bomb.run();
		}
		removeBombs.forEach(bomb -> removeBomb(bomb));
	}
	
	public static boolean haveBombAt(TileCoord coord)
		{ return bombs.containsKey(coord); }

}