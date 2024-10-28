package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.Main;
import enums.BombType;
import enums.Curse;
import enums.Direction;
import enums.GameInputs;
import enums.ItemType;
import enums.PassThrough;
import enums.TileProp;
import frameset.Tags;
import javafx.scene.canvas.GraphicsContext;
import maps.Item;
import maps.MapSet;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import tools.GameConfigs;
import tools.IniFiles;
import tools.Sound;

public class BomberMan extends Entity {
	
	private Curse curse;
	private int curseDuration = Curse.getDuration(curse);;
	private int fireRange;
	private int maxBombs;
	private int bomberIndex;
	private int palleteIndex;
	private String setBombSound;
	private List<ItemType> gotItems;
	private List<Bomb> bombs;
	private List<Direction> pressedDirs;
	private Set<GameInputs> holdedInputs;
	private List<GameInputs> queuedInputs;
	private int bombCd;
	private int score;
	private int addedScore;
	private int lives;

	public BomberMan(int bomberIndex, int palleteIndex) {
		super();
		this.bomberIndex = bomberIndex;
		this.palleteIndex = palleteIndex;
		pressedDirs = new ArrayList<>();
		holdedInputs = new HashSet<>();
		queuedInputs = new ArrayList<>();
		bombs = new ArrayList<>();
		gotItems = new ArrayList<>();
		bombCd = 0;
		score = 0;
		addedScore = 0;
		curseDuration = 0;
		lives = GameConfigs.STARTING_LIVES;
		setHitPoints(1);
		String section = "" + bomberIndex;
		updateStatusByItems();
		if (IniFiles.characters.read(section, "DefaultTags") != null)
			setDefaultTags(Tags.loadTagsFromString(IniFiles.characters.read(section, "DefaultTags")));
		for (String item : IniFiles.characters.getItemList(section)) {
			if (item.length() > 9 && item.substring(0, 9).equals("FrameSet."))
				addNewFrameSetFromString(item.substring(9), IniFiles.characters.read(section, item));
		}
		setBombSound = IniFiles.characters.read(section, "SetBombSound");
		if (setBombSound == null)
			setBombSound = "SetBomb";
		setFrameSet("Stand");
		if (IniFiles.characters.read(section, "DefaultStartTags") != null) {
			Tags tags = Tags.loadTagsFromString(IniFiles.characters.read(section, "DefaultStartTags"));
			tags.setRootSprite(getCurrentFrameSet().getSprite(0));
			tags.run();
		}
	}
	
	public int getBomberIndex()
		{ return bomberIndex; }

	public void setBomberIndex(int bomberIndex)
		{ this.bomberIndex = bomberIndex; }

	public int getPalleteIndex()
		{ return palleteIndex; }

	public void setPalleteIndex(int palleteIndex)
		{ this.palleteIndex = palleteIndex; }

	public boolean isPressed(GameInputs input)
		{ return holdedInputs.contains(input); }

	public void keyPress(GameInputs input) {
		if (isBlockedMovement()) {
			queuedInputs.add(input);
			return;
		}
		if (input == GameInputs.A) {
			bombs.sort((b1, b2) -> (int)(b1.getSetTime() - b2.getSetTime()));
			for (Bomb bomb : bombs)
				if (bomb.getBombType() == BombType.REMOTE || bomb.getBombType() == BombType.SPIKED_REMOTE) {
					bomb.detonate();
					return;
				}
		}
		else if (input == GameInputs.C) {
			bombs.sort((b1, b2) -> (int)(b1.getSetTime() - b2.getSetTime()));
			for (Bomb bomb : bombs)
				if (bomb.getPushEntity() != null) {
					bomb.stopKick();
					return;
				}
			TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(getDirection());
			while (haveItem(ItemType.LINED_BOMBS) && MapSet.tileIsFree(coord) && !Item.haveItemAt(coord)) {
				setBomb(true, coord);
				coord.incCoordsByDirection(getDirection());
			}
		}
		else {
			Direction dir = input.getDirection();
			if (dir != null && !pressedDirs.contains(dir)) {
				if (!pressedDirs.isEmpty() && (isPerfectlyBlockedDir(getDirection()) || pressedDirs.get(0).getReverseDirection() == dir))
					pressedDirs.add(0, dir);
				else
					pressedDirs.add(dir);
			}
			holdedInputs.add(input);
		}
	}
	
