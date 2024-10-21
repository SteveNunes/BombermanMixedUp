package entities;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import application.Main;
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
import maps.MapSet;
import objmoveutils.Position;
import tools.Tools;

public class Entity extends Position {
	
	private Rectangle shadow;
	private Map<String, FrameSet> frameSets;
	private Map<String, FrameSet> freshFrameSets;
	private List<LinkedEntityInfos> linkedEntityInfos;
	private List<PassThrough> passThrough;
	private double speed;
	private double tempSpeed;
	private Direction direction;
	private Elevation elevation;
	private Tags defaultTags;
	private String currentFrameSetName;
	private Entity linkedEntityFront;
	private Entity linkedEntityBack;
	private Position linkedEntityOffset;
	private TileCoord previewTileCoord;
	private boolean noMove;
	private boolean isDisabled;
	private boolean blockedMovement;
	private float shadowOpacity;
	private int elapsedSteps;
	private int elapsedFrames;
	private boolean tileWasChanged;
	private boolean isVisible;
	
	public Entity(Entity entity) {
		super(entity.getPosition());
		setTileSize(Main.TILE_SIZE);
		shadow = entity.shadow == null ? null : new Rectangle(entity.shadow);
		frameSets = new HashMap<>();
		freshFrameSets = new HashMap<>();
		passThrough = new ArrayList<>(entity.passThrough);
		entity.frameSets.keySet().forEach(fSetName -> {
			frameSets.put(fSetName, new FrameSet(entity.frameSets.get(fSetName), this));
			freshFrameSets.put(fSetName, new FrameSet(entity.freshFrameSets.get(fSetName), this));
		});
		speed = entity.speed;
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
		defaultTags = entity.defaultTags == null ? null : new Tags(defaultTags);
		previewTileCoord = new TileCoord();
		linkedEntityInfos = new LinkedList<>();
		linkedEntityBack = null;
		linkedEntityFront = null;
		linkedEntityOffset = null;
		tileWasChanged = false;
	}
	
	public Entity()
		{ this(0, 0, Direction.DOWN); }

	public Entity(int x, int y)
		{ this(x, y, Direction.DOWN); }

	public Entity(int x, int y, Direction direction) {
		super(x, y);
		setTileSize(Main.TILE_SIZE);
		currentFrameSetName = null;
		passThrough = new ArrayList<>();
		frameSets = new HashMap<>();
		freshFrameSets = new HashMap<>();
		previewTileCoord = new TileCoord();
		linkedEntityInfos = new ArrayList<>();
		linkedEntityBack = null;
		linkedEntityFront = null;
		linkedEntityOffset = null;
		defaultTags = null;
		shadow = null;
		this.direction = direction;
		speed = 0;
		tempSpeed = -1;
		elevation = Elevation.ON_GROUND;
		shadowOpacity = 0;
		elapsedSteps = 0;
		elapsedFrames = 0;
		noMove = false;
		isVisible = true;
		isDisabled = false;
		blockedMovement = false;
		tileWasChanged = false;
	}
	
	public Tags getDefaultTags()
		{ return defaultTags; }

	public void setDefaultTags(Tags tags)
		{ defaultTags = new Tags(tags); }

	public boolean isBlockedMovement()
		{ return blockedMovement; }
	
	public void setBlockedMovement(boolean state)
		{ blockedMovement = state; }
	
	public List<PassThrough> getPassThrough()
		{ return passThrough; }

	private void addPassThrough(PassThrough pass) {
		if (!passThrough.contains(pass))
			passThrough.add(pass);
	}
	
	private void removePassThrough(PassThrough pass)
		{ passThrough.remove(pass); }
	
	public void setVisible(boolean state)
		{ isVisible = state; }
	
	public boolean isVisible()
		{ return isVisible; }
	
	public void setPassThroughBrick(boolean state) {
		if (state)
			addPassThrough(PassThrough.BRICK);
		else
			removePassThrough(PassThrough.BRICK);
	}
	
