package frameset_tags;

import entities.Entity;
import entities.FrameSet;
import entities.Sprite;

public class IncEntityPos extends FrameTag {
	
	private double incrementX;
	private double incrementY;
	
	public IncEntityPos(double incrementX, double incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
	}

	public double getIncrementX()
		{ return incrementX; }

	public double getIncrementY()
		{ return incrementY; }	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + incrementX + ";" + incrementY + "}"; }

	public IncEntityPos(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementX = Double.parseDouble(params[n++]);
			incrementY = Double.parseDouble(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public IncEntityPos getNewInstanceOfThis()
		{ return new IncEntityPos(incrementX, incrementY); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.incX(getIncrementX());
		entity.incY(getIncrementY());
	}

	@Override
	public void reset() {
	}

}
