package entities;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import application.Main;
import enums.BombType;
import enums.Curse;
import enums.Direction;
import enums.DrawType;
import enums.Elevation;
import enums.PassThrough;
import enums.SpriteLayerType;
import enums.TileProp;
import frameset.FrameSet;
import frameset.Tags;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import maps.Brick;
import maps.Item;
import maps.MapSet;
import objmoveutils.GotoMove;
import objmoveutils.JumpMove;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import pathfinder.PathFinder;
import tools.Draw;
import tools.GameConfigs;
import tools.Sound;

public class Entity extends Position {

	private static Map<TileCoord, Set<Entity>> entityMap = new HashMap<>();
	private static Map<Entity, TileCoord> entityMap2 = new HashMap<>();

	private Curse curse;
	private int curseDuration = Curse.getDuration(curse);
	private Map<String, FrameSet> frameSets;
	private Map<String, FrameSet> freshFrameSets;
	private List<LinkedEntityInfos> linkedEntityInfos;
	private Set<PassThrough> passThrough;
	private Rectangle shadow;
	private PushEntity pushEntity;
	private Direction direction;
	private Elevation elevation;
	private Tags defaultTags;
	private String currentFrameSetName;
	private Entity linkedEntityFront;
	private Entity linkedEntityBack;
	private Entity focusedOn;
	private Entity holder; // Entity que está segurando o objeto
	private Entity holding; // Objeto que a entity está segurando
	private long holdingCTime; // Momento em que a entity segurou o objeto
	private Position holderDesloc; // Deslocamento do sprite do objeto que está sendo segurado
	private Position linkedEntityOffset;
	private TileCoord tileChangedCoord;
	private int currentHeight;
	private TileCoord previewTileCoord;
	private PathFinder pathFinder;
	private Shake shake;
	private int invencibleFrames;
	private int elapsedSteps;
	private int elapsedFrames;
	private int hitPoints;
	private int blinkingFrames;
	private int pushing;
	public int ghostingDistance;
	private float shadowOpacity;
	private double speed;
	private double tempSpeed;
	private boolean noMove;
	private boolean isDisabled;
	private boolean blockedMovement;
	private boolean tileWasChanged;
	private boolean isVisible;
	public Double ghostingOpacityDec;
	private JumpMove jumpMove;
	private GotoMove gotoMove;
	private Consumer<Entity> consumerWhenFrameSetEnds;

	public Entity(Entity entity) {
		super(entity.getPosition());
		shadow = entity.shadow == null ? null : new Rectangle(entity.shadow);
		frameSets = new HashMap<>();
		freshFrameSets = new HashMap<>();
		passThrough = new HashSet<>(entity.passThrough);
		entity.frameSets.keySet().forEach(fSetName -> {
			frameSets.put(fSetName, new FrameSet(entity.frameSets.get(fSetName), this));
			freshFrameSets.put(fSetName, new FrameSet(entity.freshFrameSets.get(fSetName), this));
		});
		ghostingDistance = entity.ghostingDistance;
		ghostingOpacityDec = entity.ghostingOpacityDec;
		speed = entity.speed;
		pushing = entity.pushing;
		tempSpeed = entity.tempSpeed;
		direction = entity.direction;
		elevation = entity.elevation;
		noMove = entity.noMove;
		isDisabled = entity.isDisabled;
		blockedMovement = entity.blockedMovement;
		shadowOpacity = entity.shadowOpacity;
		elapsedSteps = entity.elapsedSteps;
		elapsedFrames = entity.elapsedFrames;
		isVisible = entity.isVisible;
		blinkingFrames = entity.blinkingFrames;
		defaultTags = entity.defaultTags == null ? null : new Tags(defaultTags);
		pushEntity = entity.pushEntity == null ? null : new PushEntity(entity.pushEntity);
		shake = entity.shake == null ? null : new Shake(entity.shake);
		linkedEntityInfos = new LinkedList<>();
		linkedEntityBack = null;
		linkedEntityFront = null;
		linkedEntityOffset = null;
		tileWasChanged = false;
		previewTileCoord = null;
		tileChangedCoord = null;
		jumpMove = null;
		gotoMove = null;
		invencibleFrames = 0;
		hitPoints = entity.hitPoints;
		holder = null;
		holderDesloc = null;
		pathFinder = null;
		focusedOn = null;
		curseDuration = 0;
		currentHeight = 0;
	}

	public Entity() {
		this(0, 0, Direction.DOWN);
	}

	public Entity(int x, int y) {
		this(x, y, Direction.DOWN);
	}

	public Entity(int x, int y, Direction direction) {
		super(x, y);
		currentFrameSetName = null;
		passThrough = new HashSet<>();
		frameSets = new HashMap<>();
		freshFrameSets = new HashMap<>();
		linkedEntityInfos = new ArrayList<>();
		linkedEntityBack = null;
		linkedEntityFront = null;
		linkedEntityOffset = null;
		defaultTags = null;
		shadow = null;
		pushEntity = null;
		shake = null;
		jumpMove = null;
		gotoMove = null;
		focusedOn = null;
		this.direction = direction;
		speed = 0;
		pushing = 0;
		tempSpeed = -1;
		elevation = Elevation.ON_GROUND;
		shadowOpacity = 0;
		elapsedSteps = 0;
		elapsedFrames = 0;
		curseDuration = 0;
		hitPoints = 1;
		noMove = false;
		isVisible = true;
		blinkingFrames = 0;
		isDisabled = false;
		blockedMovement = false;
		tileWasChanged = false;
		previewTileCoord = null;
		tileChangedCoord = null;
		ghostingDistance = 0;
		ghostingOpacityDec = null;
		invencibleFrames = 0;
		currentHeight = 0;
		holder = null;
		holderDesloc = null;
		pathFinder = null;
	}
	
	public void setHeight(int height) {
		currentHeight = height;
	}
	
	public int getHeight() {
		return currentHeight;
	}

