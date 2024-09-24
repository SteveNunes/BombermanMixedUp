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

	private static Map<TileCoord, List<Bomb>> bombs = new HashMap<>();
	private static List<Bomb> bombList = new ArrayList<>();
	
	private Entity owner;
	BombType type;
	private int timer;
	private int fireDistance;
	
	public Bomb(Bomb bomb) {
		super(bomb);
		owner = bomb.owner;
		type = bomb.type;
		timer = bomb.timer;
		fireDistance = bomb.fireDistance;
	}

	public Bomb(TileCoord coord, BombType type, int fireDistance)
		{ this(null, coord, type, fireDistance); }
	
	public Bomb(Entity owner, TileCoord coord, BombType type, int fireDistance) {
		super();
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
		if (!bombs.containsKey(coord))
			bombs.put(coord.getNewInstance(), new ArrayList<>());
		if (!checkTile || MapSet.tileIsFree(coord)) {
			Bomb bomb = new Bomb(owner, coord, type, fireDistance);
			bombs.get(coord).add(bomb);
			bombList.add(bomb);
		}
	}
	
	public static void addBomb(Bomb bomb)
		{ addBomb(bomb, false); }
	
	public static void addBomb(Bomb bomb, boolean checkTile)
		{ addBomb(bomb.owner, bomb.getTileCoord(), bomb.type, bomb.fireDistance, checkTile); }

	public static void removeBomb(Bomb bomb) {
		if (bombs.containsKey(bomb.getTileCoord())) {
			bombList.remove(bomb);
			if (bombList.isEmpty())
				bombs.remove(bomb.getTileCoord());
			else
				bombs.get(bomb.getTileCoord()).remove(bomb);
		}
	}
	
	public static void removeBomb(TileCoord coord) {
		if (bombs.containsKey(coord)) {
			bombList.removeAll(bombs.get(coord));
			bombs.remove(coord);
		}
	}
	
	public static void clearBombs() {
		bombs.clear();
		bombList.clear();
	}
	
	public static int totalBombs()
		{ return bombList.size(); }

	public static List<Bomb> getBombs()
		{ return bombList; }
	
	public static void drawBombs() {
		List<Bomb> removeBombs = new ArrayList<>();
		for (Bomb bomb : bombList) {
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

	public static Bomb getBombAt(TileCoord tileCoord)
		{ return haveBombAt(tileCoord) ? bombs.get(tileCoord).get(0) : null; }

}