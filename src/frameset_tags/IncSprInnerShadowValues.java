package frameset_tags;

import entities.Sprite;

public class IncSprInnerShadowValues extends FrameTag {
	
	private double incrementOffsetX;
	private double incrementOffsetY;
	
	public IncSprInnerShadowValues(double incrementOffsetX, double incrementOffsetY) {
		this.incrementOffsetX = incrementOffsetX;
		this.incrementOffsetY = incrementOffsetY;
	}

	public double getIncrementOffsetX()
		{ return incrementOffsetX; }

	public double getIncrementOffsetY()
		{ return incrementOffsetY; }	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + incrementOffsetX + ";" + incrementOffsetY + "}"; }

	public IncSprInnerShadowValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementOffsetX = Double.parseDouble(params[n++]);
			incrementOffsetY = Double.parseDouble(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public IncSprInnerShadowValues getNewInstanceOfThis()
		{ return new IncSprInnerShadowValues(incrementOffsetX, incrementOffsetY); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getInnerShadow().incOffsetX(getIncrementOffsetX());
		sprite.getEffects().getInnerShadow().incOffsetY(getIncrementOffsetY());
	}

	@Override
	public void reset() {
	}

}