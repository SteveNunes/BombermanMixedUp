package frameset_tags;

import entities.Sprite;

public class IncOutputSprY extends FrameTag {
	
	private double increment;
	
	public IncOutputSprY(double increment)
		{ this.increment = increment; }

	public double getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncOutputSprY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncOutputSprY getNewInstanceOfThis()
		{ return new IncOutputSprY(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incY(getIncrement()); }

}
