package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Main;
import enums.BombType;
import enums.Curse;
import enums.TileProp;
import javafx.scene.canvas.GraphicsContext;
import maps.MapSet;
import objmoveutils.TileCoord;
import tools.GameConfigs;
import tools.Sound;

public class Bomb extends Entity {

	private static Map<TileCoord, List<Bomb>> bombs = new HashMap<>();
	private static List<Bomb> bombList = new ArrayList<>();
	
	private boolean nesBomb;
	private Entity owner;
	private BombType type;
	private int timer;
	private int fireDistance;
	private boolean ownerIsOver;
	private boolean isActive;
	private boolean isStucked;
	
	public Bomb(Bomb bomb) {
		super(bomb);
		owner = bomb.owner;
		type = bomb.type;
		timer = bomb.timer;
		fireDistance = bomb.fireDistance;
		ownerIsOver = bomb.ownerIsOver;
		nesBomb = bomb.nesBomb;
		isActive = bomb.isActive;
		isStucked = bomb.isStucked;
	}

	public Bomb(TileCoord coord, BombType type, int fireDistance)
		{ this(null, coord, type, fireDistance); }
	
	public Bomb(Entity owner, TileCoord coord, BombType type, int fireDistance) {
		super();
		isActive = true;
		isStucked = false;
		nesBomb = type == BombType.NES || (owner instanceof BomberMan && ((BomberMan)owner).getBomberIndex() == 0);
		if (type == BombType.P)
			fireDistance = GameConfigs.MAX_EXPLOSION_DISTANCE;
		this.type = type;
		this.fireDistance = fireDistance;
		this.owner = owner;
		int y = nesBomb ? 32 : 32 + 16 * type.getValue();
		timer = type == BombType.REMOTE || type == BombType.SPIKED_REMOTE ? -1 : 180;
		int ticksPerFrame = type == BombType.LAND_MINE ? 3 : 16;
		ownerIsOver = owner != null;
		if (owner != null) {
			if (owner instanceof BomberMan) {
				if (((BomberMan)owner).getCurse() == Curse.SLOW_EXPLODE_BOMB) {
					ticksPerFrame *= 2;
					if (timer != -1)
						timer = 270;
				}
				else if (((BomberMan)owner).getCurse() == Curse.FAST_EXPLODE_BOMB) {
					ticksPerFrame /= 2;
					if (timer != -1)
						timer = 90;
				}
			}
		}
		if (type == BombType.LAND_MINE) {
			String frameSet = "{SetSprSource;MainSprites;64;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;0},{PlayWav;Mine}|{SetSprIndex;-}|{SetSprIndex;1}|{SetSprIndex;-}|{SetSprIndex;2}|{SetSprIndex;-}|{SetSprIndex;3}|{SetSprIndex;-}|{SetSprIndex;0}|{Goto;1;1}|{SetFrameSet;LandedFrames}";
			addNewFrameSetFromString("LandingFrames", frameSet);
			setFrameSet("LandingFrames");
			frameSet = "{SetSprSource;MainSprites;64;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;-}|{}|{Goto;-1}";
			addNewFrameSetFromString("LandedFrames", frameSet);
			frameSet = "{SetSprSource;MainSprites;64;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;0},{PlayWav;Mine}|{SetSprIndex;-}|{SetSprIndex;1}|{SetSprIndex;-}|{SetSprIndex;2}|{SetSprIndex;-}|{SetSprIndex;3}|{SetSprIndex;-}|{SetSprIndex;0}|{Goto;1;1}|{ExplodeBomb}";
			addNewFrameSetFromString("UnlandingFrames", frameSet);
		}
		else {
			String frameSet = "{SetSprSource;MainSprites;64;" + y + ";16;16;0;0;0;0;16;16},{SetTicksPerFrame;" + ticksPerFrame + "},{SetSprIndex;0}|{SetSprIndex;1}|{SetSprIndex;2}|{SetSprIndex;3}|{Goto;0}";
			addNewFrameSetFromString("StandFrames", frameSet);
			setFrameSet("StandFrames");
		}
		setPosition(coord.getPosition());
	}
	
	public boolean isStucked()
		{ return isStucked; }
	
	public void setStucked(boolean state)
		{ isStucked = state; }
	
	public boolean isNesBomb()
		{ return nesBomb; }
	
	public boolean isActive()
		{ return isActive; }
	
	public BombType getBombType()
		{ return type; }

	public static Bomb addBomb(TileCoord coord, BombType type, int fireDistance)
		{ return addBomb(null, coord, type, fireDistance, false); }

	public static Bomb addBomb(Entity owner, TileCoord coord, BombType type, int fireDistance)
		{ return addBomb(owner, coord, type, fireDistance, false); }
	
	public static Bomb addBomb(TileCoord coord, BombType type, int fireDistance, boolean checkTile)
		{ return addBomb(null, coord, type, fireDistance, checkTile); }

