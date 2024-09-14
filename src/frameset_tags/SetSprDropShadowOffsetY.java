package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class SetSprDropShadowOffsetY extends FrameTag {
	
	private double value;
	
	public SetSprDropShadowOffsetY(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprDropShadowOffsetY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprDropShadowOffsetY getNewInstanceOfThis()
		{ return new SetSprDropShadowOffsetY(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getDropShadow().setOffsetY(getValue()); }

}