	public void keyRelease(GameInputs input) {
		if (input.isDirection())
			pressedDirs.removeAll(Arrays.asList(input.getDirection()));
		holdedInputs.remove(input);
	}

	@Override
	public void run()
		{ run(null, false); }
	
	@Override
	public void run(boolean isPaused)
		{ run(null, isPaused); }
	
	@Override
	public void run(GraphicsContext gc)
		{ run(gc, false); }
	
	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		bombCd--;
		for (int n = 0; n < bombs.size(); n++)
			if (!bombs.get(n).isActive())
				bombs.remove(n--);
		if (curse != null && --curseDuration == 0)
			removeCurse();
		if (MapSet.tileContainsProp(getTileCoordFromCenter(), TileProp.DAMAGE_PLAYER))
			takeDamage();
		super.run(gc, isPaused);
		if (!isBlockedMovement()) {
			if (!queuedInputs.isEmpty()) {
				List<GameInputs> list = new ArrayList<>(queuedInputs);
				queuedInputs.clear();
				list.forEach(i -> keyPress(i));
			}
			if (pressedDirs.isEmpty()) {
				setElapsedSteps(0);
				setFrameSet("Stand");
			}
			else {
				Direction dir = pressedDirs.get(0); 
				setFrameSet("Moving");
				setDirection(curse == Curse.REVERSED ? dir.getReverseDirection() : dir);
			}
			if (holdedInputs.contains(GameInputs.B)) {
				if (currentFrameSetNameIsEqual("Moving")) { // Definir a coordenada um pouco mais para as costas se ta soltando a bomba enquanto esta andando, pra evitar q a proxima bomba saia na sua frente
					Position pos = new Position((int)getX() + Main.TILE_SIZE / 2, (int)getY() + Main.TILE_SIZE / 2);
					pos.incPositionByDirection(getDirection().getReverseDirection(), Main.TILE_SIZE / 4);
					setBomb(pos.getTileCoord());
				}
				else
					setBomb();
			}
			if (tileWasChanged()) {
				if (pressedDirs.size() > 1) {
					Direction dir = pressedDirs.get(1);
					if (tileIsFree(dir)) {
						dir = pressedDirs.get(0); 
						pressedDirs.remove(0);
						pressedDirs.add(dir);
					}
				}
				MapSet.checkTileTrigger(this, getTileCoordFromCenter(), TileProp.TRIGGER_BY_PLAYER);
				MapSet.checkTileTrigger(this, getPreviewTileCoord(), TileProp.TRIGGER_BY_PLAYER, true);
				if (Item.haveItemAt(getTileCoordFromCenter()))
					pickItem(Item.getItemAt(getTileCoordFromCenter()));
			}
			TileCoord frontTile = getTileCoord().getNewInstance().incCoordsByDirection(getDirection());
			if (getPushingValue() > 5 && haveItem(ItemType.KICK_BOMB) && Bomb.haveBombAt(this, frontTile))
				Bomb.getBombAt(frontTile).kick(getDirection(), 4);
		}
	}
	
	public boolean haveItem(ItemType itemType)
		{ return gotItems.contains(itemType); }
	
	public void addScore(int score)
		{ addedScore = score; }

	public Bomb setBomb()
		{ return setBomb(false, getTileCoordFromCenter()); }

	public Bomb setBomb(boolean noCd)
		{ return setBomb(false, getTileCoordFromCenter()); }

	public Bomb setBomb(TileCoord coord)
		{ return setBomb(false, coord); }
	
	public Bomb setBomb(boolean noCd, TileCoord coord) {
		if ((noCd || bombCd <= 0) && bombs.size() < maxBombs) {
			BombType type = !gotItems.isEmpty() && gotItems.get(0).isBomb() ?
											ItemType.getBombTypeFromItemType(gotItems.get(0)) : BombType.NORMAL;
			for (Bomb bomb : bombs) {
				if (bomb.getBombType().isUnique())
					type = BombType.NORMAL;
			}
			Bomb bomb = Bomb.addBomb(this, coord, type, fireRange, true);
			if (bomb != null) {
				bombs.add(bomb);
				Sound.playWav(setBombSound);
				bombCd = 5;
				return bomb;
			}
		}
		return null;
	}

	public void pickItem(Item item) {
		item.pick();
		ItemType type = item.getItemType();
		if (item.isCurse())
			setCurse(item.getCurse());
		else if (type == ItemType.ARMOR) {
			setInvencibleFrames(3000);
			setBlinkingFrames(3000);
		}
		else if (type == ItemType.EXTRA_LIVE)
			incLives();
		else if (type == ItemType.HEART_UP)
			incHitPoints();
		else {
			if (item.getItemType().isBomb()) {
				if (!gotItems.isEmpty()) {
					if (gotItems.get(0).isBomb())
						gotItems.set(0, type);
					else
						gotItems.add(0, type);
				}
				else
					gotItems.add(type);
			}
			else
				gotItems.add(type);
		}
		updateStatusByItems();
	}

	public void updateStatusByItems() {
		fireRange = GameConfigs.STARTING_FIRE;
		maxBombs = GameConfigs.STARTING_BOMBS;
		double speed = GameConfigs.INITIAL_PLAYER_SPEED;
		clearPassThrough();
		getPassThrough().addAll(Arrays.asList(PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER));
		for (ItemType type : gotItems) {
			if (type == ItemType.BOMB_UP)
				maxBombs++;
			else if (type == ItemType.FIRE_MAX)
				fireRange = GameConfigs.MAX_EXPLOSION_DISTANCE;
			else if (type == ItemType.SPEED_UP && (speed += 0.2f) > GameConfigs.MAX_PLAYER_SPEED)
				speed = GameConfigs.MAX_PLAYER_SPEED;
			else if (type == ItemType.SPEED_DOWN && (speed -= 0.2f) < GameConfigs.MIN_PLAYER_SPEED)
				speed = GameConfigs.MIN_PLAYER_SPEED;
			else if (type == ItemType.FIRE_UP && fireRange < GameConfigs.MAX_EXPLOSION_DISTANCE)
				fireRange++;
			else if (type == ItemType.PASS_BOMB)
				setPassThroughBomb(true);
			else if (type == ItemType.PASS_BRICK)
				setPassThroughBrick(true);
		};
		setSpeed(speed);
		setCurse();
	}

	public void removeCurse() {
		if (curse == Curse.INVISIBLE)
			setVisible(true);
		curse = null;
		updateStatusByItems();
	}
	
	public Curse getCurse()
		{ return curse; }
	
	public void setCurse()
		{ setCurse(null); }
	
	public void setCurse(Curse curse) { // FALTA: Implementar BLINDNESS e SWAP_PLAYERS
		if (curse != null) {
			this.curse = curse;
			curseDuration = Curse.getDuration(curse);
		}
		else if ((curse = getCurse()) != null) {
			if (curse == Curse.MIN_BOMB)
				maxBombs = 1;
			else if (curse == Curse.MIN_FIRE)
				fireRange = 1;
			else if (curse == Curse.INVISIBLE)
				setVisible(false);
			else if (curse == Curse.MIN_SPEED)
				setTempSpeed(0.25);
			else if (curse == Curse.NO_BOMB)
				maxBombs = 0;
			else if (curse == Curse.ULTRA_SPEED)
				setTempSpeed(GameConfigs.MAX_PLAYER_SPEED);
			else if (curse == Curse.INVISIBLE)
				setVisible(false);
		}
	}

	public int getLives()
		{ return lives; }
	
	public void setLives(int lives)
		{ this.lives = lives; }
	
	public void decLives()
		{ incLives(-1); }

	public void incLives()
		{ incLives(1); }
	
	public void incLives(int value)
		{ lives += value; }

}