	public void setPassThroughBomb(boolean state) {
		if (state)
			addPassThrough(PassThrough.BOMB);
		else
			removePassThrough(PassThrough.BOMB);
	}
	
	public void setPassThroughMonster(boolean state) {
		if (state)
			addPassThrough(PassThrough.MONSTER);
		else
			removePassThrough(PassThrough.MONSTER);
	}
	
	public boolean canPassThroughBrick()
		{ return passThrough.contains(PassThrough.BRICK); }
	
	public boolean canPassThroughBomb()
		{ return passThrough.contains(PassThrough.BOMB); }

	public boolean canPassThroughMonster()
		{ return passThrough.contains(PassThrough.MONSTER); }

	public TileCoord getTileCoord()
		{ return new TileCoord(getTileX(), getTileY()); }
	
	public boolean tileWasChanged()
		{ return tileWasChanged; }

	public boolean isLinkedToAnEntity()
		{ return linkedEntityBack != null || linkedEntityFront != null; }

	public boolean isLinkedEntityFirst()
		{ return linkedEntityFront == null && linkedEntityBack != null; }
	
	public boolean isLinkedEntityLast()
		{ return linkedEntityBack == null && linkedEntityFront != null; }

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
	
	public Entity getLinkedEntityBack()
		{ return linkedEntityBack; }
	
	public Entity getLinkedEntityFront()
		{ return linkedEntityFront; }

	public void linkToEntity(Entity entity)
		{ linkToEntity(entity, 0, null); }

	public void linkToEntity(Entity entity, int delayFrames)
		{ linkToEntity(entity, delayFrames, null); }
	
	public void linkToEntity(Entity entity, Position linkedEntityOffset)
		{ linkToEntity(entity, 0, linkedEntityOffset); }
	
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

	public Map<String, FrameSet> getFrameSetsMap()
		{ return frameSets; }
	
	public void setFrameSetMap(Map<String, FrameSet> frameSetMap)
		{ frameSets = frameSetMap; }
	
	public Collection<FrameSet> getFrameSets()
		{ return frameSets.values(); }
	
	public Collection<String> getFrameSetsNames()
		{ return frameSets.keySet(); }
	
	public String getCurrentFrameSetName()
		{ return currentFrameSetName; }

	public boolean currentFrameSetNameIsEqual(String string) {
		return currentFrameSetName.equals(string) ||
					 (currentFrameSetName.length() > string.length() &&
						currentFrameSetName.charAt(string.length()) == '.' &&
						currentFrameSetName.substring(0, string.length()).equals(string));
	}
	
	public FrameSet getCurrentFrameSet()
		{ return getFrameSet(currentFrameSetName); }

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
	
	public boolean haveFrameSet(String frameSetName)
		{ return frameSets.containsKey(frameSetName); }

	public void run()
		{ run(null, false); }

	public void run(boolean isPaused)
		{ run(null, isPaused); }

	public void run(GraphicsContext gc)
		{ run(gc, false); }

	public void run(GraphicsContext gc, boolean isPaused) {
		if (!isDisabled) {
			if (frameSets.isEmpty())
				throw new RuntimeException("This entity have no FrameSets");
			if (currentFrameSetName != null && frameSets.containsKey(currentFrameSetName)) {
				processLinkedEntity();
				if (!blockedMovement)
					moveEntity();
				applyShadow();
				frameSets.get(currentFrameSetName).run(gc, isPaused);
			}
			elapsedFrames++;
		}
	}
	
	private void processLinkedEntity() {
		if (linkedEntityFront != null) {
			if (linkedEntityInfos.isEmpty()) {
				setPosition(linkedEntityFront.getX() + linkedEntityOffset.getX(),
						linkedEntityFront.getY() + linkedEntityOffset.getY());
				if (direction != linkedEntityFront.getDirection())
					setDirection(linkedEntityFront.getDirection());
			}
			else {
				setPosition(linkedEntityInfos.get(0).x + linkedEntityOffset.getX(),
										linkedEntityInfos.get(0).y + linkedEntityOffset.getY());
				if (direction != linkedEntityInfos.get(0).direction)
					setDirection(linkedEntityInfos.get(0).direction);
				linkedEntityInfos.remove(0);
				linkedEntityInfos.add(new LinkedEntityInfos(linkedEntityFront));
			}
		}
	}

