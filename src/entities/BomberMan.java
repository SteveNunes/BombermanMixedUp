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
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.JumpMove;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import tools.GameConfigs;
import tools.IniFiles;
import tools.Sound;
import util.CollectionUtils;
import util.MyMath;

public class BomberMan extends Entity {

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
	private int player;
	private String nameSound;

	public BomberMan(int player, int bomberIndex, int palleteIndex) {
		super();
		this.bomberIndex = bomberIndex;
		this.palleteIndex = palleteIndex;
		this.player = player;
		pressedDirs = new ArrayList<>();
		holdedInputs = new HashSet<>();
		queuedInputs = new ArrayList<>();
		bombs = new ArrayList<>();
		gotItems = new ArrayList<>();
		bombCd = 0;
		score = 0;
		addedScore = 0;
		lives = GameConfigs.STARTING_LIVES;
		setHitPoints(1);
		String section = "" + bomberIndex;
		updateStatusByItems();
		nameSound = IniFiles.characters.read(section, "NameSound");
		if (nameSound != null && nameSound.equals("-"))
			nameSound = null;
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
		setOnFrameSetEndsEvent(e -> changeToStandFrameSet());
		setPassThroughs(true, PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER);
	}

	public int getPlayer() {
		return player;
	}

	public String getNameSound() {
		return nameSound;
	}

	public String getSoundByName(String soundName) {
		return "/voices/" + nameSound + soundName;
	}

	public int getBomberIndex() {
		return bomberIndex;
	}

	public void setBomberIndex(int bomberIndex) {
		this.bomberIndex = bomberIndex;
	}

	public int getPalleteIndex() {
		return palleteIndex;
	}

	public void setPalleteIndex(int palleteIndex) {
		this.palleteIndex = palleteIndex;
	}

	public boolean isPressed(GameInputs input) {
		return holdedInputs.contains(input);
	}

	public void keyPress(GameInputs input) {
		if (holdedInputs.contains(input))
			return;
		if (isBlockedMovement()) {
			queuedInputs.add(input);
			return;
		}
		if (input == GameInputs.A) {
			bombs.sort((b1, b2) -> (int) (b1.getSetTime() - b2.getSetTime()));
			for (Bomb bomb : bombs)
				if (bomb.getBombType() == BombType.REMOTE || bomb.getBombType() == BombType.SPIKED_REMOTE) {
					bomb.detonate();
					return;
				}
		}
		else if (input == GameInputs.B) {
			if (getHoldingEntity() == null && haveFrameSet("HoldingStart")) {
				if (haveItem(ItemType.POWER_GLOVE) || haveItem(ItemType.HYPER_GLOVE)) {
					for (Entity entity : Entity.getEntityListFromCoord(getTileCoordFromCenter()))
						if (entity != this && !entity.isBlockedMovement())
							setFrameSet("HoldingStart");
					if (getHoldingEntity() == null && Bomb.haveBombAt(getTileCoordFromCenter()))
						setFrameSet("HoldingStart");
				}
				if (getHoldingEntity() == null && haveItem(ItemType.HYPER_GLOVE) && Brick.haveBrickAt(getTileCoordFromCenter()))
					setFrameSet("HoldingStart");
			}
		}
		else if (input == GameInputs.C) {
			bombs.sort((b1, b2) -> (int) (b1.getSetTime() - b2.getSetTime()));
			for (Bomb bomb : bombs)
				if (bomb.getPushEntity() != null) {
					bomb.stopKick();
					return;
				}
			TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(getDirection());
			if (haveItem(ItemType.LINED_BOMBS) && MapSet.tileIsFree(coord) && !Item.haveItemAt(coord)) {
				while (MapSet.tileIsFree(coord) && !Item.haveItemAt(coord)) {
					setBomb(true, coord);
					coord.incCoordsByDirection(getDirection());
				}
				return;
			}
			if ((haveItem(ItemType.PUNCH_BOMB) || haveItem(ItemType.HYPER_PUNCH)) && Bomb.haveBombAt(this, coord) && haveFrameSet("PunchBomb"))
				setFrameSet("PunchBomb");
			else if (haveItem(ItemType.HYPER_PUNCH) && Brick.haveBrickAt(coord) && haveFrameSet("PunchBomb"))
				setFrameSet("PunchBrick");
			else if (haveItem(ItemType.PUSH_POWER) && Bomb.haveBombAt(this, coord) && haveFrameSet("PushPower"))
				setFrameSet("PushPower");
		}
		else {
			Direction dir = input.getDirection();
			if (dir != null && !pressedDirs.contains(dir)) {
				if (!pressedDirs.isEmpty() && (isPerfectlyBlockedDir(getDirection()) || pressedDirs.get(0).getReverseDirection() == dir))
					pressedDirs.add(0, dir);
				else
					pressedDirs.add(dir);
			}
		}
		holdedInputs.add(input);
	}

