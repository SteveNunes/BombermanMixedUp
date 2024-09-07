package frameset_tags;

import entities.Sprite;
import tools.GameMisc;

public class IncSprColorAdjustBrightness extends FrameTag {
	
	private double increment;
	
	public IncSprColorAdjustBrightness(double increment)
		{ this.increment = increment; }

	public double getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprColorAdjustBrightness(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprColorAdjustBrightness getNewInstanceOfThis()
		{ return new IncSprColorAdjustBrightness(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getColorAdjust().incBrightness(getIncrement()); }

}
