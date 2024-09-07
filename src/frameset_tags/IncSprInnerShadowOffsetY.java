package frameset_tags;

import entities.Sprite;
import tools.GameMisc;

public class IncSprInnerShadowOffsetY extends FrameTag {
	
	private double increment;
	
	public IncSprInnerShadowOffsetY(double increment)
		{ this.increment = increment; }

	public double getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprInnerShadowOffsetY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprInnerShadowOffsetY getNewInstanceOfThis()
		{ return new IncSprInnerShadowOffsetY(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getInnerShadow().incOffsetY(getIncrement()); }

}
