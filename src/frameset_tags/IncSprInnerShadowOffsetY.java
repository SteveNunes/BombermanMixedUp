package frameset_tags;

import frameset.Sprite;

public class IncSprInnerShadowOffsetY extends FrameTag {
	
	public double increment;
	
	public IncSprInnerShadowOffsetY(double increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprInnerShadowOffsetY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprInnerShadowOffsetY getNewInstanceOfThis()
		{ return new IncSprInnerShadowOffsetY(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getInnerShadow().incOffsetY(increment); }

}