	public static Bomb addBomb(Entity owner, TileCoord coord, BombType type, int fireDistance, boolean checkTile) {
		if (!checkTile || (MapSet.tileIsFree(coord) && !MapSet.getTileProps(coord).contains(TileProp.GROUND_NO_BOMB))) {
			Bomb bomb = new Bomb(owner, coord, type, fireDistance);
			if (!bombs.containsKey(coord))
				bombs.put(coord.getNewInstance(), new ArrayList<>());
			bombs.get(coord).add(bomb);
			bombList.add(bomb);
			MapSet.checkTileTrigger(bomb, coord, TileProp.TRIGGER_BY_BOMB);
			MapSet.checkTileTrigger(bomb, coord, TileProp.TRIGGER_BY_STOPPED_BOMB);
			return bomb;
		}
		return null;
	}
	
	public static void addBomb(Bomb bomb)
		{ addBomb(bomb, false); }
	
	public static void addBomb(Bomb bomb, boolean checkTile)
		{ addBomb(bomb.owner, bomb.getTileCoordFromCenter(), bomb.type, bomb.fireDistance, checkTile); }

	public static void removeBomb(Bomb bomb) {
		if (bombs.containsKey(bomb.getTileCoordFromCenter())) {
			bombs.get(bomb.getTileCoordFromCenter()).remove(bomb);
			if (bombs.get(bomb.getTileCoordFromCenter()).isEmpty())
				bombs.remove(bomb.getTileCoordFromCenter());
		}
		bombList.remove(bomb);
	}
	
	public static void removeBomb(TileCoord coord) {
		bombList.removeAll(bombs.get(coord));
		bombs.remove(coord);
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
		List<Bomb> bombs = new ArrayList<>(bombList);
		for (Bomb bomb : bombs) {
			if (bomb.type == BombType.LAND_MINE) {
				bomb.run();
				if (bomb.currentFrameSetNameIsEqual("LandedFrames") && Entity.haveAnyEntityAtCoord(bomb.getTileCoordFromCenter()))
					bomb.setFrameSet("UnlandingFrames");
				continue;
			}
			if (bomb.owner != null && bomb.ownerIsOver) {
				int x = (int)bomb.owner.getX() + Main.TILE_SIZE / 2, y = (int)bomb.owner.getY() + Main.TILE_SIZE / 2,
						xx = (int)bomb.getX() + Main.TILE_SIZE / 2, yy = (int)bomb.getY() + Main.TILE_SIZE / 2;
				if (x <= xx - Main.TILE_SIZE / 2 || x >= xx + Main.TILE_SIZE / 2 ||
						y <= yy - Main.TILE_SIZE / 2 || y >= yy + Main.TILE_SIZE / 2)
							bomb.ownerIsOver = false;
			}
			if (bomb.type != BombType.REMOTE && bomb.type != BombType.SPIKED_REMOTE)
				bomb.timer--;
			if ((bomb.timer == -1 || bomb.timer > 0) && MapSet.tileContainsProp(bomb.getTileCoordFromCenter(), TileProp.DAMAGE_BOMB))
				bomb.timer = 0;
			if (bomb.timer == 0)
				bomb.explode();
			else
				bomb.run();
		}
	}

	public void explode() {
		isActive = false;
		centerToTile();
		unsetPushEntity();
		Sound.playWav("explosion/Explosion" + (nesBomb ? "" : fireDistance < 3 ? "1" : (int)(fireDistance / 3)));
		Explosion.addExplosion(this, getTileCoordFromCenter(), fireDistance, type == BombType.SPIKED || type == BombType.SPIKED_REMOTE);
		removeBomb(this);
	}

	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		TileCoord prevCoord = getTileCoordFromCenter().getNewInstance();
		if (bombs.containsKey(prevCoord)) {
			bombs.get(prevCoord).remove(this);
			if (bombs.get(prevCoord).isEmpty())
				bombs.remove(prevCoord);
		}
		super.run(gc, isPaused);
		TileCoord coord = getTileCoordFromCenter();
		if (isActive()) {
			if (!bombs.containsKey(coord))
				bombs.put(coord, new ArrayList<>());
			bombs.get(coord).add(this);
			if (tileWasChanged()) {
				MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_BOMB);
				MapSet.checkTileTrigger(this, prevCoord, TileProp.TRIGGER_BY_BOMB, true);
			}
		}
		if (isActive() && getSpeed() == 0 && getPushEntity() == null) {
			MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_STOPPED_BOMB);
			MapSet.checkTileTrigger(this, prevCoord, TileProp.TRIGGER_BY_STOPPED_BOMB, true);
		}
	}
	
	public static boolean haveBombAt(Entity entity, TileCoord coord) {
		if (bombs.containsKey(coord)) {
			Bomb bomb = getBombAt(coord);
			return bomb.owner == null || entity == null || bomb.owner != entity || !bomb.ownerIsOver;
		}
		return false;
	}
	
	public static Bomb getBombAt(TileCoord tileCoord)
		{ return bombs.containsKey(tileCoord) && !bombs.get(tileCoord).isEmpty() ? bombs.get(tileCoord).get(0) : null; }

}