	private void applyShadow() {
		if (haveShadow()) {
			Tools.addDrawQueue((int)getY(), SpriteLayerType.SPRITE, DrawType.SAVE);
			Tools.addDrawQueue((int)getY(), SpriteLayerType.SPRITE, DrawType.SET_FILL, Color.BLACK);
			Tools.addDrawQueue((int)getY(), SpriteLayerType.SPRITE, DrawType.SET_GLOBAL_ALPHA, shadowOpacity);
			Tools.addDrawQueue((int)getY(), SpriteLayerType.SPRITE, DrawType.FILL_OVAL, getX() + Main.TILE_SIZE / 2 - getShadowWidth() / 2, getY() + Main.TILE_SIZE - getShadowHeight(), getShadowWidth(), getShadowHeight());
			Tools.addDrawQueue((int)getY(), SpriteLayerType.SPRITE, DrawType.RESTORE);
		}
	}
	
	public boolean[] getFreeCorners()
		{ return getFreeCorners(getDirection()); }
	
	public boolean[] getFreeCorners(Direction direction) {
		boolean[] freeCorners = new boolean[4];
		int x = 0;
		for (Position pos : getCornersPositions()) {
			pos.incPositionByDirection(direction);
			TileCoord coord = new TileCoord(((int)pos.getX() / Main.TILE_SIZE), ((int)pos.getY() / Main.TILE_SIZE));
			freeCorners[x++] = MapSet.tileIsFree(this, coord, passThrough);
		}
		return freeCorners;
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
		return (dir == Direction.LEFT && freeCorners[0] && freeCorners[2]) ||
					 (dir == Direction.UP && freeCorners[0] && freeCorners[1]) ||
					 (dir == Direction.RIGHT && freeCorners[1] && freeCorners[3]) ||
					 (dir == Direction.DOWN && freeCorners[2] && freeCorners[3]);
	}
	
	public boolean isPerfectlyBlockedDir(Direction dir) {
		boolean[] freeCorners = getFreeCorners(dir);
		return (dir == Direction.LEFT && !freeCorners[0] && !freeCorners[2]) ||
					 (dir == Direction.UP && !freeCorners[0] && !freeCorners[1]) ||
					 (dir == Direction.RIGHT && !freeCorners[1] && !freeCorners[3]) ||
					 (dir == Direction.DOWN && !freeCorners[2] && !freeCorners[3]);
	}
	
	public void moveEntity()
		{ moveEntity(getDirection()); }
	
	public void moveEntity(double speed)
		{ moveEntity(getDirection(), speed); }
		
	public void moveEntity(Direction direction)
		{ moveEntity(direction, getTempSpeed() >= 0 ? getTempSpeed() : getSpeed()); }

