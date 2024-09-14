package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class IncSprGlowLevel extends FrameTag {
	
	private int increment;
	
	public IncSprGlowLevel(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprGlowLevel(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprGlowLevel getNewInstanceOfThis()
		{ return new IncSprGlowLevel(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setGlow(sprite.getEffects().getGlow().getLevel() + getIncrement(), sprite.getEffects().getGlow().getBlendMode()); }

}
