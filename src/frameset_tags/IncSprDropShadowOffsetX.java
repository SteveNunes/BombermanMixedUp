package frameset_tags;

import frameset.Sprite;

public class IncSprDropShadowOffsetX extends FrameTag {
	
	public double increment;
	
	public IncSprDropShadowOffsetX(double increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + increment + "}"; }
	
	public IncSprDropShadowOffsetX(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprDropShadowOffsetX getNewInstanceOfThis()
		{ return new IncSprDropShadowOffsetX(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getDropShadow().incOffsetX(increment); }

}



