package frameset_tags;

import frameset.Sprite;
import tools.Tools;

public class SetSprGlowLevel extends FrameTag {
	
	private int value;
	
	public SetSprGlowLevel(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprGlowLevel(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprGlowLevel getNewInstanceOfThis()
		{ return new SetSprGlowLevel(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getGlow().setLevel(getValue()); }

}