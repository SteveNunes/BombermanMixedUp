package frameset_tags;

import frameset.Sprite;

public class IncSprGaussBlurRadius extends FrameTag {
	
	public int increment;
	
	public IncSprGaussBlurRadius(int increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + increment + "}"; }
	
	public IncSprGaussBlurRadius(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprGaussBlurRadius getNewInstanceOfThis()
		{ return new IncSprGaussBlurRadius(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getGaussianBlur().incRadius(increment); }

}



