package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class IncOriginSprWidth extends FrameTag {
	
	private int increment;
	
	public IncOriginSprWidth(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncOriginSprWidth(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncOriginSprWidth getNewInstanceOfThis()
		{ return new IncOriginSprWidth(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incOriginSpriteWidth(getIncrement()); }

}