	public Entity getFocusedOn() {
		return focusedOn;
	}

	public void setFocusedOn(Entity focusedOn) {
		this.focusedOn = focusedOn;
	}

	public PathFinder getPathFinder() {
		return pathFinder;
	}

	public void setPathFinder(PathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}

	public void setOnFrameSetEndsEvent(Consumer<Entity> consumerWhenFrameSetEnds) {
		this.consumerWhenFrameSetEnds = consumerWhenFrameSetEnds;
	}

	public void setShake(Double incStrength, Double finalStrength) {
		shake = new Shake(incStrength, incStrength, finalStrength, finalStrength);
	}

	public void setShake(Double startStrength, Double incStrength, Double finalStrength) {
		shake = new Shake(startStrength, startStrength, incStrength, incStrength, finalStrength, finalStrength);
	}

	public void setShake(Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY) {
		shake = new Shake(incStrengthX > 0 ? 0 : finalStrengthX, incStrengthY > 0 ? 0 : finalStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);
	}

	public void setShake(Double startStrengthX, Double startStrengthY, Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY) {
		shake = new Shake(startStrengthX, startStrengthY, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);
	}

	public void stopShake() {
		shake.stop();
	}

	public Shake getShake() {
		return shake;
	}

	public void unsetShake() {
		shake = null;
	}

	public Tags getDefaultTags() {
		return defaultTags;
	}

	public void setDefaultTags(Tags tags) {
		defaultTags = new Tags(tags);
	}

