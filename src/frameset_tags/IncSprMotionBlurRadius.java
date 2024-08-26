package frameset_tags;

import entities.Sprite;

public class IncSprMotionBlurRadius extends FrameTag {
	
	private double increment;
	
	public IncSprMotionBlurRadius(double increment)
		{ this.increment = increment; }

	public double getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprMotionBlurRadius(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprMotionBlurRadius getNewInstanceOfThis()
		{ return new IncSprMotionBlurRadius(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getMotionBlur().incRadius(getIncrement()); }

	@Override
	public void reset() {
	}

}
