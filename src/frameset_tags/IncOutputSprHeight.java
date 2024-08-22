package frameset_tags;

public class IncOutputSprHeight extends FrameTag {
	
	private int increment;
	
	public IncOutputSprHeight(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncOutputSprHeight(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncOutputSprHeight getNewInstanceOfThis()
		{ return new IncOutputSprHeight(increment); }
	
}
