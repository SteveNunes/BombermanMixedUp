package frameset_tags;

import frameset.Sprite;

public class IncSprColorTintRed extends FrameTag {
	
	public double increment;
	
	public IncSprColorTintRed(double increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + increment + "}"; }
	
	public IncSprColorTintRed(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprColorTintRed getNewInstanceOfThis()
		{ return new IncSprColorTintRed(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getColorTint().incRed(increment); }

}



