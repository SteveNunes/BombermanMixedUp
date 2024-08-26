package frameset_tags;

import entities.Sprite;

public class IncSprMotionBlurAngle extends FrameTag {
	
	private double increment;
	
	public IncSprMotionBlurAngle(double increment)
		{ this.increment = increment; }

	public double getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprMotionBlurAngle(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprMotionBlurAngle getNewInstanceOfThis()
		{ return new IncSprMotionBlurAngle(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getMotionBlur().incAngle(getIncrement()); }

}
