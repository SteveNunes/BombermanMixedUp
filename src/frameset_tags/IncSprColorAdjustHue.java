package frameset_tags;

public class IncSprColorAdjustHue extends FrameTag {
	
	private double increment;
	
	public IncSprColorAdjustHue(double increment)
		{ this.increment = increment; }

	public double getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprColorAdjustHue(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprColorAdjustHue getNewInstanceOfThis()
		{ return new IncSprColorAdjustHue(increment); }
	
}
