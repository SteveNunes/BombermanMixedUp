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
import enums.GameInput;
import enums.GameInputMode;
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
import player.Player;
import tools.GameConfigs;
import tools.IniFiles;
import tools.Sound;
import util.CollectionUtils;
import util.Misc;
import util.MyMath;

public class BomberMan extends Entity {
	
	private static List<BomberMan> bomberManList = new ArrayList<>();

	private int fireRange;
	private int maxBombs;
	private int bomberIndex;
	private int palleteIndex;
	private String setBombSound;
	private List<ItemType> gotItems;
	private List<Bomb> bombs;
	private List<Direction> pressedDirs;
	private Set<GameInput> holdedInputs;
	private List<GameInput> queuedInputs;
	private int bombCd;
	private int score;
	private int addedScore;
	private int lives;
	private int playerId;
	private CpuPlay cpuPlay;
	private Player player;
	private String nameSound;

	public BomberMan(int playerId, int bomberIndex, int palleteIndex) {
		super();
		this.bomberIndex = bomberIndex;
		this.palleteIndex = palleteIndex;
		this.playerId = playerId;
		pressedDirs = new ArrayList<>();
		holdedInputs = new HashSet<>();
		queuedInputs = new ArrayList<>();
		bombs = new ArrayList<>();
		gotItems = new ArrayList<>();
		bombCd = 0;
		score = 0;
		addedScore = 0;
		player = null;
		cpuPlay = null;
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
				addNewFrameSetFromIniFile(this, item.substring(9), "Characters", section, item);
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
	
	public static BomberMan addBomberMan(int bomberIndex, int palleteIndex) {
		BomberMan bomber = new BomberMan(bomberManList.size(), bomberIndex, palleteIndex);
		bomberManList.add(bomber);
		return bomber;
	}
	
	public static BomberMan getBomberMan(int index) {
		if (bomberManList.isEmpty())
			throw new RuntimeException("No bombermans were added");
		if (index < 0 || index >= bomberManList.size())
			throw new RuntimeException(index + " - Invalid index (Expected: 0 - " + (bomberManList.size() - 1));
		return bomberManList.get(index);
	}

	public static BomberMan removeBomberMan(int index) {
		if (bomberManList.isEmpty())
			throw new RuntimeException("No bombermans were added");
		if (index < 0 || index >= bomberManList.size())
			throw new RuntimeException(index + " - Invalid index (Expected: 0 - " + (bomberManList.size() - 1));
		return bomberManList.remove(index);
	}

	public static void drawBomberMans() {
		for (BomberMan bomberMan : bomberManList)
			bomberMan.run();
	}
	
	public static void clearBomberMans() {
		bomberManList.clear();
	}
	
	public static int getTotalBomberMans() {
		return bomberManList.size();
	}

	public static List<BomberMan> getBomberManList() {
		return bomberManList;
	}
	
	public void setCpuPlay(CpuPlay cpuPlay) {
		this.cpuPlay = cpuPlay;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getPlayerId() {
		return playerId;
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

	public boolean isPressed(GameInput input) {
		return holdedInputs.contains(input);
	}
	
	public Set<GameInput> getHoldedInputs() {
		return holdedInputs;
	}

	public void keyPress(GameInput input) {
		if (holdedInputs.contains(input))
			return;
		if (isBlockedMovement()) {
			queuedInputs.add(input);
			return;
		}
		if (input == GameInput.A) {
			bombs.sort((b1, b2) -> (int) (b1.getSetTime() - b2.getSetTime()));
			for (Bomb bomb : bombs)
				if ((!bomb.isBlockedMovement() || bomb.isStucked()) && (bomb.getBombType() == BombType.REMOTE || bomb.getBombType() == BombType.SPIKED_REMOTE)) {
					bomb.detonate();
					return;
				}
		}
		else if (input == GameInput.B) {
			if (getHoldingEntity() == null && haveFrameSet("HoldingStart")) {
				if (haveItem(ItemType.POWER_GLOVE)) {
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
		else if (input == GameInput.C) {
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
			for (int n = 0; n < 2; n++, coord.incCoordsByDirection(getDirection())) {
				if (haveItem(ItemType.PUNCH_BOMB) && Bomb.haveBombAt(this, coord) && !Bomb.getBombAt(coord).isBlockedMovement() && haveFrameSet("PunchBomb"))
					setFrameSet("PunchBomb");
				else if (haveItem(ItemType.HYPER_PUNCH) && Brick.haveBrickAt(coord) && haveFrameSet("PunchBomb"))
					setFrameSet("PunchBrick");
				else if (n == 1 && haveItem(ItemType.PUSH_POWER) && haveFrameSet("PushPower"))
					setFrameSet("PushPower");
			}
		}
		else if (input == GameInput.D) {
			
		}
		else if (input == GameInput.E) {
			if (!gotItems.isEmpty())
				dropItem(1, false, true, true);
		}
		else if (input == GameInput.F) {
			
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

	public void keyRelease(GameInput input) {
		if (input.isDirection())
			pressedDirs.removeAll(Arrays.asList(input.getDirection()));
		holdedInputs.remove(input);
		queuedInputs.remove(input);
	}
	
	@Override
	public boolean isMoving() {
		return super.isMoving() && currentFrameSetNameIsEqual(getDefaultMovingFrameSet());
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
		if (player.getInputMode() == GameInputMode.CPU)
			cpuPlay.run();
		bombCd--;
		for (int n = 0; n < bombs.size(); n++)
			if (!bombs.get(n).isActive())
				bombs.remove(n--);
		if (MapSet.tileContainsProp(getTileCoordFromCenter(), TileProp.DAMAGE_PLAYER) ||
				(MapSet.tileContainsProp(getTileCoordFromCenter(), TileProp.EXPLOSION) && !gotItems.contains(ItemType.FIRE_IMMUNE)))
					takeDamage();
		super.run(gc, isPaused);
		if (!isBlockedMovement()) {
			if (!queuedInputs.isEmpty()) {
				List<GameInput> list = new ArrayList<>(queuedInputs);
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
			if (!holdedInputs.contains(GameInput.B) && (currentFrameSetNameIsEqual("HoldingStand") || currentFrameSetNameIsEqual("HoldingMoving")))
				setFrameSet("Release");
			if (holdedInputs.contains(GameInput.B)) {
				if (currentFrameSetNameIsEqual("Moving")) { // Definir a coordenada um pouco mais para as costas se ta soltando a bomba
				                                            // enquanto esta andando, pra evitar q a proxima bomba saia na sua frente
					Position pos = new Position((int) getX() + Main.TILE_SIZE / 2, (int) getY() + Main.TILE_SIZE / 2);
					pos.incPositionByDirection(getDirection().getReverseDirection(), Main.TILE_SIZE / 4);
					setBomb(pos.getTileCoord());
				}
				else if (getHoldingEntity() == null)
					setBomb();
			}
			if (getCurse() == Curse.SPAM_BOMB) {
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
			if (getPushingValue() > 5 && haveItem(ItemType.KICK_BOMB) && Bomb.haveBombAt(this, frontTile) && !Bomb.getBombAt(frontTile).isBlockedMovement()) {
				TileCoord nextCoord = frontTile.getNewInstance().incCoordsByDirection(getDirection());
				if (MapSet.tileIsFree(nextCoord, Set.of())) {
					if (bombs.size() > 1 && bombs.get(0) != Bomb.getBombAt(frontTile))
						bombs.get(0).kick(getDirection().getReverseDirection(), 4);
					Bomb.getBombAt(frontTile).kick(getDirection(), 4);
					setPushingValue(0);
				}
			}
			if (getPushingValue() > 5 && haveItem(ItemType.HYPER_KICK) && Brick.haveBrickAt(frontTile) && !Brick.getBrickAt(frontTile).isBlockedMovement()) {
				TileCoord nextCoord = frontTile.getNewInstance().incCoordsByDirection(getDirection());
				if (MapSet.tileIsFree(nextCoord, Set.of())) {
					Brick.getBrickAt(frontTile).kick(getDirection(), 4);
					setPushingValue(0);
				}
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
	
	public BombType getBombType() {
		for (int n = gotItems.size() - 1; n >= 0; n--)
			if (gotItems.get(n).isBomb())
				return ItemType.getBombTypeFromItemType(gotItems.get(n));
		return BombType.NORMAL;
	}

	public Bomb setBomb(boolean noCd, TileCoord coord) {
		if (MapSet.tileIsFree(coord) && getHoldingEntity() == null && (noCd || bombCd <= 0) && bombs.size() < maxBombs) {
			BombType type = getBombType();
			for (Bomb bomb : bombs) {
				if (bomb.getBombType().isUnique())
					type = BombType.NORMAL;
			}
			Bomb bomb = Bomb.addBomb(this, coord, type, type == BombType.SENSOR ? 2 : fireRange, true);
			if (bomb != null) {
				bombs.add(bomb);
				Sound.playWav(this, setBombSound);
				bombCd = type == BombType.FOLLOW || type == BombType.MAGNET ? 20 : (int) (15 / getSpeed());
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
		else if (isCursed()) {
			setCurse(null);
			Item.addItem(getTileCoordFromCenter(), ItemType.CURSE_SKULL, true);
		}
		else if (type == ItemType.ARMOR) {
			setInvencibleFrames(3000);
			setBlinkingFrames(3000);
		}
		else if (type == ItemType.EXTRA_LIVE)
			incLives();
		else if (type == ItemType.HEART_UP)
			incHitPoints();
		else
			gotItems.add(type);
		updateStatusByItems();
	}
	
	public int getFireRange() {
		return fireRange;
	}
	
	public boolean havePlacedBombs() {
		return !bombs.isEmpty();
	}
	
	public List<Bomb> getBombs() {
		return bombs;
	}
	
	public int getMaxBombs() {
		return maxBombs - bombs.size();
	}
	
	@Override
	public void removeCurse() {
		super.removeCurse();
		updateStatusByItems();
	}

	public void updateStatusByItems() {
		fireRange = GameConfigs.STARTING_FIRE;
		maxBombs = GameConfigs.STARTING_BOMBS;
		double speed = GameConfigs.INITIAL_PLAYER_SPEED;
		if (getPlayerId() == 9990 && gotItems.isEmpty()) { // TEMP
			gotItems.add(ItemType.SPIKE_REMOTE_BOMB);
			gotItems.add(ItemType.POWER_GLOVE);
			gotItems.add(ItemType.KICK_BOMB);
			for (int n = 0; n < 9; n++)
				gotItems.add(ItemType.BOMB_UP);
		}
		if (!Misc.alwaysTrue())
			if (getPlayerId() == 0 && gotItems.isEmpty()) { // TEMP
				gotItems.add(ItemType.REMOTE_BOMB);
				gotItems.add(ItemType.HYPER_KICK);
				gotItems.add(ItemType.HYPER_GLOVE);
				gotItems.add(ItemType.HYPER_PUNCH);
				gotItems.add(ItemType.POWER_GLOVE);
				gotItems.add(ItemType.PUNCH_BOMB);
				gotItems.add(ItemType.PUSH_POWER);
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
		updateStatusByCurse();
	}

	@Override
	public void setCurse(Curse curse) { // FALTA: Implementar BLINDNESS e SWAP_PLAYERS
		super.setCurse(curse);
		updateStatusByCurse();
	}
	
	private void updateStatusByCurse() {
		if (getCurse() != null) {
			if (getCurse() == Curse.STUNNED)
				changeToStandFrameSet();
			else if (getCurse() == Curse.MIN_BOMB)
				maxBombs = 1;
			else if (getCurse() == Curse.MIN_FIRE)
				fireRange = 1;
			else if (getCurse() == Curse.NO_BOMB)
				maxBombs = 0;
			else if (getCurse() == Curse.MIN_SPEED)
				setSpeed(GameConfigs.MIN_PLAYER_SPEED);
			else if (getCurse() == Curse.ULTRA_SPEED)
				setSpeed(GameConfigs.MAX_PLAYER_SPEED);
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
	
	public void dropItem(boolean setFrameSetToTaunt, boolean dropLastest) {
		dropItem(setFrameSetToTaunt, false, dropLastest);
	}
	
	public void dropItem(boolean setFrameSetToTaunt, boolean ignoreBadItens, boolean dropLastest) {
		unsetHoldingEntity();
		dropItem((int)MyMath.getRandom(1, 3), setFrameSetToTaunt, ignoreBadItens, dropLastest);
	}

	public void dropItem(int quantityToDrop, boolean setFrameSetToTaunt, boolean dropLastest) {
		dropItem(quantityToDrop, setFrameSetToTaunt, false, dropLastest);
	}
	
	public void dropItem(int quantityToDrop, boolean setFrameSetToTaunt, boolean ignoreBadItens, boolean dropLastest) {
		if (setFrameSetToTaunt)
			setFrameSet("Taunt");
		List<ItemType> list = new ArrayList<>(gotItems); 
		while (!list.isEmpty() && quantityToDrop-- > 0) {
			ItemType item;
			if (dropLastest)
				item = list.get(list.size() - 1);
			else
				item = CollectionUtils.getRandomItemFromList(list);
			if (!ignoreBadItens || !item.isBadItem()) {
				gotItems.remove(item);
				Item.addItem(getTileCoordFromCenter(), item, true);
				if (!Misc.alwaysTrue() && item.isBomb()) {
					for (ItemType t : gotItems)
						if (t.isBomb()) {
							gotItems.remove(t);
							gotItems.add(0, t);
							break;
						}
				}
			}
			else
				quantityToDrop++;
			list.remove(item);
		}
		updateStatusByItems();
	}

}
