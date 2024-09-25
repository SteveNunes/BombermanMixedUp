package frameset_tags;

import frameset.Sprite;
import tools.Tools;

public class IncOutputSprPos extends FrameTag {
	
	private double incrementX;
	private double incrementY;
	
	public IncOutputSprPos(double incrementX, double incrementY) {
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

	public IncOutputSprPos(String tags) {
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
	public IncOutputSprPos getNewInstanceOfThis()
		{ return new IncOutputSprPos(incrementX, incrementY); }

	@Override
	public void process(Sprite sprite) {
		sprite.incX(getIncrementX());
		sprite.incY(getIncrementY());
	}

}
