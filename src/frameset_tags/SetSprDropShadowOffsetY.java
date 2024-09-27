package frameset_tags;

import frameset.Sprite;

public class SetSprDropShadowOffsetY extends FrameTag {
	
	public double value;
	
	public SetSprDropShadowOffsetY(double value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprDropShadowOffsetY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprDropShadowOffsetY getNewInstanceOfThis()
		{ return new SetSprDropShadowOffsetY(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getDropShadow().setOffsetY(value); }

}



