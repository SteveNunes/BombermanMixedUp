package frameset_tags;

import frameset.FrameSet;
import frameset.Sprite;

public class IncObjPos extends FrameTag {
	
	public double incrementX;
	public double incrementY;
	
	public IncObjPos(double incrementX, double incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
	}

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
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public IncObjPos getNewInstanceOfThis()
		{ return new IncObjPos(incrementX, incrementY); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getSourceFrameSet();
		frameSet.incX(incrementX);
		frameSet.incY(incrementY);
	}

}
