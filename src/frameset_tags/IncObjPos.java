package frameset_tags;

import entities.FrameSet;
import entities.Sprite;
import tools.GameMisc;

public class IncObjPos extends FrameTag {
	
	private double incrementX;
	private double incrementY;
	
	public IncObjPos(double incrementX, double incrementY) {
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

	public IncObjPos(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementX = Double.parseDouble(params[n++]);
			incrementY = Double.parseDouble(params[n++]);
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public IncObjPos getNewInstanceOfThis()
		{ return new IncObjPos(incrementX, incrementY); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		frameSet.incX(getIncrementX());
		frameSet.incY(getIncrementY());
	}

}
