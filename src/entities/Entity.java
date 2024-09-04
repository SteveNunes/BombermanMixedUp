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
import enums.Elevation;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import objmoveutils.Position;

public class Entity extends Position {
	
	private Rectangle shadow;
	private Map<String, FrameSet> frameSets;
	private Map<String, FrameSet> freshFrameSets;
	private List<LinkedEntityInfos> linkedEntityInfos;
	private Position speed;
	private Direction direction;
	private Elevation elevation;
	private String currentFrameSetName;
	private Entity linkedEntityFront;
	private Entity linkedEntityBack;
	private Position linkedEntityOffset;
	private boolean noMove;
	private boolean isDead;
	private int invencibilityFrames;
	private float shadowOpacity;
	
	public Entity(Entity entity) {
		super(entity.getPosition());
		setTileSize(Main.tileSize);
		shadow = entity.shadow == null ? null : new Rectangle(entity.shadow);
		frameSets = new HashMap<>();
		freshFrameSets = new HashMap<>();
		for (String fSetName : entity.frameSets.keySet()) {
			frameSets.put(fSetName, new FrameSet(entity.frameSets.get(fSetName), this));
			freshFrameSets.put(fSetName, new FrameSet(entity.freshFrameSets.get(fSetName), this));
		}
		speed = new Position(entity.speed);
		direction = entity.direction;
		elevation = entity.elevation;
		noMove = entity.noMove;
		isDead = entity.isDead;
		invencibilityFrames = entity.invencibilityFrames;
		shadowOpacity = entity.shadowOpacity;
		linkedEntityInfos = new LinkedList<>();
		linkedEntityBack = null;
		linkedEntityFront = null;
		linkedEntityOffset = null;
	}
	
	public Entity()
		{ this(0, 0, Direction.DOWN); }

	public Entity(int x, int y)
		{ this(x, y, Direction.DOWN); }

	public Entity(int x, int y, Direction direction) {
		super(x, y);
		setTileSize(Main.tileSize);
		currentFrameSetName = null;
		frameSets = new HashMap<>();
		freshFrameSets = new HashMap<>();
		linkedEntityInfos = new ArrayList<>();
		linkedEntityBack = null;
		linkedEntityFront = null;
		linkedEntityOffset = null;
		shadow = null;
		this.direction = direction;
		speed = new Position();
		elevation = Elevation.ON_GROUND;
		invencibilityFrames = 0;
		shadowOpacity = 0;
		noMove = false;
		isDead = false;
	}
	
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

	public FrameSet getCurrentFrameSet()
		{ return getFrameSet(currentFrameSetName); }

	public FrameSet getFrameSet(String frameSetName)
		{ return frameSets.get(frameSetName); }
	
	public void setFrameSet(String frameSetName) {
		if (getCurrentFrameSet() != null)
			frameSets.put(frameSetName, new FrameSet(freshFrameSets.get(frameSetName), this));
		currentFrameSetName = frameSetName;
	}
	
	public void addFrameSet(String frameSetName, FrameSet frameSet) {
		if (!frameSets.containsKey(frameSetName)) {
			frameSets.put(frameSetName, frameSet);
			freshFrameSets.put(frameSetName, new FrameSet(frameSet, this));
		}
	}
	
	public void removeFrameSet(String frameSetName) {
		if (frameSets.containsKey(frameSetName)) {
			frameSets.remove(frameSetName);
			freshFrameSets.remove(frameSetName);
		}
	}
	
	public void run(GraphicsContext gc, boolean isPaused) {
		if (frameSets.containsKey(currentFrameSetName)) {
			
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
			
			if (haveShadow()) {
				gc.save();
				gc.setFill(Color.BLACK);
				gc.setGlobalAlpha(shadowOpacity);
				gc.fillOval(getX() + Main.tileSize / 2 - getShadowWidth() / 2, getY() + Main.tileSize - getShadowHeight(), getShadowWidth(), getShadowHeight());
				gc.restore();
			}
			frameSets.get(currentFrameSetName).run(gc, isPaused);
		}
	}
	
	public int getTotalFrameSets()
		{ return frameSets.size(); }

	public Direction getDirection()
		{ return direction; }

	public void setDirection(Direction direction) {
		if (this.direction != direction) {
			String name = currentFrameSetName.split("\\.")[0] + "." + direction.name();
			if (frameSets.containsKey(name))
				setFrameSet(name);
			this.direction = direction;
		}
	}

	public Elevation getElevation()
		{ return elevation; }

	public void setElevation(Elevation elevation)
		{ this.elevation = elevation; }
	
	public Position getSpeed()
		{ return speed; }
	
	public void setSpeed(double speed)
		{ this.speed.setPosition(speed, speed); }
	
	public void setSpeedX(double speed)
		{ this.speed.setPosition(speed, this.speed.getY()); }

	public void setSpeedY(double speed)
		{ this.speed.setPosition(this.speed.getX(), speed); }

	public void setSpeed(double speedX, double speedY)
		{ this.speed.setPosition(speedX, speedY); }
	
	public void setNoMove(boolean state)
		{ noMove = state; }
	
	public boolean getNoMove()
		{ return noMove; }
	
	public void setDead(boolean state)
		{ isDead = state; }
	
	public boolean isDead()
		{ return isDead; }

	public int getInvencibilityFrames()
		{ return invencibilityFrames; }

	public void setInvencibilityFrames(int invencibilityFrames)
		{ this.invencibilityFrames = invencibilityFrames; }
	
	public boolean isInvencible()
		{ return isDead || invencibilityFrames != 0; }
	
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
		frameSets.put(frameSetName, frameSet);
		freshFrameSets.put(frameSetName, new FrameSet(frameSet, this));
	}
	
	public void restartCurrentFrameSet()
		{ setFrameSet(currentFrameSetName);	}

}

class LinkedEntityInfos {
	
	public double x;
	public double y;
	public Direction direction;
	
	public LinkedEntityInfos(Entity entity) {
		x = entity.getX();
		y = entity.getY();
		direction = entity.getDirection();
	}
	
}