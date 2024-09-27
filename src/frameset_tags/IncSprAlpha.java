package frameset_tags;

import frameset.Sprite;

public class IncSprAlpha extends FrameTag {
	
	public double increment;
	
	public IncSprAlpha(double increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprAlpha(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprAlpha getNewInstanceOfThis()
		{ return new IncSprAlpha(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incAlpha(increment); }

}



