package entities;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import application.Main;
import enums.Direction;
import enums.Elevation;
import javafx.scene.paint.Color;
import objmoveutils.Position;

public class Entity extends Position {
	
	private Rectangle shadow;
	private Map<String, FrameSet> frameSets;
	private Position speed;
	private Direction direction;
	private Elevation elevation;
	private String currentFrameSetName;
	private boolean noMove;
	private boolean isDead;
	private int totalFrameSets;
	private int invencibilityFrames;
	private float shadowOpacity;
	
	public Entity(Entity entity) {
		super(entity.getPosition());
		shadow = entity.shadow == null ? null : new Rectangle(entity.shadow);
		frameSets = new HashMap<>();
		for (String fSetName : frameSets.keySet())
			frameSets.put(fSetName, new FrameSet(frameSets.get(fSetName), this));
		direction = entity.direction;
		elevation = entity.elevation;
		speed = new Position(entity.speed);
		totalFrameSets = entity.totalFrameSets;
		noMove = entity.noMove;
		isDead = entity.isDead;
		invencibilityFrames = entity.invencibilityFrames;
		shadowOpacity = entity.shadowOpacity;
	}
	
	public Entity()
		{ this(0, 0, Direction.DOWN); }

	public Entity(int x, int y)
		{ this(x, y, Direction.DOWN); }

	public Entity(int x, int y, Direction direction) {
		super(x, y);
		currentFrameSetName = null;
		frameSets = new HashMap<>();
		shadow = null;
		this.direction = direction;
		speed = new Position();
		elevation = Elevation.ON_GROUND;
		totalFrameSets = 0;
		invencibilityFrames = 0;
		shadowOpacity = 0;
		noMove = false;
		isDead = false;
	}
	
	public Map<String, FrameSet> getFrameSetsMap()
		{ return frameSets; }
	
	public void setFrameSetMap(Map<String, FrameSet> frameSetMap)
		{ frameSets = frameSetMap; }
	
	public Collection<FrameSet> getFrameSets()
		{ return frameSets.values(); }
	
	public Collection<String> getFrameSetsNames()
		{ return frameSets.keySet(); }
	
	public FrameSet getCurrentFrameSet()
		{ return getFrameSet(currentFrameSetName); }

	public FrameSet getFrameSet(String frameSetName)
		{ return frameSets.get(frameSetName); }
	
	public void setFrameSet(String currentFrameSet) {
		this.currentFrameSetName = currentFrameSet;
	}
	
	public void addFrameSet(String frameSetName, FrameSet frameSet) {
		if (!frameSets.containsKey(frameSetName)) {
			frameSets.put(frameSetName, frameSet);
			totalFrameSets++;
		}
	}
	
	public void removeFrameSet(String frameSetName) {
		if (frameSets.containsKey(frameSetName)) {
			frameSets.remove(frameSetName);
			totalFrameSets--;
		}
	}
	
	public void run(boolean isPaused) {
		if (frameSets.containsKey(currentFrameSetName)) {
			if (haveShadow()) {
				Main.gcDraw.save();
				Main.gcDraw.setFill(Color.BLACK);
				Main.gcDraw.setGlobalAlpha(shadowOpacity);
				Main.gcDraw.fillOval(getX() + Main.tileSize / 2 - getShadowWidth() / 2, getY() + Main.tileSize - getShadowHeight(), getShadowWidth(), getShadowHeight());
				Main.gcDraw.restore();
			}
			frameSets.get(currentFrameSetName).run(isPaused);
		}
	}
	
	public int getTotalFrameSets()
		{ return totalFrameSets; }

	public Direction getDirection()
		{ return direction; }

	public void setDirection(Direction direction) {
		String name = currentFrameSetName.split("\\.")[0] + "." + direction.name();
		System.out.println(name);
		if (frameSets.containsKey(name)) {
			currentFrameSetName = name;
			getCurrentFrameSet().resetTags();
		}
		this.direction = direction;
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
	}
	
}
