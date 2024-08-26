package frameset_tags;

import entities.Sprite;

public class IncSprDropShadowValues extends FrameTag {
	
	private double incrementOffsetX;
	private double incrementOffsetY;
	
	public IncSprDropShadowValues(double incrementOffsetX, double incrementOffsetY) {
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

	public IncSprDropShadowValues(String tags) {
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
	public IncSprDropShadowValues getNewInstanceOfThis()
		{ return new IncSprDropShadowValues(incrementOffsetX, incrementOffsetY); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getDropShadow().incOffsetX(getIncrementOffsetX());
		sprite.getEffects().getDropShadow().incOffsetY(getIncrementOffsetY());
	}

	@Override
	public void reset() {
	}

}