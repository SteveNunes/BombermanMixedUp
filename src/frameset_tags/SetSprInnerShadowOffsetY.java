package frameset_tags;

import frameset.Sprite;

public class SetSprInnerShadowOffsetY extends FrameTag {
	
	public int value;
	
	public SetSprInnerShadowOffsetY(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + value + "}"; }

	public SetSprInnerShadowOffsetY(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprInnerShadowOffsetY getNewInstanceOfThis()
		{ return new SetSprInnerShadowOffsetY(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getInnerShadow().setOffsetY(value); }

}



