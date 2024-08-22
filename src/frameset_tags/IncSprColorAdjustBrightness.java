package frameset_tags;

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
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprColorAdjustBrightness getNewInstanceOfThis()
		{ return new IncSprColorAdjustBrightness(increment); }
	
}
