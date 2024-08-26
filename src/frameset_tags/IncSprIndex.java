package frameset_tags;

import entities.Sprite;

public class IncSprIndex extends FrameTag {
	
	private int increment;
	
	public IncSprIndex(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprIndex(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprIndex getNewInstanceOfThis()
		{ return new IncSprIndex(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incSpriteIndex(getIncrement()); }

}