	public int getHitPoints() {
		return hitPoints;
	}

	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}

	public void decHitPoints() {
		incHitPoints(-1);
	}

	public void incHitPoints() {
		incHitPoints(1);
	}

	public void incHitPoints(int value) {
		if (hitPoints + value >= 0)
			hitPoints += value;
		else
			hitPoints = 0;
	}

	public boolean isDead() {
		return hitPoints == 0;
	}

	public boolean isInvenible() {
		return invencibleFrames != 0;
	}

	public void setInvencibleFrames(int frames) {
		invencibleFrames = frames;
	}

	public void removeInvencibleFrames() {
		invencibleFrames = 0;
	}

	public boolean isBlockedMovement() {
		return blockedMovement || isDead() || getPathFinder() != null ||
				getPushEntity() != null || getJumpMove() != null || getGotoMove() != null ||
				getHolder() != null || getCurse() == Curse.STUNNED;
	}

	public void setBlockedMovement(boolean state) {
		blockedMovement = state;
	}

	public JumpMove getJumpMove() {
		return jumpMove;
	}

	public JumpMove setJumpMove(double jumpStrenght, double strenghtMultipiler, int durationFrames) {
		setElevation(Elevation.FLYING);
		return (jumpMove = new JumpMove(new Position(), getPosition(), jumpStrenght, strenghtMultipiler, durationFrames));
	}

	public void unsetJumpMove() {
		if (jumpMove != null)
			checkOutScreenCoords();
		jumpMove = null;
	}

	public GotoMove getGotoMove() {
		return gotoMove;
	}

	public GotoMove setGotoMove(Position endPosition, int durationFrames) {
		return setGotoMove(endPosition, durationFrames, false);
	}

	public GotoMove setGotoMove(Position endPosition, int durationFrames, Boolean resetAfterFullCycle) {
		return (gotoMove = new GotoMove(new Position(), getPosition(), endPosition, durationFrames, resetAfterFullCycle));
	}

	public void unsetGotoMove() {
		gotoMove = null;
	}

	public void jumpTo(Entity entity, TileCoord coord, double jumpStrenght, double strenghtMultipiler, int durationFrames) {
		jumpTo(entity, coord, jumpStrenght, strenghtMultipiler, durationFrames, null);
	}

	public void jumpTo(Entity entity, TileCoord coord, double jumpStrenght, double strenghtMultipiler, int durationFrames, String jumpSound) {
		if (jumpSound != null)
			Sound.playWav(entity, jumpSound);
		final JumpMove jumpMove = setJumpMove(jumpStrenght, strenghtMultipiler, durationFrames);
		onJumpStartEvent(getTileCoordFromCenter(), jumpMove);
		jumpMove.setOnCycleCompleteEvent(e -> {
			TileCoord coord2 = getTileCoordFromCenter();
			if (!MapSet.tileIsFree(coord2))
				onJumpFallAtOccupedTileEvent(jumpMove);
			else
				onJumpFallAtFreeTileEvent(jumpMove);
		});
		setGotoMove(coord.getPosition(), durationFrames - 1);
	}

	public void onBeingHoldEvent(Entity holder) {}

	public void onJumpStartEvent(TileCoord coord, JumpMove jumpMove) {}

	public void onJumpFallAtFreeTileEvent(JumpMove jumpMove) {
		checkOutScreenCoords();
		setElevation(Elevation.ON_GROUND);
	}

	public void onJumpFallAtOccupedTileEvent(JumpMove jumpMove) {
		Sound.playWav("TileSlam");
		jumpMove.resetJump(4, 1.2, 14);
		checkOutScreenCoords();
		TileCoord coord = getTileCoordFromCenter().getNewInstance();
		setGotoMove(coord.incCoordsByDirection(getDirection()).getPosition(), jumpMove.getDurationFrames());
	}

	public Set<PassThrough> getPassThrough() {
		return passThrough;
	}

	private void addPassThrough(PassThrough pass) {
		if (!passThrough.contains(pass))
			passThrough.add(pass);
	}

	private void removePassThrough(PassThrough pass) {
		passThrough.remove(pass);
	}

	public void setVisible(boolean state) {
		isVisible = state;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setGhosting(int ghostingDistance, double ghostingOpacityDec) {
		this.ghostingDistance = ghostingDistance;
		this.ghostingOpacityDec = ghostingOpacityDec;
	}

	public void unsetGhosting() {
		ghostingDistance = 0;
		ghostingOpacityDec = null;
	}

	public int getBlinkingFrames() {
		return blinkingFrames;
	}

	public void setBlinkingFrames(int frames) {
		blinkingFrames = frames;
	}

	public boolean isBlinking() {
		return blinkingFrames != 0;
	}

	public void clearPassThrough() {
		passThrough.clear();
	}

	private void setPassThrough(PassThrough passThrough, boolean state) {
		if (state)
			addPassThrough(passThrough);
		else
			removePassThrough(passThrough);
	}

	public void setPassThroughs(boolean state, PassThrough... passThroughs) {
		for (PassThrough passThrough : passThroughs)
			setPassThrough(passThrough, state);
	}

	public void setPassThroughBrick(boolean state) {
		setPassThrough(PassThrough.BRICK, state);
	}

	public void setPassThroughBomb(boolean state) {
		setPassThrough(PassThrough.BOMB, state);
	}

	public void setPassThroughPlayer(boolean state) {
		setPassThrough(PassThrough.PLAYER, state);
	}

	public void setPassThroughMonster(boolean state) {
		setPassThrough(PassThrough.MONSTER, state);
	}

	public void setPassThroughItem(boolean state) {
		setPassThrough(PassThrough.ITEM, state);
	}

	public void setPassThroughWall(boolean state) {
		setPassThrough(PassThrough.WALL, state);
	}

	public void setPassThroughHole(boolean state) {
		setPassThrough(PassThrough.HOLE, state);
	}

	public void setPassThroughWater(boolean state) {
		setPassThrough(PassThrough.WATER, state);
	}

	public boolean canPassThroughBrick() {
		return passThrough.contains(PassThrough.BRICK);
	}

	public boolean canPassThroughBomb() {
		return passThrough.contains(PassThrough.BOMB);
	}

	public boolean canPassThroughMonster() {
		return passThrough.contains(PassThrough.MONSTER);
	}

	public boolean canPassThroughPlayer() {
		return passThrough.contains(PassThrough.PLAYER);
	}

	public boolean canPassThroughHole() {
		return passThrough.contains(PassThrough.HOLE);
	}

	public boolean canPassThroughItem() {
		return passThrough.contains(PassThrough.ITEM);
	}

	public boolean canPassThroughWall() {
		return passThrough.contains(PassThrough.WALL);
	}

	public boolean canPassThroughWater() {
		return passThrough.contains(PassThrough.WATER);
	}

	public void setTileWasChanged(boolean state) {
		tileWasChanged = state;
	}

	public boolean tileWasChanged() {
		return tileWasChanged;
	}

	public boolean isLinkedToAnEntity() {
		return linkedEntityBack != null || linkedEntityFront != null;
	}

	public boolean isLinkedEntityFirst() {
		return linkedEntityFront == null && linkedEntityBack != null;
	}

	public boolean isLinkedEntityLast() {
		return linkedEntityBack == null && linkedEntityFront != null;
	}

	public Entity getLinkedEntityFirst() {
		if (linkedEntityFront == null && linkedEntityBack == null)
			return null;
		if (linkedEntityBack != null && linkedEntityFront == null)
			return this;
		Entity e1 = this, e2 = null;
		while (e1 != null) {
			e2 = e1;
			e1 = e1.getLinkedEntityFront();
		}
		return e2;
	}

	public Entity getLinkedEntityLast() {
		if (linkedEntityFront == null && linkedEntityBack == null)
			return null;
		if (linkedEntityFront != null && linkedEntityBack == null)
			return this;
		Entity e1 = this, e2 = null;
		while (e1 != null) {
			e2 = e1;
			e1 = e1.getLinkedEntityBack();
		}
		return e2;
	}

	public Entity getLinkedEntityBack() {
		return linkedEntityBack;
	}

	public Entity getLinkedEntityFront() {
		return linkedEntityFront;
	}

	public void linkToEntity(Entity entity) {
		linkToEntity(entity, 0, null);
	}

	public void linkToEntity(Entity entity, int delayFrames) {
		linkToEntity(entity, delayFrames, null);
	}

	public void linkToEntity(Entity entity, Position linkedEntityOffset) {
		linkToEntity(entity, 0, linkedEntityOffset);
	}

	public void linkToEntity(Entity entity, int delayFrames, Position linkedEntityOffset) {
		if (linkedEntityFront == null) {
			entity.linkedEntityBack = this;
			linkedEntityFront = entity;
			linkedEntityInfos.clear();
			while (delayFrames-- > 0)
				linkedEntityInfos.add(new LinkedEntityInfos(entity));
			this.linkedEntityOffset = linkedEntityOffset == null ? new Position() : new Position(linkedEntityOffset);
		}
	}

	public void unlinkFromLinkedEntity() {
		if (linkedEntityBack != null && linkedEntityFront != null) {
			linkedEntityFront.linkedEntityBack = linkedEntityBack;
			linkedEntityBack.linkedEntityFront = linkedEntityFront;
		}
		else if (linkedEntityFront != null) {
			linkedEntityFront.linkedEntityBack = null;
			if (linkedEntityFront.linkedEntityFront == null)
				linkedEntityFront.clearLinkedEntityStuffs();
		}
		else if (linkedEntityBack != null) {
			linkedEntityBack.linkedEntityFront = null;
			if (linkedEntityBack.linkedEntityBack == null)
				linkedEntityBack.clearLinkedEntityStuffs();
		}
		clearLinkedEntityStuffs();
	}

	private void clearLinkedEntityStuffs() {
		linkedEntityBack = null;
		linkedEntityFront = null;
		linkedEntityOffset = null;
		linkedEntityInfos.clear();
	}

	public Entity getHolder() {
		return holder;
	}

	public void setHolder(Entity holder) {
		if (getHoldingEntity() != null)
			unsetHoldingEntity();
		unsetAllMovings();
		this.holder = holder;
		holderDesloc = new Position();
		if (haveFrameSet("BeingHolded"))
			setFrameSet("BeingHolded");
	}

	private void unsetAllMovings() {
		unsetGotoMove();
		unsetGhosting();
		unsetJumpMove();
		unsetPushEntity();
		unsetShake();
	}

	public Position getHolderDesloc() {
		return holderDesloc;
	}

	public void setHolderDesloc(int x, int y) {
		holderDesloc.setPosition(x, y);
	}

	public void incHolderDesloc(int x, int y) {
		holderDesloc.incPosition(x, y);
	}

	public void unsetHolder() { 
		int distance = (int)((System.currentTimeMillis() - holder.getHoldingCTime()) + 200) / 200;
		if (distance < 2)
			distance = 2;
		if (distance > 5)
			distance = 5;
		holder = null;
		holderDesloc = null;
		TileCoord coord = getTileCoordFromCenter().getNewInstance().incCoordsByDirection(getDirection(), distance);
		jumpTo(this, coord, distance + 1, 1.2, 20);
	}

	public Entity getHoldingEntity() {
		return holding;
	}

	public void setHoldingEntity(Entity entity) {
		holdingCTime = System.currentTimeMillis();
		holding = entity;
		entity.setHolder(this);
		entity.onBeingHoldEvent(this);
	}

	public void unsetHoldingEntity() {
		holding.unsetHolder();
		holding = null;
	}

	public long getHoldingCTime() {
		return holdingCTime;
	}

	public Map<String, FrameSet> getFrameSetsMap() {
		return frameSets;
	}

	public void setFrameSetMap(Map<String, FrameSet> frameSetMap) {
		frameSets = frameSetMap;
	}

	public Collection<FrameSet> getFrameSets() {
		return frameSets.values();
	}

	public Collection<String> getFrameSetsNames() {
		return frameSets.keySet();
	}

	public String getCurrentFrameSetName() {
		return currentFrameSetName;
	}

	public boolean currentFrameSetNameIsEqual(String string) {
		return currentFrameSetName.equals(string) || (currentFrameSetName.length() > string.length() && currentFrameSetName.charAt(string.length()) == '.' && currentFrameSetName.substring(0, string.length()).equals(string));
	}

	public FrameSet getCurrentFrameSet() {
		return getFrameSet(currentFrameSetName);
	}

	public FrameSet getFrameSet(String frameSetName) {
		String frameSetNameWithDir = frameSetName + "." + getDirection().name();
		if (frameSets.containsKey(frameSetNameWithDir))
			frameSetName = frameSetNameWithDir;
		return frameSets.get(frameSetName);
	}

	public void setFrameSet(String frameSetName) {
		String frameSetNameWithDir = frameSetName + "." + getDirection().name();
		if (frameSets.containsKey(frameSetNameWithDir))
			frameSetName = frameSetNameWithDir;
		if (currentFrameSetName != null && frameSetName != null && currentFrameSetName.equals(frameSetName))
			return;
		if (!frameSets.containsKey(frameSetName))
			throw new RuntimeException(frameSetName + " - Invalid FrameSet name for this entity");
		else if (frameSetName.equals(currentFrameSetName))
			return;
		frameSets.put(frameSetName, new FrameSet(freshFrameSets.get(frameSetName), this));
		currentFrameSetName = frameSetName;
	}

	public void addFrameSet(String frameSetName, FrameSet frameSet) {
		if (frameSets.containsKey(frameSetName))
			throw new RuntimeException(frameSetName + " - This entity already have a FrameSet with this name. Use 'replaceFrameSet()' instead.");
		frameSets.put(frameSetName, frameSet);
		freshFrameSets.put(frameSetName, new FrameSet(frameSet, this));
	}

	public void replaceFrameSet(String existingFrameSetName, FrameSet newFrameSet) {
		removeFrameSet(existingFrameSetName);
		addFrameSet(existingFrameSetName, newFrameSet);
	}

	public void removeFrameSet(String frameSetName) {
		if (!frameSets.containsKey(frameSetName))
			throw new RuntimeException(frameSetName + " - This entity don't have a FrameSet with this name.");
		frameSets.remove(frameSetName);
		freshFrameSets.remove(frameSetName);
	}

	public boolean haveFrameSet(String frameSetName) {
		String frameSetNameWithDir = frameSetName + "." + getDirection().name();
		return frameSets.containsKey(frameSetNameWithDir) || frameSets.containsKey(frameSetName);
	}

	public PushEntity getPushEntity() {
		return pushEntity;
	}

	public void setPushEntity(Double strenght, Consumer<Entity> consumerWhenCollides) {
		pushEntity = new PushEntity(this, strenght, null, getDirection()).setOnColideEvent(consumerWhenCollides);
	}

	public void setPushEntity(Double strenght, Direction direction, Consumer<Entity> consumerWhenCollides) {
		pushEntity = new PushEntity(this, strenght, null, direction).setOnColideEvent(consumerWhenCollides);
	}

	public void setPushEntity(Double startStrenght, Double decStrenght, Consumer<Entity> consumerWhenCollides) {
		pushEntity = new PushEntity(this, startStrenght, decStrenght, getDirection()).setOnColideEvent(consumerWhenCollides);
	}

	public void setPushEntity(Double startStrenght, Double decStrenght, Direction direction, Consumer<Entity> consumerWhenCollides) {
		pushEntity = new PushEntity(this, startStrenght, decStrenght, direction).setOnColideEvent(consumerWhenCollides);
	}

	public void setPushEntity(Double strenght) {
		pushEntity = new PushEntity(this, strenght, null, getDirection());
	}

	public void setPushEntity(Double strenght, Direction direction) {
		pushEntity = new PushEntity(this, strenght, null, direction);
	}

	public void setPushEntity(Double startStrenght, Double decStrenght) {
		pushEntity = new PushEntity(this, startStrenght, decStrenght, getDirection());
	}

	public void setPushEntity(Double startStrenght, Double decStrenght, Direction direction) {
		pushEntity = new PushEntity(this, startStrenght, decStrenght, direction);
	}

	public void setPushEntity(PushEntity pushEntity) {
		this.pushEntity = pushEntity;
	}

	public void unsetPushEntity() {
		pushEntity = null;
	}

	public void removeCurse() {
		if (curse == Curse.INVISIBLE)
			setVisible(true);
		curse = null;
	}

	public Curse getCurse() {
		return curse;
	}
	
	public int getCurseDuration() {
		return curseDuration;
	}
	
	public void setCurseDuration(int duration) {
		curseDuration = duration;
	}

	public void setCurse(Curse curse) { // FALTA: Implementar BLINDNESS e SWAP_PLAYERS
		if (curse != null) {
			removeCurse();
			this.curse = curse;
			curseDuration = Curse.getDuration(curse);
			if (curse == Curse.INVISIBLE)
				setVisible(false);
			else if (curse == Curse.MIN_SPEED)
				setTempSpeed(0.25);
			else if (curse == Curse.ULTRA_SPEED)
				setTempSpeed(GameConfigs.MAX_PLAYER_SPEED);
			else if (curse == Curse.INVISIBLE)
				setVisible(false);
			else if (curse == Curse.STUNNED) {
				Effect.runEffect(MapSet.getInitialPlayerPosition(0), "STUN")
					.setClosingPredicate(this, e -> e.isDead() || e.getCurse() != Curse.STUNNED)
					.linkTo(this, 0, getHeight());

			}
		}
	}
	
	public void run() {
		run(null, false);
	}

	public void run(boolean isPaused) {
		run(null, isPaused);
	}

	public void run(GraphicsContext gc) {
		run(gc, false);
	}

	public void run(GraphicsContext gc, boolean isPaused) {
		if (!(this instanceof Effect))
			removeEntityFromList(getTileCoordFromCenter(), this);
		if (curse != null && --curseDuration == 0)
			removeCurse();
		if (blinkingFrames != 0)
			blinkingFrames--;
		if (invencibleFrames != 0)
			invencibleFrames--;
		if (previewTileCoord == null) {
			previewTileCoord = getTileCoordFromCenter().getNewInstance();
			tileChangedCoord = getTileCoordFromCenter().getNewInstance();
			tileWasChanged = true;
		}
		if (jumpMove != null) {
			jumpMove.move();
			if (jumpMove.jumpIsFinished())
				unsetJumpMove();
		}
		if (getPathFinder() != null) {
			if (!getPathFinder().pathWasFound())
				setPathFinder(null);
			else {
				if (isPerfectTileCentred())
					forceDirection(getPathFinder().getNextDirectionToGoAndRemove());
				moveEntity(getDirection(), getSpeed());
			}
		}
		if (gotoMove != null) {
			gotoMove.move();
			if (gotoMove.isCycleCompleted()) {
				unsetGotoMove();
				checkOutScreenCoords();
			}
			else {
				incPosition(gotoMove.getIncrements());
				boolean[] corners = getFreeCorners();
				for (boolean b : corners)
					if (!b) {
						decPosition(gotoMove.getIncrements());
						break;
					}
			}
		}
		if (pushEntity != null) {
			pushEntity.process();
			if (!pushEntity.isActive()) {
				pushEntity = null;
				setBlockedMovement(false);
				checkOutScreenCoords();
			}
		}
		if (getHolder() != null) {
			setPosition(getHolder().getPosition());
			forceDirection(getHolder().getDirection());
		}
		if (!isDisabled) {
			if (frameSets.isEmpty())
				throw new RuntimeException("This entity have no FrameSets");
			if (currentFrameSetName != null && frameSets.containsKey(currentFrameSetName)) {
				processLinkedEntity();
				if (!isBlockedMovement())
					moveEntity();
				applyShadow();
				frameSets.get(currentFrameSetName).run(gc, isPaused);
			}
			elapsedFrames++;
		}
		if (!getCurrentFrameSet().isRunning() && consumerWhenFrameSetEnds != null)
			consumerWhenFrameSetEnds.accept(this);
		if (!(this instanceof Effect))
			addEntityToList(getTileCoordFromCenter(), this);
		setHeight((jumpMove == null ? 0 : -((int)jumpMove.getIncrements().getY()) + (int)getCurrentFrameSet().getY()));
	}

	private void processLinkedEntity() {
		if (linkedEntityFront != null) {
			if (linkedEntityInfos.isEmpty()) {
				setPosition(linkedEntityFront.getX() + linkedEntityOffset.getX(), linkedEntityFront.getY() + linkedEntityOffset.getY());
				if (direction != linkedEntityFront.getDirection())
					setDirection(linkedEntityFront.getDirection());
			}
			else {
				setPosition(linkedEntityInfos.get(0).x + linkedEntityOffset.getX(), linkedEntityInfos.get(0).y + linkedEntityOffset.getY());
				if (direction != linkedEntityInfos.get(0).direction)
					setDirection(linkedEntityInfos.get(0).direction);
				linkedEntityInfos.remove(0);
				linkedEntityInfos.add(new LinkedEntityInfos(linkedEntityFront));
			}
		}
	}

	private void applyShadow() {
		if (haveShadow()) {
			Draw.addDrawQueue((int) getY(), SpriteLayerType.SPRITE, DrawType.SAVE);
			Draw.addDrawQueue((int) getY(), SpriteLayerType.SPRITE, DrawType.SET_FILL, Color.BLACK);
			Draw.addDrawQueue((int) getY(), SpriteLayerType.SPRITE, DrawType.SET_GLOBAL_ALPHA, shadowOpacity != 0 ? shadowOpacity : (double)getShadowHeight() / 5);
			Draw.addDrawQueue((int) getY(), SpriteLayerType.SPRITE, DrawType.FILL_OVAL, getX() + Main.TILE_SIZE / 2 - getShadowWidth() / 2, getY() + Main.TILE_SIZE - getShadowHeight() * 2 + getShadowHeight() * 0.5, getShadowWidth(), getShadowHeight());
			Draw.addDrawQueue((int) getY(), SpriteLayerType.SPRITE, DrawType.RESTORE);
		}
	}

	public boolean isMoving() {
		return getSpeed() != 0 || getPushEntity() != null || getPathFinder() != null;
	}

	public void setPushingValue(int value) {
		pushing = value;
	}

	public int getPushingValue() {
		return pushing;
	}

	public boolean[] getFreeCorners() {
		return getFreeCorners(null);
	}

	
	public boolean[] getFreeCorners(Direction direction) {
		boolean[] freeCorners = new boolean[4];
		int z = -1;
		for (Position pos : getCornersPositions()) {
			if (direction != null)
				pos.incPositionByDirection(direction);
			freeCorners[++z] = tileIsFree(pos.getTileCoord());
			for (int n = 0; n < 3; n++)
				for (Entity entity : freeCorners[z] && n == 0 ? Brick.getBricks() : n == 1 ? Bomb.getBombs() : Monster.getMonsters()) {
					if (entity != this && entity.getElevation() == getElevation() &&
							((n == 0 && !canPassThroughBrick()) ||
							(n == 1 && ((Bomb) entity).getBombType() != BombType.LAND_MINE && (!((Bomb) entity).ownerIsOver(this) && !canPassThroughBomb())) ||
							(n == 2 && !canPassThroughMonster()))) {
								int x = (int) pos.getX(), y = (int) pos.getY();
								int xx = (int) entity.getX(), yy = (int) entity.getY();
								if (x >= xx && y >= yy && x <= xx + Main.TILE_SIZE && y <= yy + Main.TILE_SIZE) {
									freeCorners[z] = false;
									break;
								}
					}
				}
		}
		return freeCorners;
	}

	public boolean tileIsFree(TileCoord coord) {
		return MapSet.tileIsFree(this, coord, passThrough);
	}

	public boolean tileIsFree(Direction direction) {
		return tileIsFree(getTileCoordFromCenter().getNewInstance().incCoordsByDirection(direction));
	}

	public Position[] getCornersPositions() {
		Position[] cornersPositions = new Position[4];
		cornersPositions[0] = new Position(getX(), getY());
		cornersPositions[1] = new Position(getX() + Main.TILE_SIZE - 1, getY());
		cornersPositions[2] = new Position(getX(), getY() + Main.TILE_SIZE - 1);
		cornersPositions[3] = new Position(getX() + Main.TILE_SIZE - 1, getY() + Main.TILE_SIZE - 1);
		return cornersPositions;
	}

	public boolean isPerfectlyFreeDir(Direction dir) {
		boolean[] freeCorners = getFreeCorners(dir);
		return (dir == Direction.LEFT && freeCorners[0] && freeCorners[2]) || (dir == Direction.UP && freeCorners[0] && freeCorners[1]) || (dir == Direction.RIGHT && freeCorners[1] && freeCorners[3]) || (dir == Direction.DOWN && freeCorners[2] && freeCorners[3]);
	}

	public boolean isPerfectlyBlockedDir(Direction dir) {
		boolean[] freeCorners = getFreeCorners(dir);
		return (dir == Direction.LEFT && !freeCorners[0] && !freeCorners[2]) || (dir == Direction.UP && !freeCorners[0] && !freeCorners[1]) || (dir == Direction.RIGHT && !freeCorners[1] && !freeCorners[3]) || (dir == Direction.DOWN && !freeCorners[2] && !freeCorners[3]);
	}

	public void moveEntity() {
		moveEntity(getDirection());
	}

	public void moveEntity(double speed) {
		moveEntity(getDirection(), speed);
	}

	public void moveEntity(Direction direction) {
		moveEntity(direction, getTempSpeed() >= 0 ? getTempSpeed() : getSpeed());
	}

	public void moveEntity(Direction direction, double speed) {
		tileWasChanged = false;
		getFreeCorners(direction);
		if (speed != 0) {
			Position[] cornersPositions = getCornersPositions();
			Position lu = cornersPositions[0], ru = cornersPositions[1], ld = cornersPositions[2], rd = cornersPositions[3];
			boolean[] freeCorners = getFreeCorners(direction);
			int z = 10, prevX = (int) getX() / Main.TILE_SIZE, prevY = (int) getY() / Main.TILE_SIZE;
			if (direction == Direction.UP) {
				if (freeCorners[0] && freeCorners[1]) {
					incPositionByDirection(direction, speed);
					if (isPerfectlyBlockedDir(direction))
						centerToTile();
					pushing = 0;
				}
				else {
					if (!freeCorners[0] && freeCorners[1] && (int) ru.getX() % Main.TILE_SIZE > Main.TILE_SIZE / z)
						incPosition(speed, -speed / 2);
					else if (freeCorners[0] && !freeCorners[1] && (int) lu.getX() % Main.TILE_SIZE < Main.TILE_SIZE - Main.TILE_SIZE / z)
						incPosition(-speed, -speed / 2);
					else
						pushing++;
					if (prevX != (int) getX() / Main.TILE_SIZE)
						centerXToTile();
				}
			}
			else if (direction == Direction.DOWN) {
				if (freeCorners[2] && freeCorners[3]) {
					incPositionByDirection(direction, speed);
					if (isPerfectlyBlockedDir(direction))
						centerToTile();
					pushing = 0;
				}
				else {
					if (!freeCorners[2] && freeCorners[3] && (int) rd.getX() % Main.TILE_SIZE > Main.TILE_SIZE / z)
						incPosition(speed, speed / 2);
					else if (freeCorners[2] && !freeCorners[3] && (int) ld.getX() % Main.TILE_SIZE < Main.TILE_SIZE - Main.TILE_SIZE / z)
						incPosition(-speed, speed / 2);
					else
						pushing++;
					if (prevX != (int) getX() / Main.TILE_SIZE)
						centerXToTile();
				}
			}
			else if (direction == Direction.LEFT) {
				if (freeCorners[0] && freeCorners[2]) {
					incPositionByDirection(direction, speed);
					if (isPerfectlyBlockedDir(direction))
						centerToTile();
					pushing = 0;
				}
				else {
					if (!freeCorners[0] && freeCorners[2] && (int) ld.getY() % Main.TILE_SIZE > Main.TILE_SIZE / z)
						incPosition(-speed / 2, speed);
					else if (freeCorners[0] && !freeCorners[2] && (int) lu.getY() % Main.TILE_SIZE < Main.TILE_SIZE - Main.TILE_SIZE / z)
						incPosition(-speed / 2, -speed);
					else
						pushing++;
					if (prevY != (int) getY() / Main.TILE_SIZE)
						centerYToTile();
				}
			}
			else if (direction == Direction.RIGHT) {
				if (freeCorners[1] && freeCorners[3]) {
					incPositionByDirection(direction, speed);
					if (isPerfectlyBlockedDir(direction))
						centerToTile();
					pushing = 0;
				}
				else {
					if (!freeCorners[1] && freeCorners[3] && (int) rd.getY() % Main.TILE_SIZE > Main.TILE_SIZE / z)
						incPosition(speed / 2, speed);
					else if (freeCorners[1] && !freeCorners[3] && (int) ru.getY() % Main.TILE_SIZE < Main.TILE_SIZE - Main.TILE_SIZE / z)
						incPosition(speed / 2, -speed);
					else
						pushing++;
					if (prevY != (int) getY() / Main.TILE_SIZE)
						centerYToTile();
				}
			}
			checkOutScreenCoords();
			if (!getTileCoordFromCenter().equals(tileChangedCoord)) {
				Position pos = getTileCoordFromCenter().getPosition();
				int x = (int) getX() + Main.TILE_SIZE / 2, y = (int) getY() + Main.TILE_SIZE / 2, xx = (int) pos.getX() + Main.TILE_SIZE / 2, yy = (int) pos.getY() + Main.TILE_SIZE / 2;
				if (x >= xx - Main.TILE_SIZE / 4 && y >= yy - Main.TILE_SIZE / 4 && x <= xx + Main.TILE_SIZE / 4 && y <= yy + Main.TILE_SIZE / 4)
					updatePreviewTileCoord();
			}
			if ((this instanceof Bomb || this instanceof Brick) && Item.haveItemAt(getTileCoordFromCenter()))
				Item.getItemAt(getTileCoordFromCenter()).destroy();
		}
		else
			pushing = 0;
	}
	
	public void checkOutScreenCoords() {
		int minX = (int)MapSet.getMapMinLimit().getX() + Main.TILE_SIZE / 2;
		int maxX = (int)MapSet.getMapMaxLimit().getX() + Main.TILE_SIZE / 2;
		int minY = (int)MapSet.getMapMinLimit().getY();
		int maxY = (int)MapSet.getMapMaxLimit().getY() + Main.TILE_SIZE;
		if (getX() < minX)
			setX(maxX);
		else if (getX() > maxX) {
			setX(minX);
		}
		if (getY() < minY)
			setY(maxY);
		else if (getY() > maxY) {
			setY(minY);
		}
	}

	public TileCoord getPreviewTileCoord() {
		return previewTileCoord;
	}

	private void updatePreviewTileCoord() {
		previewTileCoord.setCoords(tileChangedCoord);
		tileChangedCoord.setCoords(getTileCoordFromCenter());
		elapsedSteps++;
		tileWasChanged = true;
	}

	public int getElapsedSteps() {
		return elapsedSteps;
	}

	public void setElapsedSteps(int elapsedSteps) {
		this.elapsedSteps = elapsedSteps;
	}

	public int getElapsedFrames() {
		return elapsedFrames;
	}

	public void setElapsedFrames(int elapsedFrames) {
		this.elapsedFrames = elapsedFrames;
	}

	public int getTotalFrameSets() {
		return frameSets.size();
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		if ((direction.isVertical() && getX() >= MapSet.getMapMinLimit().getX() && getX() <= MapSet.getMapMaxLimit().getX()) ||
				(direction.isHorizontal() && getY() >= MapSet.getMapMinLimit().getY() && getY() <= MapSet.getMapMaxLimit().getY()))
					setDirection(direction, false);
	}

	public void forceDirection(Direction direction) {
		setDirection(direction, true);
	}

	private void setDirection(Direction direction, boolean force) {
		if ((!isBlockedMovement() || force) && this.direction != direction) {
			String name = currentFrameSetName;
			int i = name.indexOf('.');
			if (i > 1)
				name = name.substring(0, i);
			name += "." + direction.name();
			if (frameSets.containsKey(name))
				setFrameSet(name);
			this.direction = direction;
		}
	}

	public Elevation getElevation() {
		return elevation;
	}

	public void setElevation(Elevation elevation) {
		this.elevation = elevation;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getTempSpeed() {
		return tempSpeed;
	}

	public void setTempSpeed(double tempSpeed) {
		this.tempSpeed = tempSpeed;
	}

	public void setNoMove(boolean state) {
		noMove = state;
	}

	public boolean getNoMove() {
		return noMove;
	}

	public void setDisabled() {
		isDisabled = true;
	}

	public void setEnabled() {
		isDisabled = false;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setShadow(int offsetX, int offsetY, int width, int height, float opacity) {
		if (shadow == null)
			shadow = new Rectangle(offsetX, offsetY, width, height);
		shadow.setBounds(offsetX, offsetY, width, height);
		shadowOpacity = opacity;
	}

	public int getShadowOffsetX() {
		return shadow == null ? 0 : (int) shadow.getX();
	}

	public int getShadowOffsetY() {
		return shadow == null ? 0 : (int) shadow.getY();
	}

	public int getShadowWidth() {
		int w = shadow == null ? 0 : (int) shadow.getWidth();
		int hh = -((int)getCurrentFrameSet().getSprite(0).outputSpriteSizePos.getY()) + getHeight();
		return w - hh / 5;
	}

	public int getShadowHeight() {
		int h = shadow == null ? 0 : (int) shadow.getHeight();
		int hh = -((int)getCurrentFrameSet().getSprite(0).outputSpriteSizePos.getY()) + getHeight();
		return h - hh / 5;
	}

	public float getShadowOpacity() {
		return shadowOpacity;
	}

	public void setShadowOffsetX(int value) {
		shadow.setLocation(value, getShadowOffsetY());
	}

	public void setShadowOffsetY(int value) {
		shadow.setLocation(getShadowOffsetX(), value);
	}

	public void setShadowWidth(int value) {
		shadow.setSize(value, (int) shadow.getHeight());
	}

	public void setShadowHeight(int value) {
		shadow.setSize((int) shadow.getWidth(), value);
	}

	public void setShadowOpacity(float value) {
		shadowOpacity = value;
	}

	public void removeShadow() {
		shadow = null;
	}

	public boolean haveShadow() {
		return shadow != null;
	}

	public void addNewFrameSetFromString(String frameSetName, String stringWithFrameTags) {
		FrameSet frameSet;
		if (frameSets.containsKey(stringWithFrameTags))
			frameSet = new FrameSet(frameSets.get(stringWithFrameTags), this);
		else {
			frameSet = new FrameSet(this);
			frameSet.loadFromString(stringWithFrameTags);
		}
		addFrameSet(frameSetName, frameSet);
	}

	public void replaceFrameSetFromString(String existingFrameSetName, String stringWithFrameTags) {
		FrameSet frameSet;
		if (frameSets.containsKey(stringWithFrameTags))
			frameSet = new FrameSet(frameSets.get(stringWithFrameTags), this);
		else {
			frameSet = new FrameSet(this);
			frameSet.loadFromString(stringWithFrameTags);
		}
		replaceFrameSet(existingFrameSetName, frameSet);
	}

	public boolean canCross(TileCoord coord) {
		// NOTA: Implementar a parte de mob nao passar por mob
		Elevation elevation = getElevation();
		if (elevation != Elevation.HIGH_FLYING)
			return true;
		for (TileProp prop : MapSet.getCurrentLayer().getTileProps(coord)) {
			if (TileProp.getCantCrossList(elevation).contains(prop) || (Brick.haveBrickAt(coord, true) && !canPassThroughBrick()) || (Bomb.haveBombAt(this, coord) && !canPassThroughBomb()))
				return false;
		}
		return true;
	}

	public void restartCurrentFrameSet() {
		setFrameSet(currentFrameSetName);
	}

	public void takeDamage() {
		if (!isInvenible() && !isDead() && !(this instanceof Effect)) {
			if (getHoldingEntity() != null)
				unsetHoldingEntity();
			if (--hitPoints == 0) {
				setFrameSet("Dead");
				if (this instanceof BomberMan)
					((BomberMan) this).decLives();
			}
			else if (haveFrameSet("TakingDamage"))
				setFrameSet("TakingDamage");
			else if (this instanceof BomberMan) {
				setInvencibleFrames(GameConfigs.PLAYER_INVENCIBLE_FRAMES_AFTER_TAKING_DAMAGE);
				setBlinkingFrames(GameConfigs.PLAYER_INVENCIBLE_FRAMES_AFTER_TAKING_DAMAGE);
			}
			else {
				setInvencibleFrames(GameConfigs.MONSTER_INVENCIBLE_FRAMES_AFTER_TAKING_DAMAGE);
				setBlinkingFrames(GameConfigs.MONSTER_INVENCIBLE_FRAMES_AFTER_TAKING_DAMAGE);
			}
		}
	}

	public static Set<Entity> getEntityListFromCoord(TileCoord coord) {
		if (entityMap.containsKey(coord))
			return entityMap.get(coord);
		return null;
	}

	public static Set<Entity> getEntityList() {
		return entityMap2.keySet();
	}

	public static Entity getFirstEntityFromCoord(TileCoord coord) {
		if (entityMap.containsKey(coord) && !entityMap.get(coord).isEmpty())
			return entityMap.get(coord).iterator().next();
		return null;
	}

	public static <T> Boolean entitiesInCoordContaisAnInstanceOf(TileCoord coord, Class<T> clazz) {
		if (!haveAnyEntityAtCoord(coord))
			throw new RuntimeException("There's no entities at coord " + coord);
		for (Entity entity : entityMap.get(coord))
			if (clazz.isInstance(entity))
				return true;
		return false;
	}

	public static void addEntityToList(TileCoord coord, Entity entity) {
		if (entity instanceof Bomb || entity instanceof Item || entity instanceof Brick)
			return;
		if (!entityMap.containsKey(coord))
			entityMap.put(coord, new LinkedHashSet<>());
		entityMap.get(coord).add(entity);
		entityMap2.put(entity, coord);
	}

	public static void removeEntityFromList(TileCoord coord, Entity entity) {
		if (entityMap.containsKey(coord)) {
			entityMap.get(coord).remove(entity);
			entityMap2.remove(entity);
		}
	}

	public static boolean entityIsAtCoord(Entity entity, TileCoord coord) {
		return entityMap.containsKey(coord) && entityMap.get(coord).contains(entity);
	}

	public static boolean haveAnyEntityAtCoord(TileCoord coord) {
		return haveAnyEntityAtCoord(coord, null);
	}

	public static boolean haveAnyEntityAtCoord(TileCoord coord, Entity ignoreEntity) {
		return entityMap.containsKey(coord) && (entityMap.get(coord).size() > 1 || (!entityMap.get(coord).isEmpty() && !entityMap.get(coord).contains(ignoreEntity)));
	}

}

class LinkedEntityInfos {

	double x;
	double y;
	Direction direction;

	public LinkedEntityInfos(Entity entity) {
		x = entity.getX();
		y = entity.getY();
		direction = entity.getDirection();
	}

}