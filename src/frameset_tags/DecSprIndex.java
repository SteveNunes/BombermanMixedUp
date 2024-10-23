package frameset_tags;

import frameset.Sprite;

public class DecSprIndex extends FrameTag {
	
	public int increment;
	
	public DecSprIndex(int increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + increment + "}"; }
	
	public DecSprIndex(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public DecSprIndex getNewInstanceOfThis()
		{ return new DecSprIndex(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.decSpriteIndex(increment); }

}



