package frameset_tags;

import frameset.Sprite;

public class IncSprDropShadowOffsetY extends FrameTag {
	
	public double increment;
	
	public IncSprDropShadowOffsetY(double increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprDropShadowOffsetY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprDropShadowOffsetY getNewInstanceOfThis()
		{ return new IncSprDropShadowOffsetY(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getDropShadow().incOffsetY(increment); }

}



