package frameset_tags;

public class SetSprColorTintAlpha extends FrameTag {
	
	private double value;
	
	public SetSprColorTintAlpha(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprColorTintAlpha(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprColorTintAlpha getNewInstanceOfThis()
		{ return new SetSprColorTintAlpha(value); }
	
}