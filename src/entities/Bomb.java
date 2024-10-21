package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import enums.BombType;
import enums.Curse;
import enums.ItemType;
import enums.TileProp;
import maps.Item;
import maps.MapSet;
import tools.Sound;

public class Bomb extends Entity {

	private static Map<TileCoord, List<Bomb>> bombs = new HashMap<>();
	private static List<Bomb> bombList = new ArrayList<>();
	
	private boolean nesBomb;
	private Entity owner;
	BombType type;
	private int timer;
	private int fireDistance;
	private boolean ownerIsOver;
	private boolean isActive;
	
	public Bomb(Bomb bomb) {
		super(bomb);
		owner = bomb.owner;
		type = bomb.type;
		timer = bomb.timer;
		fireDistance = bomb.fireDistance;
		ownerIsOver = bomb.ownerIsOver;
		nesBomb = bomb.nesBomb;
		isActive = bomb.isActive;
	}

	public Bomb(TileCoord coord, BombType type, int fireDistance)
		{ this(null, coord, type, fireDistance); }
	
	public Bomb(Entity owner, TileCoord coord, BombType type, int fireDistance) {
		super();
		isActive = true;
		nesBomb = owner instanceof BomberMan && ((BomberMan)owner).getBomberIndex() == 0;
		this.type = type;
		this.fireDistance = fireDistance;
		this.owner = owner;
		int y = nesBomb ? 32 : 32 + 16 * type.getValue();
		timer = type == BombType.REMOTE || type == BombType.SPIKED_REMOTE ? -1 : 180;
		int ticksPerFrame = 17;
		ownerIsOver = owner != null;
		if (owner != null) {
			if (owner instanceof BomberMan) {
				if (((BomberMan)owner).getCurse() == Curse.SLOW_EXPLODE_BOMB) {
					ticksPerFrame = 25;
					if (timer != -1)
						timer = 270;
				}
				else if (((BomberMan)owner).getCurse() == Curse.FAST_EXPLODE_BOMB) {
					ticksPerFrame = 8;
					if (timer != -1)
						timer = 90;
				}
			}
		}
		String fragileGroundFrameSet = "{SetSprSource;MainSprites;64;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;0}|{SetSprIndex;1}|{SetSprIndex;2}|{SetSprIndex;3}|{Goto;0}";
		addNewFrameSetFromString("StandFrames", fragileGroundFrameSet);
		setFrameSet("StandFrames");
		setPosition(coord.getPosition(Main.TILE_SIZE));
	}
	
	public boolean isNesBomb()
		{ return nesBomb; }
	
	public boolean isActive()
		{ return isActive; }

	public static Bomb addBomb(TileCoord coord, BombType type, int fireDistance)
		{ return addBomb(null, coord, type, fireDistance, false); }

	public static Bomb addBomb(Entity owner, TileCoord coord, BombType type, int fireDistance)
		{ return addBomb(owner, coord, type, fireDistance, false); }
	
	public static Bomb addBomb(TileCoord coord, BombType type, int fireDistance, boolean checkTile)
		{ return addBomb(null, coord, type, fireDistance, checkTile); }

	public static Bomb addBomb(Entity owner, TileCoord coord, BombType type, int fireDistance, boolean checkTile) {
		if (!bombs.containsKey(coord))
			bombs.put(coord.getNewInstance(), new ArrayList<>());
		if (!checkTile || MapSet.tileIsFree(owner, coord)) {
			Bomb bomb = new Bomb(owner, coord, type, fireDistance);
			bombs.get(coord).add(bomb);
			bombList.add(bomb);
			return bomb;
		}
		return null;
	}
	
	public static void addBomb(Bomb bomb)
		{ addBomb(bomb, false); }
	
	public static void addBomb(Bomb bomb, boolean checkTile)
		{ addBomb(bomb.owner, bomb.getTileCoord(), bomb.type, bomb.fireDistance, checkTile); }

	public static void removeBomb(Bomb bomb) {
		if (bombs.containsKey(bomb.getTileCoord())) {
			bombs.get(bomb.getTileCoord()).remove(bomb);
			if (bombs.get(bomb.getTileCoord()).isEmpty())
				bombs.remove(bomb.getTileCoord());
			bombList.remove(bomb);
		}
	}
	
	public static void removeBomb(TileCoord coord) {
		List<Bomb> list = new ArrayList<>(bombs.get(coord));
		for (Bomb bomb : list)
			removeBomb(bomb);
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
			if (bomb.owner != null && bomb.ownerIsOver) {
				if (!bomb.getTileCoord().equals(bomb.owner.getTileCoord()))
					bomb.ownerIsOver = false;
			}
			if ((bomb.timer == -1 || --bomb.timer > 0) && MapSet.tileContainsProp(bomb.getTileCoord(), TileProp.DAMAGE_BOMB))
				bomb.timer = 0;
			if (bomb.timer == 0) {
				bomb.isActive = false;
				Sound.playWav("explosion/Explosion" + (bomb.nesBomb ? "" : bomb.fireDistance < 3 ? "1" : (int)(bomb.fireDistance / 3)));
				Explosion.addExplosion(bomb, bomb.getTileCoord(), bomb.fireDistance, bomb.type == BombType.SPIKED || bomb.type == BombType.SPIKED_REMOTE);
				removeBombs.add(bomb);
			}
			else
				bomb.run();
		}
		removeBombs.forEach(bomb -> removeBomb(bomb));
	}
	
	public static boolean haveBombAt(Entity entity, TileCoord coord) {
		if (bombs.containsKey(coord)) {
			Bomb bomb = getBombAt(coord);
			return bomb.owner == null || entity == null || bomb.owner != entity || !bomb.ownerIsOver;
		}
		return false;
	}

	public static Bomb getBombAt(TileCoord tileCoord)
		{ return bombs.containsKey(tileCoord) ? bombs.get(tileCoord).get(0) : null; }
	
	public static BombType getBombTypeFromItem(Item item) {
		if (item.getItemType() == ItemType.FOLLOW_BOMB)
			return BombType.FOLLOW;
		if (item.getItemType() == ItemType.HEART_BOMB)
			return BombType.HEART;
		if (item.getItemType() == ItemType.MAGMA_BOMB)
			return BombType.MAGMA;
		if (item.getItemType() == ItemType.LAND_MINE_BOMB)
			return BombType.LAND_MINE;
		if (item.getItemType() == ItemType.MAGNET_BOMB)
			return BombType.MAGNET;
		if (item.getItemType() == ItemType.SPIKE_BOMB)
			return BombType.SPIKED;
		if (item.getItemType() == ItemType.REMOTE_BOMB)
			return BombType.REMOTE;
		if (item.getItemType() == ItemType.SPIKE_REMOTE_BOMB)
			return BombType.SPIKED_REMOTE;
		if (item.getItemType() == ItemType.RUBBER_BOMB)
			return BombType.RUBBER;
		if (item.getItemType() == ItemType.SENSOR_BOMB)
			return BombType.SENSOR;
		return BombType.NORMAL;
	}

}