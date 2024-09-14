package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class IncOriginSprY extends FrameTag {
	
	private int increment;
	
	public IncOriginSprY(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncOriginSprY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncOriginSprY getNewInstanceOfThis()
		{ return new IncOriginSprY(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incOriginSpriteY(getIncrement()); }

}