	public void moveEntity(Direction direction, double speed) {
		tileWasChanged = false;
		getFreeCorners(direction);
		if (speed != 0) {
			Position[] cornersPositions = getCornersPositions();
			Position lu = cornersPositions [0], ru = cornersPositions[1], ld = cornersPositions[2], rd = cornersPositions[3];
			boolean[] freeCorners = getFreeCorners(direction);
			int z = 10;
			// Sistema para alinhar o personagem quando ele esta tentando ir em uma direcao onde um dos 2 cantos a frente do personagem é um tile livre mas o canto oposto a frente do personagem está bloqueado
			if (direction == Direction.UP) {
				if (freeCorners[0] && freeCorners[1])
					incPositionByDirection(direction, speed);
				else if (!freeCorners[0] && freeCorners[1] && (int)ru.getX() % Main.TILE_SIZE > Main.TILE_SIZE / z) // Se estiver andando para cima, e o canto esquerdo superior estiver bloqueado, mas o canto direito superior estiver livre, e esse canto livre for maior que metade do tile, anda na diagonal tentando alinhar
					incPosition(speed, -speed / 2);
				else if (freeCorners[0] && !freeCorners[1] && (int)lu.getX() % Main.TILE_SIZE < Main.TILE_SIZE - Main.TILE_SIZE / z) // Se estiver andando para cima, e o canto direito superior estiver bloqueado, mas o canto esquerdo superior estiver livre, e esse canto livre for maior que metade do tile, anda na diagonal tentando alinhar
					incPosition(-speed, -speed / 2);
			}
			else if (direction == Direction.DOWN) {
				if (freeCorners[2] && freeCorners[3])
					incPositionByDirection(direction, speed);
				else if (!freeCorners[2] && freeCorners[3] && (int)rd.getX() % Main.TILE_SIZE > Main.TILE_SIZE / z) // Se estiver andando para baixo, e o canto esquerdo inferior estiver bloqueado, mas o canto direito inferior estiver livre, e esse canto livre for maior que metade do tile, anda na diagonal tentando alinhar
					incPosition(speed, speed / 2);
				else if (freeCorners[2] && !freeCorners[3] && (int)ld.getX() % Main.TILE_SIZE < Main.TILE_SIZE - Main.TILE_SIZE / z) // Se estiver andando para baixo, e o canto direito inferior estiver bloqueado, mas o canto esquerdo inferior estiver livre, e esse canto livre for maior que metade do tile, anda na diagonal tentando alinhar
					incPosition(-speed, speed / 2);
			}
			else if (direction == Direction.LEFT) {
				if (freeCorners[0] && freeCorners[2])
					incPositionByDirection(direction, speed);
				else if (!freeCorners[0] && freeCorners[2] && (int)ld.getY() % Main.TILE_SIZE > Main.TILE_SIZE / z) // Se estiver andando para esquerda, e o canto esquerdo superior estiver bloqueado, mas o canto esquerdo inferior estiver livre, e esse canto livre for maior que metade do tile, anda na diagonal tentando alinhar
					incPosition(-speed / 2, speed);
				else if (freeCorners[0] && !freeCorners[2] && (int)lu.getY() % Main.TILE_SIZE < Main.TILE_SIZE - Main.TILE_SIZE / z) // Se estiver andando para esquerda, e o canto esquerdo inferior estiver bloqueado, mas o canto esquerdo superior estiver livre, e esse canto livre for maior que metade do tile, anda na diagonal tentando alinhar
					incPosition(-speed / 2, -speed);
			}
			else if (direction == Direction.RIGHT) {
				if (freeCorners[1] && freeCorners[3])
					incPositionByDirection(direction, speed);
				else if (!freeCorners[1] && freeCorners[3] && (int)rd.getY() % Main.TILE_SIZE > Main.TILE_SIZE / z) // Se estiver andando para direita, e o canto direita superior estiver bloqueado, mas o canto direita inferior estiver livre, e esse canto livre for maior que metade do tile, anda na diagonal tentando alinhar
					incPosition(speed / 2, speed);
				else if (freeCorners[1] && !freeCorners[3] && (int)ru.getY() % Main.TILE_SIZE < Main.TILE_SIZE - Main.TILE_SIZE / z) // Se estiver andando para direita, e o canto direita inferior estiver bloqueado, mas o canto direita superior estiver livre, e esse canto livre for maior que metade do tile, anda na diagonal tentando alinhar
					incPosition(speed / 2, -speed);
			}
			if (!previewTileCoord.equals(getTileCoord()) &&
				 ((getDirection() == Direction.LEFT && (int)((getX() + Main.TILE_SIZE - 1) / Main.TILE_SIZE) < previewTileCoord.getX()) ||
				  (getDirection() == Direction.UP && (int)((getY() + Main.TILE_SIZE - 1) / Main.TILE_SIZE) < previewTileCoord.getY()) ||
				  (getDirection() == Direction.RIGHT && (int)(getX() / Main.TILE_SIZE) != previewTileCoord.getX()) ||
				  (getDirection() == Direction.DOWN && (int)(getY() / Main.TILE_SIZE) != previewTileCoord.getY()))) {
						previewTileCoord.setCoord(getTileCoord());
						elapsedSteps++;
						tileWasChanged = true;
			}
			if (isPerfectlyBlockedDir(getDirection()) && !isPerfectTileCentred())
				setPosition(getTileCoord().getX() * Main.TILE_SIZE, getTileCoord().getY() * Main.TILE_SIZE);
				
		}
	}

