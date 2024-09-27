package frameset_tags;

import frameset.Sprite;

public class IncSprInnerShadowOffsetX extends FrameTag {
	
	public double increment;
	
	public IncSprInnerShadowOffsetX(double increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprInnerShadowOffsetX(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprInnerShadowOffsetX getNewInstanceOfThis()
		{ return new IncSprInnerShadowOffsetX(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getInnerShadow().incOffsetX(increment); }

}



