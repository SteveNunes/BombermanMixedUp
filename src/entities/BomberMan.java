package entities;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import application.Main;
import enums.BombType;
import enums.CpuDificult;
import enums.Curse;
import enums.Direction;
import enums.Elevation;
import enums.FindType;
import enums.FindTypeRestriction;
import enums.GameInput;
import enums.ImageFlip;
import enums.ItemType;
import enums.PassThrough;
import enums.SpriteLayerType;
import enums.StageObjectives;
import enums.TileProp;
import frameset.FrameSet;
import frameset.Tags;
import frameset_tags.FrameTag;
import frameset_tags.SetOriginSprPerLine;
import frameset_tags.SetSprFlip;
import frameset_tags.SetSprSource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.JumpMove;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import pathfinder.PathFinderDistance;
import pathfinder.PathFinderOptmize;
import player.Player;
import tools.Draw;
import tools.FindProps;
import tools.GameConfigs;
import tools.IniFiles;
import tools.Materials;
import tools.Sound;
import tools.Tools;
import util.CollectionUtils;
import util.MyMath;

public class BomberMan extends Entity {
	
	private static List<BomberMan> bomberManList = new ArrayList<>();
	private static int bomberAlives = 0;

	private List<ItemType> gotItems;
	private List<Bomb> bombs;
	private List<Direction> pressedDirs;
	private Set<GameInput> holdedInputs;
	private List<GameInput> queuedInputs;
	private CpuPlay cpuPlay;
	private Player player;
	private String nameSound;
	private String setBombSound;
	private int fireRange;
	private int maxBombs;
	private int bomberIndex;
	private int palleteIndex;
	private int bombCd;
	private int score;
	private int addedScore;
	private int lives;
	private int playerId;
	private int transferCurseCooldown;
	private int holdingB;
	private int releasingFromHolderValue;
	private int idleFrames;
	private int[] headIndexes;
	private BomberShip bomberShip;
	private Ride ride;
	private Ride waitingForRide;

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
		holdingB = 0;
		transferCurseCooldown = 0;
		releasingFromHolderValue = 0;
		bomberShip = null;
		player = null;
		cpuPlay = null;
		ride = null;
		waitingForRide = null;
		lives = GameConfigs.STARTING_LIVES;
		idleFrames = 0;
		setHitPoints(1);
		String section = "" + bomberIndex;
		updateStatusByItems();
		nameSound = IniFiles.characters.read(section, "NameSound");
		int[] i = IniFiles.characters.readAsIntArray(section, "HeadIndexes", new int[4]);
		headIndexes = new int[] {i[0], i[1], i[2], i[3], 0};
		if (nameSound != null && nameSound.equals("-"))
			nameSound = null;
		SetSprSource[] sprSourceTag = new SetSprSource[1];
		if (IniFiles.characters.read(section, "DefaultTags") != null) {
			Tags tags = null;
			setDefaultTags(tags = Tags.loadTagsFromString(IniFiles.characters.read(section, "DefaultTags")));
			for (FrameTag tag : tags.getTags()) {
				if (tag instanceof SetSprSource)
					sprSourceTag[0] = (SetSprSource)tag;
				else if (tag instanceof SetOriginSprPerLine)
					headIndexes[4] = ((SetOriginSprPerLine)tag).value;
			}
		}
		for (String item : IniFiles.characters.getItemList(section)) {
			if (item.length() > 9 && item.substring(0, 9).equals("FrameSet.")) {
				FrameSet frameSet = addNewFrameSetFromIniFile(this, item.substring(9), "Characters", section, item)[1];
				if (item.length() >= 14 && item.substring(0, 14).equals("FrameSet.Stand") && section.equals("" + bomberIndex))
					frameSet.iterateFrameTags(tag -> {
						if (tag instanceof SetSprSource)
							sprSourceTag[0] = (SetSprSource)tag;
						else if (tag instanceof SetOriginSprPerLine)
							headIndexes[4] = ((SetOriginSprPerLine)tag).value;
					});
			}
		}
		Position[] bomerShipHeadOffset = { new Position(11, -17), new Position(0, -10), new Position(-11, -17), new Position(0, -15) };
		for (String frameSetName : IniFiles.frameSets.getItemList("BOMBER_SHIP")) {
			FrameSet frameSet = addNewFrameSetFromIniFile(this, frameSetName, "FrameSets", "BOMBER_SHIP", frameSetName)[1];
			ImageFlip[] flip = { null };
			for (int n = 0; n < 2; n++) {
				final int n2 = n;
				frameSet.iterateFrameTags(tag -> {
					if (tag instanceof SetSprSource) {
						SetSprSource tag2 = (SetSprSource)tag;
						if (tag2.originalSpriteSourceName.equals("-")) {
							int w = (int)sprSourceTag[0].originSprSizePos.width, h = (int)sprSourceTag[0].originSprSizePos.height;
							int dir = tag2.spriteIndex, v = headIndexes[dir], perLine = headIndexes[4], sprIndex = Math.abs(v);
							if (n2 == 0 && v < 0)
								flip[0] = ImageFlip.HORIZONTAL;
							tag2.originalSpriteSourceName = "Character." + bomberIndex;
							tag2.spriteSourceName = sprSourceTag[0].spriteSourceName;
							tag2.originSprSizePos.setBounds(new Rectangle(
									w * (sprIndex >= perLine && perLine > 0 ? (sprIndex % perLine) : sprIndex),
									h * (sprIndex >= perLine && perLine > 0 ? (int)(sprIndex / perLine) : 0),
									w, h));
							tag2.outputSprSizePos.setBounds((int)bomerShipHeadOffset[dir].getX(), (int)bomerShipHeadOffset[dir].getY(), w, h);
							tag2.spriteIndex = 0;
							tag2.spritesPerLine = 0;
						}
					}
					else if (flip[0] != null && n2 == 1 && tag instanceof SetSprFlip)
						((SetSprFlip)tag).flip = flip[0];
				});
			}
			replaceFrameSet(frameSetName, frameSet);
		}
		setBombSound = IniFiles.characters.read(section, "SetBombSound");
		if (setBombSound == null)
			setBombSound = "SetBomb";
		changeToStandFrameSet();
		if (IniFiles.characters.read(section, "DefaultStartTags") != null) {
			Tags tags = Tags.loadTagsFromString(IniFiles.characters.read(section, "DefaultStartTags"));
			tags.setRootSprite(getCurrentFrameSet().getSprite(0));
			tags.run();
		}
		setOnFrameSetEndsEvent(e -> changeToStandFrameSet());
		setPassThroughs(true, PassThrough.ITEM, PassThrough.MONSTER, PassThrough.PLAYER);
	}
	
	public static void setAllAliveBomberMansFrameSet(String frameSet) {
		for (BomberMan bomber : bomberManList)
			if (!bomber.isDead()) {
				for (GameInput i : GameInput.values())
					bomber.keyRelease(i);
				bomber.unsetHoldingEntity();
				bomber.setFrameSet(frameSet);
			}
	}
	
	public static BomberMan addBomberMan(int bomberIndex, int palleteIndex) {
		BomberMan bomber = new BomberMan(bomberManList.size(), bomberIndex, palleteIndex);
		bomberManList.add(bomber);
		bomberAlives++;
		return bomber;
	}
	
	public boolean isRiding() {
		return ride != null;
	}
	
	public Ride getRide() {
		return ride;
	}
	
	public static BomberMan getBomberMan(int index) {
		if (bomberManList.isEmpty())
			throw new RuntimeException("No bombermans were added");
		if (index < 0 || index >= bomberManList.size())
			throw new RuntimeException(index + " - Invalid index (Expected: 0 - " + (bomberManList.size() - 1));
		return bomberManList.get(index);
	}

	public static void removeBomberMan(BomberMan bomberMan) {
		if (bomberManList.contains(bomberMan))
			removeBomberMan(bomberManList.indexOf(bomberMan));
	}
	
	public static BomberMan removeBomberMan(int index) {
		if (bomberManList.isEmpty())
			throw new RuntimeException("No bombermans were added");
		if (index < 0 || index >= bomberManList.size())
			throw new RuntimeException(index + " - Invalid index (Expected: 0 - " + (bomberManList.size() - 1));
		return bomberManList.remove(index);
	}

	public static int getBomberAlives() {
		return bomberAlives;
	}

	public static void setBomberAlives(int bomberAlives) {
		BomberMan.bomberAlives = bomberAlives;
	}

	public static void incBomberAlives(int quant) {
		bomberAlives += quant;
	}

	public static void drawBomberMans() {
		List<BomberMan> list = new ArrayList<>(bomberManList);
		for (int n = 0; n < list.size(); n++)
			list.get(n).run();
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
	
	public CpuPlay getCpuPlay() {
		return cpuPlay;
	}
	
	public void setCpuPlay(CpuPlay cpuPlay) {
		this.cpuPlay = cpuPlay;
	}
	
	public boolean isCpu() {
		return cpuPlay != null;
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
	
	public List<Direction> getPressedDirs() {
		return pressedDirs;
	}
	
	public Set<GameInput> getHoldedInputs() {
		return holdedInputs;
	}
	
	private void tryGetFreeFromHolder() {
		if (getHolder() != null && (releasingFromHolderValue += 5) == 30)
			getHolder().unsetHoldingEntity(true);
	}
	
	@Override
	public JumpMove setJumpMove(double jumpStrenght, double strenghtMultipiler, int durationFrames) {
		JumpMove jumpMove = super.setJumpMove(jumpStrenght, strenghtMultipiler, durationFrames);
		if (isRiding())
			getRide().setJumpMove(jumpStrenght, strenghtMultipiler, durationFrames);
		return jumpMove;
	}

	public void keyPress(GameInput input) {
		idleFrames = 0;
		if (isBlockedMovement() || Main.isFreeze() || holdedInputs.contains(input) || MapSet.stageObjectiveIsCleared() || Draw.getFade() != null)
			return;
		if (bomberShipIsActive()) {
			if (isPerfectTileCentred()) {
				Direction dir = input.getDirection();
				if (dir != null && !pressedDirs.contains(dir)) {
					bomberShip.setSpeed(!(holdedInputs.contains(GameInput.E) || holdedInputs.contains(GameInput.F)) ? 1 : holdedInputs.contains(GameInput.F) ? 4 : 2);
					pressedDirs.add(dir);
				}
			}
			if (input == GameInput.B && !holdedInputs.contains(GameInput.B))
				bomberShip.pressB();
			holdedInputs.add(input);
			return;
		}
		if (getHolder() != null)
			tryGetFreeFromHolder();
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
		else if (input == GameInput.B)
			holdingB = holdingB == 0 && !Brick.haveBrickAt(getTileCoordFromCenter()) ? -1 : 1;
		else if (input == GameInput.C) {
			bombs.sort((b1, b2) -> (int) (b1.getSetTime() - b2.getSetTime()));
			for (Bomb bomb : bombs)
				if (bomb.getPushEntity() != null) {
					bomb.stopKick();
					return;
				}
			TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(getDirection());
			for (int n = 0; !isRiding() && n < 2; n++) {
				if (haveItem(ItemType.PUNCH_BOMB) && Bomb.haveBombAt(this, coord) && !Bomb.getBombAt(coord).isBlockedMovement() && haveFrameSet("PunchBomb")) {
					setFrameSet("PunchBomb");
					return;
				}
				else if (haveItem(ItemType.HYPER_PUNCH) && Brick.haveBrickAt(coord) && haveFrameSet("PunchBomb")) {
					setFrameSet("PunchBrick");
					return;
				}
			}
			coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(getDirection());
			if (haveItem(ItemType.LINED_BOMBS) && Bomb.haveBombAt(getTileCoordFromCenter())) {
				setLinedBombsInFront();
				return;
			}
			if (!isRiding() && haveItem(ItemType.PUSH_POWER) && haveFrameSet("PushPower"))
				setFrameSet("PushPower");
		}
		else if (input == GameInput.D && isRiding() && !getRide().isDisabled()) {
			String[] s = { "Special", "SpecialMoving", "SpecialStand" };
			for (int n = 0; n < s.length; n++) {
				String fName = s[n];
				if (((isMoving() && n != 1) || (!isMoving() && n != 2)) &&
						(getRide().haveFrameSet(fName) && !getRide().currentFrameSetNameIsEqual(fName))) {
							getRide().setFrameSet(fName);
							break;
				}
			}
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

	public void setLinedBombsInFront() {
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		do {
			setBomb(true, coord);
			coord.incCoordsByDirection(getDirection());
		}
		while (MapSet.tileIsFree(coord) && !Entity.haveAnyEntityAtCoord(coord, this) && !Bomb.haveBombAt(coord) && !Item.haveItemAt(coord));
	}

	public void keyRelease(GameInput input) {
		if (bomberShipIsActive()) {
			holdedInputs.remove(input);
			return;
		}
		if (input.isDirection()) {
			if (!holdedInputs.contains(GameInput.B))
				holdingB = 0;
			pressedDirs.removeAll(Arrays.asList(input.getDirection()));
		}
		holdedInputs.remove(input);
		queuedInputs.remove(input);
		if (input == GameInput.B) {
			if (holdingB > 0)
				setBomb();
		}
	}
	
	@Override
	public boolean isMoving() {
		return super.isMoving() && (getGotoMove() != null || getPushEntity() != null || currentFrameSetNameIsEqual(getDefaultMovingFrameSet()));
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

	private PathFinder testPathFind = null;
	private TileCoord testPathFindStartCoord = null;
	private TileCoord testPathFindLastCoord = null;
	private void testPathFind() {
		/**
		 * A geracao de blocos fixos e tijolos, em quantidade pequena, tende sempre
		 * a gerar na metade da esquerda, e a metade da direita fica intacta
		 */
		List<FindProps> founds = Tools.findInRect(this,
				getTileCoordFromCenter(),
				this,
				10,
				Set.of(FindType.PLAYER),
				getPassThrough(),
				FindTypeRestriction.EVERYTHING);
		if (founds != null) {
			FindProps found = founds.get(0);
			if (testPathFind != null && (!testPathFindStartCoord.equals(getTileCoordFromCenter()) || !testPathFindLastCoord.equals(found.getCoord()))) {
				testPathFind.recalculatePath(getTileCoordFromCenter(), found.getCoord(), getDirection());
				if (!testPathFind.pathWasFound())
					testPathFind = null;
			}
			if (testPathFind == null)
				testPathFind = new PathFinder(getTileCoordFromCenter(), found.getCoord(), getDirection(), PathFinderDistance.SHORTEST, PathFinderOptmize.OPTIMIZED, c -> tileIsFree(c));
			testPathFindStartCoord = getTileCoordFromCenter().getNewInstance();
			testPathFindStartCoord = found.getCoord().getNewInstance();
			Draw.markTile(found.getCoord().getPosition(), Color.YELLOW);
			for (var tc : testPathFind.getCurrentPath()) {
				int x = (int)tc.getKey().getPosition().getX(),
						y = (int)tc.getKey().getPosition().getY();
				Direction dir = tc.getValue();
				Draw.addDrawQueue(SpriteLayerType.CEIL, Materials.hud, 1024 + 16 * dir.get4DirValue(), 880, 16, 16, x, y, 16, 16);
			}
		}
	}
	
	@Override
	public void run(GraphicsContext gc, boolean isPaused) { // 1024 880 16 16
		if (getBomberIndex() == 0)
		testPathFind(); // Para testar o PathFind (aponta para o objeto mais proximo configurado no metodo testPathFind()
		if (++idleFrames == 240) {
			if (!isRiding() && haveFrameSet("Idle"))
				setFrameSet("Idle");
			else if (isRiding() && getRide().haveFrameSet("Idle"))
				getRide().setFrameSet("Idle");
			else
				idleFrames = 0;
		}
		if (releasingFromHolderValue > 0)
			--releasingFromHolderValue;
		if (cpuPlay != null)
			cpuPlay.run();
		bombCd--;
		if (transferCurseCooldown > 0)
			transferCurseCooldown--;
		for (int n = 0; n < bombs.size(); n++)
			if (!bombs.get(n).isActive())
				bombs.remove(n--);
		if (!isDead()) {
			if (MapSet.tileContainsProp(getTileCoordFromCenter(), TileProp.INSTAKILL)) {
				takeDamage(true);
				return;
			}
			if (MapSet.tileContainsProp(getTileCoordFromCenter(), TileProp.DAMAGE_PLAYER) ||
					(MapSet.tileContainsProp(getTileCoordFromCenter(), TileProp.EXPLOSION) && !gotItems.contains(ItemType.FIRE_IMMUNE)))
						takeDamage();
		}
		if (bomberShip != null)
			bomberShip.run();
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
				if (!isCpu() && (getCurse() == Curse.CONFUSED_1 || getCurse() == Curse.CONFUSED_2 || getCurse() == Curse.CONFUSED_3))
					for (int n = 0; n < getCurse().getValue(); n++)
						dir = dir.getNext4WayClockwiseDirection();
				setDirection(dir);
				changeToMovingFrameSet();
			}
			if (!holdedInputs.contains(GameInput.B) && (currentFrameSetNameIsEqual("HoldingStand") || currentFrameSetNameIsEqual("HoldingMoving"))) {
				setFrameSet("Release");
				holdingB = 0;
			}
			out:
			if (holdedInputs.contains(GameInput.B)) {
				if (!isHoldingEntity() && !isRiding() && haveFrameSet("HoldingStart")) {
					if (haveItem(ItemType.POWER_GLOVE)) {
						if (Entity.haveAnyEntityAtCoord(getTileCoordFromCenter(), this)) {
							for (Entity entity : Entity.getEntityListFromCoord(getTileCoordFromCenter()))
								if ((!(entity instanceof BomberMan) || !((BomberMan)entity).isRiding()) &&
										!(entity instanceof Ride) && entity != this && !entity.isBlockedMovement()) {
											setFrameSet("HoldingStart");
											break out;
								}
						}
						else if (holdingB >= 0 && ++holdingB == 8) {
							if (Bomb.haveBombAt(getTileCoordFromCenter()))
								setFrameSet("HoldingStart");
							holdingB = -1;
							break out;
						}
					}
					if (haveItem(ItemType.HYPER_GLOVE) && Brick.haveBrickAt(getTileCoordFromCenter())) {
						setFrameSet("HoldingStart");
						break out;
					}
				}
				boolean ok = !Entity.haveAnyEntityAtCoord(getTileCoordFromCenter(), this) || holdingB == -1;
				if (currentFrameSetNameIsEqual("Moving")) {
					// Definir a coordenada um pouco mais para as costas se ta soltando a bomba enquanto esta andando, pra evitar q a proxima bomba saia na sua frente
					Position pos = new Position((int) getX() + Main.TILE_SIZE / 2, (int) getY() + Main.TILE_SIZE / 2);
					pos.incPositionByDirection(getDirection().getReverseDirection(), Main.TILE_SIZE / 4);
					if (ok)
						setBomb(pos.getTileCoord());
				}
				else if (ok)
					setBomb();
			}
			if (getCurse() == Curse.SPAM_BOMB)
				setBomb();
			if (tileWasChanged()) {
				if (pressedDirs.size() > 1) {
					Direction dir = pressedDirs.get(1);
					if (tileIsFree(dir)) {
						dir = pressedDirs.get(0);
						pressedDirs.remove(0);
						pressedDirs.add(dir);
					}
				}
				if (getElevation() == Elevation.ON_GROUND) {
					MapSet.checkTileTrigger(this, getTileCoordFromCenter(), TileProp.TRIGGER_BY_PLAYER);
					MapSet.checkTileTrigger(this, getPreviewTileCoord(), TileProp.TRIGGER_BY_PLAYER, true);
					if (!isRiding()) {
						MapSet.checkTileTrigger(this, getTileCoordFromCenter(), TileProp.TRIGGER_BY_UNRIDE_PLAYER);
						MapSet.checkTileTrigger(this, getPreviewTileCoord(), TileProp.TRIGGER_BY_UNRIDE_PLAYER, true);
					}
					else {
						MapSet.checkTileTrigger(this, getTileCoordFromCenter(), TileProp.TRIGGER_BY_RIDE);
						MapSet.checkTileTrigger(this, getPreviewTileCoord(), TileProp.TRIGGER_BY_RIDE, true);
					}
				}
			}
			TileCoord frontTile = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(getDirection());
			if (getPushingValue() > 5 && haveItem(ItemType.KICK_BOMB) && Bomb.haveBombAt(this, frontTile) && !Bomb.getBombAt(frontTile).isBlockedMovement()) {
				TileCoord nextCoord = frontTile.getNewInstance().incCoordsByDirection(getDirection());
				if (MapSet.tileIsFree(nextCoord, Set.of())) {
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
		tryToTransferCurse();
	}

	@Override
	public void takeDamage() {
		takeDamage(false);
	}

	@Override
	public void takeDamage(boolean instaKill) {
		if (isRiding() && (instaKill || !isInvencible() && !isDead())) {
			getRide().setFrameSet(MapSet.tileContainsProp(getTileCoordFromCenter(), TileProp.EXPLOSION) ? "DeadByFire" : "Dead");
			getRide().setOwner(null);
			ride = null;
			Entity entity = this;
			while (entity.getLinkedEntityBack() != null) {
				if (entity.getLinkedEntityBack() instanceof Item && ((Item)entity.getLinkedEntityBack()).isEgg())
					((Item)entity.getLinkedEntityBack()).setInvencibleFrames(40);
				entity = entity.getLinkedEntityBack();
			}
			if (!instaKill) {
				setInvencibleFrames(-60);
				jumpTo(getPosition(), 4, 1.2, 40);
			}
			else
				super.takeDamage(instaKill);
			return;
		}
		super.takeDamage(instaKill);
	}
	
	public BomberShip getBomberShip() {
		return bomberShip;
	}

	public void unsetBomberShip() {
		bomberShip = null;
	}
	
	@Override
	public void setDisabled() {
		super.setDisabled();
		bomberShip = null;
	}
	
	public void activeBomberShip() {
		if (MapSet.hurryUpIsActive() || Main.isFreeze() || MapSet.stageObjectiveIsCleared() || Draw.getFade() != null) {
			setDisabled();
			return;
		}
		setElevation(Elevation.ON_GROUND);
		bomberShip = new BomberShip(this, MapSet.findNearestBomberShipTile(getTileCoordFromCenter()));
	}
	
	private void tryToTransferCurse() {
		if (Entity.haveAnyEntityAtCoord(getTileCoordFromCenter(), this)) {
			for (Entity entity : Entity.getEntityListFromCoord(getTileCoordFromCenter()))
				if (entity instanceof BomberMan && entity != this) {
					BomberMan bomberMan = (BomberMan)entity;
					if (bomberMan.isCursed() && bomberMan.transferCurseCooldown == 0) {
						Curse curse = bomberMan.getCurse();
						setCurse(curse);
						bomberMan.removeCurse();
					}
				}
		}
	}

	private String getDefaultStandFrameSet() {
		if (isRiding())
			return getRide().onlyHead() ? "RidingOnlyHead" : "Riding";
		else if (isHoldingEntity())
			return "HoldingStand";
		else if (getHolder() != null)
			return "BeingHolded";
		else
			return idleFrames >= 240 ? "Idle" : "Stand";
	}

	void changeToStandFrameSet() {
		if (isCursed() && getCurse() == Curse.CANT_STOP)
			return;
		setBlockedMovement(false);
		setFrameSet(getDefaultStandFrameSet());
		if (tileWasChanged())
			setTileWasChanged(true);
		if (isRiding()) {
			String[] fName = { "Special", "SpecialMoving", "SpecialStand" };
			for (int n = 0; n < fName.length; n++)
				if (getRide().currentFrameSetNameIsEqual(fName[n]) && getRide().getCurrentFrameSet().isRunning())
					return;
			getRide().setFrameSet(idleFrames >= 240 ? "Idle" : "Stand");
			setTempSpeed(0);
		}
	}

	private String getDefaultMovingFrameSet() {
		if (isRiding())
			return getRide().onlyHead() ? "RidingOnlyHead" : "Riding";
		else if (isHoldingEntity())
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
		if (isRiding()) {
			String[] fName = { "Special", "SpecialMoving", "SpecialStand" };
			for (int n = 0; n < fName.length; n++)
				if (getRide().currentFrameSetNameIsEqual(fName[n]) && getRide().getCurrentFrameSet().isRunning())
					return;
			getRide().setFrameSet("Moving");
			setTempSpeed(-1);
		}
	}

	public List<ItemType> getItemList() {
		return gotItems;
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
		if (MapSet.tileIsFree(coord) && !isHoldingEntity() && (noCd || bombCd <= 0) && bombs.size() < maxBombs) {
			BombType type = getBombType();
			for (Bomb bomb : bombs) {
				if (bomb.getBombType().isUnique())
					type = BombType.NORMAL;
			}
			Bomb bomb = Bomb.addBomb(this, coord, type, type == BombType.SENSOR ? 2 : getFireRange(), true);
			if (bomb != null) {
				bombs.add(bomb);
				Sound.playWav(this, setBombSound);
				bombCd = type == BombType.FOLLOW || type == BombType.MAGNET ? 20 : (int) (15 / getSpeed());
				return bomb;
			}
		}
		return null;
	}
	
	public Ride isWaitingForRide() {
		return waitingForRide;
	}

	public void setWaitingForRide(Ride ride) {
		waitingForRide = ride;
	}

	public void pickItem(Item item) {
		item.pick(this);
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
		else if (type == ItemType.TIME_STOP)
			MapSet.addStageTimePauseDuration(MapSet.getLeftStageClearCriterias().contains(StageObjectives.LAST_PLAYER_SURVIVOR) ? 10 : 30);
		else if (type == ItemType.EXTRA_LIVE)
			incLives();
		else if (type == ItemType.HEART_UP)
			incHitPoints();
		else
			gotItems.add(type);
		updateStatusByItems();
	}
	
	public int getFireRange() {
		return getCurse() == Curse.MIN_FIRE ? 1 :
			getBombType() == BombType.P ? GameConfigs.MAX_EXPLOSION_DISTANCE : fireRange;
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
		if (getPlayerId() == 1111111110 && gotItems.isEmpty()) { // TEMP
			gotItems.add(ItemType.REMOTE_BOMB);
			gotItems.add(ItemType.PASS_BRICK);
			//gotItems.add(ItemType.PASS_BOMB);
			gotItems.add(ItemType.POWER_GLOVE);
			gotItems.add(ItemType.HYPER_KICK);
			gotItems.add(ItemType.HYPER_GLOVE);
			gotItems.add(ItemType.HYPER_PUNCH);
			gotItems.add(ItemType.POWER_GLOVE);
			gotItems.add(ItemType.PUNCH_BOMB);
			gotItems.add(ItemType.KICK_BOMB);
			gotItems.add(ItemType.PUSH_POWER);
			gotItems.add(ItemType.LINED_BOMBS);
			gotItems.add(ItemType.FIRE_MAX);
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
			else if (type == ItemType.FIRE_UP && getFireRange() < GameConfigs.MAX_EXPLOSION_DISTANCE)
				fireRange++;
			else if (type == ItemType.PASS_BOMB)
				setPassThroughBomb(true);
			else if (type == ItemType.PASS_BRICK)
				setPassThroughBrick(true);
		}
		setSpeed(speed);
		updateStatusByCurse();
		if (getLinkedEntityBack() instanceof Item && ((Item)getLinkedEntityBack()).isEgg())
			((Item)getLinkedEntityBack()).updateEggFollowingDistance();
	}

	@Override
	public void setCurse(Curse curse) { // FALTA: Implementar BLINDNESS
		Sound.playWav(this, "Curse");
		transferCurseCooldown = 120;
		if (curse == Curse.SWAP_PLAYERS && Entity.getEntityList().size() > 1) {
			BomberMan other = null;
			Entity[] list = Entity.getEntityList().toArray(new Entity[Entity.getEntityList().size()]);
			int i = (int)MyMath.getRandom(0, list.length - 1), i2 = i;
			while (other == null && ((!(list[i] instanceof BomberMan) || list[i] == this) && ++i != i2)) {
				if (i == list.length)
					i = 0;
				Object o = list[i];
				if (o instanceof BomberMan && !((BomberMan)o).isBlockedMovement() && !((BomberMan)o).isDead())
					other = (BomberMan)o;
			}
			if (other != null) {
				Position p = new Position(other.getPosition());
				other.setPosition(getPosition());
				setPosition(p);
				return;
			}
		}
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
	public void setJumpingFrameSet(int durationFrames) {
		super.setJumpingFrameSet(durationFrames);
		if (isRiding() && !getRide().isDisabled())
			getRide().setJumpingFrameSet(durationFrames);
	}
	
	@Override
	public void onJumpFallAtFreeTileEvent(JumpMove jumpMove) {
		changeToStandFrameSet();
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		MapSet.checkTileTrigger(this, coord, TileProp.TRIGGER_BY_PLAYER);
		MapSet.checkTileTrigger(this, coord, isRiding() ? TileProp.TRIGGER_BY_RIDE : TileProp.TRIGGER_BY_UNRIDE_PLAYER, true);
		for (Ride ride : Ride.getRides())
			if (ride.getOwner() == this) {
				if (ride.getTileCoordFromCenter().equals(getTileCoordFromCenter()))
					this.ride = ride;
				else
					ride.setOwner(null);
				setWaitingForRide(null);
				break;
			}
		changeToStandFrameSet();
	}

	@Override
	public void onJumpFallAtOccupedTileEvent(JumpMove jumpMove) {
		Sound.playWav(this, "VOICETaunt");
		jumpMove.resetJump(4, 1.2, 14);
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		setJumpingFrameSet(jumpMove.getDurationFrames());
		if (isRiding())
			getRide().setJumpMove(4, 1.2, 14);
		setGotoMove(coord.incCoordsByDirection(getDirection()).getPosition(), jumpMove.getDurationFrames());
	}
	
	public void dropItem(boolean setFrameSetToTaunt, boolean dropLastest) {
		dropItem(setFrameSetToTaunt, false, dropLastest);
	}
	
	public void dropItem(boolean setFrameSetToTaunt, boolean ignoreBadItens, boolean dropLastest) {
		dropItem((int)MyMath.getRandom(1, 3), setFrameSetToTaunt, ignoreBadItens, dropLastest);
	}

	public void dropItem(int quantityToDrop, boolean setFrameSetToTaunt, boolean dropLastest) {
		dropItem(quantityToDrop, setFrameSetToTaunt, false, dropLastest);
	}
	
	public void dropItem(int quantityToDrop, boolean setFrameSetToTaunt, boolean ignoreBadItens, boolean dropLastest) {
		if (setFrameSetToTaunt)
			tauntMe();
		List<ItemType> list = new ArrayList<>(gotItems); 
		HashSet<TileCoord> coords = new LinkedHashSet<>();
		while (!list.isEmpty() && quantityToDrop-- > 0) {
			ItemType item;
			if (dropLastest)
				item = list.get(list.size() - 1);
			else
				item = CollectionUtils.getRandomItemFromList(list);
			if (!ignoreBadItens || !item.isBadItem()) {
				gotItems.remove(item);
				MapSet.getRandomFreeTileAsync(t -> !coords.contains(t)).thenAccept(t -> {
					Item i = new Item(getTileCoordFromCenter().getNewInstance(), item);
					i.jumpTo(t);
					Item.addItem(i);
					coords.add(t);
				});
			}
			else
				quantityToDrop++;
			list.remove(item);
		}
		updateStatusByItems();
	}

	public void dropAllItems() {
		dropItem(gotItems.size(), false, false, false);
	}

	public static BomberMan dropNewCpu(TileCoord coord, int bomberManId, int palleteId, CpuDificult dificult) {
		return dropNewCpu(coord, bomberManId, palleteId, dificult, null, null);
	}

	public static BomberMan dropNewCpu(TileCoord coord, int bomberManId, int palleteId, CpuDificult dificult, List<ItemType> initialItems) {
		return dropNewCpu(coord, bomberManId, palleteId, dificult, null, initialItems);
	}

	public static BomberMan dropNewCpu(TileCoord coord, int bomberManId, int palleteId, CpuDificult dificult, Integer invencibilityFrames) {
		return dropNewCpu(coord, bomberManId, palleteId, dificult, invencibilityFrames, null);
	}

	public static BomberMan dropNewCpu(TileCoord coord, int bomberManId, int palleteId, CpuDificult dificult, Integer invencibilityFrames, List<ItemType> initialItems) {
		BomberMan bomber = BomberMan.addBomberMan(bomberManId, palleteId);
		bomber.setPosition(coord.getPosition());
		bomber.setCpuPlay(new CpuPlay(bomber, dificult));
		if (invencibilityFrames != null && invencibilityFrames != 0)
			bomber.setInvencibleFrames(invencibilityFrames);
		if (initialItems != null && !initialItems.isEmpty()) {
			bomber.gotItems.addAll(initialItems);
			bomber.updateStatusByItems();
		}
		return bomber;
	}

	public static void reviveAllBomberMansAndClearTheirItens() {
		setBomberAlives(0);
		for (int n = 0; n < bomberManList.size(); n++) {
			BomberMan bomber = bomberManList.get(n);
			bomber.reviveAndClearItens();
			if (n < MapSet.getInitialPlayerPositions().size())
				bomber.setPosition(MapSet.getInitialPlayerPosition(n));
			else {
				MapSet.getRandomFreeTileAsync().thenAccept(tileCoord ->
					bomber.setPosition(tileCoord.getPosition()));
			}
		}
	}
	
	public static void softResetAllBomberMansAfterMapChange() {
		for (BomberMan bomber : bomberManList)
			bomber.softResetAfterMapChange();
	}
	
	public void softResetAfterMapChange() {
		softResetAfterMapChange(0);
	}
	
	public void softResetAfterMapChange(int invencibleFrames) {
		bomberShip = null;
		bombs.clear();
		holdedInputs.clear();
		queuedInputs.clear();
		unsetHolder();
		unsetHoldingEntity();
		unsetAllMovings();
		changeToStandFrameSet();
		forceDirection(Direction.DOWN);
		setBlockedMovement(false);
		setEnabled();
		removeCurse();
		incBomberAlives(1);
		updateStatusByItems();
		setInvencibleFrames(invencibleFrames);
	}
	
	public void reviveAndClearItens() {
		reviveAndClearItens(0);
	}
	
	public void reviveAndClearItens(int invencibleFrames) {
		gotItems.clear();
		softResetAfterMapChange(invencibleFrames);
		setHitPoints(1);
	}

	public boolean bomberShipIsActive() {
		return bomberShip != null;
	}

}