package frameset_tags;

import frameset.Sprite;

public class IncSprColorAdjustSaturation extends FrameTag {
	
	public double increment;
	
	public IncSprColorAdjustSaturation(double increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + increment + "}"; }
	
	public IncSprColorAdjustSaturation(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprColorAdjustSaturation getNewInstanceOfThis()
		{ return new IncSprColorAdjustSaturation(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getColorAdjust().incSaturation(increment); }

}