	public void keyRelease(GameInputs input) {
		if (input.isDirection())
			pressedDirs.removeAll(Arrays.asList(input.getDirection()));
		holdedInputs.remove(input);
		queuedInputs.remove(input);
	}

	@Override
	public void run() {
		run(null, false);
	}

	@Override
	public void run(boolean isPaused) {
		run(null, isPaused);
	}

	@Override
	public void run(GraphicsContext gc) {
		run(gc, false);
	}

	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		bombCd--;
		for (int n = 0; n < bombs.size(); n++)
			if (!bombs.get(n).isActive())
				bombs.remove(n--);
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
				changeToStandFrameSet();
			}
			else {
				Direction dir = pressedDirs.get(0);
				setDirection(getCurse() == Curse.REVERSED ? dir.getReverseDirection() : dir);
				changeToMovingFrameSet();
			}
			if (!holdedInputs.contains(GameInputs.B) && (currentFrameSetNameIsEqual("HoldingStand") || currentFrameSetNameIsEqual("HoldingMoving")))
				setFrameSet("Release");
			if (holdedInputs.contains(GameInputs.B)) {
				if (currentFrameSetNameIsEqual("Moving")) { // Definir a coordenada um pouco mais para as costas se ta soltando a bomba
				                                            // enquanto esta andando, pra evitar q a proxima bomba saia na sua frente
					Position pos = new Position((int) getX() + Main.TILE_SIZE / 2, (int) getY() + Main.TILE_SIZE / 2);
					pos.incPositionByDirection(getDirection().getReverseDirection(), Main.TILE_SIZE / 4);
					setBomb(pos.getTileCoord());
				}
				else if (getHoldingEntity() == null)
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
			}
			TileCoord frontTile = getTileCoord().getNewInstance().incCoordsByDirection(getDirection());
			if (getPushingValue() > 5 && haveItem(ItemType.KICK_BOMB) && Bomb.haveBombAt(this, frontTile)) {
				TileCoord nextCoord = frontTile.getNewInstance().incCoordsByDirection(getDirection());
				if (MapSet.tileIsFree(nextCoord, Set.of()))
					Bomb.getBombAt(frontTile).kick(getDirection(), 4);
			}
		}
		if (!getCurrentFrameSet().isRunning())
			changeToStandFrameSet();

	}

	private String getDefaultStandFrameSet() {
		if (getHoldingEntity() != null)
			return "HoldingStand";
		else if (getHolder() != null)
			return "BeingHolded";
		else
			return "Stand";
	}

	private void changeToStandFrameSet() {
		setBlockedMovement(false);
		setFrameSet(getDefaultStandFrameSet());
		if (tileWasChanged())
			setTileWasChanged(true);
	}

	private String getDefaultMovingFrameSet() {
		if (getHoldingEntity() != null)
			return "HoldingMoving";
		else if (getHolder() != null)
			return "BeingHolded";
		else
			return "Moving";
	}

	private void changeToMovingFrameSet() {
		setBlockedMovement(false);
		setFrameSet(getDefaultMovingFrameSet());
		if (tileWasChanged())
			setTileWasChanged(true);
	}

	public boolean haveItem(ItemType itemType) {
		return gotItems.contains(itemType);
	}

	public void addScore(int score) {
		addedScore = score;
	}

	public Bomb setBomb() {
		return setBomb(false, getTileCoordFromCenter());
	}

	public Bomb setBomb(boolean noCd) {
		return setBomb(false, getTileCoordFromCenter());
	}

	public Bomb setBomb(TileCoord coord) {
		return setBomb(false, coord);
	}

	public Bomb setBomb(boolean noCd, TileCoord coord) {
		if (MapSet.tileIsFree(coord) && getHoldingEntity() == null && (noCd || bombCd <= 0) && bombs.size() < maxBombs) {
			BombType type = !gotItems.isEmpty() && gotItems.get(0).isBomb() ? ItemType.getBombTypeFromItemType(gotItems.get(0)) : BombType.NORMAL;
			for (Bomb bomb : bombs) {
				if (bomb.getBombType().isUnique())
					type = BombType.NORMAL;
			}
			Bomb bomb = Bomb.addBomb(this, coord, type, fireRange, true);
			if (bomb != null) {
				bombs.add(bomb);
				Sound.playWav(this, setBombSound);
				bombCd = type == BombType.FOLLOW || type == BombType.MAGNET ? 20 : (int) (10 / getSpeed());
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
		if (gotItems.isEmpty()) { // TEMP
			gotItems.add(ItemType.FOLLOW_BOMB);
			gotItems.add(ItemType.POWER_GLOVE);
			gotItems.add(ItemType.HYPER_GLOVE);
			gotItems.add(ItemType.PUSH_POWER);
			gotItems.add(ItemType.KICK_BOMB);
			gotItems.add(ItemType.PUNCH_BOMB);
			gotItems.add(ItemType.PASS_BRICK);
			gotItems.add(ItemType.LINED_BOMBS);
			for (int n = 0; n < 9; n++)
				gotItems.add(ItemType.BOMB_UP);
			for (int n = 0; n < 3; n++)
				gotItems.add(ItemType.SPEED_UP);
		}
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
		}
		setSpeed(speed);
		super.removeCurse();
	}

	@Override
	public void setCurse(Curse curse) { // FALTA: Implementar BLINDNESS e SWAP_PLAYERS
		super.setCurse(curse);
		if (getCurse() == null) {
			if (curse == Curse.MIN_BOMB)
				maxBombs = 1;
			else if (curse == Curse.MIN_FIRE)
				fireRange = 1;
			else if (curse == Curse.NO_BOMB)
				maxBombs = 0;
		}
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public void decLives() {
		incLives(-1);
	}

	public void incLives() {
		incLives(1);
	}

	public void incLives(int value) {
		lives += value;
	}

	@Override
	public void onJumpFallAtFreeTileEvent(JumpMove jumpMove) {
		changeToStandFrameSet();
		checkOutScreenCoords();
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_PLAYER);
	}

	@Override
	public void onJumpFallAtOccupedTileEvent(JumpMove jumpMove) {
		Sound.playWav(this, "VOICETaunt");
		jumpMove.resetJump(4, 1.2, 14);
		checkOutScreenCoords();
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		setGotoMove(coord.incCoordsByDirection(getDirection()).getPosition(), jumpMove.getDurationFrames());
	}
	
	public void dropItem() {
		dropItem((int)MyMath.getRandom(1, 3));
	}

	public void dropItem(int quantityToDrop) {
		setFrameSet("Taunt");
		while (!gotItems.isEmpty() && quantityToDrop-- > 0) {
			ItemType item = CollectionUtils.getRandomItemFromList(gotItems);
			gotItems.remove(item);
			Item.addItem(getTileCoordFromCenter(), item, true);
		}
		updateStatusByItems();
	}

}
