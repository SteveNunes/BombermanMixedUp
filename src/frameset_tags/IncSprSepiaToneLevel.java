package frameset_tags;

import frameset.Sprite;

public class IncSprSepiaToneLevel extends FrameTag {
	
	public int increment;
	
	public IncSprSepiaToneLevel(int increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + increment + "}"; }
	
	public IncSprSepiaToneLevel(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprSepiaToneLevel getNewInstanceOfThis()
		{ return new IncSprSepiaToneLevel(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getSepiaTone().incLevel(increment); }

}



