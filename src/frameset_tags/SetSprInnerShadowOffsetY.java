package frameset_tags;

import entities.Sprite;

public class SetSprInnerShadowOffsetY extends FrameTag {
	
	private int value;
	
	public SetSprInnerShadowOffsetY(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprInnerShadowOffsetY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
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
		{ sprite.getEffects().getInnerShadow().setOffsetY(getValue()); }

	@Override
	public void reset() {
	}

}