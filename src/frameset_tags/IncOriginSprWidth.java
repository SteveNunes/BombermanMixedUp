package frameset_tags;

import frameset.Sprite;

public class IncOriginSprWidth extends FrameTag {
	
	public int increment;
	
	public IncOriginSprWidth(int increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncOriginSprWidth(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncOriginSprWidth getNewInstanceOfThis()
		{ return new IncOriginSprWidth(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incOriginSpriteWidth(increment); }

}



