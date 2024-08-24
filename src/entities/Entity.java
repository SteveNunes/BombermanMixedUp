package entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import enums.Direction;
import enums.Elevation;
import objmoveutils.Position;

public class Entity extends Position {
	
	private Map<String, FrameSet> frameSets;
	private Position speed;
	private Direction direction;
	private Elevation elevation;
	private String currentFrameSet;
	private int totalFrameSets;
	
	public Entity(Entity entity) {
		super(entity.getPosition());
		frameSets = new HashMap<>();
		for (String fSetName : frameSets.keySet())
			frameSets.put(fSetName, new FrameSet(frameSets.get(fSetName), this));
		direction = entity.direction;
		elevation = entity.elevation;
		speed = new Position(entity.speed);
		totalFrameSets = entity.totalFrameSets;
	}
	
	public Entity()
		{ this(0, 0, Direction.DOWN); }

	public Entity(int x, int y)
		{ this(x, y, Direction.DOWN); }

	public Entity(int x, int y, Direction direction) {
		super(x, y);
		currentFrameSet = null;
		frameSets = new HashMap<>();
		this.direction = direction;
		speed = new Position();
		elevation = Elevation.ON_GROUND;
		totalFrameSets = 0;
	}
	
	public Map<String, FrameSet> getFrameSetsMap()
		{ return frameSets; }
	
	public void setFrameSetMap(Map<String, FrameSet> frameSetMap)
		{ frameSets = frameSetMap; }
	
	public Collection<FrameSet> getFrameSets()
		{ return frameSets.values(); }
	
	public Collection<String> getFrameSetsNames()
		{ return frameSets.keySet(); }

	public FrameSet getFrameSet(String frameSetName)
		{ return frameSets.get(frameSetName); }
	
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
	
	public void run() {
		frameSets.get(currentFrameSet).run();
	}
	
	public int getTotalFrameSets()
		{ return totalFrameSets; }

	public Direction getDirection()
		{ return direction; }

	public void setDirection(Direction direction)
		{ this.direction = direction; }

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
	
}