	public int getElapsedSteps()
		{ return elapsedSteps; }

	public void setElapsedSteps(int elapsedSteps)
		{ this.elapsedSteps = elapsedSteps; }

	public int getElapsedFrames()
		{ return elapsedFrames; }

	public void setElapsedFrames(int elapsedFrames)
		{ this.elapsedFrames = elapsedFrames; }

	public int getTotalFrameSets()
		{ return frameSets.size(); }

	public Direction getDirection()
		{ return direction; }

	public void setDirection(Direction direction) {
		if (!blockedMovement && this.direction != direction) {
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

	public Elevation getElevation()
		{ return elevation; }

	public void setElevation(Elevation elevation)
		{ this.elevation = elevation; }
	
	public double getSpeed()
		{ return speed; }
	
	public void setSpeed(double speed)
		{ this.speed = speed; }
	
	public double getTempSpeed()
		{ return tempSpeed; }
	
	public void setTempSpeed(double tempSpeed)
		{ this.tempSpeed = tempSpeed; }

	public void setNoMove(boolean state)
		{ noMove = state; }
	
	public boolean getNoMove()
		{ return noMove; }
	
	public void setDisabled()
		{ isDisabled = true; }
	
	public void setEnabled()
		{ isDisabled = false; }

	public boolean isDisabled()
		{ return isDisabled; }

	public void setShadow(int offsetX, int offsetY, int width, int height, float opacity) {
		if (shadow == null)
			shadow = new Rectangle(offsetX, offsetY, width, height);
		shadow.setBounds(offsetX, offsetY, width, height);
		shadowOpacity = opacity;
	}
	
	public int getShadowOffsetX()
		{ return shadow == null ? 0 : (int)shadow.getX(); }
	
	public int getShadowOffsetY()
		{ return shadow == null ? 0 : (int)shadow.getY(); }
	
	public int getShadowWidth()
		{ return shadow == null ? 0 : (int)shadow.getWidth(); }

	public int getShadowHeight()
		{ return shadow == null ? 0 : (int)shadow.getHeight(); }
	
	public float getShadowOpacity()
		{ return shadowOpacity; }

	public void setShadowOffsetX(int value)
		{ shadow.setLocation(value, getShadowOffsetY()); }

	public void setShadowOffsetY(int value)
		{ shadow.setLocation(getShadowOffsetX(), value); }

	public void setShadowWidth(int value)
		{ shadow.setSize(value, (int)shadow.getHeight()); }

	public void setShadowHeight(int value)
		{ shadow.setSize((int)shadow.getWidth(), value); }
	
	public void setShadowOpacity(float value)
		{ shadowOpacity = value; }

	public void removeShadow()
		{ shadow = null; }

	public boolean haveShadow()
		{ return shadow != null; }

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
			if (TileProp.getCantCrossList(elevation).contains(prop) ||
					(Brick.haveBrickAt(coord, true) && !canPassThroughBrick()) || (Bomb.haveBombAt(this, coord) && !canPassThroughBomb()))
						return false;
		}
		return true;
	}
	
	public void restartCurrentFrameSet()
		{ setFrameSet(currentFrameSetName);	}
	
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