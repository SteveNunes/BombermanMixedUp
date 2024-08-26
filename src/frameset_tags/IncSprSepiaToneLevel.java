package frameset_tags;

import entities.Sprite;

public class IncSprSepiaToneLevel extends FrameTag {
	
	private int increment;
	
	public IncSprSepiaToneLevel(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprSepiaToneLevel(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
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
		{ sprite.getEffects().getSepiaTone().incLevel(getIncrement()); }

	@Override
	public void reset() {
	}

}
