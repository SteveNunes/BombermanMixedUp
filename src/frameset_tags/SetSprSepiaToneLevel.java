package frameset_tags;

public class SetSprSepiaToneLevel extends FrameTag {
	
	private double value;
	
	public SetSprSepiaToneLevel(double value)
		{ this.value = value; }

	public double getGlow()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprSepiaToneLevel(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

}